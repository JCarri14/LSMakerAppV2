package com.salle.projects.lsmakerappv2.viewmodel;

import android.bluetooth.BluetoothDevice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.salle.projects.lsmakerappv2.model.Device;

import java.util.List;
import java.util.Map;

public class ScanViewModel extends ViewModel {

    private MutableLiveData<List<BluetoothDevice>> mDevicesObj;
    private MutableLiveData<Map<String, Integer>> mRssiValues;

    private MutableLiveData<List<Device>> mDevices;

    public void requestBluetoothScan() {}

    public LiveData<List<BluetoothDevice>> getBluetoothDevicesObj() {
        return mDevicesObj;
    }

    public LiveData<Map<String, Integer>> getRssiValues() {
        return mRssiValues;
    }

    public LiveData<List<Device>> getBluetoothDevices() {
        return mDevices;
    }


}
