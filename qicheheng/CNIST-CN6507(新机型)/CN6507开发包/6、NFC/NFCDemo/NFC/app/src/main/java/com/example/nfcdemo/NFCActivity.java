package com.example.nfcdemo;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.nfcdemo.util.Converter;
import com.example.nfcdemo.util.NfcUtils;
import com.example.nfcdemo.util.UriRecord;


public class NFCActivity extends BaseActivity implements OnClickListener {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the device is supported nfc
        if (NfcUtils.hasNfc(this)) {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            /**
             *
             *  IntentRelease system
             *  The configuration declared in manifests.xml is registered within the system, and the system matches which applications conform to Action
             *  Then when we detect an Action like this, it will pop up on the desktop and let the user choose a list of applications. We can intercept the Intent publishing system of the system in the code
             *  So we can specify a particular Action, jump to the page of the application that we specify,
             *  In this way, there is no need for the system to pop up the list of application selection, that is, we take over the Intent publishing system of the system in the code
             */
            //  get pendingIntent
            pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            // check NFC status.
            checkNfc();
            onNewIntent(getIntent());
            initListener();
        } else {
            Toast.makeText(this, "Your device is not supported nfc", Toast.LENGTH_SHORT).show();
        }
    }


    // Set listener
    private void initListener() {
        readButton.setOnClickListener(this);
        wriButton.setOnClickListener(this);
        modifyButton.setOnClickListener(this);
        mHintButton.setOnClickListener(this);
        hintButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_read:
                if (checkBlock()) {
                    MifareClassicCard mifareClassicCard = new MifareClassicCard(mifareClassic, this);
                    int block = Integer.parseInt(blockIdEditText.getText().toString().trim());
                    String content = mifareClassicCard.readCarCode(block, keyEdittext.getText().toString().trim());
                    if (getResources().getString(R.string.alert_auth_err).equals(content) || getResources().getString(R.string.alert_read_err).equals(content)) {
                        setHintToContentEd(content);
                    } else {
                        contentEditText.setText(content);
                    }
                }
                break;
            case R.id.button_write:
                if (checkBlock()) {
                    MifareClassicCard mifareClassicCard = new MifareClassicCard(mifareClassic, this);
                    int block = Integer.parseInt(blockIdEditText.getText().toString().trim());
                    String content = contentEditText.getText().toString().trim();
                    String result = mifareClassicCard.wirteCarCode(content, block, keyEdittext.getText().toString().trim());
                    if ("".equals(result)) {
                        setHintToContentEd(getResources().getString(R.string.modify_ok));
                    } else {
                        setHintToContentEd(result);
                    }
                }
                break;
            case R.id.button_modify:
                if (checkBlock()) {
                    MifareClassicCard mifareClassicCard = new MifareClassicCard(mifareClassic, this);
                    int block = Integer.parseInt(blockIdEditText.getText().toString().trim());
                    String content = contentEditText.getText().toString().trim();
                    String key = keyEdittext.getText().toString().trim();
                    String result = mifareClassicCard.modifyPassword(block, content, key);
                    if ("".equals(result)) {
                        setHintToContentEd(getResources().getString(R.string.modify_ok));
                    } else {
                        setHintToContentEd(result);
                    }
                }
                break;
            case R.id.button_hint:
                hint();
                break;
            case R.id.button_hint1:
                hint();
                break;
        }
    }

    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            /**
             * This method must be called from the main thread
             * the PendingIntent to start for the dispatch
             * the FILTERS to override dispatching for, or null to always dispatch
             * the TECHLISTS used to perform matching for dispatching of the {@link NfcAdapter#ACTION_TECH_DISCOVERED} intent
             */
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, FILTERS, TECHLISTS);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            // Get card type & tag lists.
            String[] techList = tag.getTechList();
            StringBuffer techString = new StringBuffer();
            for (int i = 0; i < techList.length; i++) {
                techString.append(techList[i]);
                techString.append(";\n");
            }
            typeEditText.setText(techString.toString());

            // Get card ID
            Long cardNo = Long.parseLong(Converter.flipHexStr(Converter.ByteArrayToHexString(tag.getId())), 16);
            carCodeEditText.setText(cardNo.toString());

            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {//NDEF ??????

                showView(0);//??????????????????

                Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);//??????NDEF??????
                if (rawMsgs != null && rawMsgs.length > 0) {
                    NdefMessage ndefMessage = (NdefMessage) rawMsgs[0];//???????????????1???????????????????????????????????????
                    try {
                        NdefRecord ndefRecord = ndefMessage.getRecords()[0];
                        UriRecord uriRecord = UriRecord.parse(ndefRecord);
                        String mTagText = uriRecord.getUri().toString();
                        contentEditText.setText(mTagText);
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                }
            } else {
                // operate mifare card
                mifareClassic = MifareClassic.get(tag);
                if (mifareClassic != null) {
                    showView(1);
                } else {
                    showView(0);
                }
            }
        }
    }

    /**
     * ???NDEF??????uri
     *
     * @param tag
     */
    public void writeTag(Tag tag) {
        if (tag == null) {
            return;
        }

        // ????????????????????????????????????
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{NdefRecord
                .createUri(Uri.parse("https://www.baidu.com"))});

        // ????????????????????????
        int size = ndefMessage.toByteArray().length;
        try {
            // ??????Nedf
            Ndef ndef = Ndef.get(tag);
            // ???????????????????????????Nedf??????
            if (ndef != null) {
                ndef.connect();
                // ????????????
                if (!ndef.isWritable()) {
                    Toast.makeText(this, "?????????????????????", Toast.LENGTH_SHORT).show();
                    return;
                }
                // ????????????????????????????????????????????????????????????
                if (ndef.getMaxSize() < size) {
                    Toast.makeText(this, "??????????????????", Toast.LENGTH_SHORT).show();
                    return;
                }
                // ????????????
                ndef.writeNdefMessage(ndefMessage);
                Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
            } else { // ?????????Nedf???????????????
                NdefFormatable format = NdefFormatable.get(tag);
                // ??????????????????????????????????????????Ndef??????
                if (format != null) {
                    format.connect();
                    // ??????????????????Nedf??????
                    format.format(ndefMessage);
                    Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "???????????????Nedf??????", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
