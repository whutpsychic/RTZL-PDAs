package com.example.scanconfig;

import android.R.integer;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.device.ScanManager;
import android.device.scanner.configuration.PropertyID;
import android.device.scanner.configuration.Triggering;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private final static String SCAN_ACTION = ScanManager.ACTION_DECODE;//default action
    
    private EditText showScanResult;
    private Button btn;
    private Button mScan;
    private Button mClose;
    private int type;
    private int outPut;
    private CheckBox enableCheckDigit;
    private CheckBox sendCheckDigit;
    private CheckBox convertEAN13;
    EditText minLength;
    EditText maxLength;
    
    private Vibrator mVibrator;
    private ScanManager mScanManager;
    private SoundPool soundpool = null;
    private int soundid;
    private String barcodeStr;
    private boolean isScaning = false;
    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            isScaning = false;
            soundpool.play(soundid, 1, 1, 0, 0, 1);
            showScanResult.setText("");
            mVibrator.vibrate(100);

            byte[] barcode = intent.getByteArrayExtra(ScanManager.DECODE_DATA_TAG);
            int barcodelen = intent.getIntExtra(ScanManager.BARCODE_LENGTH_TAG, 0);
            byte temp = intent.getByteExtra(ScanManager.BARCODE_TYPE_TAG, (byte) 0);
            android.util.Log.i("debug", "----codetype--" + temp);
            barcodeStr = new String(barcode, 0, barcodelen);

            showScanResult.setText(barcodeStr);

        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        setupView();
    }

    private void initScan() {
        // TODO Auto-generated method stub
        mScanManager = new ScanManager();
        mScanManager.openScanner(); 
      
        mScanManager.switchOutputMode( 0);
        soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100); // MODE_RINGTONE
        soundid = soundpool.load("/etc/Scan_new.ogg", 1);
        value_buff = mScanManager.getParameterInts(id);
        if(value_buff != null) {
            enableCheckDigit.setChecked(value_buff[0] == 1);
            sendCheckDigit.setChecked(value_buff[1] == 1);
            convertEAN13.setChecked(value_buff[2] == 1);
            minLength.setText(String.valueOf(value_buff[3]));
            maxLength.setText(String.valueOf(value_buff[4]));
        }
    }
    int[] id = new int[]{
            PropertyID.I25_ENABLE_CHECK,
            PropertyID.I25_SEND_CHECK,
            PropertyID.I25_TO_EAN13,
            PropertyID.I25_LENGTH1,
            PropertyID.I25_LENGTH2
    };
    int[] value_buff;
    private void setupView() {
        // TODO Auto-generated method stub
        showScanResult = (EditText) findViewById(R.id.scan_result);
        enableCheckDigit= (CheckBox) findViewById(R.id.checkBox1);
        sendCheckDigit= (CheckBox) findViewById(R.id.checkBox2);
        convertEAN13= (CheckBox) findViewById(R.id.checkBox3);
        minLength= (EditText) findViewById(R.id.editText1);
        maxLength= (EditText) findViewById(R.id.editText2);
        btn = (Button) findViewById(R.id.manager);
        btn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                 String min = minLength.getText().toString();
                 String max = maxLength.getText().toString();
                 
                 if(value_buff == null) value_buff = new int[5];
                 value_buff[0] = enableCheckDigit.isChecked() ? 1 : 0;
                 value_buff[1] = sendCheckDigit.isChecked() ? 1 : 0;
                 value_buff[2] = convertEAN13.isChecked() ? 1 : 0;
                 if(!min.equals("")) {
                     value_buff[3] = Integer.parseInt(min);
                 } else {
                     value_buff[3] = 6;
                 }
                 if(!max.equals("")) {
                     value_buff[4] = Integer.parseInt(max);
                 } else {
                     value_buff[4] = 10;
                 }
                 mScanManager.setParameterInts(id, value_buff);
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if(mScanManager != null) {
            mScanManager.stopDecode();
            isScaning = false;
        }
        unregisterReceiver(mScanReceiver);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        initScan();
        showScanResult.setText("");
        IntentFilter filter = new IntentFilter();
        int[] idbuf = new int[]{PropertyID.WEDGE_INTENT_ACTION_NAME, PropertyID.WEDGE_INTENT_DATA_STRING_TAG};
        String[] value_buf = mScanManager.getParameterString(idbuf);
        if(value_buf != null && value_buf[0] != null && !value_buf[0].equals("")) {
            filter.addAction(value_buf[0]);
        } else {
            filter.addAction(SCAN_ACTION);
        }
       
        registerReceiver(mScanReceiver, filter);
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        PackageManager pk = getPackageManager();
        PackageInfo pi;
        try {
            pi = pk.getPackageInfo(getPackageName(), 0);
            Toast.makeText(this, "V" +pi.versionName , Toast.LENGTH_SHORT).show();
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }
}
