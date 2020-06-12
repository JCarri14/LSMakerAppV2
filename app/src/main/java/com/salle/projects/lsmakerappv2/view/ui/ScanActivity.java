package com.salle.projects.lsmakerappv2.view.ui;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.salle.projects.lsmakerappv2.R;
import com.salle.projects.lsmakerappv2.view.callbacks.ScanItemCallback;

public class ScanActivity extends AppCompatActivity implements ScanItemCallback {

    private Button btnScan, btnConnect, btnFilter;
    private RecyclerView mRecyclerView;
    private TextView tvDeviceName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        initViews();
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

            }
        });

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
