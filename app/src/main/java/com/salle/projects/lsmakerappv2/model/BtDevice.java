package com.salle.projects.lsmakerappv2.model;

import android.bluetooth.BluetoothDevice;

public class BtDevice {

    private String name;
    private String address;
    private Integer rssiValue;

    public BtDevice(String name, String address, Integer rssi) {
        this.name = name;
        this.address = address;
        this.rssiValue = rssi;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getRssiValue() {
        return rssiValue;
    }

    public void setRssiValue(Integer rssiValue) {
        this.rssiValue = rssiValue;
    }
}
