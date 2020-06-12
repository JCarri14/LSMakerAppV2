package com.salle.projects.lsmakerappv2.viewmodel;

import android.bluetooth.BluetoothDevice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Map;

public class ScanViewModel extends ViewModel {

    private MutableLiveData<List<BluetoothDevice>> mDevices;
    private MutableLiveData<Map<String, Integer>> mRssiValues;

    public void requestBluetoothScan() {}

    public LiveData<List<BluetoothDevice>> getBluetoothDevices() {
        return mDevices;
    }

    public LiveData<Map<String, Integer>> getRssiValues() {
        return mRssiValues;
    }

}
