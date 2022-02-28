package com.example.rtzl_zhongtiaoshan2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import android.device.ScanDevice;

public class MainActivity extends FlutterActivity {

  private static final String CHANNEL = "rtzl_cnscan.flutter.io/key";
  private static final String SCAN_ACTION = "scan.rcv.message";
  private MethodChannel mMethodChannel;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ScanDevice sd = new ScanDevice();

    // 设置为广播输出模式
    sd.setOutScanMode(0);

    mMethodChannel =
      new MethodChannel(
        getFlutterEngine().getDartExecutor().getBinaryMessenger(),
        CHANNEL
      );

    IntentFilter mFilter = new IntentFilter();

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {

          String action = intent.getAction();
          if(action.equals(SCAN_ACTION)){
              byte[] barcode = intent.getByteArrayExtra("barocode");
              int broadCodeLen = intent.getIntExtra("length",0);
              byte temp = intent.getByteExtra("barcodeType",(byte) 0);
              byte[] aimid = intent.getByteArrayExtra("aimid");

              String barcodeStr = new String(barcode, 0, broadCodeLen);
              // System.out.println(barcodeStr);

              //成功
              if(barcodeStr!=""&&barcode!=null){
                System.out.println(barcodeStr);
                mMethodChannel.invokeMethod("getCode", barcodeStr);
              }
            }
       
      }
    };
    
    mFilter.addAction(SCAN_ACTION);
    registerReceiver(mReceiver, mFilter);
  }
}
