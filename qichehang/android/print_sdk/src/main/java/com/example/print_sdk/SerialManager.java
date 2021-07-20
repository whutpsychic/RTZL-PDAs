package com.example.print_sdk;

import android.util.Log;

import com.example.print_sdk.util.ByteUtils;
import com.lcserial.www.SerialPort;
import com.lcserial.www.SerialPortTool;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;



/**
 * Created by moxiaomo
 * on 2020/4/1
 */
public class SerialManager {

    private final static String TAG="SerialManager";

    public static final String PATH="/dev/ttyS2";
    public static final int BAUTRATE=115200;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    protected SerialPortTool serialPortTool;
    protected SerialPort mSerialPort;

    private ReadThread mReadThread;
    private boolean isStop=false;
    private boolean isPause=true;
    private OnDataReceiveListener onDataReceiveListener=null;


    private enum Singleton {
        INSTANCE;

        private final SerialManager client;

        Singleton() {
            client=new SerialManager ();
        }

        private SerialManager getInstance() {
            return client;
        }
    }

    public static SerialManager getClient() {

        return Singleton.INSTANCE.getInstance ();
    }

    public interface OnDataReceiveListener {
        public void onDataReceive(byte[] buffer, int size);
    }

    public void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener=dataReceiveListener;
    }

    /**
     * Name：open Function:open printer
     *
     * @return true：sucessful；false：failed
     */
    public boolean open() {
        if (!poweron ())
            return false;
        boolean bRet=true;
        if (serialPortTool == null)
            serialPortTool=new SerialPortTool ();
        try {
            mSerialPort=serialPortTool.getSerialPort (PATH, BAUTRATE);
            mOutputStream=mSerialPort.getOutputStream ();
            mInputStream=mSerialPort.getInputStream ();
            bRet=true;
        } catch (SecurityException e) {

        } catch (IOException e) {

        } catch (InvalidParameterException e) {

        } catch (Exception e) {

        }
        return bRet;
    }

    public void startReadThread() {
        mReadThread=new ReadThread ();
        isStop=false;
        mReadThread.start ();
    }

    /**
     * Name：close; Function:close printer
     */
    public void close() {
        isStop=true;
        if (mReadThread != null) {
            mReadThread.interrupt ();
        }
        if (serialPortTool != null)
            serialPortTool.closeSerialPort ();
        mSerialPort=null;
        poweroff ();
    }



    public static String Bytes2HexString(byte[] b) {
        String ret="";

        for (int i=0; i < b.length; ++i) {
            String hex=Integer.toHexString (b[i] & 255);
            if (hex.length () == 1) {
                hex='0' + hex;
            }

            ret=ret + " " + hex.toUpperCase ();
        }
        return ret;
    }

    /**
     * Name：escCommand
     * Function:send ESC command
     *
     * @param cmd
     * @return
     */
    public boolean escCommand(String cmd) {
        if (cmd == null || cmd.length () == 0)
            return false;

        byte[] buffer=cmd.getBytes ();
        return escCommand (buffer);
    }

    public boolean escCommand(byte[] cmd) {
        Log.e (TAG, "escCommand: " + Bytes2HexString (cmd));
        boolean bRet=true;
        try {
            if (cmd != null && cmd.length > 0) {
                mOutputStream.write (cmd);
            }
        } catch (Exception ex) {
            bRet=false;
        }
        return bRet;
    }

    public boolean escCommand(byte[] cmd, int count) {
        boolean bRet=true;
        try {
            if (cmd != null && cmd.length > 0) {
                mOutputStream.write (cmd, 0, count);
            }
        } catch (Exception ex) {
            bRet=false;
            Log.e ("escCommand", ex.getLocalizedMessage ());
        }
        return bRet;
    }

    public InputStream getInputStream() {
        return mInputStream;
    }

    public OutputStream getOutputStream() {
        return mOutputStream;
    }

    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run ();
            while (!isStop && !isInterrupted ()) {
                // Log.i (TAG, "run: 线程运行中" + isPause);
                if (isPause) {
                    int size;
                    try {
                        if (mInputStream == null)
                            return;
                        int ret=mInputStream.available ();
                        Thread.sleep (15);
                        byte[] buffer=new byte[ret];
                        if (ret > 0) {
                            size=mInputStream.read (buffer);
                            if (null != onDataReceiveListener) {
                                Log.e (TAG, "run: " + buffer);
                                onDataReceiveListener.onDataReceive (buffer, size);
                            }
                        }
                        Thread.sleep (15);
                    } catch (Exception e) {
                        e.printStackTrace ();
                        return;
                    }
                } else {
                    // Log.i (TAG, "run: 线程未运行");
                }

            }
        }
    }


    /**
     * 读psam数据
     *
     * @param data
     * @param timeout
     * @return
     */
    public int receivePsam(byte[] data, long timeout) {
        isPause=false;
        try {
            Thread.sleep (100);
        } catch (Exception e) {

        }

        byte[] bytes=new byte[1024];
        int tmpLength=0;
        int timeoutAdder=0;
        while (true) {
            ++timeoutAdder;
            if (timeoutAdder >= timeout) {
                isPause=true;
                break;
            }
            try {
                int av=this.mInputStream.available ();
                Thread.sleep (50);
                if (av != 0) {
                    int readLength=this.mInputStream.read (bytes);
                    if (bytes[0] != 0x2D && bytes[0] != 0x73) {
                        if (readLength > 0) {
                            tmpLength+=readLength;
                            System.arraycopy (bytes, 0, data, 0, tmpLength);
                            Log.i ("SerialManager", "receivePsam: " + ByteUtils.Bytes2HexString (bytes));
                            break;
                        }

                        Thread.sleep (5L);
                    }
                } else {
                    Thread.sleep (5L);
                }
            } catch (IOException e) {
                e.printStackTrace ();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace ();
                break;
            } catch (Exception e) {
                e.printStackTrace ();
                break;
            }
        }
        if (tmpLength != 0) {
            isPause=true;
        }
        return tmpLength;
    }

    String extvcc="/proc/gpiocontrol/extvcc";
    String setSam="/proc/gpiocontrol/set_sam";

    private boolean poweron() {
        boolean bRet=true;
        try {
            FileWriter localFileWriterOn=new FileWriter (new File (setSam));
            //localFileWriterOn.write ("3v3 1");
            localFileWriterOn.write ("1");
            localFileWriterOn.close ();
            Thread.sleep (300);
        } catch (Exception e) {
            e.printStackTrace ();
            bRet=false;
        }
        return bRet;
    }


    private void poweroff() {
        try {
            FileWriter localFileWriterOff=new FileWriter (new File (setSam));
           // localFileWriterOff.write ("3v3 0");
            localFileWriterOff.write ("0");
            localFileWriterOff.close ();
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
