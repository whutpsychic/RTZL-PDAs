package com.example.print_sdk.util;

import android.graphics.Bitmap;

/**
 * Created by moxiaomo
 * on 2020/8/18
 */
public class BitmapToByteUtils {

    /*************************************************************************
     * 假设一个360*360的图片，分辨率设为24, 共分15行打印 每一行,是一个 360 * 24 的点阵,y轴有24个点,存储在3个byte里面。
     * 即每个byte存储8个像素点信息。因为只有黑白两色，所以对应为1的位是黑色，对应为0的位是白色
     *
     * Suppose a 360*360 picture, the resolution is set to 24, a total of 15 lines are printed.
     * Each line is a 360 * 24 dot matrix, the y-axis has 24 points, stored in 3 bytes.
     * That is, each byte stores 8 pixel information.
     * Because there are only black and white, the bit corresponding to 1 is black, and the bit corresponding to 0 is white
     **************************************************************************/
    public static byte[] draw2PxPoint(Bitmap bitmap) {
        int scaleHeight=bitmap.getHeight ();
        //宽度要是8的倍数
        int bitWidth=(bitmap.getWidth () + 7) / 8 * 8;
        int width=bitmap.getWidth ();
        int data[]=new int[width * scaleHeight];
        byte dataVec[]=new byte[bitWidth * scaleHeight / 8 + 8];
        long start=System.currentTimeMillis ();
        dataVec[0]=0x1D;
        dataVec[1]=0x76;
        dataVec[2]=0x30;
        dataVec[3]=0;
        dataVec[4]=(byte) (bitWidth / 8 % 256);
        dataVec[5]=(byte) (bitWidth / 8 / 256);
        dataVec[6]=(byte) (scaleHeight % 256);
        dataVec[7]=(byte) (scaleHeight / 256);
        int k=8;
        bitmap.getPixels (data, 0, width, 0, 0, width, scaleHeight);
        for (int h=0; h < scaleHeight; h++) {
            for (int w=0; w < bitWidth; w+=8) {
                int value=0;
                for (int i=0; i < 8; i++) {
                    int index=h * width + w + i;
                    if (w + i >= width) {
                        // 超度图片的大小零填充
                        value|=0;
                    } else {
                        //这里就是高低在前低位在后
                        value|=px2Byte (data[index]) << (7 - i);
                    }
                }
                dataVec[k++]=(byte) value;
            }
        }
        return dataVec;
    }

    public static byte px2Byte(int pixel) {

        byte b;
        int red=(pixel & 0x00ff0000) >> 16; // 取高两位
        int green=(pixel & 0x0000ff00) >> 8; // 取中两位
        int blue=pixel & 0x000000ff; // 取低两位
        int gray=RGB2Gray (red, green, blue);
        if (gray < 127) {
            b=1;
        } else {
            b=0;
        }
        return b;
    }

    /**
     * 图片二值化，黑色是1，白色是0
     * Image binarization, black is 1, white is 0
     *
     * @param x   横坐标 Abscissa
     * @param y   纵坐标 Y-axis
     * @param bit 位图 bitmap
     * @return
     */
    private static byte px2Byte(int x, int y, Bitmap bit) {
        if (x < bit.getWidth () && y < bit.getHeight ()) {
            byte b;
            int pixel=bit.getPixel (x, y);
            int red=(pixel & 0x00ff0000) >> 16; // 取高两位 High two
            int green=(pixel & 0x0000ff00) >> 8; // 取中两位 Hit two
            int blue=pixel & 0x000000ff; // 取低两位 Lower two
            int gray=RGB2Gray (red, green, blue);
            if (gray < 128) {
                b=1;
            } else {
                b=0;
            }
            return b;
        }
        return 0;
    }

    /**
     * 图片灰度的转化
     * Image grayscale conversion
     */
    private static int RGB2Gray(int r, int g, int b) {
        //int gray=(int) (0.29900 * r + 0.58700 * g + 0.11400 * b); // 灰度转化公式 Grayscale conversion formula
        int gray=(int) ((float) r * 0.3 + (float) g * 0.59 + (float) b * 0.11);
        return gray;
    }
}
