package com.rtlink.px50s.webIO

import android.Manifest.*
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.rtlink.px50s.GlobalConfig.Companion.RAM_NAME
import com.rtlink.px50s.GlobalConfig.Companion.WEB_URL
import com.rtlink.px50s.R
import android.device.ScanManager
import com.rtlink.px50s.activities.WebViewActivity
import com.rtlink.px50s.activities.WebViewActivity.Companion.CHANNEL_ID
import com.rtlink.px50s.activities.WebViewIPConfigActivity
import com.rtlink.px50s.utils.LocalStorage
import com.rtlink.px50s.utils.makeToast
import com.rtlink.px50s.webIO.CallbackKeys.Companion.GET_SAFE_HEIGHTS
import com.rtlink.px50s.webIO.CallbackKeys.Companion.NETWORK_TYPE
import com.rtlink.px50s.webIO.CallbackKeys.Companion.READ_LOCAL


class Index(private val activity: WebViewActivity, private val webView: WebView?) {

    /** Show a toast from the web page  */
    @JavascriptInterface
    fun showToast(toast: String) {
        makeToast(activity, toast)
    }

    /** Jump to native page for configuring web ip  */
    @JavascriptInterface
    fun ipConfig() {
        val intent = Intent(activity, WebViewIPConfigActivity::class.java)
        activity.ipConfigLauncher.launch(intent)
    }
    /**  ----------------------------------------------------------------------------- */
    /** Write local storage from web  */
    /** Work with SharedPreferences  */
    @JavascriptInterface
    fun writeLocal(key: String, value: String) {
        val localStorage = LocalStorage(activity)
        localStorage.write(key, value)
    }

    /** Read local storage from web  */
    @JavascriptInterface
    fun readLocal(key: String) {
        val localStorage = LocalStorage(activity)
        val content = localStorage.read(key)
        activity.runOnUiThread {
            webView?.evaluateJavascript("$RAM_NAME.callback.$READ_LOCAL('$content')", null)
        }
    }

    /** Dial numbers to prepare a phone call  */
    @JavascriptInterface
    fun preDial(number: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
        activity.startActivity(intent)
    }

    /** Scan qrcode or barcode */
    @JavascriptInterface
    fun scan() {
//        val intent = Intent(activity, ScanningActivity::class.java)
//        activity.scanResultLauncher.launch(intent)
        activity.mScanManager.startDecode()
    }

    /** End a modalLoading from the web page  */
    @JavascriptInterface
    fun finish() {
        activity.mScanManager.stopDecode()
    }

    /** Check for network type  */
    @JavascriptInterface
    fun checkNetworkType() {
        val cm =
            activity.baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val res = cm.getActiveNetworkInfo()?.typeName

        activity.runOnUiThread {
            webView?.evaluateJavascript("$RAM_NAME.callback.$NETWORK_TYPE('$res')", null)
        }
    }

    /** Get Safe Height  */
    @JavascriptInterface
    fun getSafeHeights() {
        val windowInsets = activity.window.decorView.rootWindowInsets
        val top: Int = windowInsets.systemWindowInsetTop / 2;
        val bottom: Int = windowInsets.systemWindowInsetBottom / 2;

        activity.runOnUiThread {
            webView?.evaluateJavascript(
                "$RAM_NAME.callback.$GET_SAFE_HEIGHTS([$top, $bottom])",
                null
            )
        }
    }

    /** Go to take a photo  */
    @JavascriptInterface
    fun takePhoto() {
        activity.prepareTakePhoto()
    }

    /** Vibrate Action  */
    @RequiresApi(Build.VERSION_CODES.Q)
    @JavascriptInterface
    fun vibrate() {
        val vibe: Vibrator = activity.getSystemService("vibrator") as Vibrator
        vibe.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
    }

    // 强制横屏/恢复横屏
    @JavascriptInterface
    fun setScreenHorizontal() {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    // 恢复竖屏/恢复竖屏
    @JavascriptInterface
    fun setScreenPortrait() {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    /** Display a notification on top  */
    @JavascriptInterface
    fun notificationAsync(id: Int, title: String, content: String, num: Int, priority: Int) {
        // Create an explicit intent for an Activity in your app.
        val intent = Intent(activity, WebViewActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_IMMUTABLE)

//        println(" ------------------------------ This is a piece of notification ")
//        println(" ------------------------------ prepare to execute notificationAsync ")
//        println(" ------------------------------ title: $title content: $content badge: $num ")

        val notification = NotificationCompat.Builder(activity, CHANNEL_ID)
            // 通知图标
            .setSmallIcon(R.drawable.ic_launcher_background)
            // 通知标题
            .setContentTitle(title)
            // 通知内容
            .setContentText(content)
            // 角标数字
            .setNumber(num)
            // 优先级
            .setPriority(priority)
            // 自动关闭
            .setAutoCancel(true)
            // 点击打开app
            .setContentIntent(pendingIntent).build()

        with(NotificationManagerCompat.from(activity)) {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                // ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                // public fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                //                                        grantResults: IntArray)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
//                println(" ------------------------------ 此时你没有通知权限 ")
                return@with
            }
            // notificationId is a unique int for each notification that you must define.
//            println(" ------------------------------ 你已拥有通知权限 ")
            notify(id, notification)
        }

    }

    @JavascriptInterface
    fun notification(id: Int, title: String, content: String, num: Int) {
//        println(" ------------------------------ prepare to execute notification ")
//        println(" ------------------------------ title: $title content: $content badge: $num ")
        // 延迟0.3s后执行
        Handler().postDelayed(Runnable {
            notificationAsync(id, title, content, num, NotificationCompat.PRIORITY_MAX)
        }, (300).toLong())
    }

    // 获取设备信息
    @JavascriptInterface
    fun getDeviceInfo() {
//        println(" --------------------------------------------------------------- getDeviceInfo ")
        collectDeviceInfo(activity, webView)
    }


//    // 清除角标数字(不建议使用)
//    @JavascriptInterface
//    fun clearBadgeNum(id: Int) {
//        clearBadgeNum(id, activity)
//    }

    // 关闭通知
    @JavascriptInterface
    fun cancelNotification(id: Int) {
        val context = activity.baseContext
        with(NotificationManagerCompat.from(activity)) {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@with
            }
            // notificationId is a unique int for each notification that you must define.
            NotificationManagerCompat.from(context).cancel(id)
        }
        // huawei

    }

    // 清除浏览器缓存
    @JavascriptInterface
    fun clearWebCache() {
        activity.runOnUiThread {
            webView?.clearCache(true)
        }
    }

    // 重新加载网页
    @JavascriptInterface
    fun reloadWebUrl(url: String) {
        activity.runOnUiThread {
            webView?.loadUrl(url)
        }
    }

    private fun getCurrWebUrl(): String {
        val localStorage = LocalStorage(activity)
        val currUrl: String = localStorage.read("weburl") ?: WEB_URL
        return currUrl
    }

    // 重新加载程序起始网页
    @JavascriptInterface
    fun reloadApp() {
        activity.runOnUiThread {
            // 缓存模式
            webView?.settings?.cacheMode = WebSettings.LOAD_NO_CACHE;
            webView?.clearCache(true)
            Handler().postDelayed(Runnable {
                webView?.loadUrl(getCurrWebUrl())
            }, 300)

            Handler().postDelayed(Runnable {
                // 恢复设置
                webView?.settings?.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK;
            }, 10000)
        }
    }

}