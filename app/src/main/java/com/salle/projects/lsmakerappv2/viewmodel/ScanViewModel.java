package com.salle.projects.lsmakerappv2.viewmodel;

import android.bluetooth.BluetoothDevice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.salle.projects.lsmakerappv2.bluetooth.BluetoothService;
import com.salle.projects.lsmakerappv2.bluetooth.callbacks.BtDiscoveryCallback;
import com.salle.projects.lsmakerappv2.model.BtDevice;
import com.salle.projects.lsmakerappv2.utils.BluetoothDeviceComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScanViewModel extends ViewModel implements BtDiscoveryCallback {

    private MutableLiveData<List<BtDevice>> mDevices;
    private List<BtDevice> mAllDevices;


    public void requestBluetoothScan() {}

    public LiveData<List<BtDevice>> getBluetoothDevices() {
        return mDevices;
    }

    /*****************************************************************************
     * ***************************  SCANNING CALLBACK  ***************************/
    @Override
    public void onDeviceDiscovered(BluetoothDevice device, int rssi) {
        boolean deviceFound = false;
        if (mDevices.getValue() != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                deviceFound = mDevices.getValue().stream()
                        .anyMatch(d -> d.getAddress().equals(device.getAddress()));
            } else {
                for (BtDevice listDev : mDevices.getValue()) {
                    if (listDev.getAddress().equals(device.getAddress())) {
                        deviceFound = true;
                        break;
                    }
                }
            }
        }
        if (!deviceFound) {
            if (mDevices.getValue() == null) { mDevices.setValue(new ArrayList<>());}
            mDevices.getValue().add(new BtDevice(device.getName(), device.getAddress(), rssi));
            Collections.sort(mDevices.getValue(), new BluetoothDeviceComparator());
        }

    }

}
