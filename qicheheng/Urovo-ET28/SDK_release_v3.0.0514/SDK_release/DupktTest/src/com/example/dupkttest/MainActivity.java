package com.example.dupkttest;

import android.device.SEManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private MagReadService mReadService;
    SEManager mSEManager;
    TextView tv;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MagReadService.MESSAGE_READ_MAG:
                    int track1_l = msg.getData().getInt("CARD_TRACK1_L");
                    byte[] outTrack1 = msg.getData().getByteArray(MagReadService.CARD_TRACK1);
                    int track2_l = msg.getData().getInt("CARD_TRACK2_L");
                    byte[] outTrack2 = msg.getData().getByteArray(MagReadService.CARD_TRACK2);
                    int track3_l = msg.getData().getInt("CARD_TRACK3_L");
                    byte[] outTrack3 = msg.getData().getByteArray(MagReadService.CARD_TRACK3);
                    byte[] KSN = msg.getData().getByteArray(MagReadService.CARD_KSN);

                    String CardNo = msg.getData().getString(MagReadService.CARD_NUMBER);
                    StringBuffer trackOne = new StringBuffer();
                    if(track1_l > 0) {
                        tv.append("ECCTrack1=");
                        tv.append(DecodeConvert.bytesToHexString(outTrack1, 0, track1_l));
                        tv.append("\n");
                        trackOne.append("ECCTrack1=" + DecodeConvert.bytesToHexString(outTrack1, 0, track1_l));
                    }
                    if(track2_l > 0) {
                        tv.append("ECCTrack2=");
                        tv.append(DecodeConvert.bytesToHexString(outTrack2, 0, track2_l));
                        tv.append("\n");
                        trackOne.append("ECCTrack1=" + DecodeConvert.bytesToHexString(outTrack2, 0, track2_l));
                    }
                    if(track3_l > 0) {
                        tv.append("ECCTrack3=");
                        tv.append(DecodeConvert.bytesToHexString(outTrack3, 0, track3_l));
                        trackOne.append("ECCTrack1=" + DecodeConvert.bytesToHexString(outTrack3, 0, track3_l));
                        tv.append("\n");
                    }
                    trackOne.append("CardNo=" + CardNo);
                    Log.d("trackOne", trackOne.toString());
                    tv.append("CardNo=");
                    tv.append(CardNo);
                    tv.append("\n");
                    tv.append("KSN=");
                    tv.append(DecodeConvert.bytesToHexString(KSN, 0, KSN.length));
                    tv.append("\n");
                    break;
                case MagReadService.MESSAGE_OPEN_MAG:
                    break;
                case MagReadService.MESSAGE_CHECK_FAILE:
                    break;
                case MagReadService.MESSAGE_CHECK_OK:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Example of a call to a native method
        tv = (TextView) findViewById(R.id.sample_text);
        //tv.setText(stringFromJNI());
        mSEManager = new SEManager();
        mReadService = new MagReadService(this, mHandler);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(android.view.View view) {
                //(byte[] bsBdk ,int iBdkLen ,int iKeyType ,byte[] bsKsn ,int iKsnLen ,byte[] bsIpek ,int iIpekLen);
                //BDK:620CA71586367D928F3DABA4B9B79C9F
                //IPEK:397126A1B93150C5D54B0CC672F89EA5
                //KSN:11111746011BEDE00001
                byte[] bsBdk = DecodeConvert.hexStringToByteArray("620CA71586367D928F3DABA4B9B79C9F");
                byte[] bsKsn = DecodeConvert.hexStringToByteArray("11111746011BEDE00001");
                byte[] bsIpek = DecodeConvert.hexStringToByteArray("397126A1B93150C5D54B0CC672F89EA5");
                int ret = mSEManager.downloadKeyDukpt(1, bsBdk, bsBdk.length, bsKsn, bsKsn.length, bsIpek, bsIpek.length);
                if(ret == 0) {
                    tv.append("Dukpt MSR_KEY\n");
                    tv.append("BDK:620CA71586367D928F3DABA4B9B79C9F\n");
                    tv.append("IPEK:397126A1B93150C5D54B0CC672F89EA5\n");
                    tv.append("KSN:11111746011BEDE00001\n");
                }
                ret =mSEManager.downloadKeyDukpt(3, bsBdk, bsBdk.length, bsKsn, bsKsn.length, bsIpek, bsIpek.length);
                if(ret == 0) {
                    tv.append("Dukpt PIN_KEY\n");
                    tv.append("BDK:620CA71586367D928F3DABA4B9B79C9F\n");
                    tv.append("IPEK:397126A1B93150C5D54B0CC672F89EA5\n");
                    tv.append("KSN:11111746011BEDE00001\n");
                }
            }
        });
        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(android.view.View view) {
                Bundle param = new Bundle();
                    param.putInt("KeyUsage", 3);
                    param.putInt("PINKeyNo", 1);
                    param.putInt("pinAlgMode", 5);// 5 dupkt
                    param.putString("cardNo", "6210817200002086930");
                    param.putBoolean("sound", true);
                    param.putBoolean("onlinePin", true);
                    param.putBoolean("FullScreen", true);
                    param.putLong("timeOutMS", 60000);
                    param.putString("supportPinLen", "0,4,6,8,10,12");
                    param.putString("title", "Security Keyboard");
                    param.putString("message", "please input password \n 6225****0299");
                mSEManager.getPinBlockEx(param, mPedInputListener);
            }
        });
    }
    private SEManager.OperationPedInputListener mPedInputListener = new SEManager.OperationPedInputListener() {
        @Override
        public void handleResult(int result, int length, Bundle bundle) {
            if(result == 0) {
                final byte[] pinBlock = bundle.getByteArray("pinBlock");
                final byte[] ksn = bundle.getByteArray("ksn");
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(pinBlock != null) {
                            tv.append("\npinBlock:");
                            tv.append(DecodeConvert.bytesToHexString(pinBlock));
                        }
                        if(ksn != null) {
                            tv.append("\npinBlock:");
                            tv.append(DecodeConvert.bytesToHexString(ksn));
                        }

                    }
                });
            } else {
                Toast.makeText(MainActivity.this, "result = " + result + " length = " + length, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onChanged(int i, int i1, byte[] bytes) {

        }
    };

    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mReadService.stop();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mReadService.start();
    }
}
