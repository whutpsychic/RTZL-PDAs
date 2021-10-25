package com.example.dupkttest;

import android.content.Context;
import android.device.MagManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.HashMap;

public class MagReadService {
    
    public final static int MESSAGE_OPEN_MAG = 1;
    public final static int MESSAGE_CHECK_FAILE = 2;
    public final static int MESSAGE_READ_MAG = 3;
    public final static int MESSAGE_CHECK_OK = 4;
    public final static String CARD_TRACK1 = "track1";
    public final static String CARD_NUMBER = "number";
    public final static String CARD_TRACK2 = "track2";
    public final static String CARD_TRACK3 = "track3";
    public final static String CARD_KSN = "KSN";
    
    private Context mContext;
    private Handler mHandler;
    private MagManager magManager;
    private MagReaderThread magReaderThread;
    private static final int DEFAULT_TAG =1;
    private byte[] magBuffer = new byte[1024];
    public MagReadService(Context context, Handler handler) {
        mHandler = handler;
        mContext = context;
        magManager = new MagManager();
    }
    
    // 从字节数组到十六进制字符串转换
    public static String Bytes2HexString(byte[] b) {
        String ret = "";

        String hex = "";
        for (int i = 0; i < b.length; i++) {
            hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            // ret.append(hex.toUpperCase());
            ret += hex.toUpperCase();
        }

        return ret;
    }
    
    public synchronized void start() {
        
        if(magReaderThread != null) {
            magReaderThread.stopMagReader();
            magReaderThread = null;
        }
        magReaderThread = new MagReaderThread("reader--" + DEFAULT_TAG);
        magReaderThread.start();
    }
    
    public synchronized void stop() {
        if(magManager != null) {
            magManager.close();
            //magManager = null;
        }
        if(magReaderThread != null) {
            magReaderThread.stopMagReader();
            magReaderThread = null;
        }
        
        
    }
    
    private class MagReaderThread extends Thread {
        private boolean running = true;

        private boolean isValid;

        public MagReaderThread(String name) {
            super(name);
            running = true;
        }

        public void stopMagReader() {
            running = false;
        }

        public void run() {
            if (magManager != null) {
                int ret = magManager.open();
                if (ret != 0) {
                    mHandler.sendEmptyMessage(MESSAGE_OPEN_MAG);
                    return;
                }
            }
            while (running) {
                int size = 0;
                if (magManager == null)
                    return;
                int ret = magManager.checkCard();
                if (ret != 0) {
                    mHandler.sendEmptyMessage(MESSAGE_CHECK_FAILE);
                    try {
                        Thread.sleep(600);
                    } catch (Exception e) {
                    }
                    continue;
                } else {
                    mHandler.sendEmptyMessage(MESSAGE_CHECK_OK);
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                    }
                }
                StringBuffer trackOne = new StringBuffer();
                byte[] stripInfo = new byte[1024];
                byte[] CardNo = new byte[20];
                byte[] KSN = new byte[10];
                int allLen = magManager.getEncryptStripInfo(1, 3, stripInfo, CardNo, KSN);
                Log.d("MagReadService", "getAllStripInfo = " + allLen);
                if (allLen > 0) {
                    mHandler.removeMessages(MESSAGE_CHECK_FAILE);
                    Message msg = mHandler.obtainMessage(MESSAGE_READ_MAG);
                    Bundle bundle = new Bundle();
                    bundle.putString(CARD_NUMBER, (new String(CardNo).trim()));
                    bundle.putByteArray(CARD_KSN, KSN);
                    Log.d("MagReadService", "getAllStripInfo = " + (new String(CardNo).trim()));
                    Log.d("MagReadService", "getAllStripInfo = " + DecodeConvert.bytesToHexString(stripInfo, 0, allLen));
                    int len = stripInfo[1];

                    byte[] CARD_TRACK_1;
                    byte[] CARD_TRACK_2;
                    byte[] CARD_TRACK_3;
                    bundle.putInt("CARD_TRACK1_L",len );
                    if (len != 0) {
                        CARD_TRACK_1 = new byte[len];
                        System.arraycopy(stripInfo, 2, CARD_TRACK_1, 0, len);
                        bundle.putByteArray(CARD_TRACK1, CARD_TRACK_1);
                    }
                    int len2 = stripInfo[3 + len];
                    bundle.putInt("CARD_TRACK2_L",len2 );
                    if (len2 != 0) {
                        CARD_TRACK_2 = new byte[len2];
                        System.arraycopy(stripInfo, 4 + len, CARD_TRACK_2, 0, len2);
                        bundle.putByteArray(CARD_TRACK2, CARD_TRACK_2);
                    }
                    int len3 = stripInfo[5 + len+len2];
                    Log.d("MagReadService", "getAllStripInfo len= " + len + " len2= " + len2+ " len3= " + len3);
                    bundle.putInt("CARD_TRACK3_L",len3 );
                    if (len3 != 0 && len3 < 1024) {
                        CARD_TRACK_3 = new byte[len3];
                        bundle.putString(CARD_TRACK3, new String(stripInfo, 6 + len + len2, len3));

                        System.arraycopy(stripInfo, 6 + len + len2, CARD_TRACK_3, 0, len3);
                        bundle.putByteArray(CARD_TRACK3, CARD_TRACK_3);
                    }
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                    trackOne = null;
                }
                try {
                    Thread.sleep(800);
                } catch (Exception e) {
                }
            }
           
        }
    }
}
