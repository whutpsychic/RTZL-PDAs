package com.rtzl.qichehang;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.print_sdk.PrintUtil;
import com.example.print_sdk.enums.ALIGN_MODE;
import com.example.print_sdk.enums.MODE_ENLARGE;

import com.rtzl.qicheheng.BitmapUtils;
import com.google.zxing.BarcodeFormat;
import java.util.Timer;
import java.util.TimerTask;

public class PrintContract {

  private Context mContext;
  private PrintUtil pUtil;

  public PrintContract(Context context, PrintUtil printUtil) {
    this.mContext = context;
    this.pUtil = printUtil;
  }

  public void printInit() {
    // 编码模式
    pUtil.setEncoding("GB2312");
    // 打印布局（居中）
    pUtil.printAlignment(ALIGN_MODE.ALIGN_CENTER);
    pUtil.printEnableCertificate(true);
    pUtil.printEnableMark(true);
    pUtil.printAutoEnableMark(true);
    pUtil.printLanguage(15);
    pUtil.printEncode(3);
    pUtil.printFontSize(MODE_ENLARGE.NORMAL); 
    pUtil.printTextBold (true);
    // pUtil.getVersion();
  }

  public void printBarcode(String text) {
    pUtil.printBarcode(text, 200, 300);
  }

  public void printQRcode(String text,int size) {
    Bitmap bitmap=BitmapUtils.encode2dAsBitmap (text, 200, 200, 2);
    // try{
    //   Thread.sleep(2000);
    // } catch(Exception e){
    // }
    pUtil.printQR(bitmap);
    pUtil.printLargeText(text);
    pUtil.printLine(5);
    // pUtil.printText(text);
    // pUtil.printLine(7);
}

  // public void printQRcode(int offset,int height,String text){
  //   pUtil.printQR(offset,height,text);
  //   // pUtil.printQR(text);
  // }
}
