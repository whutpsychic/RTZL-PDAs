package com.example.rtzl_zhongtiaoshan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
// import android.widget.Toast;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

public class MainActivity extends FlutterActivity {

  private static final String CHANNEL = "rtzl_nlscan.flutter.io/key";

  private MethodChannel mMethodChannel;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mMethodChannel =
      new MethodChannel(
        getFlutterEngine().getDartExecutor().getBinaryMessenger(),
        CHANNEL
      );

    IntentFilter mFilter = new IntentFilter("nlscan.action.SCANNER_RESULT");

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        final String scanResult_1 = intent.getStringExtra("SCAN_BARCODE1");
        final String scanResult_2 = intent.getStringExtra("SCAN_BARCODE2");
        final int barcodeType = intent.getIntExtra("SCAN_BARCODE_TYPE", -1); // -1:unknown
        final String scanStatus = intent.getStringExtra("SCAN_STATE");
        if ("ok".equals(scanStatus)) {
          //成功
          // System.out.println(scanResult_1);
          mMethodChannel.invokeMethod("getCode", scanResult_1);
        } else {
          //失败如超时等
        }
      }
    };

    registerReceiver(mReceiver, mFilter);
  }
}
