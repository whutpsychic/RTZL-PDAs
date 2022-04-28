package com.example.esc_printdemo.ui;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.esc_printdemo.PrintContract;
import com.example.esc_printdemo.R;
import com.example.esc_printdemo.util.BluetoothUtil;
import com.example.esc_printdemo.util.buttonUtils;
import com.example.print_sdk.util.ByteUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class BluetoothActivity extends BluetoothBasePrintActivity implements View.OnClickListener {

    private static final String TAG=BluetoothActivity.class.getName ();
    ListView mLvPairedDevices;
    Button mBtnSetting;
    Button mBtnTest;
    Button mBtnPrint;

    DeviceListAdapter mAdapter;
    int mSelectedPosition=-1;

    final static int TASK_TYPE_CONNECT=1;
    final static int TASK_TYPE_PRINT=2;
    private InputStream mInputStream;

    private Handler handler=new Handler ();

    Runnable readRun=new Runnable () {
        @Override
        public void run() {
            while (true) {
                int size;
                try {
                    if (mInputStream == null)
                        return;
                    int ret=mInputStream.available ();
                    Thread.sleep (15);
                    byte[] buffer=new byte[ret];
                    if (ret > 0) {
                        size=mInputStream.read (buffer);
                        if (size > 0) {
                            Log.e (TAG, "run: " + ByteUtils.Bytes2HexString (buffer));
                            Toast.makeText (BluetoothActivity.this, getString (R.string.total_07), Toast.LENGTH_SHORT).show ();
                            handler.removeCallbacks (readRun);
                            return;
                        }
                    }
                    Thread.sleep (15);
                } catch (Exception e) {
                    e.printStackTrace ();
                    return;
                }
            }
        }
    };

    public static boolean setBluetooth(boolean enable) {
        BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter ();
        boolean isEnabled=bluetoothAdapter.isEnabled ();
        if (enable && !isEnabled) {
            return bluetoothAdapter.enable ();
        } else if (!enable && isEnabled) {
            return bluetoothAdapter.disable ();
        }
        // No need to change bluetooth state
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_bluetooth);
       // setBluetooth (true);
        if (!BluetoothUtil.isBluetoothOn ()) {
            AlertDialog dialog=new AlertDialog.Builder (this).setTitle (getString (R.string.dialog_03))
                    .setMessage (getString (R.string.dialog_02))
                    .setPositiveButton (getString (R.string.dialog_04), new DialogInterface.OnClickListener () {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            startActivity (new Intent (Settings.ACTION_BLUETOOTH_SETTINGS));

                        }
                    }).create ();
            dialog.show ();
        }
        initViews ();
    }

    @Override
    protected void onResume() {
        super.onResume ();
        Log.e (TAG, "onResume: 1");
        fillAdapter ();
    }

    private void initViews() {

        mLvPairedDevices=(ListView) findViewById (R.id.lv_paired_devices);
        mBtnSetting=(Button) findViewById (R.id.btn_goto_setting);
        mBtnTest=(Button) findViewById (R.id.btn_test_conntect);
        mBtnPrint=(Button) findViewById (R.id.btn_print);

        mLvPairedDevices.setOnItemClickListener (new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedPosition=position;
                mAdapter.notifyDataSetChanged ();
            }
        });

        mBtnSetting.setOnClickListener (this);
        mBtnTest.setOnClickListener (this);
        mBtnPrint.setOnClickListener (this);
        mAdapter=new DeviceListAdapter (this);
        mLvPairedDevices.setAdapter (mAdapter);

    }

    /**
     * 从所有已配对设备中找出打印设备并显示
     * Find and display the printing device from all paired devices
     */
    private void fillAdapter() {
        //推荐使用 BluetoothUtil.getPairedPrinterDevices()
        if (BluetoothUtil.isBluetoothOn ()) {
            List<BluetoothDevice> printerDevices=BluetoothUtil.getPairedDevices ();
            Log.e (TAG, "fillAdapter: " + printerDevices.size ());
            mAdapter.clear ();
            mAdapter.addAll (printerDevices);
            refreshButtonText (printerDevices);
            mAdapter.notifyDataSetChanged ();
        }
    }

    private void refreshButtonText(List<BluetoothDevice> printerDevices) {
        if (printerDevices.size () > 0) {
            mBtnSetting.setText (getString (R.string.total_01));
        } else {
            mBtnSetting.setText (getString (R.string.total_02));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId ()) {
            case R.id.btn_goto_setting:
                startActivity (new Intent (Settings.ACTION_BLUETOOTH_SETTINGS));
                break;
            case R.id.btn_test_conntect:
                connectDevice (TASK_TYPE_CONNECT);
                break;
            case R.id.btn_print:
                if (!buttonUtils.isButtonFastClick ()) {
                    Toast.makeText (this, getString (R.string.toast_chick), Toast.LENGTH_SHORT).show ();
                    return;
                }
                connectDevice (TASK_TYPE_PRINT);
                break;
        }
    }

    private void connectDevice(int taskType) {
        if (mSelectedPosition >= 0) {
            BluetoothDevice device=mAdapter.getItem (mSelectedPosition);
            if (device != null)
                super.connectDevice (device, taskType);
        } else {
            Toast.makeText (this, getString (R.string.total_05), Toast.LENGTH_SHORT).show ();
        }
    }

    int number=1000000001; // 初始化票据流水号 Initial bill serial number

    @Override
    public void onConnected(BluetoothSocket socket, int taskType) {
        try {
            if (socket!=null){
                mInputStream=socket.getInputStream ();
                switch (taskType) {
                    case TASK_TYPE_PRINT:
                        handler.post (readRun);
                        printUtil.setSocket (socket,"GB2312");
                        PrintContract printContract=new PrintContract (this,printUtil);

                        printContract.printInit ();
                        printContract.printText (number,"39");
                        break;
                }
            }else{
                runOnUiThread (new Runnable () {
                    @Override
                    public void run() {
                        Toast.makeText (BluetoothActivity.this, "请在设置---》个性化里打开虚拟蓝牙", Toast.LENGTH_SHORT).show ();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }


    class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {

        public DeviceListAdapter(Context context) {
            super (context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            BluetoothDevice device=getItem (position);
            if (convertView == null) {
                convertView=LayoutInflater.from (getContext ()).inflate (R.layout.item_bluetooth_device, parent, false);
            }

            TextView tvDeviceName=(TextView) convertView.findViewById (R.id.tv_device_name);
            CheckBox cbDevice=(CheckBox) convertView.findViewById (R.id.cb_device);

            tvDeviceName.setText (device.getName ());
            cbDevice.setChecked (position == mSelectedPosition);

            return convertView;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy ();
        if (handler != null) {
            handler.removeCallbacks (readRun);
        }
    }
}
