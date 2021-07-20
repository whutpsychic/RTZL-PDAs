package com.example.print_sdk.enums;

/**
 * Created by moxiaomo
 * on 2019/8/8
 *  Name： MODE_ENLARGE(enum)
 * Function： character size
 */
public enum MODE_ENLARGE {
    NORMAL(0x00), // Normal
    HEIGHT_DOUBLE(0x01), // Double-height
    WIDTH_DOUBLE(0x10), // (Double-width
    HEIGHT_WIDTH_DOUBLE(0x11); // Double-width&height

    private int iSet;

    MODE_ENLARGE(int iValue) {
        this.iSet = iValue;
    }

    public int Get() {
        return this.iSet;
    }
}
