package com.example.nfcdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.util.Log;

import com.example.nfcdemo.util.Converter;

import java.io.IOException;
import java.util.Arrays;

public class MifareClassicCard {
    private MifareClassic mClassic = null;
    private Context mContext;


    public MifareClassicCard(MifareClassic mClassic, Context context) {
        this.mClassic = mClassic;
        this.mContext = context;
    }

    public void connect() {
        try {
            mClassic.connect();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    public void colse() throws Exception {
        mClassic.close();
    }

    /**
     * Authenticate a sector with key A.
     */
    public boolean authenticateSectorWithKeyA(int sectorIndex, byte[] key)
            throws Exception {
        return mClassic.authenticateSectorWithKeyA(sectorIndex, key);
    }

    /**
     * Authenticate a sector with key B.
     */
    public boolean authenticateSectorWithKeyB(int sectorIndex, byte[] key)
            throws Exception {
        return mClassic.authenticateSectorWithKeyB(sectorIndex, key);
    }

    /**
     * Return the sector that contains a given block.
     */
    public int blockToSector(int blockIndex) {
        return mClassic.blockToSector(blockIndex);
    }

    /**
     * @param blockIndex
     * @param value
     */
    public void decrement(int blockIndex, int value) {
        try {
            mClassic.decrement(blockIndex, value);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Return the total number of MIFARE Classic blocks.
     */
    public int getBlockCount() {
        return mClassic.getBlockCount();
    }

    /**
     * Return the number of blocks in the given sector.
     */
    public int getBlockCountInSector(int sectorIndex) {
        return mClassic.getBlockCount();
    }

    /**
     * Return the maximum number of bytes that can be sent with
     */
    public int getMaxTransceiveLength() {
        return mClassic.getMaxTransceiveLength();
    }

    /**
     * Return the number of MIFARE Classic sectors.
     */
    public int getSectorCount() {
        return mClassic.getMaxTransceiveLength();
    }

    /**
     * Return the size of the tag in bytes
     */
    public int getSize() {
        return mClassic.getSize();
    }

    /**
     * Return the type of this MIFARE Classic compatible tag.
     */
    public int getType() {
        return mClassic.getType();
    }

    /**
     * Increment a value block, storing the result in the temporary block on the
     * tag.
     */
    public void increment(int blockIndex, int value) {
        try {
            mClassic.increment(blockIndex, value);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * isConnected
     */
    public boolean isConnected() {
        return mClassic.isConnected();
    }

    /**
     * Read 16-byte block.
     */
    public byte[] readBlock(int blockIndex) {
        try {
            return mClassic.readBlock(blockIndex);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Copy from a value block to the temporary block.
     */
    public void restore(int blockIndex) {
        try {
            mClassic.restore(blockIndex);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Return the first block of a given sector.
     */
    public int sectorToBlock(int sectorIndex) {
        return mClassic.sectorToBlock(sectorIndex);
    }

    /**
     * timeout
     */
    public void setTimeout(int timeout) {
        mClassic.setTimeout(timeout);
    }

    /**
     * Send raw NfcA data to a tag and receive the response.
     */
    public byte[] transceive(byte[] data) {
        try {
            return mClassic.transceive(data);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Copy from the temporary block to a value block.
     */
    public void transfer(int blockIndex) {
        try {
            mClassic.transfer(blockIndex);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Write 16-byte block.
     */
    public void writeBlock(int blockIndex, byte[] data) {
        try {
            mClassic.writeBlock(blockIndex, data);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Write data
     */
    @SuppressLint("NewApi")
    public String wirteCarCode(String carCode, int block, String key) {
        boolean isTrue;
        String string = "";
        try {
            if (block % 4 == 3) {
                string = mContext.getResources().getString(R.string.alert_auth_block);
                return string;
            }
            connect();
            int keyblock = block / 4;
            if ("".equals(key)) {
                isTrue = authenticateSectorWithKeyA(keyblock,MifareClassic.KEY_DEFAULT);
            } else {
                byte[] bytes = hexStringToByte(key);
                byte[] keyBytes = Arrays.copyOf(bytes, 6);
                isTrue = authenticateSectorWithKeyA(keyblock, keyBytes);
            }
            byte[] bytes = carCode.trim().getBytes("UTF-8");
            byte[] a = Arrays.copyOf(bytes, 16);
            writeBlock(block, a);
            if (isTrue) {
            } else {
                string = mContext.getResources().getString(R.string.alert_auth_err);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            string = mContext.getResources().getString(R.string.alert_write_err);
            ;
        } finally {
            try {
                colse();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return string;
    }

    /**
     * 读取块数
     */
    @SuppressLint("NewApi")
    public String readCarCode(int block, String key) {
        String string = "";
        boolean isTrue;
        try {
            connect();
            int keyblock = block / 4;
            if ("".equals(key)) {
                isTrue = authenticateSectorWithKeyA(keyblock,MifareClassic.KEY_DEFAULT);
            } else {
                byte[] bytes = hexStringToByte(key);
                byte[] keyBytes = Arrays.copyOf(bytes, 6);
                isTrue = authenticateSectorWithKeyA(keyblock, keyBytes);
            }
            if (isTrue) {
                byte[] data = readBlock(block);
//                String card_number = Converter.getHexString(data, data.length);
//                byte[] hexbytes = new byte[card_number.length() / 2];
//                for (int l = 0; l < card_number.length() / 2; l++) {
//                    int high = Integer.parseInt(card_number.substring(l * 2, l * 2 + 1), 16);
//                    int low = Integer.parseInt(card_number.substring(l * 2 + 1, l * 2 + 2), 16);
//                    hexbytes[l] = (byte) (high * 16 + low);
//                }
//                card_number=new String(hexbytes);//将获得快区数据转化为卡号
                String dataString = bytesToHexString(data);
                string = dataString;
            } else {
                string = mContext.getResources().getString(R.string.alert_auth_err);
            }
        } catch (Exception e) {
            e.printStackTrace();
            string = mContext.getResources().getString(R.string.alert_read_err);
        } finally {
            try {
                colse();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return string;
    }


    /**
     * modify authenticate key.
     */
    @SuppressLint("NewApi")
    public String modifyPassword(int block, String newPassword, String key) {
        boolean isTrue;
        String string = "";
        try {
            if (block % 4 != 3) {
                string = mContext.getResources().getString(R.string.alert_not_auth_block);
                return string;
            }

            connect();
            int keyblock = block / 4;
            if ("".equals(key)) {
                isTrue = authenticateSectorWithKeyA(keyblock, MifareClassic.KEY_DEFAULT);
            } else {
                byte[] bytes = hexStringToByte(key);
                byte[] keyBytes = Arrays.copyOf(bytes, 6);
                isTrue = authenticateSectorWithKeyA(keyblock, keyBytes);
            }
            if (isTrue) {
                byte[] k = readBlock(block);
                byte[] bytes = hexStringToByte(newPassword);
                byte[] a = Arrays.copyOf(bytes, 6);
                for (int i = 0; i < 6; i++) {
                    k[i] = a[i];
                }
                writeBlock(block, k);
            } else {
                string = mContext.getResources().getString(R.string.alert_auth_err);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            string = mContext.getResources().getString(R.string.alert_write_err);
        } finally {
            try {
                colse();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return string;
    }

    // bytesToHexString
    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            System.out.println(buffer);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }

    /**
     * hexStringToByte
     *
     * @return byte[]
     */
    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static int toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }
}
