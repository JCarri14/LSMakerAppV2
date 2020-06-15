package com.salle.projects.lsmakerappv2.view.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.salle.projects.lsmakerappv2.R;
import com.salle.projects.lsmakerappv2.bluetooth.BluetoothService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private Button btnConnect, btnDrive,btnConfig;
    private TextView tvState, tvName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkDeviceInformation();
    }

    private void initViews() {
        btnConnect = (Button) findViewById(R.id.activity_main_connect_btn);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ScanActivity.class);
                startActivity(intent);
            }
        });

        btnDrive = (Button) findViewById(R.id.activity_main_drive_btn);
        btnDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),DriveActivity.class);
                startActivity(intent);
            }
        });

        btnConfig = (Button) findViewById(R.id.activity_main_config_btn);
        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ConfigActivity.class);
                startActivity(intent);
            }
        });

        tvState = findViewById(R.id.activity_main_state);
        tvName = findViewById(R.id.activity_main_device);
        checkDeviceInformation();
    }

    private void checkDeviceInformation() {
        BluetoothService manager = BluetoothService.getInstance();
        if (manager.getDevice() != null) {
            tvState.setText(getText(R.string.connection_state_connected));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvState.setTextColor(getColor(R.color.colorConnected));
            } else {
                tvState.setTextColor(ContextCompat.getColor(this, R.color.colorConnected));
            }
            tvName.setText(manager.getDevice().getName());
        }
    }
}
