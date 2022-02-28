package com.rtzl.zhongtiaoshanmes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import com.idata.ocr.Predictor;
import com.idata.ocr.OcrResult;

import android.hardware.Camera;
import android.graphics.PixelFormat;

public class MainActivity extends FlutterActivity {

  private static final String CHANNEL = "rtzl.distinguish";
  private MethodChannel mMethodChannel;
  private Predictor predictor = Predictor.getInstance();
  private Bitmap mImage = null;

  private Camera mCamera;
  public int mScreenWidth;
  public int mScreenHeight;
  private Camera.Size picSize;

  private double width;
  private double height;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // ===================== 识别相关 =====================
    mMethodChannel = new MethodChannel(
        getFlutterEngine().getDartExecutor().getBinaryMessenger(),
        CHANNEL);

    // 主动调取方法绑定
    mMethodChannel.setMethodCallHandler(
        new MethodChannel.MethodCallHandler() {
          @Override
          public void onMethodCall(MethodCall call, MethodChannel.Result result) {

            // 初始化调用
            if (call.method.equals("deviceInit")) {
              System.out.println("--------deviceInit--------");
              new Thread(new Runnable() {
                @Override
                public void run() {
                  predictor.initModel(getApplicationContext());
                }
              }).start();
            }
            // 调用识别
            else if (call.method.equals("operate")) {
              Object args = call.arguments;
              String argStr = null;
              if (args != null)
                argStr = call.arguments.toString().trim();
              if (argStr != null) {
                System.out.println(argStr);
                // 调用相册路径
                String filePath = argStr;
                File file = new File(filePath);
                if (file.exists()) {
                  mImage = BitmapFactory.decodeFile(filePath);
                  int w = mImage.getWidth();
                  int h = mImage.getHeight();

                  System.out.println(w);
                  System.out.println(h);
                  System.out.println("-=-=-=-=-=-=-=-=-=-=trying-=-=-=-=-=-=-=-=-=-");

                  ByteArrayOutputStream stream = new ByteArrayOutputStream();
                  mImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                  byte[] byteArray = stream.toByteArray();
                  System.out.println("-=-=-=-=-=-=-=-=-=-=tried-=-=-=-=-=-=-=-=-=-");
                  mMethodChannel.invokeMethod("callback", byteArray);

                  mImage = Bitmap.createBitmap(mImage, 0, 0, w, h);
                  new Thread(new Runnable() {
                    @Override
                    public void run() {
                      int ret = predictor.getModelState();
                      if (ret == 0) {
                        ArrayList<OcrResult> arrayList = predictor.runOcrPredictor(mImage, 0);
                        System.out.println("-------------arrayList-------------");
                        System.out.println(arrayList);
                      }
                    }
                  }).start();
                }
              }
            }
            // 设定宽高
            else if (call.method.equals("setWH")) {
              Object args = call.arguments;
              if (args instanceof List) {
                List array = (List) args;
                width =(double) (array.get(0));
                height =(double) (array.get(1));
              }
            }
            // 调用识别2
            else if (call.method.equals("operate2")) {
              Object args = call.arguments;
              byte[] da;
              if (args instanceof byte[]) {
                da = (byte[]) args;
                List<OcrResult> arrayList = predictor.runOcrPredictor(da, (int) width, (int) height, 0);
                List<List<String>> resultArr = new ArrayList<>();
                arrayList.forEach((it) -> {
                  List<String> _r = new ArrayList<>();
                  _r.add(it.getLabel());
                  _r.add(String.valueOf(it.getConfidence()));
                  resultArr.add(_r);
                });
                mMethodChannel.invokeMethod("callback", resultArr);
              }
            }
          }
        });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    predictor.releaseModel();
  }
}
