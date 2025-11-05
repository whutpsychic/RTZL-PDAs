package com.rtlink.px50s.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.rtlink.px50s.GlobalConfig.Companion.WEB_URL
import com.rtlink.px50s.R
import com.rtlink.px50s.utils.LocalStorage

class WebViewIPConfigActivity : ComponentActivity() {

    // init
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 显示注册的页面
        setContentView(R.layout.activity_webview_ipconfig)

        // 解析默认的地址，并加载至表单显示
        analyzeInitUrl()

        // 绑定按钮点击事件
        val resetBtn: Button = findViewById(R.id.reset)
        val okBtn: Button = findViewById(R.id.go)

        // 将本地存储的web地址转变为默认地址
        resetBtn.setOnClickListener {
            val localStorage = LocalStorage(this)
            localStorage.write("weburl", WEB_URL)
            analyzeInitUrl()
        }

        // 存储本地地址并带着结果返回
        okBtn.setOnClickListener {
            val url = getCurrUrl()

            val intent = Intent().putExtra("url", url)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun analyzeInitUrl() {
        // 先从本地存储读取
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        val localUrl = sharedPref.getString("weburl", WEB_URL)

        // 以 :// 为界分割成数组取第一个匹配为http或https值
        val resultArr = localUrl?.split("://")

        val httpStr = resultArr?.get(0)
        val urlStr = resultArr?.get(1)

        val httpRadio: RadioButton = findViewById(R.id.http)
        val httpsRadio: RadioButton = findViewById(R.id.https)
        // 设置 http
        if (httpStr == "http") {
            httpRadio.isChecked = true
            httpsRadio.isChecked = false
        } else if (httpStr == "https") {
            httpRadio.isChecked = false
            httpsRadio.isChecked = true
        }

        // 设置进input
        val input: EditText = findViewById(R.id.input)
        input.setText(urlStr)

        // 显示最终结果
        val des: TextView = findViewById(R.id.des)
        des.text = localUrl
    }

    private fun getCurrHttpType(): String {
        val httpsBtn: RadioButton = findViewById(R.id.https)
        if (httpsBtn.isChecked) {
            return "https"
        }
        return "http"
    }

    private fun getCurrUrl(): String {
        val httpStr = getCurrHttpType()
        val inputEl: EditText = findViewById(R.id.input)
        val urlStr = inputEl.text

        return "$httpStr://$urlStr"
    }

}
