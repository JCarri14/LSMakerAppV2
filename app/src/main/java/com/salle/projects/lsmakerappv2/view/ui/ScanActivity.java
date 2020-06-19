package com.salle.projects.lsmakerappv2.view.ui;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.salle.projects.lsmakerappv2.R;
import com.salle.projects.lsmakerappv2.bluetooth.BluetoothService;
import com.salle.projects.lsmakerappv2.model.BtDevice;
import com.salle.projects.lsmakerappv2.services.UartService;
import com.salle.projects.lsmakerappv2.utils.Utils;
import com.salle.projects.lsmakerappv2.view.adapters.ScanItemAdapter;
import com.salle.projects.lsmakerappv2.view.callbacks.ScanItemCallback;
import com.salle.projects.lsmakerappv2.viewmodel.ScanViewModel;

import java.util.List;

public class ScanActivity extends AppCompatActivity implements ScanItemCallback {

    // UI
    private Button btnBack, btnScan, btnConnect, btnFilter;
    private RecyclerView mRecyclerView;
    private ScanItemAdapter mItemAdapter;

    // Filter Dialog
    private CharSequence[] mFilterItems = new CharSequence[]{"lsmaker"};
    private boolean[] mCheckedFilterItems = new boolean[]{false};

    // Connection Dialog
    private AlertDialog mConnectDialog;


    // ViewModel
    private ScanViewModel mViewModel;

    // Bluetooth
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_FINE_LOCATION = 2; // Needed on API >= 23
    private boolean askForEnableBLE = true;

    private BluetoothService mBluetoothService;
    private BluetoothConnectionTask mAuthTask = null;
    private BtDevice mSelectedDevice;
    private boolean doConfig = true;

    // IntentFilter to configure the broadcast receiver
    private IntentFilter intentFilter = new IntentFilter(BluetoothService.SCAN_STOPPED);


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

    // Broadcast receiver for the scanning progress
    private BroadcastReceiver scanningReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            final Intent mIntent = intent;
            //*********************//
            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                if (mConnectDialog != null && mConnectDialog.isShowing()) {
                    mConnectDialog.dismiss();
                }
            }

        }
    };




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        loadPermissions(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_FINE_LOCATION);
        mBluetoothService = BluetoothService.getInstance();
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
            unregisterReceiver(scanningReceiver);
        }

    }

    private void initViews() {

        mRecyclerView = (RecyclerView) findViewById(R.id.activity_scan_recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);

        btnBack = findViewById(R.id.activity_scan_back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnScan = (Button) findViewById(R.id.activity_scan_btn);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScanning();
            }
        });

        btnConnect = (Button) findViewById(R.id.activity_scan_connect_btn);
        // Check if we are already connected to a device
        if (mBluetoothService.getDevice() != null) {
            btnConnect.setText(R.string.preferences_connection_disconnect);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btnConnect.setBackgroundTintList(getResources().getColorStateList(R.color.colorError));
            } else {
                btnConnect.setBackgroundColor(ContextCompat.getColor(this, R.color.colorError));
            }
            btnConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayConnectDialog(false);
                    mBluetoothService.disconnect();
                    mBluetoothService.setDevice(null);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        btnConnect.setBackgroundTintList(getResources().getColorStateList(R.color.colorConnected));
                    } else {
                        btnConnect.setBackgroundColor(ContextCompat.getColor(ScanActivity.this, R.color.colorError));
                    }
                    btnConnect.setText(R.string.connection_action);
                }
            });
        } else {
            btnConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mSelectedDevice != null) {
                        mBluetoothService.setDevice(mSelectedDevice);
                        displayConnectDialog(true);
                        attemptLogin(mSelectedDevice);
                        mSelectedDevice = null;
                    } else {
                        showNoDeviceSelectedPopUp();
                    }
                }
            });
        }

        btnFilter = (Button) findViewById(R.id.activity_scan_filter_btn);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayFilterDialog();
            }
        });

    }

    private void initViewModel() {
        mViewModel = new ViewModelProvider(ScanActivity.this).get(ScanViewModel.class);
        mViewModel.getBluetoothDevices().observe(this, btDevices -> {
            if (mRecyclerView == null) {
                mRecyclerView = findViewById(R.id.activity_scan_recyclerView);
            }
            mItemAdapter = new ScanItemAdapter(ScanActivity.this, ScanActivity.this, btDevices);
            mRecyclerView.setAdapter(mItemAdapter);

        });
    }

    private void service_init() {
        mBluetoothService.service_init(this, mViewModel);
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
            showScanningProgress(true);

            mRecyclerView = (RecyclerView) findViewById(R.id.activity_scan_recyclerView);
            if (mRecyclerView != null) {
                mItemAdapter = new ScanItemAdapter(this, this, null);
                LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
                mRecyclerView.setLayoutManager(manager);
                mRecyclerView.setAdapter(mItemAdapter);
            }
        }
        registerReceiver(scanningReceiver, intentFilter);
    }

    public void startScanning() {
        mBluetoothService.startScanningDevices();
        showScanningProgress(false);
    }

    /**
     * Attempts to bind the android device to the bluetooth device.
     */
    public void attemptLogin(BtDevice device) {
        if (mAuthTask != null) {
            return;
        }

        //Utils.hideKeyboard(this);
        //mBluetoothService.enableBluetooth(this, REQUEST_ENABLE_BT);

        mAuthTask = new BluetoothConnectionTask(device, getApplicationContext());
        mAuthTask.execute((Void) null);
    }

    private void gotToMainActivity() {
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void goToDrivingActivity() {
        final Intent intent = new Intent(this, DriveActivity.class);
        startActivity(intent);
    }

    /*****************************************************************************
     * ***************************  FILTER DIALOG  ******************************/
    private void displayFilterDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Filter By Devices Name");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setIcon(getResources().getDrawable(R.drawable.ic_filter, null));
            builder.setBackground(getResources().getDrawable(R.drawable.back_white_rad, null));
        } else {
            builder.setBackground(ContextCompat.getDrawable(this, R.drawable.back_white_rad));
        }
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setMultiChoiceItems(mFilterItems, mCheckedFilterItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                if (b) {
                    mViewModel.setFilter((String) mFilterItems[i]);
                } else {
                    mViewModel.setFilter(mViewModel.DEFAULT_FILTER_VALUE);
                }
            }
        });
        builder.show();
    }

    private void displayConnectDialog(boolean connecting) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        if (connecting) { builder.setView(R.layout.dialog_connecting);
        } else { builder.setView(R.layout.dialog_disconnecting);}

        builder.setCancelable(false);
        mConnectDialog = builder.create();
        mConnectDialog.show();
    }

    /*****************************************************************************
     * ******************************  SNACKBAR  *********************************/
    private void showScanningProgress(boolean status) {
        if (status) {
            View contextView = findViewById(R.id.activity_scan_coordinator);
            Snackbar.make(contextView, R.string.bluetooth_state_stop_scan, Snackbar.LENGTH_SHORT)
                    .show();
        } else {
            View contextView = findViewById(R.id.activity_scan_coordinator);
            Snackbar.make(contextView, R.string.bluetooth_state_scanning, Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    private void showConnectionProgress(boolean status) {
        if (status) {
            View contextView = findViewById(R.id.activity_scan_coordinator);
            Snackbar.make(contextView, R.string.connection_action_connect, Snackbar.LENGTH_SHORT)
                    .show();
        } else {
            View contextView = findViewById(R.id.activity_scan_coordinator);
            Snackbar.make(contextView, R.string.connection_state_connected, Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    private void showConnectionStopped() {
        View contextView = findViewById(R.id.activity_scan_coordinator);
        Snackbar.make(contextView, R.string.connection_action_stoped, Snackbar.LENGTH_SHORT)
                .show();
    }

    /*****************************************************************************
     * ******************************  POPUPS  *********************************/
    private void showNoDeviceSelectedPopUp() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.connection_action))
                .setMessage("No device was selected")
                .setPositiveButton(getString(R.string.pop_up_accept), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
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
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(getString(R.string.bluetooth_not_compatible_title));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setIcon(getResources().getDrawable(R.drawable.ic_alert, null));
        }
        builder.setMessage(R.string.bluetooth_not_compatible_message);
        builder.setPositiveButton(getString(R.string.pop_up_accept), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.show();
    }

    /**
     * Shows a pop up informing the user that Location permissions aren't granted and the app will close.
     */
    private void showLocationPermissionNotGrantedPopUp() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(getString(R.string.location_not_granted_title));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setIcon(getResources().getDrawable(R.drawable.ic_alert, null));
        }
        builder.setMessage(R.string.location_not_granted_message);
        builder.setPositiveButton(getString(R.string.pop_up_accept), new DialogInterface.OnClickListener() {
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
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(getString(R.string.bluetooth_not_granted_title));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setIcon(getResources().getDrawable(R.drawable.ic_alert, null));
        }
        builder.setMessage(R.string.bluetooth_not_granted_message);
        builder.setPositiveButton(getString(R.string.pop_up_accept), new DialogInterface.OnClickListener() {
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
        mSelectedDevice = (BtDevice) obj;
    }

    /*****************************************************************************
     * ***********************  CONNECTION INNER CLASS  **************************/

    /**
     * Represents an asynchronous binding task used to connect the user with the bluetooth device.
     *
     */
    public class BluetoothConnectionTask extends AsyncTask<Void, Void, Boolean> {

        private final BtDevice device;
        private final Context context;

        BluetoothConnectionTask(BtDevice device, Context context) {
            this.device = device;
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return mBluetoothService.connect(device, context);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            //mAuthTask = null;
            showConnectionProgress(false);

            if (success) {
                mConnectDialog.dismiss();
                gotToMainActivity();
            } else {
                showConnectionErrorPopUp(getString(R.string.connection_error_message));
            }
        }

        @Override
        protected void onCancelled() {
            //mAuthTask = null;
            showConnectionStopped();
        }
    }


}
