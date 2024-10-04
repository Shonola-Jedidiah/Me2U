package com.luhyah.me2u;


import android.Manifest;

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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;


public class UsersActivity extends AppCompatActivity {

    private CardView turnOnBluetooth, Messages;
    private TextView text;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    Functions functions = new Functions();
    BluetoothAdapter bluetoothAdapter;
    private final int BLUETOOTH_REQUEST = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == BLUETOOTH_REQUEST && resultCode == RESULT_OK){
            turnOnBluetooth.setVisibility(View.GONE);
            tabLayout.setEnabled(true);
        } else  if (requestCode == BLUETOOTH_REQUEST && resultCode == RESULT_CANCELED){
            turnOnBluetooth.setVisibility(View.VISIBLE);
            tabLayout.setEnabled(false);
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
        tabLayout = findViewById(R.id.TabLayout);
        viewPager2 = findViewById(R.id.View_Pager);
        TabAdapter tabAdapter =  new TabAdapter(this);
        viewPager2.setAdapter(tabAdapter);


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        functions.checkBluetooth(text, turnOnBluetooth, bluetoothAdapter);



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

        if(!bluetoothAdapter.isEnabled()){
            tabLayout.setEnabled(false);
        }
        //CardView Button to call Enable Bluetooth Intent if Bluetooth is off
        turnOnBluetooth.setOnClickListener(view -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) !=
                            PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_REQUEST);
                    return;
            }
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, BLUETOOTH_REQUEST);
        });


        //Go to Message Activity
        Messages.setOnClickListener(view ->{
            Intent toMessage =  new Intent(this, MessageActivity.class);
            startActivity(toMessage);
        });


        //Broadcast to detect change in Bluetooth State
        IntentFilter bluetoothChangeState = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothBroadCastReciever,bluetoothChangeState);
    }
    @Override
    protected void onStart() {
        super.onStart();
        functions.checkBluetooth(text, turnOnBluetooth, bluetoothAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        functions.checkBluetooth(text, turnOnBluetooth,  bluetoothAdapter);
    }

    private final BroadcastReceiver bluetoothBroadCastReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
             String action = intent.getAction();
            if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        ///DO SoMETHING
                        turnOnBluetooth.setVisibility(View.GONE);
                        //Write More code here when you implement chat feature
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        turnOnBluetooth.setVisibility(View.VISIBLE);
                        break;

                }
            }
        }
    };

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
        unregisterReceiver(bluetoothBroadCastReciever);
    }


}