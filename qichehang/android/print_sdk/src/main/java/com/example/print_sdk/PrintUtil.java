package com.example.print_sdk;

import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.example.print_sdk.enums.ALIGN_MODE;
import com.example.print_sdk.enums.BARCODE_1D_TYPE;
import com.example.print_sdk.enums.MODE_ENLARGE;
import com.example.print_sdk.interfaces.OnPrintEventListener;
import com.example.print_sdk.util.BitmapToByteUtils;
import com.example.print_sdk.util.ByteUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * 打印工具类
 */
public class PrintUtil implements SerialManager.OnDataReceiveListener {

    private static final String TAG=PrintUtil.class.getName ();
    private OutputStreamWriter mWriter=null;
    private OutputStream mOutputStream=null;
    private InputStream mInputStream=null;
    public final static int WIDTH_PIXEL=384;
    private OnPrintEventListener mListener=null;

    // Esc Data length
    private int mEscLength=0;
    // Esc Data buffer
    private byte[] mEscBuf=null;
    private String mEncoding="GB2312";
    public boolean check_paper=true;

    private Handler mHandler;
    private long time=2000;
    private static boolean bRet=false;

    /**
     * 恶汉,只实例化一次
     */
    private enum Singleton {
        INSTANCE;
        private PrintUtil client;

        Singleton() {
            try {
                client=new PrintUtil ();
                client.initSDK ();
                Log.e (TAG, "Singleton: ");
            } catch (IOException e) {
                e.printStackTrace ();
            }
        }

        private PrintUtil getInstance() {
            return client;
        }
    }

    public static PrintUtil getClient() {
        return Singleton.INSTANCE.getInstance ();
    }


    /**
     * 懒汉模式,用的时候才实例化
     */
    private static PrintUtil singleCase=null;


    public static synchronized PrintUtil getInstance() {
        if (singleCase == null) {
            try {
                singleCase=new PrintUtil ();
                singleCase.initSDK ();
                Log.e (TAG, "Singleton: 2");
            } catch (IOException e) {
                e.printStackTrace ();
            }
        }
        return singleCase;
    }


    /**
     * init Pos
     *
     * @throws IOException
     */
    public PrintUtil() throws IOException {

    }

    public void sleep(int longs) {
        try {
            Thread.sleep (longs);
        } catch (InterruptedException e) {
            e.printStackTrace ();
        }
    }

    public void initSDK() {
        try {
            mHandler=new Handler (Looper.getMainLooper ());
            SerialManager.getClient ().open ();
            SerialManager.getClient ().setOnDataReceiveListener (this);
            SerialManager.getClient ().startReadThread ();

            mWriter=new OutputStreamWriter (SerialManager.getClient ().getOutputStream (), mEncoding);
            mOutputStream=SerialManager.getClient ().getOutputStream ();
            mInputStream=SerialManager.getClient ().getInputStream ();
            initPrinter ();
            Thread.sleep (50);
            bRet=false;
        } catch (InterruptedException e) {
            e.printStackTrace ();
            bRet=true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace ();
            bRet=true;
        }
    }

    public synchronized void setEncoding(String encoding) {
        this.mEncoding=encoding;
    }

    public synchronized void setSocket(BluetoothSocket socket, String encoding) throws IOException {
        mWriter=new OutputStreamWriter (socket.getOutputStream (), encoding);
        mOutputStream=socket.getOutputStream ();
        this.mEncoding=encoding;
        initPrinter ();
    }


    public synchronized void print(final byte[] bs) throws IOException {
        if (!check_paper) {
            return;
        }
        Log.e (TAG, "print: " + ByteUtils.Bytes2HexString (bs));
        for (byte by : bs) {
            mOutputStream.write (by);
            mOutputStream.flush ();
        }
    }

    /**
     * init printer
     *
     * @throws IOException
     */
    public synchronized void initPrinter() {
        try {
            if (!check_paper) {
                return;
            }
            mWriter.write (0x1B);
            mWriter.write (0x40);
            mWriter.flush ();
            sleep (50);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }


    /**
     * 获取温度
     *
     * @throws IOException
     */
    public synchronized void getTemperature() {
        try {
            if (!check_paper) {
                return;
            }
            mWriter.write (0x1D);
            mWriter.write (0x67);
            mWriter.write (0x36);
            mWriter.flush ();
            sleep (50);
        } catch (IOException e) {
            Log.e (TAG, "getTemperature: " + e.getMessage ());
        }

    }


    /**
     * 加浓
     *
     * @param bool
     * @throws IOException
     */
    public synchronized void printThicken(boolean bool) {
        try {
            if (!check_paper) {
                return;
            }
            mEscBuf=new byte[64];
            mEscLength=0;
            // enable block mark
            mEscBuf[mEscLength++]=0x1B;
            mEscBuf[mEscLength++]=0x23;
            mEscBuf[mEscLength++]=0x23;
            mEscBuf[mEscLength++]=0x45;
            mEscBuf[mEscLength++]=0x4E;
            mEscBuf[mEscLength++]=0x44;
            mEscBuf[mEscLength++]=0x45;
            if (bool) {
                mEscBuf[mEscLength++]=0x31;
            } else {
                mEscBuf[mEscLength++]=0x30;
            }
            byte[] buffer=new byte[mEscLength];
            System.arraycopy (mEscBuf, 0, buffer, 0, mEscLength);
            Log.e ("TAG", "printThicken: " + ByteUtils.Bytes2HexString (buffer));
            print (buffer);
            sleep (50);
        } catch (IOException e) {
            Log.e (TAG, "printThicken: ");
        }
    }


    /**
     * 获取打印状态
     * Get print status
     *
     * @throws IOException
     */
    public synchronized void printState() {
        try {
            mWriter.write (0x1D);
            mWriter.write (0x61);
            mWriter.write (0x22);
            mWriter.flush ();
            sleep (50);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }

    /**
     * 设置语言
     * language setting
     *
     * @param mode
     * @throws IOException
     */
    public synchronized void printLanguage(int mode) {
        try {
            if (!check_paper) {
                return;
            }
            mEscBuf=new byte[64];
            mEscLength=0;
            // enable block mark
            mEscBuf[mEscLength++]=0x1B;
            mEscBuf[mEscLength++]=0x23;
            mEscBuf[mEscLength++]=0x23;
            mEscBuf[mEscLength++]=0x53;
            mEscBuf[mEscLength++]=0x4C;
            mEscBuf[mEscLength++]=0x41;
            mEscBuf[mEscLength++]=0x4E;
            mEscBuf[mEscLength++]=((byte) mode);
            byte[] buffer=new byte[mEscLength];
            System.arraycopy (mEscBuf, 0, buffer, 0, mEscLength);
            print (buffer);
            sleep (50);
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    /**
     * 设置浓度
     * Set encoding
     *
     * @param level
     * @throws IOException
     */
    public synchronized void printConcentration(int level){
        try {
            if (!check_paper) {
                return;
            }
            mEscBuf=new byte[64];
            mEscLength=0;
            // enable block mark
            mEscBuf[mEscLength++]=0x1B;
            mEscBuf[mEscLength++]=0x23;
            mEscBuf[mEscLength++]=0x23;
            mEscBuf[mEscLength++]=0x53;
            mEscBuf[mEscLength++]=0x54;
            mEscBuf[mEscLength++]=0x44;
            mEscBuf[mEscLength++]=0x50;
            mEscBuf[mEscLength++]=(byte) level;
            byte[] buffer=new byte[mEscLength];
            System.arraycopy (mEscBuf, 0, buffer, 0, mEscLength);
            print (buffer);
            sleep (50);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }

    /**
     * 设置编码
     * Set encoding
     *
     * @param encode
     * @throws IOException
     */
    public synchronized void printEncode(int encode){
        try {
            if (!check_paper) {
                return;
            }
            mEscBuf=new byte[64];
            mEscLength=0;
            // enable block mark
            mEscBuf[mEscLength++]=0x1B;
            mEscBuf[mEscLength++]=0x23;
            mEscBuf[mEscLength++]=0x23;
            mEscBuf[mEscLength++]=0x43;
            mEscBuf[mEscLength++]=0x44;
            mEscBuf[mEscLength++]=0x54;
            mEscBuf[mEscLength++]=0x59;
            if (encode == 1) {
                mEscBuf[mEscLength++]=(byte) 2;
            } else {
                mEscBuf[mEscLength++]=(byte) encode;
            }
            byte[] buffer=new byte[mEscLength];
            System.arraycopy (mEscBuf, 0, buffer, 0, mEscLength);
            print (buffer);
            sleep (50);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }

    public synchronized void printBackPaper(int param){
        try {
            if (!check_paper) {
                return;
            }
            mEscBuf=new byte[64];
            mEscLength=0;
            mEscBuf[mEscLength++]=0x1B;
            mEscBuf[mEscLength++]=0x23;
            mEscBuf[mEscLength++]=0x23;
            mEscBuf[mEscLength++]=0x54;
            mEscBuf[mEscLength++]=0x45;
            mEscBuf[mEscLength++]=0x41;
            mEscBuf[mEscLength++]=0x52;
            mEscBuf[mEscLength++]=(byte) param;
            mEscBuf[mEscLength++]=(byte) 0x00;
            byte[] buffer=new byte[mEscLength];
            System.arraycopy (mEscBuf, 0, buffer, 0, mEscLength);
            print (buffer);
            sleep (50);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }

    /**
     * 文本强调模式
     * Text emphasis mode
     *
     * @param bold
     */
    public synchronized void printTextBold(boolean bold){
        try {
            if (!check_paper) {
                return;
            }
            mWriter.write (0x1B);
            mWriter.write (0x45);
            if (bold) {
                mWriter.write (0x01);
            } else {
                mWriter.write (0x00);
            }
            mWriter.flush ();
            sleep (50);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }

    /**
     * 设置字体大小
     * Set font size
     *
     * @param mode
     * @throws IOException
     */
    public synchronized void printFontSize(MODE_ENLARGE mode){
        try {
            if (!check_paper) {
                return;
            }
            mWriter.write (0x1D);
            mWriter.write (0x21);
            mWriter.write (mode.Get ());
            mWriter.flush ();
            sleep (50);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }

    /**
     * 打印换行
     * Print line break
     *
     * @return length 需要打印的空行数 Number of blank lines to be printed
     * @throws IOException
     */
    public synchronized void printLine(int lineNum){
        try {
            if (!check_paper) {
                return;
            }
            for (int i=0; i < lineNum; i++) {
                mWriter.write ("\n");
            }
            mWriter.flush ();
            sleep (50);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }

    /**
     * 打印换行(只换一行)
     * Print line breaks (only line breaks)
     *
     * @throws IOException
     */
    public synchronized void printLine(){
        printLine (1);
    }

    /**
     * 打印空白(一个Tab的位置，约4个汉字)
     * Print blank (a tab position, about 4 Chinese characters)
     *
     * @param length 需要打印空白的长度 Need to print the length of the blank,
     * @throws IOException
     */
    public synchronized void printTabSpace(int length){
        try {
            if (!check_paper) {
                return;
            }
            for (int i=0; i < length; i++) {
                mWriter.write ("\t");
            }
            mWriter.flush ();
            sleep (50);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }

    /**
     * 绝对打印位置
     * Absolute print position
     *
     * @return
     * @throws IOException
     */
    public byte[] setLocation(int offset) throws IOException {
        byte[] bs=new byte[4];
        bs[0]=0x1B;
        bs[1]=0x24;
        bs[2]=(byte) (offset % 256);
        bs[3]=(byte) (offset / 256);
        return bs;
    }

    public byte[] getGbk(String stText) throws IOException {
        byte[] returnText=stText.getBytes (mEncoding); // Must be placed in try
        return returnText;
    }

    private int getStringPixLength(String str) {
        int pixLength=0;
        char c;
        for (int i=0; i < str.length (); i++) {
            c=str.charAt (i);
            if (isChinese (c)) {
                pixLength+=24;
            } else {
                pixLength+=12;
            }
        }
        return pixLength;
    }

    // 判断一个字符是否是中文
    public boolean isChinese(char c) {
        return c >= 0x4E00 && c <= 0x9FA5;// 根据字节码判断
    }

    // 判断一个字符串是否含有中文
    public boolean isChinese(String str) {
        if (str == null)
            return false;
        for (char c : str.toCharArray ()) {
            if (isChinese (c))
                return true;// 有一个中文字符就返回
        }
        return false;
    }

    public int getOffset(String str) {
        return WIDTH_PIXEL - getStringPixLength (str);
    }

    /**
     * 打印文字
     * Print text
     *
     * @param text
     * @throws IOException
     */
    public synchronized void printText(String text){
        try {
            if (!check_paper) {
                return;
            }
            mWriter.write (text);
            mWriter.flush ();
            sleep (5);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }

    /**
     * 对齐0:左对齐，1：居中，2：右对齐
     * Alignment 0: Left alignment, 1: Center, 2: Right alignment
     */
    public synchronized void printAlignment(ALIGN_MODE alignment){
        try {
            if (!check_paper) {
                return;
            }
            mWriter.write (0x1b);
            mWriter.write (0x61);
            mWriter.write (alignment.Get ());
            mWriter.flush ();
            sleep (50);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }

    public synchronized void printMarginLeft(int Param){
        try {
            if (!check_paper) {
                return;
            }
            mWriter.write (0x1D);
            mWriter.write (0x4C);
            mWriter.write ((byte) Param);
            mWriter.write ((byte) (Param >> 8));
            mWriter.flush ();
            sleep (50);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }

    /**
     * Large Text
     * 大文字
     *
     * @param text
     * @throws IOException
     */
    public synchronized void printLargeText(String text) {
        try {
            if (!check_paper) {
                return;
            }
            mWriter.write (0x1b);
            mWriter.write (0x21);
            mWriter.write (48);
            mWriter.write (text);
            mWriter.write (0x1b);
            mWriter.write (0x21);
            mWriter.write (0);
            mWriter.flush ();
            sleep (50);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }


    /**
     * 开启一票一证
     * Open one ticket and one certificate
     *
     * @param bool
     * @throws IOException
     */
    public synchronized void printEnableCertificate(boolean bool) {
        try {
            if (!check_paper) {
                return;
            }
            mWriter.write (0x1B);
            mWriter.write (0x23);
            mWriter.write (0x23);
            mWriter.write (0x46);
            mWriter.write (0x54);
            mWriter.write (0x4B);
            mWriter.write (0x54);
            if (bool) {
                mWriter.write (0x31);
            } else {
                mWriter.write (0x30);
            }
            mWriter.flush ();
            sleep (50);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }

    /**
     * 小票开始
     * Small ticket start
     *
     * @param number
     * @throws IOException
     */
    public synchronized void printStartNumber(int number){
        try {
            if (!check_paper) {
                return;
            }
            byte[] topByte=new byte[]{0x1D, 0x23, 0x53};
            byte[] endByte=ByteUtils.little_intToByte (number);
            byte[] senByte=ByteUtils.addBytes (topByte, endByte);
            mOutputStream.write (senByte);
            mOutputStream.flush ();
            sleep (50);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }

    /**
     * 小票结尾
     * End of ticket
     *
     * @throws IOException
     */
    public synchronized void printEndNumber() {
        try {
            if (!check_paper) {
                return;
            }
            mWriter.write (0x1D);
            mWriter.write (0x23);
            mWriter.write (0x45);
            mWriter.flush ();
            sleep (50);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }


    /**
     * printer barcode
     *
     * @param text
     * @param Height
     * @param Width  1-4
     * @throws IOException
     */
    public synchronized void printBarcode(String text, int Height, int Width){
        try {
            if (!check_paper) {
                return;
            }
            int dataLen=text.getBytes (mEncoding).length;
            mWriter.write (0x1D);
            mWriter.write ("h");
            mWriter.write (Height);

            mWriter.write (0x1D);
            mWriter.write ("w");
            mWriter.write (Width);

            mWriter.write (0x1D);
            mWriter.write ("k");
            mWriter.write ((byte) BARCODE_1D_TYPE.CODE128.Get ());
            mWriter.write (dataLen);
            mWriter.write (text);
            mWriter.flush ();
            sleep (50);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }

    public synchronized void printBarcode2(Bitmap bitmap){
        try {
            if (!check_paper) {
                return;
            }
            byte[] bmpByteArray=BitmapToByteUtils.draw2PxPoint (bitmap);
            print (bmpByteArray);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }


    public synchronized void printNoBarcodeText(){
        try {
            if (!check_paper) {
                return;
            }
            mEscBuf=new byte[64];
            mEscLength=0;
            // enable block mark
            mEscBuf[mEscLength++]=0x1D;
            mEscBuf[mEscLength++]=0x48;
            mEscBuf[mEscLength++]=0x00;
            mEscBuf[mEscLength++]=0x30;
            byte[] buffer=new byte[mEscLength];
            System.arraycopy (mEscBuf, 0, buffer, 0, mEscLength);
            print (buffer);
            sleep (50);
            print (buffer);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }

    /**
     * printer QR
     *
     * @param text
     * @param height
     * @param width  384
     * @throws IOException
     */
    public synchronized void printQR(Bitmap bitmap) {
        try {
            if (!check_paper) {
                return;
            }

            byte[] bmpByteArray=BitmapToByteUtils.draw2PxPoint (bitmap);
            //    Log.i (TAG, "printQR: "+ByteUtils.Bytes2HexString (bmpByteArray));
            print (bmpByteArray);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }


    /**
     * @param pix       pixel size n>=1 , n<=24
     * @param unit      Unin size 1≤n ≤16
     * @param level     48 49 50 51
     * @param AlignMode
     * @param CodeText
     * @throws IOException
     */
    public synchronized void printQR2(int pix, int unit, int level, ALIGN_MODE AlignMode, String CodeText) {
        try {
            if (!check_paper) {
                return;
            }
            mEscBuf=new byte[2048];
            mEscLength=0;
            short dataLen=0;
            try {
                dataLen=(short) CodeText.getBytes (mEncoding).length;
                Log.e (TAG, "esc_barcode_2D_print: " + dataLen);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace ();
            }
            dataLen+=3;
            byte pl=(byte) (dataLen & 0x00ff);
            byte ph=(byte) ((dataLen & 0xff00) >> 8);

            mEscBuf[mEscLength++]=0x0D;
            mEscBuf[mEscLength++]=0x0A;
            // QR Code pixel size n>=1 , n<=24
            // QR码像素大小
            mEscBuf[mEscLength++]=0x1B; // GS
            mEscBuf[mEscLength++]=0x23;
            mEscBuf[mEscLength++]=0x23;
            mEscBuf[mEscLength++]=0x51;
            mEscBuf[mEscLength++]=0x50;
            mEscBuf[mEscLength++]=0x49;
            mEscBuf[mEscLength++]=0x58;
            mEscBuf[mEscLength++]=(byte) pix;

            mEscBuf[mEscLength++]=0x1B; // ESC
            mEscBuf[mEscLength++]=0x61; // a(Align)
            mEscBuf[mEscLength++]=(byte) AlignMode.Get ();

            // QR Code Unin size 1≤n ≤16
            // 二维码尺寸
            mEscBuf[mEscLength++]=0x1D; // GS
            mEscBuf[mEscLength++]=0x28;
            mEscBuf[mEscLength++]=0x6B;
            mEscBuf[mEscLength++]=0x03;
            mEscBuf[mEscLength++]=0x00;
            mEscBuf[mEscLength++]=0x31;
            mEscBuf[mEscLength++]=0x43;
            mEscBuf[mEscLength++]=(byte) unit;

            // QR Code Error correction level
            // QR码纠错等级
            mEscBuf[mEscLength++]=0x1D; // GS
            mEscBuf[mEscLength++]=0x28;
            mEscBuf[mEscLength++]=0x6B;
            mEscBuf[mEscLength++]=0x03;
            mEscBuf[mEscLength++]=0x00;
            mEscBuf[mEscLength++]=0x31;
            mEscBuf[mEscLength++]=0x45;
            mEscBuf[mEscLength++]=(byte) level;

            // QR Code Transfer data to the encoding cache
            // QR Code将数据传输到编码缓存
            mEscBuf[mEscLength++]=0x1D; // GS
            mEscBuf[mEscLength++]=0x28;
            mEscBuf[mEscLength++]=0x6B;
            mEscBuf[mEscLength++]=pl;// PL
            mEscBuf[mEscLength++]=ph;// PH
            mEscBuf[mEscLength++]=0x31;
            mEscBuf[mEscLength++]=0x50;
            mEscBuf[mEscLength++]=0x30;
            esc_text_print (CodeText);

            // QR Code Print 2d barcode in code cache.
            // QR Code在代码缓存中打印二维条码。
            dataLen-=3;
            pl=(byte) (dataLen & 0x00ff);
            ph=(byte) ((dataLen & 0xff00) >> 8);
            mEscBuf[mEscLength++]=0x1D; // GS
            mEscBuf[mEscLength++]=0x28;
            mEscBuf[mEscLength++]=0x6B;
            mEscBuf[mEscLength++]=pl;// PL
            mEscBuf[mEscLength++]=ph;// PH
            mEscBuf[mEscLength++]=0x31;
            mEscBuf[mEscLength++]=0x51;
            mEscBuf[mEscLength++]=0x30;
            byte[] buffer=new byte[mEscLength];
            System.arraycopy (mEscBuf, 0, buffer, 0, mEscLength);
            print (buffer);
            sleep (50);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }

    public synchronized int esc_text_print(String Text) {

        byte[] SourceTextGBK_Bytes=null;
        try {
            SourceTextGBK_Bytes=Text.getBytes ("utf-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace ();
        }
        int SourceGBK_Length=SourceTextGBK_Bytes.length;
        System.arraycopy (SourceTextGBK_Bytes, 0, mEscBuf, mEscLength, SourceGBK_Length);
        mEscLength+=SourceGBK_Length;
        return mEscLength;
    }

    /**
     * 启用黑标检测
     * Enable black mark detection
     *
     * @param bool
     * @throws IOException
     */
    public synchronized void printEnableMark(boolean bool){
        try {
            if (!check_paper) {
                return;
            }
            mEscBuf=new byte[64];
            mEscLength=0;
            // enable block mark
            mEscBuf[mEscLength++]=0x1F;
            mEscBuf[mEscLength++]=0x1B;
            mEscBuf[mEscLength++]=0x1F;
            mEscBuf[mEscLength++]=(byte) 0x80;
            mEscBuf[mEscLength++]=0x04;
            mEscBuf[mEscLength++]=0x05;
            mEscBuf[mEscLength++]=0x06;
            if (bool) {
                mEscBuf[mEscLength++]=0x44;
            } else {
                mEscBuf[mEscLength++]=0x66;
            }
            byte[] buffer=new byte[mEscLength];
            System.arraycopy (mEscBuf, 0, buffer, 0, mEscLength);
            print (buffer);
            sleep (50);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }

    /**
     * 转到下一个黑色标记
     * Go to next black mark
     *
     * @throws IOException
     */
    public synchronized void printGoToNextMark(){
        try {
            if (!check_paper) {
                return;
            }
            mEscBuf=new byte[2];
            mEscLength=0;
            // check mark
            mEscBuf[mEscLength++]=0x1D;
            mEscBuf[mEscLength++]=0x0C;
            print (mEscBuf);
            sleep (50);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }


    /**
     * 自动黑标
     *
     * @param bool
     */
    public synchronized void printAutoEnableMark(boolean bool) {
        try {
            if (!check_paper) {
                return;
            }
            mEscBuf=new byte[64];
            mEscLength=0;
            mEscBuf[mEscLength++]=0x1B;
            mEscBuf[mEscLength++]=0x23;
            mEscBuf[mEscLength++]=0x23;
            mEscBuf[mEscLength++]=(byte) 0x45;
            mEscBuf[mEscLength++]=0x41;
            mEscBuf[mEscLength++]=0x46;
            mEscBuf[mEscLength++]=0x42;
            if (bool) {
                mEscBuf[mEscLength++]=0x31; // 0x31为开启
            } else {
                mEscBuf[mEscLength++]=0x30;
            }
            byte[] buffer=new byte[mEscLength];
            System.arraycopy (mEscBuf, 0, buffer, 0, mEscLength);
            print (buffer);
            sleep (50);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }

    /**
     * 打印功能列表
     * Print function list
     *
     * @throws IOException
     */
    public synchronized void printFeatureList(){
        try {
            if (!check_paper) {
                return;
            }
            mWriter.write (0x1B);
            mWriter.write (0x23);
            mWriter.write (0x46);
            mWriter.flush ();
            sleep (50);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }

    /**
     * 重置打印机
     * reset printer
     *
     * @throws IOException
     */
    public synchronized void resetPrint(){
        try {
            if (!check_paper) {
                return;
            }
            mWriter.write (0x1B);
            mWriter.write (0x23);
            mWriter.write (0x23);
            mWriter.write (0x52);
            mWriter.write (0x54);
            mWriter.write (0x46);
            mWriter.write (0x41);
            mWriter.flush ();
            sleep (50);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }

    public synchronized void exitUnicode(){
        try {
            mEscBuf=new byte[64];
            mEscLength=0;

            mEscBuf[mEscLength++]=0x1B;
            mEscBuf[mEscLength++]=0x00; // 退出UNICODE需要添加次字节，并且编码设置为3
            mEscBuf[mEscLength++]=0x23;
            mEscBuf[mEscLength++]=0x23;
            mEscBuf[mEscLength++]=0x43;
            mEscBuf[mEscLength++]=0x44;
            mEscBuf[mEscLength++]=0x54;
            mEscBuf[mEscLength++]=0x59;
            mEscBuf[mEscLength++]=0x03;
            byte[] buffer=new byte[mEscLength];
            System.arraycopy (mEscBuf, 0, buffer, 0, mEscLength);
            print (buffer);
            sleep (50);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }

    /**
     * 获取打印固件版本
     *
     * @throws IOException
     */
    public synchronized void getVersion(){
        try {
            if (!check_paper) {
                return;
            }
            mWriter.write (0x1D);
            mWriter.write (0x49);
            mWriter.write (0x41);
            mWriter.flush ();
            sleep (50);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }


    public synchronized void printTwoColumn(String title, String content){
        try {
            int iNum=0;
            byte[] byteBuffer=new byte[100];
            byte[] tmp;

            tmp=getGbk (title);
            System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
            iNum+=tmp.length;

            tmp=setLocation (getOffset (content));
            System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
            iNum+=tmp.length;

            tmp=getGbk (content);
            System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);

            print (byteBuffer);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }

    public synchronized void printThreeColumn(String left, String middle, String right){
        try {
            int iNum=0;
            byte[] byteBuffer=new byte[200];
            byte[] tmp=new byte[0];

            System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
            iNum+=tmp.length;

            tmp=getGbk (left);
            System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
            iNum+=tmp.length;

            int pixLength=getStringPixLength (left) % WIDTH_PIXEL;
            if (pixLength > WIDTH_PIXEL / 2 || pixLength == 0) {
                middle="\n\t\t" + middle;
            }

            tmp=setLocation (192);
            System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
            iNum+=tmp.length;

            tmp=getGbk (middle);
            System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
            iNum+=tmp.length;

            tmp=setLocation (getOffset (right));
            System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
            iNum+=tmp.length;

            tmp=getGbk (right);
            System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);

            print (byteBuffer);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }

    public synchronized void printFourColumn(String left, String content1, String content2, String right){
        try {
            int iNum=0;
            byte[] byteBuffer=new byte[200];
            byte[] tmp=new byte[0];
            String newLeft="";
            String newLeft2="";

            System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
            iNum+=tmp.length;

            int pixLength=getStringPixLength (left);
            if (pixLength > WIDTH_PIXEL / 2) {
                newLeft=left.substring (0, left.length () / 3);
                newLeft2=left.substring (left.length () / 3, left.length ());
                tmp=getGbk (newLeft);
                System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
                iNum+=tmp.length;
            } else {
                tmp=getGbk (left);
                System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
                iNum+=tmp.length;
            }

            tmp=setLocation (190);
            System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
            iNum+=tmp.length;

            tmp=getGbk (content1);
            System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
            iNum+=tmp.length;

            if (isChinese (content2)) {
                tmp=setLocation (260);
                System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
                iNum+=tmp.length;
            } else {
                if (content2.length () > 2) {
                    tmp=setLocation (260);
                    System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
                    iNum+=tmp.length;
                } else {
                    tmp=setLocation (280);
                    System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
                    iNum+=tmp.length;
                }
            }
            tmp=getGbk (content2);
            System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
            iNum+=tmp.length;

            tmp=setLocation (getOffset (right));
            System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
            iNum+=tmp.length;

            tmp=getGbk (right);
            System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);

            if (!TextUtils.isEmpty (newLeft2)) {
                iNum+=tmp.length;
                tmp=getGbk ("\n" + newLeft2.trim ());
                System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
            }
            print (byteBuffer);
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }


    public synchronized void printDashLine(){
        printText ("--------------------------------");
    }

    public synchronized void printBitmap(final Bitmap bmp) {
        // bmp=BitmapUtils.compressPic (bmp);
        try {
            byte[] bmpByteArray=BitmapToByteUtils.draw2PxPoint (bmp);
            // Thread.sleep (time);
            print (bmpByteArray);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }

    public synchronized void printBitmap2(final Bitmap bmp){
        // bmp=BitmapUtils.compressPic (bmp);
        printImage (bmp);

    }

    /**
     * Name：printImage，Print the monochrome bitmap
     *
     * @param bitmap
     */
    public void printImage(Bitmap bitmap) {
        try {
            //Thread.sleep (time);
            int width=bitmap.getWidth ();
            int height=bitmap.getHeight ();
            byte[] data=new byte[]{0x1B, 0x33, 0x00};//Set the row spacing to 0.
            mOutputStream.write (data, 0, data.length);
            data[0]=(byte) 0x00;
            data[1]=(byte) 0x00;
            data[2]=(byte) 0x00;    //reset parameters

            int pixelColor;

            // ESC * m nL nH bitmap
            byte[] escBmp=new byte[]{0x1B, 0x2A, 0x00, 0x00, 0x00};
            escBmp[2]=(byte) 0x21;
            //nL, nH
            escBmp[3]=(byte) (width % 256);
            escBmp[4]=(byte) (bitmap.getWidth () / 256);

            // print each line
            for (int i=0; i < (height / 24) + 1; i++) {
                mOutputStream.write (escBmp, 0, escBmp.length);
                for (int j=0; j < width; j++) {
                    for (int k=0; k < 24; k++) {
                        if (((i * 24) + k) < height) {
                            pixelColor=bitmap.getPixel (j, (i * 24) + k);
                            if (pixelColor != -1) {
                                data[k / 8]+=(byte) (128 >> (k % 8));
                            }
                        }
                    }
                    mOutputStream.write (data, 0, data.length);
                    //reset parameters
                    data[0]=(byte) 0x00;
                    data[1]=(byte) 0x00;
                    data[2]=(byte) 0x00;

                }
                //out
//                byte[] byte_send1=new byte[3];
//                byte_send1[0]=0x1B;
//                byte_send1[1]=0x4A;
//                byte_send1[2]=0x00;
                mOutputStream.write ("\n".getBytes ());
            }
        } catch (IOException e) {
            e.printStackTrace ();
        } finally {
            try {
                mOutputStream.flush ();
            } catch (IOException e) {
                e.printStackTrace ();
            }
        }
    }


    public void closeDev() {

        if (mWriter != null) {
            mWriter=null;
            mOutputStream=null;
            mInputStream=null;
        }
        if (singleCase != null) {
            singleCase=null;
        }
        SerialManager.getClient ().close ();
        bRet=false;
    }


    //================================================================= psam=======================================

    /**
     * psam卡复位
     *
     * @param type
     * @param data
     * @return
     * @throws IOException
     */
    public synchronized int resetPsam(int type, byte[] data) {
        try {
            mEscBuf=new byte[1024];
            mEscLength=0;

            mEscBuf[mEscLength++]=0x1B;
            mEscBuf[mEscLength++]=0x23;
            mEscBuf[mEscLength++]=0x23;
            mEscBuf[mEscLength++]=0x50;
            mEscBuf[mEscLength++]=0x53;
            mEscBuf[mEscLength++]=0x41;
            mEscBuf[mEscLength++]=0x4D;
            if (type == 1) {
                mEscBuf[mEscLength++]=0x31;
                mEscBuf[mEscLength++]=0x00;
            } else if (type == 2) {
                mEscBuf[mEscLength++]=0x32;
                mEscBuf[mEscLength++]=0x00;
            }
            byte[] buffer=new byte[mEscLength];
            System.arraycopy (mEscBuf, 0, buffer, 0, mEscLength);
            print (buffer);
            sleep (10);
            int length=SerialManager.getClient ().receivePsam (data, 500);
            return length;
        } catch (IOException e) {
            Log.i (TAG, "resetPsam: " + e.getMessage ());
        }
        return 0;
    }

    /**
     * 发送apdu指令
     *
     * @param type    卡类型
     * @param apduHex 发送指令
     * @param data    返回数据
     * @return
     * @throws IOException
     */
    public synchronized int sendApdu(int type, String apduHex, byte[] data) {
        int resLength=0;
        byte[] cmd=ByteUtils.HexString2Bytes (apduHex);
        int tmpLength=(apduHex.length () / 2);

        resLength=executeAndResponse (type, data, cmd, tmpLength);
        if (resLength != 0) {
            if (data[0] == 0x61) {
                byte[] tmpCmd=new byte[]{0x00, (byte) 0xC0, 0x00, 0x00, 0x00, 0x00};
                tmpCmd[5]=data[1];
                resLength=executeAndResponse (type, data, tmpCmd, tmpCmd.length);
            }
        }
        return resLength;
    }


    /**
     * psam指令执行与返回
     *
     * @param type      卡类型
     * @param response  返回数据
     * @param cmd       发送的指令
     * @param cmdLength 指令长度
     * @return
     */
    private int executeAndResponse(int type, byte[] response, byte[] cmd, int cmdLength) {
        try {
            mEscBuf=new byte[1024];
            mEscLength=0;
            if (type == 1) {
                mEscBuf[mEscLength++]=0x1B;
                mEscBuf[mEscLength++]=0x23;
                mEscBuf[mEscLength++]=0x23;
                mEscBuf[mEscLength++]=0x50;
                mEscBuf[mEscLength++]=0x53;
                mEscBuf[mEscLength++]=0x41;
                mEscBuf[mEscLength++]=0x4D;
                mEscBuf[mEscLength++]=0x31;
                mEscBuf[mEscLength++]=(byte) cmdLength;
                for (int i=0; i < cmdLength; i++) {
                    mEscBuf[mEscLength++]=cmd[i];
                }
            } else if (type == 2) {
                mEscBuf[mEscLength++]=0x1B;
                mEscBuf[mEscLength++]=0x23;
                mEscBuf[mEscLength++]=0x23;
                mEscBuf[mEscLength++]=0x50;
                mEscBuf[mEscLength++]=0x53;
                mEscBuf[mEscLength++]=0x41;
                mEscBuf[mEscLength++]=0x4D;
                mEscBuf[mEscLength++]=0x31;
                mEscBuf[mEscLength++]=(byte) cmdLength;
                for (int i=0; i < cmdLength; i++) {
                    mEscBuf[mEscLength++]=cmd[i];
                }
            }
            byte[] buffer=new byte[mEscLength];
            System.arraycopy (mEscBuf, 0, buffer, 0, mEscLength);
            print (buffer);
            sleep (10);
            int length=SerialManager.getClient ().receivePsam (response, 500);
            return length;
        } catch (IOException e) {
            e.printStackTrace ();
        }
        return 0;
    }

    // ================================================================ 串口接收数据部分 ===================================================

    @Override
    public void onDataReceive(final byte[] buffer, final int size) {

        Log.e (TAG, "onDataReceive: " + ByteUtils.Bytes2HexString (buffer) + " 大小 " + size);
        if (mListener == null) {
            Log.e (TAG, "OnPrintEventListener is null");
            return;
        }
        mHandler.post (new Runnable () {
            @Override
            public void run() {
                if (buffer[0] == 0x39) {
                    try {
                        String version=new String (buffer, 0, buffer.length, "ISO-8859-1");
                        mListener.onVersion (version);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace ();
                    }
                } else if (buffer[0] == 0x65) {
                    try {
                        String ASCII=new String (buffer, "ascii");
                        mListener.onTemperature (ASCII);
                    } catch (Exception e) {
                        e.printStackTrace ();
                    }
                } else if (size > 2 && buffer[2] == 0x0C) {
                    try {
                        Log.e (TAG, "run: no_paper");
                        check_paper=false;
                        mListener.onPrintStatus (1); // no_paper
                    } catch (Exception e) {
                        e.printStackTrace ();
                    }
                } else if (size > 2 && buffer[2] == 0x00) {
                    check_paper=true;
                } else if (size > 2 && buffer[1] == 0x40) {
                    try {
                        Log.e (TAG, "run: error");
                        mListener.onPrintStatus (2); // error
                    } catch (Exception e) {
                        e.printStackTrace ();
                    }
                } else if (size == 7) {
                    try {
                        Log.e (TAG, "run: success");
                        mListener.onPrintStatus (0); // success
                    } catch (Exception e) {
                        e.printStackTrace ();
                    }
                }
            }
        });
    }


    public void setPrintEventListener(OnPrintEventListener printEventListener) {
        this.mListener=printEventListener;
    }
}