package com.example.print_sdk;


import android.util.Log;

import com.example.print_sdk.enums.ALIGN_MODE;
import com.example.print_sdk.enums.BARCODE_1D_TYPE;
import com.example.print_sdk.enums.FONT_ID;
import com.example.print_sdk.enums.MODE_ENLARGE;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;

// *-------------------------------------------------------------------------------------------------------------------------------------*//
// ESC command 
// *-------------------------------------------------------------------------------------------------------------------------------------*//
public class EscUtils {

    private final static String TAG="ESC";


    // Esc Data length
    private int mEscLength=0;
    // Esc Data buffer
    private byte[] mEscBuf=null;

    public synchronized byte[] getEscBuffer() {
        byte[] buffer=new byte[mEscLength];
        System.arraycopy (mEscBuf, 0, buffer, 0, mEscLength);
        return buffer;
    }

    // *--------------------------------------------------------------------------------------------------------------------------------------*//
    // Name：Esc
    // Function：Creat ESC Object.
    // param：Esc Buffer length；
    // return：none
    // *--------------------------------------------------------------------------------------------------------------------------------------*//
    public EscUtils(int len) {
        mEscBuf=new byte[len];
        mEscLength=0;
    }

    // *-------------------------------------------------------------------------------------------------------------------------------------*//
    // Name：esc_buffer_clear
    // Function：Clear ESC buffer
    // *-------------------------------------------------------------------------------------------------------------------------------------*//
    public synchronized void esc_buffer_clear() {
        Array.setByte (mEscBuf, mEscBuf.length, (byte) 0);
        mEscLength=0;
    }

    // *-------------------------------------------------------------------------------------------------------------------------------------*//
    // Name：esc_length_get
    // Function：Get ESC length
    // return：ESC data length；
    // *-------------------------------------------------------------------------------------------------------------------------------------*//
    public synchronized int esc_length_get() {
        return mEscLength;
    }


    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    // Name：esc_init_print
    // Function：Initialize printer 初始化打印机
    // param：无；
    // return：ESC data length；
    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    public synchronized int esc_init_print() {
        mEscBuf[mEscLength++]=0x1B; // ESC
        mEscBuf[mEscLength++]=0x40; // @
        return mEscLength;
    }

    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    // Name：esc_default_print
    // Function：恢复出厂默认
    // param：无；
    // return：ESC data length；
    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    public synchronized int esc_default_print() {
        mEscBuf[mEscLength++]=0x1B;
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x52;
        mEscBuf[mEscLength++]=0x54;
        mEscBuf[mEscLength++]=0x46;
        mEscBuf[mEscLength++]=0x41;
        return mEscLength;
    }

    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    // Name：esc_set_black
    // Function：esc_set_black 退纸
    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    public synchronized int esc_set_black(int param1, int param2) {
        mEscBuf[mEscLength++]=0x1B;
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x54;
        mEscBuf[mEscLength++]=0x45;
        mEscBuf[mEscLength++]=0x41;
        mEscBuf[mEscLength++]=0x52;
        mEscBuf[mEscLength++]=(byte) param1;
        mEscBuf[mEscLength++]=(byte) param2;
        return mEscLength;
    }

    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    // Name：esc_set_concentration
    // Function：set print concentration  设置打印浓度
    // param： concentration level level [0~39]
    // return：ESC data length；
    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    public synchronized int esc_set_concentration(int level) {

        mEscBuf[mEscLength++]=0x1B;
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x53;
        mEscBuf[mEscLength++]=0x54;
        mEscBuf[mEscLength++]=0x44;
        mEscBuf[mEscLength++]=0x50;
        mEscBuf[mEscLength++]=(byte) level;

        return mEscLength;
    }

    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    // Name：esc_set_concentration
    // Function：使能一票一控功能
    // param： level n = 0x30 n = 0x31(能使)
    // return：ESC data length；
    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    public synchronized int esc_open_one_vote_one_control(int level) {
        mEscBuf[mEscLength++]=0x1B;
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x46;
        mEscBuf[mEscLength++]=0x54;
        mEscBuf[mEscLength++]=0x4B;
        mEscBuf[mEscLength++]=0x54;
        if (level == 0) {
            mEscBuf[mEscLength++]=0x30;
        } else if (level == 1) {
            mEscBuf[mEscLength++]=0x31;
        }
        return mEscLength;
    }


    // *-----------------------------------------------------------------------------------------------------------------------------------*//

    /**
     * Name：esc_set_sn
     * Function：One vote, one control；一票一控头;
     * param：
     */
    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    public synchronized int esc_set_sn(byte[] data) {

        mEscBuf[mEscLength++]=0x1D;
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x53;
        for (int i=0; i < data.length; i++) {
            mEscBuf[mEscLength++]=data[i];
        }
        return mEscLength;
    }

    // *-----------------------------------------------------------------------------------------------------------------------------------*//

    /**
     * Name：esc_set_sn_end
     * Function：One vote, one tail；一票一控尾;
     * param：
     */
    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    public synchronized int esc_set_sn_end() {
        mEscBuf[mEscLength++]=0x1D;
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x45;
        return mEscLength;
    }

    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    // Name：esc_print_line_space
    // Function：Select default line spacing；选择默认行间距;
    // param：Default  line spacing mode，Default = true,Select default line spacing，ignore Param；
    // 若Default = false ,  Set line spacing for[n × 0.125mm]. 。
    // Param In standard mode, the vertical motion unit (y) is used;
    // return：ESC data length；
    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    public synchronized int esc_print_line_space(boolean Default, byte Param) {
        mEscBuf[mEscLength++]=0x1B;
        if (Default) {
            mEscBuf[mEscLength++]=0x32;
            return mEscLength;
        } else {
            mEscBuf[mEscLength++]=0x33;
            mEscBuf[mEscLength++]=Param;
            return mEscLength;
        }
    }


    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    // Name：esc_main_mark
    // Function： enable black mark detection 启用/禁用黑标检测
    // return：ESC data length；
    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    public synchronized int esc_enable_mark(boolean bool) {
        mEscBuf[mEscLength++]=0x1F;
        mEscBuf[mEscLength++]=0x1B;
        mEscBuf[mEscLength++]=0x1F;
        mEscBuf[mEscLength++]=(byte) 0x80;
        mEscBuf[mEscLength++]=0x04;
        mEscBuf[mEscLength++]=0x05;
        mEscBuf[mEscLength++]=0x06;
        if (bool) {
            mEscBuf[mEscLength++]=0x44;
        } else {
            mEscBuf[mEscLength++]=0x66;
        }
        return mEscLength;
    }

    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    // Name：esc_main_mark
    // Function：Go to the next black mark and start printing. 转到下一个黑色标记并开始打印。
    // return：ESC data length；
    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    public synchronized int esc_main_mark() {
        mEscBuf[mEscLength++]=0x1D;
        mEscBuf[mEscLength++]=0x0C;
        return mEscLength;
    }


    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    // Name：esc_feature_list
    // Function： Print function list   打印功能列表
    // return：The function of a printer.
    // *-----
    public synchronized int esc_feature_list() {
        mEscBuf[mEscLength++]=0x1B;
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x46;
        return mEscLength;
    }

    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    // Name：esc_encode_set
    // Function：Set encoding type  设置编码类型
    // *-----
    public synchronized int esc_set_encode(byte encode) {
        mEscBuf[mEscLength++]=0x1B;
//        mEscBuf[mEscLength++]=0x00; // 退出UNICODE需要添加次字节，并且编码设置为3
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x43;
        mEscBuf[mEscLength++]=0x44;
        mEscBuf[mEscLength++]=0x54;
        mEscBuf[mEscLength++]=0x59;
        // mEscBuf[mEscLength++]=encode;
        if (encode == 1) { // 设置UNICODE会乱码，固encode==1的时候，实际设置为2
            mEscBuf[mEscLength++]=(byte) 2;
        } else {
            mEscBuf[mEscLength++]=encode;
        }

        return mEscLength;
    }


    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    // Name：esc_exit_unicode
    // Function：esc_exit_unicode 退出UNICODE
    // *-----
    public synchronized int esc_exit_unicode() {
        mEscBuf[mEscLength++]=0x1B;
        mEscBuf[mEscLength++]=0x00; // 退出UNICODE需要添加次字节，并且编码设置为3
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x43;
        mEscBuf[mEscLength++]=0x44;
        mEscBuf[mEscLength++]=0x54;
        mEscBuf[mEscLength++]=0x59;
        mEscBuf[mEscLength++]=0x03;
        return mEscLength;
    }

    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    // Name：esc_language_switck
    // Function：language switch 语言选项
    // *-----
    public synchronized int esc_set_language(byte encode) {
        mEscBuf[mEscLength++]=0x1B;
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x53;
        mEscBuf[mEscLength++]=0x4C;
        mEscBuf[mEscLength++]=0x41;
        mEscBuf[mEscLength++]=0x4E;
        mEscBuf[mEscLength++]=encode;
        return mEscLength;
    }



    /**
     * 打印语言列表
     * esc_print_languageList
     *
     * @return
     */
    // *-------------------------------------------------------------------------------------------------------
    public synchronized int esc_print_languageList() {
        mEscBuf[mEscLength++]=0x1B;
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x48;
        return mEscLength;
    }


    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    // Name：esc_text_print
    // Function：Print text 打印文本
    // param： Text string need to be printed.
    // return：ESC data length；
    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    public synchronized int esc_text_print(String Text) {

        byte[] SourceTextGBK_Bytes=null;
        try {
            SourceTextGBK_Bytes=Text.getBytes ("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace ();
        }
        int SourceGBK_Length=SourceTextGBK_Bytes.length;
        System.arraycopy (SourceTextGBK_Bytes, 0, mEscBuf, mEscLength, SourceGBK_Length);
        mEscLength+=SourceGBK_Length;
        return mEscLength;
    }

    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    // Name：esc_enter_print
    // Function：Print CR. 打印CR，转行
    // return：ESC data length；
    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    public synchronized int esc_enter_print() {
        mEscBuf[mEscLength++]=0x0D;
        mEscBuf[mEscLength++]=0x0A;
        return mEscLength;
    }

    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    // Name：esc_feed_paper
    // Function：esc_feed_paper 走纸
    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    public synchronized int esc_feed_paper(boolean LineFlag, int length) {
        mEscBuf[mEscLength++]=0x1B;
        if (LineFlag)
            mEscBuf[mEscLength++]=0x64;
        else
            mEscBuf[mEscLength++]=0x4A;
        mEscBuf[mEscLength++]=(byte) length;
        return mEscLength;
    }


    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    // Name：esc_char_enlarger
    // Function：Set character size ；设置字符大小
    // param：Param mode；
    // return：ESC data length；
    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    public synchronized int esc_char_enlarge(MODE_ENLARGE mode) {
        mEscBuf[mEscLength++]=0x1D;
        mEscBuf[mEscLength++]=0x21;
        mEscBuf[mEscLength++]=(byte) mode.Get ();
        return mEscLength;
    }

    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    // Name：esc_char_enlarger
    // Function：Select character font; 选择字符字体;
    // param：id font number；
    // return：ESC data length；
    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    public synchronized int esc_char_font(FONT_ID id) {
        mEscBuf[mEscLength++]=0x1B;
        mEscBuf[mEscLength++]=0x4D;
        mEscBuf[mEscLength++]=(byte) id.Get ();
        return mEscLength;
    }


    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    // Name：esc_print_align
    // Function：Select justification；对齐方式；
    // param：AlignMode Justification Mode；
    // return：ESC data length；
    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    public synchronized int esc_print_align(ALIGN_MODE AlignMode) {
        mEscBuf[mEscLength++]=0x1B; // ESC
        mEscBuf[mEscLength++]=0x61; // a(Align)
        mEscBuf[mEscLength++]=(byte) AlignMode.Get ();
        return mEscLength;
    }

    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    // Name：esc_print_align
    // Function：Set left margin ；设置左边距
    // param：Param value；
    // return：ESC data length；
    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    public synchronized int esc_print_left_margin(short Param) {
        mEscBuf[mEscLength++]=0x1D; // ESC
        mEscBuf[mEscLength++]=0x4C; // a(Align)
        mEscBuf[mEscLength++]=(byte) Param;
        mEscBuf[mEscLength++]=(byte) (Param >> 8);
        return mEscLength;
    }


    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    // Name：esc_print_hri
    // Function：
    // param：Param value；
    // return：ESC data length；
    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    public synchronized int esc_print_hri() {
        mEscBuf[mEscLength++]=0x1D; // ESC
        mEscBuf[mEscLength++]=0x48; // a(Align)
        mEscBuf[mEscLength++]=0x00;
        mEscBuf[mEscLength++]=0x30;
        return mEscLength;
    }


    // *------------------------------------------------------------------------------------------------------------------------------------*//
    // Name：esc_barcode_height
    // Function：Selects the height of the bar code. 选择条形码的高度。
    // param：Height specifies the number of dots in the vertical direction.
    // return：ESC data length；
    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    public synchronized int esc_barcode_height(int Height) {
        mEscBuf[mEscLength++]=0x1D; // GS
        mEscBuf[mEscLength++]=0x68; // h(height)
        mEscBuf[mEscLength++]=(byte) Height;
        return mEscLength;
    }

    // *------------------------------------------------------------------------------------------------------------=-----------------------*//
    // Name：esc_barcode_width
    // Function：Set the horizontal size of the bar code；选择条码宽度
    // param：Width  [1，4]
    // Width value 1 2 3 4
    // width(mm) 0.125 0.250 0.375 0.500
    // return：ESC data length；
    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    public synchronized int esc_barcode_width(int Width) {
        if ((Width == 0) || Width > 4)
            return mEscLength;
        mEscBuf[mEscLength++]=0x1D; // GS
        mEscBuf[mEscLength++]=0x77; // w(width)
        mEscBuf[mEscLength++]=(byte) Width;
        return mEscLength;
    }


    // *----------------------------------------------------------------------------------------------------------------*//
    // Name：esc_1D_barcode_print
    // Function：Print barcode；打印条码
    // param：CodeText   barcode string
    // BarcodeID barcode type
    // return：ESC data length；
    // *----------------------------------------------------------------------------------------------------------------*//
    public synchronized int esc_barcode_1D_print(String CodeText, BARCODE_1D_TYPE BarcodeID) {
        switch (BarcodeID) {
            case UPCA:
                return esc_upca_print (CodeText);
            case UPCE:
                return esc_upce_print (CodeText);
            case EAN8:
                return esc_ean8_print (CodeText);
            case EAN13:
                return esc_ean13_print (CodeText);
            case CODE39:
                return esc_code39_print (CodeText);
            case ITF25:
                return esc_itf25_print (CodeText);
            case CODE93:
                return esc_code93_print (CodeText);
            default:
            case CODE128:
                return esc_code128_print (CodeText);
        }
    }

    // *----------------------------------------------------------------------------------------------------------------*//
    // Name：esc_barcode_2D_print
    // Function：Print QR- CODE；打印QR
    // param：pix,unit,level
    // CodeText String
    // return：ESC data length；
    // *----------------------------------------------------------------------------------------------------------------*//
    public synchronized int esc_barcode_2D_print(int pix, int unit, int level, ALIGN_MODE AlignMode, String CodeText) {
        short dataLen=0;
        try {
            dataLen=(short) CodeText.getBytes ("UTF-8").length;
            Log.e (TAG, "esc_barcode_2D_print: " + dataLen);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace ();
            return -1;
        }
        dataLen+=3;
        byte pl=(byte) (dataLen & 0x00ff);
        byte ph=(byte) ((dataLen & 0xff00) >> 8);

        mEscBuf[mEscLength++]=0x0D;
        mEscBuf[mEscLength++]=0x0A;
        // QR Code pixel size n>=1 , n<=24
        // QR码像素大小
        mEscBuf[mEscLength++]=0x1B; // GS
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x51;
        mEscBuf[mEscLength++]=0x50;
        mEscBuf[mEscLength++]=0x49;
        mEscBuf[mEscLength++]=0x58;
        mEscBuf[mEscLength++]=(byte) pix;

        mEscBuf[mEscLength++]=0x1B; // ESC
        mEscBuf[mEscLength++]=0x61; // a(Align)
        mEscBuf[mEscLength++]=(byte) AlignMode.Get ();

        // QR Code Unin size 1≤n ≤16
        // 二维码尺寸
        mEscBuf[mEscLength++]=0x1D; // GS
        mEscBuf[mEscLength++]=0x28;
        mEscBuf[mEscLength++]=0x6B;
        mEscBuf[mEscLength++]=0x03;
        mEscBuf[mEscLength++]=0x00;
        mEscBuf[mEscLength++]=0x31;
        mEscBuf[mEscLength++]=0x43;
        mEscBuf[mEscLength++]=(byte) unit;

        // QR Code Error correction level
        // QR码纠错等级
        mEscBuf[mEscLength++]=0x1D; // GS
        mEscBuf[mEscLength++]=0x28;
        mEscBuf[mEscLength++]=0x6B;
        mEscBuf[mEscLength++]=0x03;
        mEscBuf[mEscLength++]=0x00;
        mEscBuf[mEscLength++]=0x31;
        mEscBuf[mEscLength++]=0x45;
        mEscBuf[mEscLength++]=(byte) level;

        // QR Code Transfer data to the encoding cache
        // QR Code将数据传输到编码缓存
        mEscBuf[mEscLength++]=0x1D; // GS
        mEscBuf[mEscLength++]=0x28;
        mEscBuf[mEscLength++]=0x6B;
        mEscBuf[mEscLength++]=pl;// PL
        mEscBuf[mEscLength++]=ph;// PH
        mEscBuf[mEscLength++]=0x31;
        mEscBuf[mEscLength++]=0x50;
        mEscBuf[mEscLength++]=0x30;
        esc_text_print (CodeText);

        // QR Code Print 2d barcode in code cache.
        // QR Code在代码缓存中打印二维条码。
        dataLen-=3;
        pl=(byte) (dataLen & 0x00ff);
        ph=(byte) ((dataLen & 0xff00) >> 8);
        mEscBuf[mEscLength++]=0x1D; // GS
        mEscBuf[mEscLength++]=0x28;
        mEscBuf[mEscLength++]=0x6B;
        mEscBuf[mEscLength++]=pl;// PL
        mEscBuf[mEscLength++]=ph;// PH
        mEscBuf[mEscLength++]=0x31;
        mEscBuf[mEscLength++]=0x51;
        mEscBuf[mEscLength++]=0x30;

        return mEscLength;
    }


    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    // Name：esc_char_bold
    // Function：Turn emphasized mode on/off; 打开/关闭强调模式;
    // param：Bold mode, Bold = true,emphasized mode is turned on，Bold = false, normal；
    // return：ESC data length；
    // *-----------------------------------------------------------------------------------------------------------------------------------*//
    public synchronized int esc_char_bold(boolean Bold) {
        mEscBuf[mEscLength++]=0x1B;
        mEscBuf[mEscLength++]=0x45;
        if (Bold)
            mEscBuf[mEscLength++]=0x01;
        else
            mEscBuf[mEscLength++]=0x00;
        return mEscLength;
    }


    /**
     * Name：esc_no_paper_print_status 无纸时，打印机状态
     * Type: 0无纸时打印机停止打印，并把缓冲区的数据清掉;1 无纸时打印机停止打印，保留缓冲区的数据当有纸时继续把缓冲
     * 区的数据打印
     *
     * @return
     */
    public synchronized int esc_no_paper_print_status(byte type) {
        mEscBuf[mEscLength++]=0x1B;
        mEscBuf[mEscLength++]=0x36;
        mEscBuf[mEscLength++]=type; // 0 clear;1 save
        return mEscLength;
    }


    /**
     * 选择传感器允许纸将尽停止打印
     *
     * @return
     */
    public synchronized int esc_select_sensor_stop_print() {
        mEscBuf[mEscLength++]=0x1B;
        mEscBuf[mEscLength++]=0x63;
        mEscBuf[mEscLength++]=0x34;
        mEscBuf[mEscLength++]=0x02; // 01 ，02
        return mEscLength;
    }


    /**
     * 打印状态
     * esc_print_state
     *
     * @return
     */
    public synchronized int esc_print_state() {
        mEscBuf[mEscLength++]=0x1D;
        mEscBuf[mEscLength++]=0x61;
        mEscBuf[mEscLength++]=0x22;
        return mEscLength;
    }


    /**
     * 获取固件版本好
     * esc_lib_version
     *
     * @return
     */
    public synchronized int esc_lib_version() {
        mEscBuf[mEscLength++]=0x1D;
        mEscBuf[mEscLength++]=0x49;
        mEscBuf[mEscLength++]=0x41;
        return mEscLength;
    }


    /***
     * 打印测试
     * esc_test
     * @return
     */
    public synchronized int esc_test() {
        mEscBuf[mEscLength++]=0x1D;
        mEscBuf[mEscLength++]=0x67;
        mEscBuf[mEscLength++]=0x36;
        return mEscLength;
    }


    // *--------------1D code bar-------------------------------------------------------------------------------------*//
    // *----------------------------------------------------------------------------------------------------------------*//
    // Name：esc_upca_print
    // Function：UPCA
    // param：CodeText String
    // return：ESC data length；
    // *----------------------------------------------------------------------------------------------------------------*//
    private synchronized int esc_upca_print(String CodeText) {
        int dataLen=0;
        try {
            dataLen=CodeText.getBytes ("GB2312").length;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace ();
            return -1;
        }
        mEscBuf[mEscLength++]=0x1D; // GS
        mEscBuf[mEscLength++]=0x6B; // k
        mEscBuf[mEscLength++]=(byte) BARCODE_1D_TYPE.UPCA.Get (); // 65
        mEscBuf[mEscLength++]=(byte) dataLen;// n
        esc_text_print (CodeText);
        // mEscBuf[mEscLength++] = 0x00;
        return mEscLength;
    }

    // *----------------------------------------------------------------------------------------------------------------*//
    // Name：esc_upce_print
    // Function：UPCE
    // param：CodeText String
    // return：ESC data length；
    // *----------------------------------------------------------------------------------------------------------------*//
    private synchronized int esc_upce_print(String CodeText) {
        int dataLen=0;
        try {
            dataLen=CodeText.getBytes ("GB2312").length;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace ();
            return -1;
        }
        mEscBuf[mEscLength++]=0x1D; // GS
        mEscBuf[mEscLength++]=0x6B; // k
        mEscBuf[mEscLength++]=(byte) BARCODE_1D_TYPE.UPCE.Get (); // 66
        mEscBuf[mEscLength++]=(byte) dataLen;// n
        esc_text_print (CodeText);

        return mEscLength;
    }

    // *----------------------------------------------------------------------------------------------------------------*//
    // Name：esc_ean8_print
    // Function：EAN8
    // param：CodeText String
    // return：ESC data length；
    // *----------------------------------------------------------------------------------------------------------------*//
    private synchronized int esc_ean8_print(String CodeText) {
        int dataLen=0;
        try {
            dataLen=CodeText.getBytes ("GB2312").length;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace ();
            return -1;
        }
        mEscBuf[mEscLength++]=0x1D; // GS
        mEscBuf[mEscLength++]=0x6B; // k
        mEscBuf[mEscLength++]=(byte) BARCODE_1D_TYPE.EAN8.Get ();
        mEscBuf[mEscLength++]=(byte) dataLen;// n
        esc_text_print (CodeText);
        // mEscBuf[mEscLength++] = 0x00;
        return mEscLength;
    }

    // *----------------------------------------------------------------------------------------------------------------*//
    // Name：esc_ean13_print
    // Function：EAN13
    // param：CodeText String
    // return：ESC data length；
    // *----------------------------------------------------------------------------------------------------------------*//
    private synchronized int esc_ean13_print(String CodeText) {
        int dataLen=0;
        try {
            dataLen=CodeText.getBytes ("GB2312").length;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace ();
            return -1;
        }
        mEscBuf[mEscLength++]=0x1D; // GS
        mEscBuf[mEscLength++]=0x6B; // k
        mEscBuf[mEscLength++]=(byte) BARCODE_1D_TYPE.EAN13.Get ();
        mEscBuf[mEscLength++]=(byte) dataLen;// n
        esc_text_print (CodeText);
        // mEscBuf[mEscLength++] = 0x00;
        return mEscLength;
    }

    // *----------------------------------------------------------------------------------------------------------------*//
    // Name：esc_code39_print
    // Function：CODE39
    // param：CodeText String
    // return：ESC data length；
    // *----------------------------------------------------------------------------------------------------------------*//
    private synchronized int esc_code39_print(String CodeText) {
        int dataLen=0;
        try {
            dataLen=CodeText.getBytes ("GB2312").length;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace ();
            return -1;
        }
        mEscBuf[mEscLength++]=0x1D; // GS
        mEscBuf[mEscLength++]=0x6B; // k
        mEscBuf[mEscLength++]=(byte) BARCODE_1D_TYPE.CODE39.Get (); // 4
        mEscBuf[mEscLength++]=(byte) dataLen;// n
        esc_text_print (CodeText);
        // mEscBuf[mEscLength++] = 0x2A; // '*'
        // esc_text_print(CodeText);
        // mEscBuf[mEscLength++] = 0x2A; // '*'
        // mEscBuf[mEscLength++] = 0x00;
        return mEscLength;
    }

    // *----------------------------------------------------------------------------------------------------------------*//
    // Name：esc_itf25_print
    // Function：ITF25
    // param：CodeText String
    // return：ESC data length；
    // *----------------------------------------------------------------------------------------------------------------*//
    private synchronized int esc_itf25_print(String CodeText) {
        int dataLen=0;
        try {
            dataLen=CodeText.getBytes ("GB2312").length;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace ();
            return -1;
        }
        mEscBuf[mEscLength++]=0x1D; // GS
        mEscBuf[mEscLength++]=0x6B; // k
        mEscBuf[mEscLength++]=(byte) BARCODE_1D_TYPE.ITF25.Get ();
        mEscBuf[mEscLength++]=(byte) dataLen;// n
        esc_text_print (CodeText);
        // mEscBuf[mEscLength++] = 0x00;
        return mEscLength;
    }

    // *----------------------------------------------------------------------------------------------------------------*//
    // Name：esc_code93_print
    // Function：CODE93
    // param：CodeText String
    // return：ESC data length；
    // *----------------------------------------------------------------------------------------------------------------*//
    private synchronized int esc_code93_print(String CodeText) {
        int dataLen=0;
        try {
            dataLen=CodeText.getBytes ("GB2312").length;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace ();
            return -1;
        }
        mEscBuf[mEscLength++]=0x1D; // GS
        mEscBuf[mEscLength++]=0x6B; // k
        mEscBuf[mEscLength++]=(byte) BARCODE_1D_TYPE.CODE93.Get ();
        mEscBuf[mEscLength++]=(byte) dataLen;// n
        esc_text_print (CodeText);
        // mEscBuf[mEscLength++] = 0x00;
        return mEscLength;
    }

    // *----------------------------------------------------------------------------------------------------------------*//
    // Name：esc_code128_print
    // Function：CODE128
    // param：CodeText String
    // return：ESC data length；
    // *----------------------------------------------------------------------------------------------------------------*//
    private synchronized int esc_code128_print(String CodeText) {
        int dataLen=0;
        try {
            dataLen=CodeText.getBytes ("GB2312").length;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace ();
            return -1;
        }

        mEscBuf[mEscLength++]=0x1D; // GS
        mEscBuf[mEscLength++]=0x6B; // k
        mEscBuf[mEscLength++]=(byte) BARCODE_1D_TYPE.CODE128.Get (); // 24;
        mEscBuf[mEscLength++]=(byte) dataLen;// n
        esc_text_print (CodeText + "\n");
        // mEscBuf[mEscLength++] = 0x00;
        return mEscLength;
    }

    // =================================================== psam ========================================================

    /**
     * // TODO get psam info
     * Name：esc_psam_info
     * Function：psam card reset psam卡复位
     * @param type Card 1, Card 2 卡1、卡2
     * @return
     */
    public synchronized int esc_psam_info(int type) {
        mEscBuf[mEscLength++]=0x1B;
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x50;
        mEscBuf[mEscLength++]=0x53;
        mEscBuf[mEscLength++]=0x41;
        mEscBuf[mEscLength++]=0x4D;
        if (type == 1) {
            mEscBuf[mEscLength++]=0x31;
            mEscBuf[mEscLength++]=0x00;
        } else if (type == 2) {
            mEscBuf[mEscLength++]=0x32;
            mEscBuf[mEscLength++]=0x00;
        }

        return mEscLength;
    }


    /**
     * // TODO send psam data
     * apdu CLA  INS  P1  P2  Lc  Data  Le
     * Name：esc_psam_send
     * Function：esc_psam_send
     * @param type Card 1, Card 2 卡1、卡2
     * @param datas apdu command
     * @param length apdu length
     * @return
     */
    public synchronized int esc_psam_send(int type, byte[] datas, int length) {
        if (type == 1) {
            mEscBuf[mEscLength++]=0x1B;
            mEscBuf[mEscLength++]=0x23;
            mEscBuf[mEscLength++]=0x23;
            mEscBuf[mEscLength++]=0x50;
            mEscBuf[mEscLength++]=0x53;
            mEscBuf[mEscLength++]=0x41;
            mEscBuf[mEscLength++]=0x4D;
            mEscBuf[mEscLength++]=0x31;
            mEscBuf[mEscLength++]=(byte) length;// n
            for (int i=0; i < length; i++) {
                mEscBuf[mEscLength++]=datas[i];
            }
        } else if (type == 2) {
            mEscBuf[mEscLength++]=0x1B;
            mEscBuf[mEscLength++]=0x23;
            mEscBuf[mEscLength++]=0x23;
            mEscBuf[mEscLength++]=0x50;
            mEscBuf[mEscLength++]=0x53;
            mEscBuf[mEscLength++]=0x41;
            mEscBuf[mEscLength++]=0x4D;
            mEscBuf[mEscLength++]=0x32;
            mEscBuf[mEscLength++]=(byte) length;// n
            for (int i=0; i < length; i++) {
                mEscBuf[mEscLength++]=datas[i];
            }
        }
        return mEscLength;
    }

    // =======================================================================================================================
}
