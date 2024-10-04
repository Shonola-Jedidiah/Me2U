package com.luhyah.me2u.fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.luhyah.me2u.R;

import java.util.ArrayList;


public class Discovered_Devices extends Fragment {

    private static final int BLUETOOTH_SCAN_REQUEST = 1;
    private static final int BLUETOOTH_CONNECT_REQUEST = 2;
    private static final int BLUETOOTH_REQUEST = 3;
    private RecyclerView discoveredDevices;
    private TextView bluetoothIsOff;
    BluetoothAdapter bluetoothAdapter;
    ArrayList<PairedDevicesModel> devicesModels = new ArrayList<>();
    PairedDevices_RecyclerViewAdapter adapter;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == BLUETOOTH_CONNECT_REQUEST && resultCode == RESULT_OK){
            hideXshow(true);
            getFoundDevice(null,discoveredDevices,devicesModels);
        }else{
            hideXshow(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Check if Permission ws granted. If yes CHeck if bluetooth isEnabled() if not turn it on
        //If permission !isEnabled() Re-Request Permission
        if (requestCode == BLUETOOTH_SCAN_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            handleDiscovery();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN) !=
                            PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.BLUETOOTH_SCAN}, BLUETOOTH_SCAN_REQUEST);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_discovered__devices, container, false);
        discoveredDevices = rootView.findViewById(R.id.discoveredDevices);
        bluetoothIsOff = rootView.findViewById(R.id.bluetoothIsOff);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        discoveredDevices.setLayoutManager(new LinearLayoutManager(requireContext()));
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            hideXshow(false);
            turnOnBluetooth();
        } else {
            hideXshow(true);
            getFoundDevice(null,discoveredDevices,devicesModels);
        }


        IntentFilter deviceDiscovered = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter bluetoothStateChanged = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);

        requireActivity().registerReceiver(bluetoothReceiver,bluetoothStateChanged);
        requireActivity().registerReceiver(bluetoothReceiver,deviceDiscovered);
        handleDiscovery();

        return rootView;
    }

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                getFoundDevice(device,discoveredDevices,devicesModels);
                adapter.notifyDataSetChanged();
            }else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (state){
                    case BluetoothAdapter.STATE_ON:
                        hideXshow(true);
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        hideXshow(false);
                        break;
                }
            }
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
        if(bluetoothAdapter != null){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN)
                        != PackageManager.PERMISSION_GRANTED) {
           return;
        }
            if(bluetoothAdapter.isDiscovering()){
                bluetoothAdapter.cancelDiscovery();
            }
        }


        requireActivity().unregisterReceiver(bluetoothReceiver);

    }

    public void hideXshow(Boolean bool) {
        if (bool) {
            discoveredDevices.setVisibility(View.VISIBLE);
            bluetoothIsOff.setVisibility(View.GONE);
        } else {
            discoveredDevices.setVisibility(View.GONE);
            bluetoothIsOff.setVisibility(View.VISIBLE);
        }
    }

    public void turnOnBluetooth() {
        Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBt, BLUETOOTH_REQUEST);
    }
//Let this function also take in recyler view and arrayList
    public void getFoundDevice(BluetoothDevice device,RecyclerView discoveredDevices,ArrayList<PairedDevicesModel>  devicesModels ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT)
                        != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.BLUETOOTH_CONNECT},BLUETOOTH_CONNECT_REQUEST);
            return;
        }

        int a = devicesModels.size();
        if (device != null && device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.PHONE_SMART &&
                !devicesModels.contains(new PairedDevicesModel(device.getName(),device.getAddress()))) {
            Log.d("Got here", device.getName());
            devicesModels.add(a, new PairedDevicesModel(device.getName(), device.getAddress()));
        }

        adapter = new PairedDevices_RecyclerViewAdapter(requireContext(),devicesModels);
        discoveredDevices.setAdapter(adapter);


    }

    public void handleDiscovery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN)
                        != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.BLUETOOTH_SCAN},BLUETOOTH_SCAN_REQUEST);

            return;
        }
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        bluetoothAdapter.startDiscovery();
        Log.d("Got here 222", "ARGHHH");
        if (!bluetoothAdapter.isDiscovering()) {
            Log.d("Got here", "ARGHHH");
        }
    }
}