package com.example.nfcdemo.util;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import android.net.Uri;
import android.nfc.NdefRecord;

public class UriRecord {

    private final Uri mUri;// 存uri

    /*
     * uri 格式数据中 第一个字节 存uri的头值映射表
     * 如 0x01 表示uri以 http://www.开头,
     * 存uri的头值是为了省空间.
     */
    public static final Map<Byte, String> URI_PREFIX_MAP = new HashMap<Byte, String>();

    static {
        URI_PREFIX_MAP.put((byte) 0x00, "");
        URI_PREFIX_MAP.put((byte) 0x01, "http://www.");
        URI_PREFIX_MAP.put((byte) 0x02, "https://www.");
        URI_PREFIX_MAP.put((byte) 0x03, "http://");
        URI_PREFIX_MAP.put((byte) 0x04, "https://");
        URI_PREFIX_MAP.put((byte) 0x05, "tel:");
        URI_PREFIX_MAP.put((byte) 0x06, "mailto:");
        URI_PREFIX_MAP.put((byte) 0x07, "ftp://anonymous:anonymous@");
        URI_PREFIX_MAP.put((byte) 0x08, "ftp://ftp.");
        URI_PREFIX_MAP.put((byte) 0x09, "ftps://");
        URI_PREFIX_MAP.put((byte) 0x0A, "sftp://");
        URI_PREFIX_MAP.put((byte) 0x0B, "smb://");
        URI_PREFIX_MAP.put((byte) 0x0C, "nfs://");
        URI_PREFIX_MAP.put((byte) 0x0D, "ftp://");
        URI_PREFIX_MAP.put((byte) 0x0E, "dav://");
        URI_PREFIX_MAP.put((byte) 0x0F, "news:");
        URI_PREFIX_MAP.put((byte) 0x10, "telnet://");
        URI_PREFIX_MAP.put((byte) 0x11, "imap:");
        URI_PREFIX_MAP.put((byte) 0x12, "rtsp://");
        URI_PREFIX_MAP.put((byte) 0x13, "urn:");
        URI_PREFIX_MAP.put((byte) 0x14, "pop:");
        URI_PREFIX_MAP.put((byte) 0x15, "sip:");
        URI_PREFIX_MAP.put((byte) 0x16, "sips:");
        URI_PREFIX_MAP.put((byte) 0x17, "tftp:");
        URI_PREFIX_MAP.put((byte) 0x18, "btspp://");
        URI_PREFIX_MAP.put((byte) 0x19, "btl2cap://");
        URI_PREFIX_MAP.put((byte) 0x1A, "btgoep://");
        URI_PREFIX_MAP.put((byte) 0x1B, "tcpobex://");
        URI_PREFIX_MAP.put((byte) 0x1C, "irdaobex://");
        URI_PREFIX_MAP.put((byte) 0x1D, "file://");
        URI_PREFIX_MAP.put((byte) 0x1E, "urn:epc:id:");
        URI_PREFIX_MAP.put((byte) 0x1F, "urn:epc:tag:");
        URI_PREFIX_MAP.put((byte) 0x20, "urn:epc:pat:");
        URI_PREFIX_MAP.put((byte) 0x21, "urn:epc:raw:");
        URI_PREFIX_MAP.put((byte) 0x22, "urn:epc:");
        URI_PREFIX_MAP.put((byte) 0x23, "urn:nfc:");
    }

    private UriRecord(Uri uri) {
        this.mUri = uri;
    }

    public Uri getUri() {
        return mUri;
    }

    /*
     * 这时,uri数据里没有uri头值,整个uri数据就是一个uri
     */
    private static UriRecord parseAbsolute(NdefRecord ndefRecord) {
        //1,取得数据流
        byte[] payload = ndefRecord.getPayload();
        //2,解析出一个uri
        String uriStr = new String(payload, Charset.forName("UTF-8"));
        Uri uri = Uri.parse(uriStr);
        return new UriRecord(uri);

    }
    /*
     * 解析nfc uri格式的数据
     *
     */
    private static UriRecord parseWellKnown(NdefRecord ndefRecord) {
        //解析uri第1步,判断record中是否是 uri
        if (!Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_URI))
            return null;
        //解析uri第2步,获取字节数据.
        byte[] payload = ndefRecord.getPayload();

        //解析uri第3步,解析出uri头,如https://等
        String prefix = URI_PREFIX_MAP.get(payload[0]);
        //解析uri第4步,解析出uri的内容,如:www.g.cn
        byte[] prefixBytes = prefix.getBytes(Charset.forName("UTF-8"));

        //解析uri第5步,合并uri头各内容.
        byte[] fullUri = new byte[prefixBytes.length + payload.length - 1];
        System.arraycopy(prefixBytes, 0, fullUri, 0, prefixBytes.length);
        System.arraycopy(payload, 1, fullUri, prefixBytes.length,payload.length - 1);
        //解析uri第6步,构造Uri
        String fullUriStr = new String(fullUri, Charset.forName("UTF-8"));
        Uri uri = Uri.parse(fullUriStr);
        //解析uri第7步,
        return new UriRecord(uri);

    }
    /*
     * 解析uri格式数据.
     */
    public static UriRecord parse(NdefRecord record) {
        short tnf = record.getTnf();
        if (tnf == NdefRecord.TNF_WELL_KNOWN) {
            return parseWellKnown(record);
        } else if (tnf == NdefRecord.TNF_ABSOLUTE_URI) {
            return parseAbsolute(record);
        }
        throw new IllegalArgumentException("Unknown TNF " + tnf);
    }
}