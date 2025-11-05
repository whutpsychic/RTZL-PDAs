package com.rtlink.px50s.webIO

import android.os.Build
import android.webkit.WebView
import androidx.activity.ComponentActivity
import com.rtlink.px50s.GlobalConfig.Companion.RAM_NAME
import com.rtlink.px50s.webIO.CallbackKeys.Companion.GET_DEVICE_INFO

fun collectDeviceInfo(activity: ComponentActivity, webView: WebView?) {

    // 设备ID
    val deviceId: String? = Build.ID
    // 设备原标识码
    val device: String? = Build.DEVICE
    // 设备型号码
    val model: String? = Build.MODEL
    // 设备品牌 / 设备制造商
    val brand: String? = Build.BRAND
    // API级别
    val sdk: Int = Build.VERSION.SDK_INT
    // Android版本
    val androidVersion = getAndroidVersionByApiLevel(sdk)

    val result: String =
        "{deviceId:'${deviceId}',device:'${device}',model:'${model}',brand:'${brand}',sdk:${sdk},Android_Version:'${androidVersion}'}"

    activity.runOnUiThread {
        webView?.evaluateJavascript(
            "$RAM_NAME.callback.$GET_DEVICE_INFO($result)",
            null
        )
    }
}

fun getAndroidVersionByApiLevel(lvl: Int): String {
    when (lvl) {
        // ------------- 主要版本 -------------
        35 -> {
            return "15"
        }

        34 -> {
            return "14"
        }

        33 -> {
            return "13"
        }

        31, 32 -> {
            return "12"
        }

        30 -> {
            return "11"
        }

        29 -> {
            return "10"
        }

        28 -> {
            return "9"
        }
        // ------------- 非主要版本 -------------
        27 -> {
            return "8.1"
        }

        26 -> {
            return "8"
        }

        25 -> {
            return "7.1"
        }

        24 -> {
            return "7"
        }

        23 -> {
            return "6.0"
        }

        22 -> {
            return "5.1"
        }

        21 -> {
            return "5.0"
        }

        20 -> {
            return "4.4W"
        }

        19 -> {
            return "4.4"
        }

        18 -> {
            return "4.3"
        }

        17 -> {
            return "4.2"
        }

        16 -> {
            return "4.1"
        }

        15 -> {
            return "4.0.3"
        }

        14 -> {
            return "4.0"
        }

        13 -> {
            return "3.2"
        }

        12 -> {
            return "3.1"
        }

        11 -> {
            return "3.0"
        }

        10 -> {
            return "2.3.3"
        }

        9 -> {
            return "2.3"
        }

        else -> {
            return "Unknown"
        }
    }

}


