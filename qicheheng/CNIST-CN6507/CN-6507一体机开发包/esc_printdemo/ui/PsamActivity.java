package com.example.esc_printdemo.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.esc_printdemo.R;
import com.example.print_sdk.PrintUtil;
import com.example.print_sdk.util.ByteUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class PsamActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG=PsamActivity.class.getName ();


    private TextView show, status, cu;
    private EditText msg;
    private Button po1, po2, cmd, pof;
    private PrintUtil printUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_psam);

        Toolbar toolbar_pasm=findViewById (R.id.toolbar_pasm);
        toolbar_pasm.setTitle ("");
        setSupportActionBar (toolbar_pasm);

        cu=findViewById (R.id.textView_b);
        show=findViewById (R.id.textView_v);
        status=findViewById (R.id.textView_s);
        msg=findViewById (R.id.editText_msg);

        cu.setText (getString (R.string.txt_select));
        show.setText ("");
        status.setText ("");
        msg.setText ("008400000008");

        po1=findViewById (R.id.button_p1);
        po2=findViewById (R.id.button_p2);
        cmd=findViewById (R.id.button_cmd);
        pof=findViewById (R.id.button_off);

        po1.setOnClickListener (this);
        po2.setOnClickListener (this);
        cmd.setOnClickListener (this);
        pof.setOnClickListener (this);

        printUtil=PrintUtil.getClient ();

    }

    int psamId=1;

    @Override
    public void onClick(View view) {
        String resHex="";
        int length=0;
        byte[] bytes=null;
        switch (view.getId ()) {
            case R.id.button_p1:
                bytes=new byte[32];
                length=printUtil.resetPsam (1, bytes);
                resHex=ByteUtils.Bytes2HexString (bytes) + " length： " + length;
                show.append ("" + resHex + "\n");
                psamId=1;
                status.setText (getString (R.string.txt_success));
                cu.setText (getString (R.string.txt_select_card1));
                po1.setEnabled (false);
                po2.setEnabled (false);
                cmd.setEnabled (true);
                pof.setEnabled (true);
                break;
            case R.id.button_p2:
                bytes=new byte[32];
                length=printUtil.resetPsam (2, bytes);
                resHex=ByteUtils.Bytes2HexString (bytes) + " length： " + length;
                show.append ("" + resHex + "\n");
                psamId=2;
                status.setText (getString (R.string.txt_success));
                cu.setText (getString (R.string.txt_select_card2));
                po1.setEnabled (false);
                po2.setEnabled (false);
                cmd.setEnabled (true);
                pof.setEnabled (true);
                break;
            case R.id.button_cmd:
                if (TextUtils.isEmpty (show.getText ().toString ())) {
                    Toast.makeText (this, getString (R.string.toast_01), Toast.LENGTH_SHORT).show ();
                    return;
                }
                String sendHex=msg.getText ().toString ().trim ();
                bytes=new byte[32];
                length=printUtil.sendApdu (psamId, sendHex, bytes);
                resHex=ByteUtils.Bytes2HexString (bytes) + " length： " + length;
                show.append (resHex + "\n");
                status.setText (getString (R.string.txt_success_execute));
                //status.setText (getString (R.string.txt_exec_cmd));
                break;
            case R.id.button_off:
                status.setText ("");
                show.setText ("");
                cu.setText (getString (R.string.txt_select));
                po1.setEnabled (true);
                po2.setEnabled (true);
                cmd.setEnabled (false);
                pof.setEnabled (false);
                psamId=1;
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume ();

    }

    @Override
    protected void onPause() {
        super.onPause ();

    }

    @Override
    protected void onStop() {
        super.onStop ();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy ();
        if (printUtil != null) {
            printUtil.closeDev ();
        }
    }


}
