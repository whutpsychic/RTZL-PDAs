package com.example.devicemanager;

import android.app.Activity;
import android.device.DeviceManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
    DeviceManager mDevice;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDevice = new DeviceManager();

        Button disableHomekey = (Button) findViewById(R.id.button1);
        disableHomekey.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDevice.enableHomeKey(false);
            }
        });
        
        Button enableHomekey = (Button) findViewById(R.id.button2);
        enableHomekey.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDevice.enableHomeKey(true);
            }
        });
        
        
        Button disableStatuBar= (Button) findViewById(R.id.button3);
        disableStatuBar.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDevice.enableStatusBar(false);
            }
        });
        
        Button enableStatuBar = (Button) findViewById(R.id.button4);
        enableStatuBar.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDevice.enableStatusBar(true);
            }
        });
        
        Button setTime = (Button) findViewById(R.id.button5);
        setTime.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                long cur = System.currentTimeMillis();
                mDevice.setCurrentTime(cur+ 60*60*1000);
                //mDevice.setAPN("中国联通 3g 网络", "3gnet", null, 0, "none",null, "none", null, "460", "01", null,0, 0, "default,supl,dun", null, 0, null, true);
              //mDevice.setAPN("中国移动NET设置", "cmnet", null, 0, "none",null, "none", null, "460", "02", null,0, 0, "default,supl,dun", null, 0, null, true);
                /*  (String name, String apn, String proxy, int port, String user,
                String server, String password, String mmsc, String mcc, String mnc, String mmsproxy,
                int mmsport, int authtype, String type, String protocol, int bearer, String roamingprotocol, boolean current) */
                //电信    
                //mDevice.setAPN("Telecom", "ctnet", null, 0, "ctnet@mycdma.cn",null, "vnet.mobi", null, "460", "03", null,0, 3, "default,hipri", "IP", 0, "IP", true);
            }
        });
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //mDevice.switchHomeKey(true);
        //mDevice.switchStatusBar(true);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        setTitle("DeviceID:   " + mDevice.getDeviceId());
    }

}
