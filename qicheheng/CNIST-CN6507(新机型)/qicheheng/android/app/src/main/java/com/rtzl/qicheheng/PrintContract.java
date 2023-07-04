package com.rtzl.qicheheng;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import android.bld.print.configuration.PrintConfig;
import com.example.lc_print_sdk.PrintUtil;

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
    // pUtil.setEncoding("GB2312");
    // 打印布局（居中）
    // pUtil.printAlignment(ALIGN_MODE.ALIGN_CENTER);
    // pUtil.printEnableCertificate(true);
    // pUtil.printAutoEnableMark(true);
    // pUtil.printLanguage(15);
    // pUtil.printEncode(3);
    // pUtil.printFontSize(MODE_ENLARGE.NORMAL); 
    // pUtil.printTextBold (true);
    // pUtil.getVersion();
    pUtil.printEnableMark(true);
  }

  public void printBarcode(String text) {
    pUtil.printBarcode (PrintConfig.Align.ALIGN_CENTER, 100, text, PrintConfig.BarCodeType.TOP_TYPE_CODE128, PrintConfig.HRIPosition.POSITION_BELOW);
    pUtil.start();
  }

  public void printQRcode(String text) {
    // pUtil.printQR(PrintConfig.Align.ALIGN_CENTER, 200, text);
    pUtil.printQR(PrintConfig.Align.ALIGN_CENTER, 120, text);
    pUtil.printText(PrintConfig.Align.ALIGN_CENTER, PrintConfig.FontSize.TOP_FONT_SIZE_MIDDLE, true, true, text);
    // pUtil.printLine(5);
    pUtil.printLine(1);
    pUtil.start();
    // pUtil.printText(text);
    // pUtil.printLine(7);
}


}
