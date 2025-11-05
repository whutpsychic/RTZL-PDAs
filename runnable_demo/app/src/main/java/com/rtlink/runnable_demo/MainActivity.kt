package com.rtlink.runnable_demo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.device.ScanManager
import android.device.scanner.configuration.PropertyID
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var textView: TextView? = null
    private lateinit var strs: Array<String>
    private var mAction: String = ScanManager.ACTION_DECODE //"android.provider.sdlMessage";
    private var mKey: String = ScanManager.BARCODE_STRING_TAG
    private val mScanManager: ScanManager = ScanManager()
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.tv_code_type)
        btnStart = findViewById(R.id.btn_scan_start)
        btnStop = findViewById(R.id.btn_scan_stop)

        mScanManager.switchOutputMode(0) //0：开启广播模式   1：键盘模式

        btnStart.setOnClickListener(View.OnClickListener { //
            mScanManager.startDecode()
        })
        btnStop.setOnClickListener(View.OnClickListener { mScanManager.stopDecode() })

        try {
            mScanManager.addStatusActionListener { event, bundle ->
                //TODO  event   4:开始出光扫描     3：松开按键（停止扫描）  2：扫描超时
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        realTimeAction
        registerReceiver(true)
    }

    private val realTimeAction: Unit
        /**
         * 实时获取扫描头 ACTION 以及 key 参数
         */
        get() {
            strs = ScanManager().getParameterString(
                intArrayOf(
                    PropertyID.WEDGE_INTENT_ACTION_NAME,
                    PropertyID.WEDGE_INTENT_DATA_STRING_TAG
                )
            )
            mAction = strs[0] //实时获取的action
            mKey = strs[1] //实时获取的key
        }

    override fun onPause() {
        super.onPause()
        registerReceiver(false)
    }

    /**
     * @param register , ture register , false unregister
     */
    private fun registerReceiver(register: Boolean) {
        if (register) {
            val filter = IntentFilter()
            filter.addAction(mAction)
            registerReceiver(mReceiver, filter)
        } else {
            unregisterReceiver(mReceiver)
        }
    }


    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == mAction) {
                val barcodeStr = intent.getStringExtra(mKey) //barcode value  获取扫描的条码数据
                textView!!.text = barcodeStr
            }
        }
    }
}