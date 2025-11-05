package com.example.scanmanagerdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanManager;
import android.device.scanner.configuration.PropertyID;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ubx.scanner.StatusListener;



public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private String[] strs;
    private String mAction =  ScanManager.ACTION_DECODE;//"android.provider.sdlMessage";
    private String mKey = ScanManager.BARCODE_STRING_TAG;
    private ScanManager mScanManager = new ScanManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.tv_code_type);

        mScanManager.switchOutputMode(0);//0：开启广播模式   1：键盘模式

        findViewById(R.id.btn_scan_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//

                mScanManager.startDecode();
            }
        });
        findViewById(R.id.btn_scan_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScanManager.stopDecode();
            }
        });


        try {
            mScanManager.addStatusActionListener(new StatusListener() {
                @Override
                public void onStatus(int event, Bundle bundle) {
                    //TODO  event   4:开始出光扫描     3：松开按键（停止扫描）  2：扫描超时

                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getRealTimeAction();
        registerReceiver(true);
    }

    /**
     * 实时获取扫描头 ACTION 以及 key 参数
     */
    private void getRealTimeAction() {
        strs = new ScanManager().getParameterString(new int[]{PropertyID.WEDGE_INTENT_ACTION_NAME, PropertyID.WEDGE_INTENT_DATA_STRING_TAG});
        mAction = strs[0];//实时获取的action
        mKey = strs[1];//实时获取的key
    }

    @Override
    protected void onPause() {
        super.onPause();
        registerReceiver(false);
    }

    /**
     * @param register , ture register , false unregister
     */
    private void registerReceiver(boolean register) {
        if (register) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(mAction);
            registerReceiver(mReceiver, filter);
        } else {
            unregisterReceiver(mReceiver);
        }
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {



            String action = intent.getAction();
            if (action.equals(mAction)) {
                String barcodeStr = intent.getStringExtra(mKey);//barcode value  获取扫描的条码数据
                textView.setText(barcodeStr);
            }

        }
    };
}