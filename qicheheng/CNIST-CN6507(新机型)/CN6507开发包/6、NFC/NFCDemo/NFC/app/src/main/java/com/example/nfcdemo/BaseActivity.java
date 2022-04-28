package com.example.nfcdemo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
@SuppressLint("NewApi")
public class BaseActivity extends Activity{

    protected NfcAdapter nfcAdapter;
    protected PendingIntent pendingIntent;

    protected EditText carCodeEditText, typeEditText, keyEdittext, blockIdEditText, contentEditText;
    protected Button hintButton, mHintButton, readButton, wriButton, modifyButton;

    protected MifareClassic mifareClassic;
    protected LinearLayout linearLayout;
    protected LinearLayout mifareclassicLinearLayout;

    public static String[][] TECHLISTS;
    @SuppressLint("NewApi")
    public static IntentFilter[] FILTERS;

    static {
        try {
            // Nfc card type
            TECHLISTS = new String[][]{
                    {IsoDep.class.getName()},
                    {NfcV.class.getName()}, {NfcF.class.getName()},
            };

            /**
             * get intentFilter
             *
             */
            FILTERS = new IntentFilter[]{
                    new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED, "*/*")
            };
        } catch (Exception e) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_check);
        initView();
    }

    public void initView() {

        carCodeEditText = (EditText) findViewById(R.id.edittext_car_code);
        typeEditText = (EditText) findViewById(R.id.edittext_car_type);
        hintButton = (Button) findViewById(R.id.button_hint1);
        linearLayout = (LinearLayout) findViewById(R.id.m1_le);
        mifareclassicLinearLayout = (LinearLayout) findViewById(R.id.mifareclassic);
        readButton = (Button) findViewById(R.id.button_read);
        wriButton = (Button) findViewById(R.id.button_write);
        modifyButton = (Button) findViewById(R.id.button_modify);
        blockIdEditText = (EditText) findViewById(R.id.edittext_block_id);
        keyEdittext = (EditText) findViewById(R.id.edittext_key);
        contentEditText = (EditText) findViewById(R.id.edittext_content);
        mHintButton = (Button) findViewById(R.id.button_hint);
    }

    protected void checkNfc() {
        if (!nfcAdapter.isEnabled()) {
            startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
        }
    }

    protected void showView(int type) {
        switch (type) {
            case 1:
                mifareclassicLinearLayout.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
                hintButton.setVisibility(View.GONE);
                break;
            default:
                mifareclassicLinearLayout.setVisibility(View.GONE);
                linearLayout.setVisibility(View.GONE);
                hintButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    protected void hint() {
        typeEditText.setText("");
        carCodeEditText.setText("");
        keyEdittext.setText("");
        contentEditText.setText("");
        contentEditText.setHint ("");
        blockIdEditText.setText("");
        blockIdEditText.setHint ("");
    }

    protected void setHintToContentEd(String msg) {
        contentEditText.setText("");
        contentEditText.setHint(msg);
        contentEditText.setHintTextColor(Color.RED);
    }

    protected boolean checkBlock() {

        if ("".equals(blockIdEditText.getText().toString().trim())) {
            blockIdEditText.setText("");
            blockIdEditText.setHint(R.string.alert_input_blockindex);
            blockIdEditText.setHintTextColor(Color.RED);
            return false;
        } else {
            return true;
        }
    }
}
