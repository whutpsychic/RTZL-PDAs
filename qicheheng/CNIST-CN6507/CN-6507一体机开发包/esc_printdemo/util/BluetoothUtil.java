package com.example.esc_printdemo.util;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BluetoothUtil {

    /**
     * 蓝牙是否打开
     * Whether Bluetooth is on
     */
    public static boolean isBluetoothOn() {
        BluetoothAdapter mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter ();
        if (mBluetoothAdapter != null)
            // 蓝牙已打开
            if (mBluetoothAdapter.isEnabled ())
                return true;

        return false;
    }

    /**
     * 获取所有已配对的设备
     * Get all paired devices
     */
    public static List<BluetoothDevice> getPairedDevices() {
        List deviceList=new ArrayList<> ();
        Set<BluetoothDevice> pairedDevices=BluetoothAdapter.getDefaultAdapter ().getBondedDevices ();
        if (pairedDevices.size () > 0) {
            for (BluetoothDevice device : pairedDevices) {
                deviceList.add (device);
            }
        }
        return deviceList;
    }

    /**
     * 获取所有已配对的打印类设备
     * Get all paired printing devices
     */
    public static List<BluetoothDevice> getPairedPrinterDevices() {
        return getSpecificDevice (BluetoothClass.Device.Major.IMAGING);
    }

    /**
     * 从已配对设配中，删选出某一特定类型的设备展示
     * Select a specific type of device display from the paired configuration
     *
     * @param deviceClass
     * @return
     */
    public static List<BluetoothDevice> getSpecificDevice(int deviceClass) {
        List<BluetoothDevice> devices=BluetoothUtil.getPairedDevices ();
        List<BluetoothDevice> printerDevices=new ArrayList<> ();

        for (BluetoothDevice device : devices) {
            BluetoothClass klass=device.getBluetoothClass ();
            // 关于蓝牙设备分类参考 http://stackoverflow.com/q/23273355/4242112
            if (klass.getMajorDeviceClass () == deviceClass)
                printerDevices.add (device);
        }

        return printerDevices;
    }

    /**
     * 弹出系统对话框，请求打开蓝牙
     * A system dialog box pops up, requesting to turn on Bluetooth
     */
    public static void openBluetooth(Activity activity) {
        Intent enableBtIntent=new Intent (BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult (enableBtIntent, 666);
    }

    private static final UUID CONNECT_UUID=UUID.fromString ("00001101-0000-1000-8000-00805F9B34FB");

    public static BluetoothSocket connectDevice(BluetoothDevice device) {
        BluetoothSocket socket=null;
        try {
            int sdk=Build.VERSION.SDK_INT;
            if (sdk >= 10) {
                socket=device.createInsecureRfcommSocketToServiceRecord (CONNECT_UUID);
            } else {
                socket=device.createRfcommSocketToServiceRecord (CONNECT_UUID);
            }
            socket.connect ();
        } catch (IOException e) {
            Log.e ("BluetoothUtil", "connectDevice: " + e.getLocalizedMessage ());
            try {
                //socket.close();
                Thread.sleep (500);
                socket.connect ();
            } catch (IOException closeException) {
                return null;
            } catch (InterruptedException ex) {
                ex.printStackTrace ();
            }
            return null;
        }
        return socket;
    }


}