package com.rtzl.qichehang;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanDevice;
import android.os.Bundle;
import com.rtzl.qichehang.PrintContract;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import com.example.print_sdk.PrintUtil;
// import com.example.print_sdk.interfaces.OnPrintEventListener;

public class MainActivity extends FlutterActivity {

  private static final String CHANNEL = "rtzl.scanner";
  private static final String CHANNEL2 = "rtzl.printer";
  private static final String SCAN_ACTION = "scan.rcv.message";
  private MethodChannel mMethodChannel;
  private MethodChannel mMethodChannel2;
  private PrintUtil pUtil;
  private PrintContract printContract;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // ===================== 扫描相关 =====================
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
        if (action.equals(SCAN_ACTION)) {
          byte[] barcode = intent.getByteArrayExtra("barocode");
          int broadCodeLen = intent.getIntExtra("length", 0);
          byte temp = intent.getByteExtra("barcodeType", (byte) 0);
          byte[] aimid = intent.getByteArrayExtra("aimid");

          String barcodeStr = new String(barcode, 0, broadCodeLen);

          //成功
          if (barcodeStr != "" && barcode != null) {
            System.out.println(barcodeStr);
            mMethodChannel.invokeMethod("getCode", barcodeStr);
          }
        }
      }
    };

    mFilter.addAction(SCAN_ACTION);
    registerReceiver(mReceiver, mFilter);
    // ===================== 扫描相关 =====================
    // ===================== 打印相关 =====================
    pUtil = PrintUtil.getClient();
    if (pUtil != null) {
      printContract = new PrintContract(this, pUtil);
      printContract.printInit();
    }

    mMethodChannel2 =
      new MethodChannel(
        getFlutterEngine().getDartExecutor().getBinaryMessenger(),
        CHANNEL2
      );
    mMethodChannel2.setMethodCallHandler(
      new MethodChannel.MethodCallHandler() {
        @Override
        public void onMethodCall(MethodCall call, MethodChannel.Result result) {
          if (call.method.equals("printBarcode")) {
            String barcode = call.arguments.toString().trim();
            printContract.printBarcode(barcode);
          }else if (call.method.equals("printQRcode")){
            String qrcode = call.arguments.toString().trim();
            printContract.printQRcode(qrcode);
          }
        }
      }
    );
  }
}
