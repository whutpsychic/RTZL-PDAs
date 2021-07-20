package com.rtzl.qichehang;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.print_sdk.PrintUtil;
import com.example.print_sdk.enums.ALIGN_MODE;

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
    pUtil.printAutoEnableMark(false);
    pUtil.printLanguage(15);
    pUtil.printEncode(3);
    pUtil.getVersion();

  }

  public void printBarcode(String text) {
    pUtil.printBarcode(text, 200, 300);
    pUtil.printLine(5);
  }

  public void printQRcode(String text) {
    pUtil.printQR2 (8, 3, 49, ALIGN_MODE.ALIGN_CENTER, text);
    pUtil.printLine(5);
  }
}
