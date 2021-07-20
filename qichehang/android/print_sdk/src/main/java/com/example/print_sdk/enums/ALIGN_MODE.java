package com.example.print_sdk.enums;

/**
 * Created by moxiaomo
 * on 2019/8/8
 * Name： ALIGN_MODE(enum)
 * Function： Select justification
 * ALIGN_LEFG: Left justification
 * ALIGN_CENTER: Centering
 * ALIGN_RIGHT: Right justification
 */
public enum  ALIGN_MODE {
    ALIGN_LEFT(0), ALIGN_CENTER(1), ALIGN_RIGHT(2);

    private int iSet;

    ALIGN_MODE(int iValue) {
        this.iSet = iValue;
    }

    public int Get() {
        return this.iSet;
    }
}
