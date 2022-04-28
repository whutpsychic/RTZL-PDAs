package com.example.esc_printdemo.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;
import java.util.List;

/**
 * Created by moxiaomo
 * on 2020/7/21
 */
public class CanvasUtil {

    private static final String CHARSET="UTF-8";
    private static final String FORMAT_NAME="JPG";
    // 二维码尺寸
    private static final int QRCODE_SIZE=314;
    // LOGO宽度
    private static final int WIDTH=60;
    // LOGO高度
    private static final int HEIGHT=60;

    /**
     * user: Rex
     * date: 2016年12月29日  上午12:31:29
     *
     * @param content 二维码内容
     * @return 返回二维码图片
     */
    public static Bitmap createImage(String content, int heights, int widths) {
        Hashtable<EncodeHintType, Object> hints=new Hashtable<> ();
        hints.put (EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put (EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put (EncodeHintType.MARGIN, 2);
        BitMatrix bitMatrix=null;
        Bitmap image=null;
        try {
            bitMatrix=new MultiFormatWriter ().encode (content, BarcodeFormat.QR_CODE, widths, heights, hints);
            int width=bitMatrix.getWidth ();
            int height=bitMatrix.getHeight ();
            int[] pixels=new int[width * height];
            for (int x=0; x < width; x++) {
                for (int y=0; y < height; y++) {
                    if (bitMatrix.get (y, x)) {
                        pixels[y * width + x]=0xff000000;
                    } else {
                        pixels[y * width + x]=0xffffffff;
                    }
//                    image.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            image=Bitmap.createBitmap (pixels, width, height, Bitmap.Config.ARGB_8888);
        } catch (Exception e) {
            e.printStackTrace ();
        }
        // 插入图片
//        CanvasUtil.insertImage(image, logoImgPath, needCompress);
        return image;
    }

    /**
     * 生成图片  加上title的图片
     *
     * @param content
     * @param title
     * @return
     */
    public static Bitmap createImage(String content, String title) {
        int picWidth=720;//生成图片的宽度
        int picHeight=765;//生成图片的高度
        int titleTextSize=30;
        int contentTextSize=22;
        int textColor=Color.BLACK;
        int qrWidth=366;
        int qrHeight=366;
        int paddingTop=152;
        int paddingMiddle=40;
        int paddingBottom=24;

        //最终生成的图片
        Bitmap result=Bitmap.createBitmap (picWidth, picHeight, Bitmap.Config.ARGB_8888);

        Paint paint=new Paint ();
        paint.setColor (Color.WHITE);
        Canvas canvas=new Canvas (result);

        //先画一整块白色矩形块
        canvas.drawRect (0, 0, picWidth, picHeight, paint);

        //画title文字
        Rect bounds=new Rect ();
        paint.setColor (textColor);
        paint.setTextSize (titleTextSize);
        //获取文字的字宽高，以便将文字与图片中心对齐
        paint.getTextBounds (title, 0, title.length (), bounds);
        canvas.drawText (title, picWidth / 2 - bounds.width () / 2, paddingTop + bounds.height () / 2, paint);

        //画白色矩形块
        int qrTop=paddingTop + titleTextSize + paddingMiddle;//二维码的顶部高度

        //画二维码
        Hashtable<EncodeHintType, Object> hints=new Hashtable<> ();
        hints.put (EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put (EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put (EncodeHintType.MARGIN, 5);
        BitMatrix bitMatrix;
        Bitmap image=null;
        try {
            bitMatrix=new MultiFormatWriter ().encode (content, BarcodeFormat.QR_CODE, qrWidth, qrHeight, hints);
            int width=bitMatrix.getWidth ();
            int height=bitMatrix.getHeight ();
            image=Bitmap.createBitmap (width, height, Bitmap.Config.ARGB_8888);
            for (int x=0; x < width; x++) {
                for (int y=0; y < height; y++) {
                    image.setPixel (x, y, bitMatrix.get (x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }

        paint.setColor (Color.BLACK);
        canvas.drawBitmap (image, (picWidth - qrWidth) / 2, qrTop, paint);

        //画文字
        paint.setColor (Color.BLACK);
        paint.setTextSize (contentTextSize);
        int lineTextCount=(int) ((picWidth - 50) / contentTextSize);
        int line=(int) (Math.ceil (Double.valueOf (content.length ()) / Double.valueOf (lineTextCount)));
        int textTop=qrTop + qrHeight + paddingBottom;//地址的顶部高度

        for (int i=0; i < line; i++) {
            String s;
            if (i == line - 1) {
                s=content.substring (i * lineTextCount, content.length ());
            } else {
                s=content.substring (i * lineTextCount, (i + 1) * lineTextCount);
            }
            paint.getTextBounds (content, 0, s.length (), bounds);

            canvas.drawText (s, picWidth / 2 - bounds.width () / 2, textTop + i * contentTextSize + i * 5 + bounds.height () / 2, paint);
        }

        canvas.save ();
        canvas.restore ();

        return result;
    }


    /**
     * 生成图片  加上title的图片
     *
     * @param content
     * @param list
     * @return
     */
    public static Bitmap createLeftImage(String content, List<String> list) {
        int picWidth=384;//生成图片的宽度
        int picHeight=300;//生成图片的高度
        int titleTextSize=20;
        int textColor=Color.BLACK;
        int qrWidth=200;
        int qrHeight=200;
        int paddingTop=10;
        int textX = 160;
        int textY = 50;

        //最终生成的图片
        Bitmap result=Bitmap.createBitmap (picWidth, picHeight, Bitmap.Config.ARGB_8888);

        Paint paint=new Paint ();
        paint.setColor (Color.WHITE);
        Canvas canvas=new Canvas (result);

        //先画一整块白色矩形块
        canvas.drawRect (0, 0, picWidth, picHeight, paint);

        //画title文字
        Rect bounds=new Rect ();
        paint.setColor (textColor);
        paint.setTextSize (titleTextSize);
        for (int i=0; i < list.size (); i++) {
            //获取文字的字宽高，以便将文字与图片中心对齐
            paint.getTextBounds (list.get (i), 0, list.get (i).length (), bounds);
            canvas.drawText (list.get (i), textX, textY, paint);
            textY+=30;
        }

        //画白色矩形块
        int qrTop=paddingTop;//二维码的顶部高度

        //画二维码
        Hashtable<EncodeHintType, Object> hints=new Hashtable<> ();
        hints.put (EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put (EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put (EncodeHintType.MARGIN, 5);
        BitMatrix bitMatrix;
        Bitmap image=null;
        try {
            bitMatrix=new MultiFormatWriter ().encode (content, BarcodeFormat.QR_CODE, qrWidth, qrHeight, hints);
            int width=bitMatrix.getWidth ();
            int height=bitMatrix.getHeight ();
            image=Bitmap.createBitmap (width, height, Bitmap.Config.ARGB_8888);
            for (int x=0; x < width; x++) {
                for (int y=0; y < height; y++) {
                    image.setPixel (x, y, bitMatrix.get (x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }

        paint.setColor (Color.BLACK);
        canvas.drawBitmap (image, -40, qrTop, paint);
        canvas.save ();
        canvas.restore ();
        return result;
    }


    /**
     * 生成图片  加上title的图片
     *
     * @param content
     * @param list
     * @return
     */
    public static Bitmap createRightImage(String content, List<String> list) {
        int picWidth=384;//生成图片的宽度
        int picHeight=300;//生成图片的高度
        int titleTextSize=20;
        int textColor=Color.BLACK;
        int qrWidth=200;
        int qrHeight=200;
        int paddingTop=10;
        int textX = 20;
        int textY = 50;

        //最终生成的图片
        Bitmap result=Bitmap.createBitmap (picWidth, picHeight, Bitmap.Config.ARGB_8888);

        Paint paint=new Paint ();
        paint.setColor (Color.WHITE);
        Canvas canvas=new Canvas (result);

        //先画一整块白色矩形块
        canvas.drawRect (0, 0, picWidth, picHeight, paint);

        //画title文字
        Rect bounds=new Rect ();
        paint.setColor (textColor);
        paint.setTextSize (titleTextSize);
        for (int i=0; i < list.size (); i++) {
            //获取文字的字宽高，以便将文字与图片中心对齐
            paint.getTextBounds (list.get (i), 0, list.get (i).length (), bounds);
            canvas.drawText (list.get (i), textX, textY, paint);
            textY+=30;
        }

        //画白色矩形块
        int qrTop=paddingTop;//二维码的顶部高度

        //画二维码
        Hashtable<EncodeHintType, Object> hints=new Hashtable<> ();
        hints.put (EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put (EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put (EncodeHintType.MARGIN, 5);
        BitMatrix bitMatrix;
        Bitmap image=null;
        try {
            bitMatrix=new MultiFormatWriter ().encode (content, BarcodeFormat.QR_CODE, qrWidth, qrHeight, hints);
            int width=bitMatrix.getWidth ();
            int height=bitMatrix.getHeight ();
            image=Bitmap.createBitmap (width, height, Bitmap.Config.ARGB_8888);
            for (int x=0; x < width; x++) {
                for (int y=0; y < height; y++) {
                    image.setPixel (x, y, bitMatrix.get (x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }

        paint.setColor (Color.BLACK);
        canvas.drawBitmap (image, 220, qrTop, paint);

        canvas.save ();
        canvas.restore ();
        return result;
    }



}
