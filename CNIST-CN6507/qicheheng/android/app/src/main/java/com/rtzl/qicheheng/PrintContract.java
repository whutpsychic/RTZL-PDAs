package com.rtzl.qichehang;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.print_sdk.PrintUtil;
import com.example.print_sdk.enums.ALIGN_MODE;

import com.rtzl.qicheheng.BitmapUtils;
import com.google.zxing.BarcodeFormat;

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
    pUtil.printEnableMark(false);
    pUtil.printAutoEnableMark(true);
    pUtil.printLanguage(15);
    pUtil.printEncode(3);
    // pUtil.getVersion();
  }

  public void printBarcode(String text) {
    pUtil.printBarcode(text, 200, 300);
  }

  public void printQRcode(String text) {
    // pUtil.printQR2 (8, 3, 49, ALIGN_MODE.ALIGN_CENTER, text);
    Bitmap bitmap=BitmapUtils.encode2dAsBitmap (text, 200, 200, 2);
    pUtil.printQR(bitmap);
    pUtil.printLine(3);
  }
}
