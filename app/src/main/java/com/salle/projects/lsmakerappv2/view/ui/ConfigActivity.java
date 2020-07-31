package com.salle.projects.lsmakerappv2.view.ui;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.salle.projects.lsmakerappv2.R;
import com.salle.projects.lsmakerappv2.bluetooth.BluetoothService;
import com.salle.projects.lsmakerappv2.view.managers.DrivingDataManager;

public class ConfigActivity extends AppCompatActivity {

    private Button btnDisconnect;
    private TextView tvName, tvAddress;
    private ImageButton btnBack;
    private CheckBox cbMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDefaultDeviceInformation();
        checkDeviceInformation();
    }

    private void initViews() {
        btnDisconnect = findViewById(R.id.preferences_disconnect_btn);
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothService.getInstance().getDevice() != null) {
                    boolean res = BluetoothService.getInstance().disconnect();
                    setDefaultDeviceInformation();
                    showConnectionResult(res);
                }
            }
        });

        btnBack = findViewById(R.id.preferences_back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvName = findViewById(R.id.preferences_device_name);
        tvAddress = findViewById(R.id.preferences_device_address);
        checkDeviceInformation();
        cbMode = findViewById(R.id.preferences_invert_checkbox);
    }

    private void checkDeviceInformation() {
        BluetoothService manager = BluetoothService.getInstance();
        if (manager.getDevice() != null) {
            tvName.setText(manager.getDevice().getName());
            tvAddress.setText(manager.getDevice().getAddress());
        }
    }

    private void setDefaultDeviceInformation() {
        tvAddress.setText(getString(R.string.hint_device_name_alt));
        tvName.setText(getString(R.string.hint_device_name_alt));

    }

    /*****************************************************************************
     * ***************************  SNACKBAR   ******************************/
    private void showConnectionResult(boolean result) {
        if (result) {
            View contextView = findViewById(R.id.preferences_coordinator);
            Snackbar.make(contextView, R.string.bluetooth_disconnection_ok, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(getResources().getColor(R.color.colorWhite))
                    .setTextColor(getResources().getColor(R.color.colorPrimaryDark))
                    .show();
        } else {
            View contextView = findViewById(R.id.preferences_coordinator);
            Snackbar.make(contextView, R.string.bluetooth_disconnection_ko, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(getResources().getColor(R.color.colorWhite))
                    .setTextColor(getResources().getColor(R.color.colorPrimaryDark))
                    .show();
        }
    }

}
