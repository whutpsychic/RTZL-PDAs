package com.example.nfcdemo.util;

import android.content.Context;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;

public class NfcUtils {

    public static boolean hasNfc(Context context){
        boolean bRet=false;
        if(context==null)
            return bRet;
        NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter != null && adapter.isEnabled()) {
            // adapter存在，能启用
            bRet=true;
        }
        return bRet;
    }

    // converts byte arrays to string
    public static String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = {
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
                "B", "C", "D", "E", "F"
        };
        String out = "";
        for (j = 0; j < inarray.length; ++j) {
            in = inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }

}
