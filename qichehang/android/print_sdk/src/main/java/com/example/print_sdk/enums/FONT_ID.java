package com.example.print_sdk.enums;

/**
 * Created by moxiaomo
 * on 2019/8/8
 * Name： FONT_ID(enum)
 * Function： character font
 */
public enum FONT_ID {

    FONT_ASCII_12x24(0x00), FONT_ASCII_9x17(0x01);
    private int iSet;

    FONT_ID(int iValue) {
        this.iSet = iValue;
    }

    public int Get() {
        return this.iSet;
    }
}
