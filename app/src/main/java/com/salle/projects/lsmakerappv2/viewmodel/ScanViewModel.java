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
import java.util.stream.Collectors;

public class ScanViewModel extends ViewModel implements BtDiscoveryCallback {

    private final String DEFAULT_FILTER_VALUE = "None";

    private MutableLiveData<List<BtDevice>> mDevices;
    private List<BtDevice> mAllDevices;
    private String filter = DEFAULT_FILTER_VALUE;


    public void requestBluetoothScan() {}

    public LiveData<List<BtDevice>> getBluetoothDevices() {
        if (mDevices == null) {
            mDevices = new MutableLiveData<List<BtDevice>>();
            mDevices.setValue(new ArrayList<>());
        }
        return mDevices;
    }

    /*****************************************************************************
     * ***************************  SCANNING CALLBACK  ***************************/
    @Override
    public void onDeviceDiscovered(BluetoothDevice device, int rssi) {
        boolean deviceFound = false;
        if (mAllDevices == null) {
            mAllDevices = new ArrayList<>();
        }

        if (device.getName() != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                deviceFound = mAllDevices.stream().anyMatch(d -> d.getAddress().equals(device.getAddress()));
            } else {
                for (BtDevice listDev : mAllDevices) {
                    if (listDev.getAddress().equals(device.getAddress())) {
                        deviceFound = true;
                        break;
                    }
                }
            }
            if (!deviceFound) {
                mAllDevices.add(new BtDevice(device.getName(), device.getAddress(), rssi));
                Collections.sort(mAllDevices, new BluetoothDeviceComparator());
                updateObservableList();
            }
        }

    }

    private void updateObservableListWithSingleItem(BtDevice device) {
        if (!filter.equals(DEFAULT_FILTER_VALUE)) {
            if (device.getName().contains(filter))
                mDevices.getValue().add(device);
        } else {
            mDevices.getValue().add(device);
        }
    }

    private void updateObservableList() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            if (!filter.equals(DEFAULT_FILTER_VALUE)) {
                mDevices.setValue(mAllDevices.stream().filter(d -> d.getName().contains(filter)).collect(Collectors.toList()));
            } else {
                mDevices.setValue(mAllDevices);
            }
        } else {
            for (BtDevice listDev : mAllDevices) {
                if (!filter.equals(DEFAULT_FILTER_VALUE)) {
                    if (listDev.getName().contains(filter))
                        mDevices.getValue().add(listDev);
                } else {
                    mDevices.getValue().add(listDev);
                }
            }
        }
    }

    public void setFilter(String newFilter) {
        this.filter = newFilter;
    }

}
