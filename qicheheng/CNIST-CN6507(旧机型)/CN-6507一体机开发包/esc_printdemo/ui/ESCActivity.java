package com.example.esc_printdemo.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.esc_printdemo.PrintContract;
import com.example.esc_printdemo.R;
import com.example.esc_printdemo.util.BitmapUtils;
import com.example.esc_printdemo.util.buttonUtils;
import com.example.print_sdk.PrintUtil;
import com.example.print_sdk.interfaces.OnPrintEventListener;

import java.io.IOException;

public class ESCActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private String TAG=ESCActivity.class.getName ();
    private PrintContract printContract;
    private TextView tv_version;
    private Bitmap bitmap;

    int number=1000000001; // 初始化票据流水号 Initial bill serial number

    private RadioGroup rb_g,rb_g_mode;
    private EditText edit_scan, tv_con;
    private PrintUtil pUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_esc);

        Button btn_text=findViewById (R.id.btn_text);
        Button btn_image=findViewById (R.id.btn_image);
        Button btn_barcode=findViewById (R.id.btn_barcode);
        Button btn_qr=findViewById (R.id.btn_qr);
        Button btn_label=findViewById (R.id.btn_label);
        Button btn_position=findViewById (R.id.btn_position);
        Button btn_selfTest=findViewById (R.id.btn_selfTest);
        Button btn_default=findViewById (R.id.btn_default);

        tv_version=findViewById (R.id.tv_version);
        rb_g=findViewById (R.id.rb_g);
        rb_g_mode = findViewById (R.id.rb_g_mode);
        edit_scan=findViewById (R.id.edit_scan);

        tv_con=findViewById (R.id.tv_con);


        btn_text.setOnClickListener (this);
        btn_image.setOnClickListener (this);
        btn_barcode.setOnClickListener (this);
        btn_qr.setOnClickListener (this);
        btn_label.setOnClickListener (this);
        btn_position.setOnClickListener (this);
        btn_selfTest.setOnClickListener (this);
        btn_default.setOnClickListener (this);
        rb_g.setOnCheckedChangeListener (this);
        rb_g_mode.setOnCheckedChangeListener (this);

        pUtil=PrintUtil.getClient ();
        if (pUtil != null) {
            pUtil.setPrintEventListener (new OnPrintEventListener () {
                @Override
                public void onPrintStatus(int state) {
                    Log.e (TAG, "onPrintState: " + state);
                    switch (state) {
                        case 0:
                            number+=1; // 流水号自加1 Serial number plus 1
                            Toast.makeText (ESCActivity.this, getString (R.string.toast_print_success), Toast.LENGTH_SHORT).show ();
                            break;
                        case 1:
                            Toast.makeText (ESCActivity.this, getString (R.string.toast_no_paper), Toast.LENGTH_SHORT).show ();
                            break;
                        case 2:
                            Toast.makeText (ESCActivity.this, getString (R.string.toast_print_error), Toast.LENGTH_SHORT).show ();
                            break;
                    }
                }

                @Override
                public void onVersion(String version) {
                    Log.e (TAG, "onVersion: " + version);
                    tv_version.setText (version);
                }

                @Override
                public void onTemperature(String str) {
                    Log.e (TAG, "onTemperature: "+str);
                }
            });

            printContract=new PrintContract (this, pUtil);
            printContract.printInit ();

        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId ()) {
            case R.id.btn_text:
                if (!buttonUtils.isButtonFastClick ()) {
                    Toast.makeText (this, getString (R.string.toast_chick), Toast.LENGTH_SHORT).show ();
                    return;
                }

                if (TextUtils.isEmpty (tv_con.getText ().toString ().trim ())) {
                    Toast.makeText (this, getString (R.string.toast_con), Toast.LENGTH_SHORT).show ();
                    return;
                }

                printContract.printText (number, tv_con.getText ().toString ().trim ());
                break;
            case R.id.btn_image:
                if (!buttonUtils.isButtonFastClick ()) {
                    Toast.makeText (this, getString (R.string.toast_chick), Toast.LENGTH_SHORT).show ();
                    return;
                }

                if (TextUtils.isEmpty (tv_con.getText ().toString ().trim ())) {
                    Toast.makeText (this, getString (R.string.toast_con), Toast.LENGTH_SHORT).show ();
                    return;
                }

                bitmap=BitmapFactory.decodeResource (this.getResources (), R.drawable.icon_test);
                bitmap=BitmapUtils.compressPic (bitmap, 384, 300);
                printContract.printImg (bitmap, tv_con.getText ().toString ().trim ());
                break;
            case R.id.btn_barcode:
                if (!buttonUtils.isButtonFastClick ()) {
                    Toast.makeText (this, getString (R.string.toast_chick), Toast.LENGTH_SHORT).show ();
                    return;
                }
                if (TextUtils.isEmpty (edit_scan.getText ().toString ())) {
                    Toast.makeText (this, getString (R.string.toast_input_content), Toast.LENGTH_SHORT).show ();
                    return;
                }

                if (TextUtils.isEmpty (tv_con.getText ().toString ().trim ())) {
                    Toast.makeText (this, getString (R.string.toast_con), Toast.LENGTH_SHORT).show ();
                    return;
                }

                printContract.printBarcode (edit_scan.getText ().toString (), number, tv_con.getText ().toString ().trim ());
                break;
            case R.id.btn_qr:
                if (!buttonUtils.isButtonFastClick ()) {
                    Toast.makeText (this, getString (R.string.toast_chick), Toast.LENGTH_SHORT).show ();
                    return;
                }
                if (TextUtils.isEmpty (edit_scan.getText ().toString ())) {
                    Toast.makeText (this, getString (R.string.toast_input_content), Toast.LENGTH_SHORT).show ();
                    return;
                }

                if (TextUtils.isEmpty (tv_con.getText ().toString ().trim ())) {
                    Toast.makeText (this, getString (R.string.toast_con), Toast.LENGTH_SHORT).show ();
                    return;
                }

                printContract.printQR (edit_scan.getText ().toString (), number, tv_con.getText ().toString ().trim ());
                break;
            case R.id.btn_position:
                if (!buttonUtils.isButtonFastClick ()) {
                    Toast.makeText (this, getString (R.string.toast_chick), Toast.LENGTH_SHORT).show ();
                    return;
                }
                if (rb_g.getCheckedRadioButtonId () != R.id.rb_open_mark) {
                    Toast.makeText (this, getString (R.string.toast_mark), Toast.LENGTH_SHORT).show ();
                    return;
                }
                printContract.printGoToNextMark ();
                break;
            case R.id.btn_label:
                if (!buttonUtils.isButtonFastClick ()) {
                    Toast.makeText (this, getString (R.string.toast_chick), Toast.LENGTH_SHORT).show ();
                    return;
                }
                if (TextUtils.isEmpty (edit_scan.getText ().toString ())) {
                    Toast.makeText (this, getString (R.string.toast_input_content), Toast.LENGTH_SHORT).show ();
                    return;
                }
                if (rb_g.getCheckedRadioButtonId () != R.id.rb_open_mark) {
                    Toast.makeText (this, getString (R.string.toast_mark), Toast.LENGTH_SHORT).show ();
                    return;
                }

                if (TextUtils.isEmpty (tv_con.getText ().toString ().trim ())) {
                    Toast.makeText (this, getString (R.string.toast_con), Toast.LENGTH_SHORT).show ();
                    return;
                }

                printContract.printLabel (edit_scan.getText ().toString (), number, tv_con.getText ().toString ().trim ());
                break;
            case R.id.btn_selfTest:
                if (!buttonUtils.isButtonFastClick ()) {
                    Toast.makeText (this, getString (R.string.toast_chick), Toast.LENGTH_SHORT).show ();
                    return;
                }
                printContract.printFeatureList ();
                break;
            case R.id.btn_default:
                printContract.resetPrint ();
                break;
        }

    }

    @Override
    protected void onStop() {
        super.onStop ();
        Log.e ("TAG", "onStop: ");
    }

    @Override
    protected void onPause() {
        super.onPause ();
        Log.e ("TAG", "onPause: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy ();
        Log.e ("TAG", "onDestroy: ");
        printContract.closeDev ();


    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group.getCheckedRadioButtonId () == R.id.rb_open_mark) {
            printContract.printEnableMark (true);
        } else if (group.getCheckedRadioButtonId () == R.id.rb_close_mark) {
            printContract.printEnableMark (false);
        }
//        else if (group.getCheckedRadioButtonId () == R.id.rb_open_con_mode) {
//            printContract.printThicken (true);
//        } else if (group.getCheckedRadioButtonId () == R.id.rb_close_con_mode) {
//            printContract.printThicken (false);
//        }
    }
}
