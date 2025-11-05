// 集中式的权限申请模块
// 做任何动作前，涉及到需调用带权限功能时都要向用户征求权限授予意见，获得用户授权后方可调用
package com.rtlink.px50s.utils

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlin.reflect.KFunction0

class RequirePermission {

    constructor(activity: ComponentActivity, permission: String) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(permission),
            getCodeByPermissionType(permission)
        )
    }

    constructor(activity: ComponentActivity, permission: String, fn: KFunction0<Unit>) {
        // 如果有该权限则执行后续函数
        if (
            ContextCompat.checkSelfPermission(
                activity,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fn()
        }
        // 否则请求该权限
        else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(permission),
                getCodeByPermissionType(permission)
            )
        }
    }


    companion object {
        // 相机
        const val CAMERA_PERMISSION_REQUEST_CODE = 2

        // 通知
        const val NOTIFICATION_PERMISSION_REQUEST_CODE = 3
    }

    private fun getCodeByPermissionType(permission: String): Int {
        return when (permission) {
            android.Manifest.permission.CAMERA -> CAMERA_PERMISSION_REQUEST_CODE
            android.Manifest.permission.POST_NOTIFICATIONS -> NOTIFICATION_PERMISSION_REQUEST_CODE
            else -> 0
        }
    }
}
