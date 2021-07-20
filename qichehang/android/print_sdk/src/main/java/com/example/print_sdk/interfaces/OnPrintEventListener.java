package com.example.print_sdk.interfaces;

/**
 * Created by moxiaomo
 * on 2020/12/6
 */
public interface OnPrintEventListener {

    void onPrintStatus(int state);

    void onVersion(String version);

    void onTemperature(String str);
}