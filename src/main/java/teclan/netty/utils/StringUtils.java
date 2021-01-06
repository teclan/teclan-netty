package teclan.netty.utils;

import java.io.UnsupportedEncodingException;

public class StringUtils {

    public static byte[] getBytes(String value) throws UnsupportedEncodingException {
        return getBytes(value,"UTF-8");
    }
    public static byte[] getBytes(String value,String coding) throws UnsupportedEncodingException {
        if(value==null){
            return null;
        }
        return value.getBytes(coding);
    }

    public static String getString(byte[] bytes) throws UnsupportedEncodingException {
        return new String(bytes,"UTF-8");
    }

    public static String getString(byte[] bytes,String coding) throws UnsupportedEncodingException {
        return new String(bytes,coding);
    }

}
