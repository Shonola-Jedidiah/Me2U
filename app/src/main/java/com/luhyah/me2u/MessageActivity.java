package com.luhyah.me2u;


import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
    BluetoothManager bluetoothManager;
   private final int BLUETOOTH_REQUEST = 1;


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        Log.d("BluetoothAdapter3", bluetoothAdapter.toString());
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == BLUETOOTH_REQUEST && grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
            if(!bluetoothAdapter.isEnabled()) {
                Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBt, BLUETOOTH_REQUEST);
            }
        }
        else{

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
//
        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); //bluetoothManager.getAdapter();

        functions.checkBluetooth(text, turnOnBluetooth, bluetoothAdapter);

            turnOnBluetooth.setOnClickListener(view -> {
                if(!bluetoothAdapter.isEnabled()) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    //If permission is not granted request for it and handle the request
                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN,Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_REQUEST);
                    }else{

                   }
                }else{
                    Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBt, BLUETOOTH_REQUEST);
                }

                }
            });

            Users.setOnClickListener(view ->{
                Intent toUsers =  new Intent(this,UsersActivity.class);
                startActivity(toUsers);
            });


    }

    @Override
    protected void onResume() {
        super.onResume();
        functions.checkBluetooth(text,turnOnBluetooth,bluetoothAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        functions.checkBluetooth(text,turnOnBluetooth, bluetoothAdapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }
}