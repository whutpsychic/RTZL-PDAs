// 本地键值对存储
// ======================================================
// - weburl: webView 当前指向的URL
// ======================================================
package com.rtlink.px50s.utils

import android.content.Context
import androidx.activity.ComponentActivity

class LocalStorage(act: ComponentActivity) {

    private val activity: ComponentActivity = act

    fun write(key: String, value: String) {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(key, value)
            apply()
        }
    }

    fun read(key: String): String? {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return null
        val content = sharedPref.getString(key, null)

        return content
    }

}
