package com.example.esc_printdemo;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.esc_printdemo.ui.BluetoothActivity;
import com.example.esc_printdemo.ui.CanvasActivity;
import com.example.esc_printdemo.ui.PsamActivity;
import com.example.esc_printdemo.ui.ESCActivity;

public class SwitchActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_switch);

        Toolbar toolbar=findViewById (R.id.toolbar_printer);
        toolbar.setTitle ("");
        setSupportActionBar (toolbar);

        Button btn_printer=findViewById (R.id.btn_printer);
        Button btn_psam=findViewById (R.id.btn_psam);
        Button btn_bluetooth_printer=findViewById (R.id.btn_bluetooth_printer);

        btn_printer.setOnClickListener (this);
        btn_bluetooth_printer.setOnClickListener (this);
        btn_psam.setOnClickListener (this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId ()) {
            case R.id.btn_printer:
                Intent intent=new Intent (this, ESCActivity.class);
                startActivity (intent);
                break;
            case R.id.btn_bluetooth_printer:
                intent=new Intent (this, BluetoothActivity.class);
                startActivity (intent);
                break;
            case R.id.btn_psam:
                intent=new Intent (this, PsamActivity.class);
                startActivity (intent);
                break;
        }
    }
}
