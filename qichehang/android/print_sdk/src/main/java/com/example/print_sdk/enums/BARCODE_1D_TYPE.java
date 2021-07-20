package com.example.print_sdk.enums;

/**
 * Created by moxiaomo
 * on 2019/8/8
 * Name： BARCODE_1D_TYPE(enum)
 * Function：1D bar code type
 */
public enum BARCODE_1D_TYPE {
    UPCA(65), UPCE(66), EAN13(67), EAN8(68), CODE39(69), ITF25(70), CODE93(72), CODE128(73);
    private int iSet;

    BARCODE_1D_TYPE(int iValue) {
        this.iSet = iValue;
    }

    public int Get() {
        return this.iSet;
    }
}
