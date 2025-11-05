// 创建一个快捷的短提示
package com.rtlink.px50s.utils

import android.content.Context
import android.widget.Toast

fun makeToast(context: Context, content: String) {
    Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
}


