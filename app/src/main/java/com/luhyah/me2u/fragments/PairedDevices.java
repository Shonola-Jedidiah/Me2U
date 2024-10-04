package com.luhyah.me2u.fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import static androidx.core.content.ContextCompat.registerReceiver;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.luhyah.me2u.Functions;
import com.luhyah.me2u.R;

import java.util.ArrayList;


public class PairedDevices extends Fragment {


    RecyclerView pairedDevicesRecyclerView;
    TextView noPairedDevice;
    private final int BLUETOOTH_REQUEST = 1;
    ArrayList<PairedDevicesModel> pairedDevicesModels = new ArrayList<>();
    Functions functions = new Functions();
    BluetoothAdapter bluetoothAdapter;
    PairedDevices_RecyclerViewAdapter adapter;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BLUETOOTH_REQUEST && resultCode == RESULT_OK) {
            //Bluetooth is not null and was Turned On
            hideXShow(true);

           search4PairedDevices(pairedDevicesModels,pairedDevicesRecyclerView);

        } else  if (requestCode == BLUETOOTH_REQUEST && resultCode == RESULT_CANCELED) {
            //BlueTooth was not turned on
            hideXShow(false);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Check if Permission ws granted. If yes CHeck if bluetooth isEnabled() if not turn it on
        //If permission !isEnabled() Re-Request Permission
        if (requestCode == BLUETOOTH_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            search4PairedDevices(pairedDevicesModels, pairedDevicesRecyclerView);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) !=
                            PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_REQUEST);
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        View rootview = inflater.inflate(R.layout.fragment_paired_devices, container, false);
        pairedDevicesRecyclerView = rootview.findViewById(R.id.pairedDevices);
        pairedDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        noPairedDevice = rootview.findViewById(R.id.noPairedDevice);

        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            //If Bluetooth is not Null and it is off call intent to turn it on
            turnOnBluetooth();
        } else {
            search4PairedDevices(pairedDevicesModels,pairedDevicesRecyclerView);
        }

        //Bluetooth Broadcast
        IntentFilter bluetoothStateChanged = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        requireActivity().registerReceiver(bluetoothBroadcastReceiver,bluetoothStateChanged);
        return rootview;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onDetach() {
        super.onDetach();
        requireActivity().unregisterReceiver(bluetoothBroadcastReceiver);
    }

    public void search4PairedDevices(ArrayList<PairedDevicesModel> pairedDevicesModels, RecyclerView pairedDevicesRecyclerView) {
        int i = 0;
        for (BluetoothDevice device : functions.mobileDevices(bluetoothAdapter, requireContext(), requireActivity())) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) !=
                            PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_REQUEST);
                return;
            }
            pairedDevicesModels.add(i, new PairedDevicesModel(device.getName(), device.getAddress()));
            i++;
        }
        adapter = new PairedDevices_RecyclerViewAdapter(requireContext(), pairedDevicesModels);
        pairedDevicesRecyclerView.setAdapter(adapter);

    }

    public void hideXShow(Boolean bool) {
        if (bool) {
            pairedDevicesRecyclerView.setVisibility(View.VISIBLE);
            noPairedDevice.setVisibility(View.GONE);
        } else {
            pairedDevicesRecyclerView.setVisibility(View.GONE);
            noPairedDevice.setVisibility(View.VISIBLE);
        }
    }
        public void turnOnBluetooth(){
            Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBt,BLUETOOTH_REQUEST);
        }

        private final BroadcastReceiver bluetoothBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.ERROR);
                    switch (state){
                        case BluetoothAdapter.STATE_ON:
                            hideXShow(true);
                            search4PairedDevices(new ArrayList<PairedDevicesModel>(),pairedDevicesRecyclerView);
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            hideXShow(false);
                            break;
                    }
                }
            }
        };
    }
