package com.salle.projects.lsmakerappv2.view.ui;

import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.salle.projects.lsmakerappv2.R;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        initViews();
        initViewModel();
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
        mItemAdapter.notifyDataSetChanged();
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
