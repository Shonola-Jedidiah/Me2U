package com.luhyah.me2u;


import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class UsersActivity extends AppCompatActivity {

    private CardView turnOnBluetooth, Messages;
    private TextView text, noAvailableDevices;
    private RecyclerView pairedDevicesRecyclerView;
    Functions functions = new Functions();
    BluetoothAdapter bluetoothAdapter;
    BluetoothManager bluetoothManager;
    private final int BLUETOOTH_REQUEST = 1;


    ArrayList<PairedDevicesModel> pairedDevicesModels = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        Log.d("BluetoothAdapter3", bluetoothAdapter.toString());
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == BLUETOOTH_REQUEST && grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
            Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBt,BLUETOOTH_REQUEST);
        }
        else{

            text.setText(R.string.device_failed_to_grant_permission_retry);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_users);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        turnOnBluetooth = findViewById(R.id.turnOnBlueTooth);
        Messages = findViewById(R.id.Messages);
        text = findViewById(R.id.turnOnBlueToothMessage);
        pairedDevicesRecyclerView = findViewById(R.id.pairedDevices);

       bluetoothManager = getSystemService(BluetoothManager.class);
         bluetoothAdapter = bluetoothManager.getAdapter();

        functions.checkBluetooth(text, turnOnBluetooth, bluetoothAdapter);
        if (bluetoothAdapter == null) {
            Log.d("BluetoothAdap", "Null");
        } else if (!bluetoothAdapter.isEnabled()) {
            Log.d("BluetoothAdap", "Not Working");
        }else{Log.d("BluetoothAdap", "Working");}

        //CardView Button to call Enable Bluetooth Intent if Bluetooth is off
        turnOnBluetooth.setOnClickListener(view -> {
            if(bluetoothAdapter != null) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    //If permission is not granted request for it and handle the request
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_REQUEST);
                    }
                }else{
                    Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBt, BLUETOOTH_REQUEST);
                }

            }
        });

        if (bluetoothAdapter.isEnabled()) {
            //Loads a set of Smart Bluetooth Device
            //Discrimination for only android devices should be added later
            if (functions.mobileDevices(bluetoothAdapter,getApplicationContext(),this)!= null && !functions.mobileDevices(bluetoothAdapter,getApplicationContext(),this).isEmpty()) {
                //WRITE CODE TO LOAD THE INDIVIDUAL DEVICES AND THEIR MAC ADDRESS IN A RECYCLER VIEW INFLATED WITH CARDVIEWS
                pairedDevicesRecyclerView.setVisibility(View.VISIBLE);
                noAvailableDevices.setVisibility(View.GONE);
                int i =0;
                for(BluetoothDevice device : functions.mobileDevices(bluetoothAdapter,getApplicationContext(),this)){
                    pairedDevicesModels.add(i,new PairedDevicesModel(device.getName(),device.getAddress()));
                    i++;
                }
                PairedDevices_RecyclerViewAdapter adapter = new PairedDevices_RecyclerViewAdapter(this,pairedDevicesModels);
                pairedDevicesRecyclerView.setAdapter(adapter);

            } else {
                //Write code to show text "NO PAIRED BLUETOOTH DEVICE"
                pairedDevicesRecyclerView.setVisibility(View.GONE);
                noAvailableDevices.setVisibility(View.VISIBLE);
            }

            //Load Discovered Devices
            IntentFilter listenForAvailableDevices = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        }
        Messages.setOnClickListener(view ->{
            Intent toMessage =  new Intent(this, MessageActivity.class);
            startActivity(toMessage);
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        functions.checkBluetooth(text, turnOnBluetooth, bluetoothManager, bluetoothAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        functions.checkBluetooth(text, turnOnBluetooth, bluetoothManager, bluetoothAdapter);
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent toMessage =  new Intent(this, MessageActivity.class);
            startActivity(toMessage);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}