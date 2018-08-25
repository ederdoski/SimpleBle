package com.ederdoski.simpleble.models;

import android.bluetooth.BluetoothDevice;

public class BluetoothLE {

    private String name;
    private String macAddress;
    private int rssi;
    private BluetoothDevice device;

    public BluetoothLE(String name, String macAddress, int rssi, BluetoothDevice device) {
        this.name       = name;
        this.macAddress = macAddress;
        this.rssi       = rssi;
        this.device     = device;
    }

    @Override
    public String toString() {
        return name + " Mac: " + macAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

}
