package com.luhyah.me2u.fragments;

public class PairedDevicesModel {
    String DeviceName;
    String MacAddress;

    public PairedDevicesModel(String deviceName, String macAddress) {
        DeviceName = deviceName;
        MacAddress = macAddress;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public String getMacAddress() {
        return MacAddress;
    }
}
