package com.example.esc_printdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.esc_printdemo.util.BitmapUtils;
import com.example.esc_printdemo.util.SystemUtils;
import com.example.print_sdk.EscUtils;
import com.example.print_sdk.PrintUtil;
import com.example.print_sdk.SerialManager;
import com.example.print_sdk.enums.ALIGN_MODE;
import com.example.print_sdk.enums.BARCODE_1D_TYPE;
import com.example.print_sdk.enums.MODE_ENLARGE;
import com.example.print_sdk.util.ByteUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by moxiaomo
 * on 2020/4/1
 */
public class PrintContract {

    private String TAG=PrintContract.class.getName ();


    private Context mContext;
    private PrintUtil pUtil;

    public PrintContract() {
    }

    public PrintContract(Context context, PrintUtil printUtil) {
        this.mContext=context;
        this.pUtil=printUtil;
    }


    public void printInit() {

            pUtil.setEncoding ("GB2312");
            pUtil.printEnableCertificate (true);
            pUtil.printEnableMark (false);
            pUtil.printAutoEnableMark(false);
            pUtil.printLanguage (15);
            pUtil.printEncode (3);
          //  pUtil.printThicken (false);
            pUtil.getVersion ();
            //pUtil.getTemperature ();
            //Thread.sleep (100);

    }

    public void printLabel(String text,int number, String con) {
        pUtil.printEnableMark (true);
        pUtil.printState ();
        pUtil.printStartNumber (number);
        pUtil.printConcentration (Integer.valueOf (con));
        pUtil.printAlignment (ALIGN_MODE.ALIGN_CENTER);

        pUtil.printText ("test test test test test");
        pUtil.printLine ();
//            pUtil.printAlignment (ALIGN_MODE.ALIGN_LEFT);
//            pUtil.printTextBold (false);
//            pUtil.printLine ();
//            pUtil.printText ("test test test test test");
//            pUtil.printLine ();
//            pUtil.printText ("test test test test test");
//            pUtil.printLine ();
//            pUtil.printAlignment (ALIGN_MODE.ALIGN_CENTER);
//            pUtil.printLine ();
        pUtil.printBarcode (text, 80, 2);
        pUtil.printLine ();
        pUtil.printGoToNextMark ();
        pUtil.printEndNumber ();
    }


    public void printText(int number, String con) {
        String s_gbk=null;
        try {
            s_gbk=new String ("12345678".getBytes (), "utf-8");
            Bitmap bitmap=BitmapUtils.encode2dAsBitmap (s_gbk, 200, 200, 2);

            pUtil.printState ();
            pUtil.printStartNumber (number);
            pUtil.printConcentration (Integer.valueOf (con));

            pUtil.printFontSize (MODE_ENLARGE.NORMAL);
            pUtil.printTextBold (false); // 是否加粗
            pUtil.printAlignment (ALIGN_MODE.ALIGN_LEFT); // 对齐方式
            pUtil.printFontSize (MODE_ENLARGE.NORMAL); // 字体大小
            pUtil.printText (SystemUtils.LanguageChange (mContext));

            pUtil.printLine ();
            pUtil.printDashLine ();
            pUtil.printLine (2);
            pUtil.printAlignment (ALIGN_MODE.ALIGN_CENTER);
            pUtil.printBarcode ("123456", 80, 2);
            pUtil.printLine ();
            //pUtil.printQR (bitmap);
            pUtil.printQR2 (8, 3, 49, ALIGN_MODE.ALIGN_CENTER, "13245678");
            pUtil.printLine (2);
            pUtil.printEndNumber ();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace ();
        }

    }



    public void printQR(String text, int number, String con) {
        pUtil.printState ();
        pUtil.printStartNumber (number);
        pUtil.printConcentration (Integer.valueOf (con));
        pUtil.printQR2 (8, 3, 49, ALIGN_MODE.ALIGN_CENTER, text);
        pUtil.printLine (3);
        pUtil.printEndNumber ();
    }

    public void printBarcode(String text, int number, String con) {
        pUtil.printState ();
        pUtil.printStartNumber (number);
        pUtil.printConcentration (Integer.valueOf (con));
        pUtil.printBarcode (text, 200, 300);
        pUtil.printLine (3);
        pUtil.printEndNumber ();

    }

    public void printImg(Bitmap bitmap, String con) {
        pUtil.printState ();
        pUtil.printConcentration (Integer.valueOf (con));
        pUtil.printLine ();
        pUtil.printBitmap2 (bitmap);

    }


    public void printFeatureList() {
        pUtil.printState ();
        pUtil.printFeatureList ();
    }

    public void printThai() {
        pUtil.printState ();
        pUtil.printConcentration (25);
        pUtil.printFontSize (MODE_ENLARGE.NORMAL);
        pUtil.printAlignment (ALIGN_MODE.ALIGN_LEFT); // 对齐方式
        pUtil.printText ("หลังจากการจัดกลุ่มหมายเลขในรูปภาพดิจิทัลที่เขียนด้วยลายมือ");
        pUtil.printLine ();
    }


    public void setLanguage(int mode) {
        pUtil.printState ();
        pUtil.printLanguage (mode);
    }

    public void printEnableMark(boolean bool) {
        pUtil.printState ();
        pUtil.printEnableMark (bool);
    }

    public void printGoToNextMark() {
        pUtil.printState ();
        pUtil.printGoToNextMark ();
    }

    public void printThicken(boolean bool){
        pUtil.printThicken (bool);
    }

    public void resetPrint() {
        pUtil.printState ();
        pUtil.resetPrint ();
    }

    public void closeDev() {
        pUtil.closeDev ();
    }


}
