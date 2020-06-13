package com.salle.projects.lsmakerappv2.view.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.salle.projects.lsmakerappv2.R;
import com.salle.projects.lsmakerappv2.bluetooth.BluetoothService;
import com.salle.projects.lsmakerappv2.view.adapters.ScanItemAdapter;
import com.salle.projects.lsmakerappv2.view.callbacks.ScanItemCallback;
import com.salle.projects.lsmakerappv2.viewmodel.ScanViewModel;

public class ScanActivity extends AppCompatActivity implements ScanItemCallback {

    private Button btnScan, btnConnect, btnFilter;
    private RecyclerView mRecyclerView;
    private ScanItemAdapter mItemAdapter;
    private TextView tvDeviceName;

    private CharSequence[] mFilterItems = new CharSequence[]{"lsmaker"};
    private boolean[] mCheckedFilterItems = new boolean[]{false};

    private ScanViewModel mViewModel;

    // Bluetooth
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_FINE_LOCATION = 2; // Needed on API >= 23
    private boolean askForEnableBLE = true;

    private BluetoothService mBluetoothService;
    private boolean doConfig = true;

    /**
     * Asks the system and / or the user for a given permission.
     *
     * @param perm permission name to request
     * @param requestCode internal code to identify the permission request result on an onActivityResult.
     */
    private void loadPermissions(String perm, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
            //if (!ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
            ActivityCompat.requestPermissions(this, new String[]{perm},requestCode);
            //}
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        loadPermissions(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_FINE_LOCATION);
        mBluetoothService = BluetoothService.getInstance(this);
        initViews();
        initViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bluetoothCompatibility();
        if (doConfig) bluetoothConfiguration();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (doConfig) {
            mBluetoothService.pauseBluetooth();
            //unregisterReceiver(scanningReceiver);
        }
    }

    private void initViews() {

        if (mRecyclerView == null) {
            mRecyclerView = (RecyclerView) findViewById(R.id.scanning_recyclerView);
            LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
            mRecyclerView.setLayoutManager(manager);
        }

        btnScan = (Button) findViewById(R.id.activity_scan_btn);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnConnect = (Button) findViewById(R.id.activity_scan_connect_btn);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnFilter = (Button) findViewById(R.id.activity_scan_filter_btn);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayFilterDialog();
            }
        });

    }

    private void initViewModel() {
        mViewModel = new ViewModelProvider(this).get(ScanViewModel.class);

    }

    private void service_init() {
        mBluetoothService.service_init(this);
    }

    private void bluetoothCompatibility() {
        boolean bluetoothCompatibility =
                mBluetoothService.checkDeviceCompatibility(this,getPackageManager(),
                        (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE));
        if (!bluetoothCompatibility) {
            showBluetoothNoCompatiblePopUp();
            doConfig = false;
        }

    }

    private void bluetoothConfiguration() {

        service_init();
        if (askForEnableBLE) {
            mBluetoothService.enableBluetooth(this, REQUEST_ENABLE_BT);
            showProgress(true);

            mRecyclerView = (RecyclerView) findViewById(R.id.scanning_recyclerView);
            if (mRecyclerView != null) {
                mItemAdapter = new ScanItemAdapter(this,
                        mBluetoothService.getDeviceList(), mBluetoothService.getDevRssiValues());
                LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
                mRecyclerView.setLayoutManager(manager);
                mRecyclerView.setAdapter(mItemAdapter);
            }
        }
        //registerReceiver(scanningReceiver, intentFilter);
    }

    public void startScanning(View view) {
        mBluetoothService.startScanningDevices();
        showProgress(true);
    }

    private void displayFilterDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle("Filter By Devices Name")
                .setIcon(getResources().getDrawable(R.drawable.ic_filter, null))
                //.setBackground(getResources().getDrawable(R.drawable.back_white_rad, null))
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setMultiChoiceItems(mFilterItems, mCheckedFilterItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {

                    }
                });
        builder.show();
    }

    public void notifyDataSetChanged() {
        if (mRecyclerView != null) {
            mItemAdapter = new ScanItemAdapter(this,
                    mBluetoothService.getDeviceList(), mBluetoothService.getDevRssiValues());
            mRecyclerView.setAdapter(mItemAdapter);
        }
    }

    /*****************************************************************************
     * **************************  POPUPS  **************************/

    private void showProgress(boolean status) {
        if (status) {
            View contextView = this.getCurrentFocus();
            Snackbar.make(contextView, R.string.bluetooth_state_stop_scan, Snackbar.LENGTH_SHORT)
                    .show();
        } else {
            View contextView = this.getCurrentFocus();
            Snackbar.make(contextView, R.string.bluetooth_state_scanning, Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * Shows a pop up informing the user that there's been an error during connection.
     *
     * @param message error text message to show to the user
     */
    private void showConnectionErrorPopUp(String message) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.connection_error_title))
            .setMessage(message)
            .setPositiveButton(getString(R.string.pop_up_accept), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //pincodeView.requestFocus();
                //pincodeView.setText("");
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * Shows a pop up informing the user that the device isn't compatible with Bluetooth Low Energy
     * and the app will close.
     */
    private void showBluetoothNoCompatiblePopUp() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.bluetooth_not_compatible_title))
            .setIcon(getResources().getDrawable(R.drawable.ic_alert, null))
            .setMessage(R.string.bluetooth_not_compatible_message)
            .setPositiveButton(getString(R.string.pop_up_accept), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    /**
     * Shows a pop up informing the user that Location permissions aren't granted and the app will close.
     */
    private void showLocationPermissionNotGrantedPopUp() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.location_not_granted_title))
            .setIcon(getResources().getDrawable(R.drawable.ic_alert, null))
            .setMessage(R.string.location_not_granted_message)
            .setPositiveButton(getString(R.string.pop_up_accept), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.show();
    }

    /**
     * Shows a pop up informing the user that Bluetooth isn't enabled.
     * @param scanningActivity
     */
    private void showBluetoothPermissionNotGrantedPopUp(final ScanActivity scanningActivity) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.bluetooth_not_granted_title))
            .setIcon(getResources().getDrawable(R.drawable.ic_alert, null))
            .setMessage(R.string.bluetooth_not_granted_message)
            .setPositiveButton(getString(R.string.pop_up_accept), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                askForEnableBLE = true;
                mBluetoothService.enableBluetooth(scanningActivity, REQUEST_ENABLE_BT);
            }
        });
        builder.setNegativeButton(getString(R.string.pop_up_close), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    /*****************************************************************************
     * **************************  SCAN ITEM CALLBACK  **************************/
     @Override
    public void onItemClick(int index) {

    }

    @Override
    public void onItemClick(Object obj) {

    }
}
