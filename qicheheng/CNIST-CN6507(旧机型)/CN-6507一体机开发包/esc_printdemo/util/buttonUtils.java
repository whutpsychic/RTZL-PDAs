package com.example.esc_printdemo.util;

/**
 * Created by moxiaomo
 * on 2020/6/30
 */
public class buttonUtils {

    private static long lastTime= 0;
    // 两次点击按钮之间的点击间隔不能少于5000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 2000;

    public static boolean isButtonFastClick() {
        boolean flag = false;
        long mCurTime = System.currentTimeMillis();
        if ((mCurTime - lastTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastTime= mCurTime;
        return flag;
    }
}
