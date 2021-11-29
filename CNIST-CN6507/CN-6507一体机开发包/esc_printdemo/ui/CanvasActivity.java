package com.example.esc_printdemo.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.esc_printdemo.PrintContract;
import com.example.esc_printdemo.R;
import com.example.print_sdk.PrintUtil;
import com.example.esc_printdemo.util.CanvasUtil;
import com.example.print_sdk.interfaces.OnPrintEventListener;

import java.util.ArrayList;
import java.util.List;

public class CanvasActivity extends AppCompatActivity {

    private Bitmap bmp;
    private PrintContract printContract;
    private PrintUtil pUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_canvas);

        ImageView imageView=findViewById (R.id.imageview);
        pUtil=PrintUtil.getClient ();
        pUtil.setEncoding ("GB2312");
        pUtil.setPrintEventListener (new OnPrintEventListener () {
            @Override
            public void onPrintStatus(int state) {
                switch (state) {
                    case 0:
                        Toast.makeText (CanvasActivity.this, getString (R.string.toast_print_success), Toast.LENGTH_SHORT).show ();
                        break;
                    case 1:
                        Toast.makeText (CanvasActivity.this, getString (R.string.toast_no_paper), Toast.LENGTH_SHORT).show ();
                        break;
                    case 2:
                        Toast.makeText (CanvasActivity.this, getString (R.string.toast_print_error), Toast.LENGTH_SHORT).show ();
                        break;

                }
            }

            @Override
            public void onVersion(String version) {

            }

            @Override
            public void onTemperature(String str) {

            }
        });


        List<String> list=new ArrayList<String> ();
        list.add ("test test test");
        list.add ("test test test");
        list.add ("test test test test test");
        list.add ("test test test");
        list.add ("test test test test test");
        list.add ("test test test");
        bmp=CanvasUtil.createLeftImage ("https://www.hao123.com", list);

        imageView.setImageBitmap (bmp);
        printContract=new PrintContract (this, pUtil);
        printContract.printInit ();
        printContract.printEnableMark (true);

        Button btn_printer=findViewById (R.id.btn_printer);
        btn_printer.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                printContract.printImg (bmp,"39");
                printContract.printGoToNextMark ();
            }
        });


    }


}
