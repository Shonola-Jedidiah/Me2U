package com.luhyah.me2u;


import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

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

public class MessageActivity extends AppCompatActivity {


    private CardView turnOnBluetooth, Users;
    private TextView text;
    Functions functions = new Functions();

    BluetoothAdapter bluetoothAdapter;
    private final int BLUETOOTH_REQUEST = 1;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == BLUETOOTH_REQUEST && resultCode == RESULT_OK){
            turnOnBluetooth.setVisibility(View.GONE);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        Log.d("BluetoothAdapter3", bluetoothAdapter.toString());
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == BLUETOOTH_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBt, BLUETOOTH_REQUEST);
            }
        } else {

            text.setText(R.string.device_failed_to_grant_permission_retry);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_message);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        turnOnBluetooth = findViewById(R.id.turnOnBlueTooth);
        Users = findViewById(R.id.Users);
        text = findViewById(R.id.turnOnBlueToothMessage);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); //bluetoothManager.getAdapter();


        functions.checkBluetooth(text, turnOnBluetooth, bluetoothAdapter);

        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, BLUETOOTH_REQUEST);
        }

        turnOnBluetooth.setOnClickListener(view -> {

            if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                        (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) !=
                        PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)) {
                    //If permission is not granted request for it and handle the request
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_REQUEST);
                } else {
                    Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBt, BLUETOOTH_REQUEST);
                }

            }
        });

        Users.setOnClickListener(view -> {
            Intent toUsers = new Intent(this, UsersActivity.class);
            startActivity(toUsers);
        });

        //Receive Broadcast For Action Change
        IntentFilter bluetoothStateChanged = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothBroadcastReceiver, bluetoothStateChanged);
    }

    @Override
    protected void onResume() {
        super.onResume();
        functions.checkBluetooth(text, turnOnBluetooth, bluetoothAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        functions.checkBluetooth(text, turnOnBluetooth, bluetoothAdapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    private final BroadcastReceiver bluetoothBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.ERROR);
                switch (state){
                    case BluetoothAdapter.STATE_ON:
                        turnOnBluetooth.setVisibility(View.GONE);
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        turnOnBluetooth.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothBroadcastReceiver);
    }
}