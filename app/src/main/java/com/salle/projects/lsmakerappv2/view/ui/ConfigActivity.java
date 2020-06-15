package com.salle.projects.lsmakerappv2.view.ui;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.salle.projects.lsmakerappv2.R;
import com.salle.projects.lsmakerappv2.bluetooth.BluetoothService;
import com.salle.projects.lsmakerappv2.view.managers.DrivingDataManager;

public class ConfigActivity extends AppCompatActivity {

    private Button btnDisconnect;
    private TextView tvName, tvAddress;
    private CheckBox cbMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        initViews();
    }

    private void initViews() {
        btnDisconnect = findViewById(R.id.preferences_disconnect_btn);
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean res = BluetoothService.getInstance().disconnect();
                showConnectionResult(res);
            }
        });

        tvName = findViewById(R.id.preferences_device_name);
        tvAddress = findViewById(R.id.preferences_device_address);

        cbMode = findViewById(R.id.preferences_invert_checkbox);
    }

    private void checkDeviceInformation() {
        BluetoothService manager = BluetoothService.getInstance();
        if (manager.getDevice() != null) {
            tvName.setText(manager.getDevice().getName());
            tvAddress.setText(manager.getDevice().getAddress());
        }
    }

    /*****************************************************************************
     * ***************************  SNACKBAR   ******************************/
    private void showConnectionResult(boolean result) {
        if (result) {
            View contextView = findViewById(R.id.preferences_coordinator);
            Snackbar.make(contextView, R.string.bluetooth_disconnection_ok, Snackbar.LENGTH_SHORT)
                    .show();
        } else {
            View contextView = findViewById(R.id.preferences_coordinator);
            Snackbar.make(contextView, R.string.bluetooth_disconnection_ko, Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

}
