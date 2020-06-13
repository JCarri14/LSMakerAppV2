package com.salle.projects.lsmakerappv2.bluetooth.callbacks;

import android.bluetooth.BluetoothDevice;

public interface BtDiscoveryCallback {
    void onDeviceDiscovered(BluetoothDevice device, int rssi);
}
