package com.salle.projects.lsmakerappv2.view.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.salle.projects.lsmakerappv2.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private Button btnConnect, btnDrive,btnConfig;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
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
    }
}
