package com.rtlink.runnable_demo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GetBitmapActivity : AppCompatActivity() {
    private val action_scanner_capture_image_result = "scanner_capture_image_result"

    private var imageView: ImageView? = null
    private lateinit var btn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_bitmap)
        imageView = findViewById(R.id.iv_bitmap)
        btn = findViewById(R.id.btn_get_bitmap)

        btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                System.out.println(" ------------------------------------ ")
//                this.bitmap
            }
        })

        //        //开启或关闭 [应用标识符 ( GS1 、DataMatrix Code128 )]
//        int [] keyInt = new int[]{PropertyID.LABEL_SEPARATOR_ENABLE};
//        int [] valueInt = new int[]{1};//0：关闭   1：开启
//        new ScanManager().setPropertyInts(keyInt,valueInt);
    }

    private val bitmap: Unit
        get() {
            //发送广播---取图
            val action = "action.scanner_capture_image"
            val intentImage = Intent(action)
            sendBroadcast(intentImage)
        }

    override fun onResume() {
        super.onResume()
        setReceiverRegister()
    }


    override fun onPause() {
        super.onPause()
        unregisterReceiver(mGetBitmapReceiver) //注销广播
    }


    /**
     * 广播接收器
     */
    private val mGetBitmapReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == action_scanner_capture_image_result) {
                val bmp = intent.getByteArrayExtra("bitmapBytes")
                if (bmp != null && bmp.size > 1) {
                    val mBitmap = BitmapFactory.decodeByteArray(bmp, 0, bmp.size)
                        .copy(Bitmap.Config.RGB_565, true)
                    imageView!!.setImageBitmap(mBitmap)
                } else {
                    Toast.makeText(this@GetBitmapActivity, "获取图片失败 ", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    /**
     * 注册广播
     */
    private fun setReceiverRegister() {
        try {
            val filter = IntentFilter()
            filter.addAction(action_scanner_capture_image_result)
            registerReceiver(mGetBitmapReceiver, filter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}