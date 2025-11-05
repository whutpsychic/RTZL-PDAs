package com.example.scanmanagerdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GetBitmapActivity extends AppCompatActivity {

    private String action_scanner_capture_image_result = "scanner_capture_image_result";

    private ImageView imageView ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_bitmap);
        imageView = findViewById(R.id.iv_bitmap);



        findViewById(R.id.btn_get_bitmap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBitmap();
            }
        });
        
//        //开启或关闭 [应用标识符 ( GS1 、DataMatrix Code128 )]
//        int [] keyInt = new int[]{PropertyID.LABEL_SEPARATOR_ENABLE};
//        int [] valueInt = new int[]{1};//0：关闭   1：开启
//        new ScanManager().setPropertyInts(keyInt,valueInt);

    }

    private void getBitmap(){
        //发送广播---取图
        String action = "action.scanner_capture_image" ;
        Intent intentImage = new Intent(action);
        sendBroadcast(intentImage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setReceiverRegister();
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGetBitmapReceiver);//注销广播
    }


    /**
     *广播接收器
     */
    private BroadcastReceiver mGetBitmapReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action  = intent.getAction();
            if(action.equals(action_scanner_capture_image_result)) {
                byte[] bmp = intent.getByteArrayExtra("bitmapBytes");
                if(bmp != null && bmp.length > 1) {

                    Bitmap mBitmap = BitmapFactory.decodeByteArray(bmp, 0, bmp.length).copy(Bitmap.Config.RGB_565, true);
                    imageView.setImageBitmap(mBitmap);

                }else {
                    Toast.makeText(GetBitmapActivity.this, "获取图片失败 " , Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    /**
     *   注册广播
     */
    private void setReceiverRegister() {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(action_scanner_capture_image_result);
            registerReceiver(mGetBitmapReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
