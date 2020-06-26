package com.salle.projects.lsmakerappv2.model;

import android.bluetooth.BluetoothDevice;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "devices")
public class BtDevice {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String address;
    private Integer rssiValue;

    public BtDevice(String name, String address, Integer rssi) {
        this.name = name;
        this.address = address;
        this.rssiValue = rssi;
    }

    public BtDevice() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
