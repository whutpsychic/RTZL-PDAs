package com.android.printer.demo;

import android.app.IntentService;
import android.content.Intent;
import android.device.PrinterManager;

public class PrintBillService extends IntentService {

    private PrinterManager printer;
    
    public PrintBillService() {
        super("bill");
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        printer = new PrinterManager();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO Auto-generated method stub
        String context = intent.getStringExtra("SPRT");
        if(context== null || context.equals("")) return ;

         
        printer.setupPage(384, -1);
        //int ret =printer.drawTextEx(context, 5, 0,300,-1, "arial", 50, 0, 0x0002 | 0x0004, 0);
        int ret =printer.drawTextEx(context, 5, 0,384,-1, "simsun", 24, 0, 0, 0);
        android.util.Log.i("debug", "ret:" + ret);
        //ret += printer.drawTextEx(context, 5, ret,300,-1, "arial", 25, 0, 0x0001, 1);
        //ret += printer.drawTextEx(context, 5, ret,-1,-1, "arial", 25, 0, 0x0008, 0);
        //ret +=printer.drawTextEx(context, 300, ret,-1,-1, "arial", 25, 1, 0, 0);
        //ret +=printer.drawTextEx(context, 0, ret,-1,-1, "/system/fonts/DroidSans-Bold.ttf", 25, 0, 0, 0);
        //ret +=printer.drawTextEx(context, 0, ret,-1,-1, "/system/fonts/kaishu.ttf", 25, 0, 0x0001, 0);
        android.util.Log.i("debug", "ret:" + ret);
        //printer.drawTextEx(context, 5, 60,160,-1, "arial", 25, 0, 0x0001 |0x0008, 0);
        //printer.drawTextEx(context, 180, 0,160,-1, "arial", 25, 1, 0x0008, 0);
        //printer.drawTextEx(context, 300, 30,160,-1, "arial", 25, 2, 0x0008, 0);
        //printer.drawTextEx(context, 300, 160,160,-1, "arial", 25, 3, 0x0008, 0);
        //printer.drawTextEx(context, 0, 0,160,-1, "arial", 25, 1, 0x0008, 0);
        //printer.drawTextEx(context, 160, 30,200,-1, "arial", 28, 0, 2,1);
        //printer.drawTextEx(context, 0, 180,-1,-1, "arial", 28, 0, 2,1);
        ret=printer.printPage(0);
    
        
        
            Intent i = new Intent("android.prnt.message");
            i.putExtra("ret", ret);
            this.sendBroadcast(i);
    }
    
    private void sleep(){
        //延时1秒
        try {
            Thread.currentThread();
			Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}