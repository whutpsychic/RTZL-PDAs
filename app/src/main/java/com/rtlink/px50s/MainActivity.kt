package com.rtlink.px50s

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.rtlink.px50s.activities.WebViewActivity

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 沉浸式渲染
        enableEdgeToEdge()
        // 显示主入口页面
        setContentView(R.layout.activity_main)

        nextStep()
    }

    private fun nextStep() {
        val currDev = Intent(this, WebViewActivity::class.java)
        startActivity(currDev)
    }

}
