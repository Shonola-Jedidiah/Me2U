package com.luhyah.me2u;


import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Functions {

    private final int BLUETOOTH_REQUEST = 1;

    //BluetoothManager bluetoothManager = getSystemService(this, Context.)


    public void checkBluetooth(TextView text, CardView turnOnBluetooth, BluetoothAdapter bluetoothAdapter) {

        if (bluetoothAdapter == null) {
            text.setText(R.string.this_device_is_not_bluetooth_enabled);
            //Write code to disable sending of messages
        } else if (!bluetoothAdapter.isEnabled()) {
            turnOnBluetooth.setVisibility(View.VISIBLE);
        } else {
            turnOnBluetooth.setVisibility(View.GONE);
        }
    }



    public Set<BluetoothDevice> mobileDevices(BluetoothAdapter bluetoothAdapter,Context context, Activity activity ) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_REQUEST);
            return null;
        }

        Set<BluetoothDevice> allPairedDevices = bluetoothAdapter.getBondedDevices();
        Set<BluetoothDevice> Device = new HashSet<>() ;
        if(allPairedDevices != null && !allPairedDevices.isEmpty()) {

            for (BluetoothDevice device : allPairedDevices) {
                BluetoothClass bluetoothClass = device.getBluetoothClass();

                if (bluetoothClass.getDeviceClass() == BluetoothClass.Device.PHONE_SMART) {
                    Device.add(device);
                    Log.d("GOT HERE", device.getName());
                }
            }
            return Device;
        }
        else{

            return Collections.emptySet();}

    }


}

