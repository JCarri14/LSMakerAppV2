package com.salle.projects.lsmakerappv2.model;

import android.bluetooth.BluetoothDevice;

import java.util.HashMap;

public class Device {

    private String name;
    private HashMap<String, Integer> rssiValue;

    public Device(String name, HashMap<String, Integer> rssiValue) {
        this.name = name;
        this.rssiValue = rssiValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Integer> getRssiValue() {
        return rssiValue;
    }

    public void setRssiValue(HashMap<String, Integer> rssiValue) {
        this.rssiValue = rssiValue;
    }
}
