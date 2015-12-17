package ac.airconditionsuit.app.util;


import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.ResultSet;

/**
 * Created by ac on 9/24/15.
 */
public class ByteUtil {
    public static final String TAG = "ByteUtil";

    static public short byteArrayToShort(byte[] bytes) {
        return byteArrayToShort(bytes, 0);
    }

    static public short byteArrayToShort(byte[] bytes, int offset) {
        short result = 0;
        result |= (bytes[offset] & 0xff);
        result |= ((bytes[offset + 1] << 8) & 0xffff);
        return result;
    }

    static public short byteArrayToShortAsBigEndian(byte[] bytes) {
        return byteArrayToShortAsBigEndian(bytes, 0);
    }

    static public short byteArrayToShortAsBigEndian(byte[] bytes, int offset) {
        short result = 0;
        result |= (bytes[offset + 1] & 0xff);
        result |= ((bytes[offset] << 8) & 0xffff);
        return result;
    }

    public static long byteArrayToLong(byte[] receiveData) {
        return byteArrayToLong(receiveData, 0);
    }

    public static long byteArrayToLong(byte[] receiveData, int start) {
        long result = 0;
        for (int i = start + 7; i >= start; --i) {
            result <<= 8;
            result |= (receiveData[i] & 0xff);
        }
        return result;
    }

    static public byte[] shortToByteArray(short input) {
        byte[] result = new byte[2];
        result[0] = (byte) (input & 0xff);
        result[1] = (byte) (input >>> 8);
        return result;
    }

    static public byte[] shortToByteArrayAsBigEndian(int input) {
        byte[] result = new byte[2];
        result[1] = (byte) (input & 0xff);
        result[0] = (byte) (input >>> 8);
        return result;
    }

    public static byte[] shortToByteArray(long input) {
        byte[] result = new byte[2];
        input &= 0xffff;
        result[0] = (byte) (input & 0xff);
        result[1] = (byte) (input >>> 8);
        return result;
    }

    public static byte[] longToByteArray(Long input) {
        byte[] result = new byte[8];
        int i = 0;
        while (input != 0) {
            result[i++] = (byte) (input & 0xff);
            input >>>= 8;
        }
        return result;
    }

    public static byte[] intToByteArray(int input) {
        byte[] result = new byte[4];
        int i = 0;
        while (input != 0) {
            result[i++] = (byte) (input & 0xff);
            input >>>= 8;
        }
        return result;
    }

    public static byte[] hexStringToByteArray(String s) throws Exception {
        int len = s.length();
        if (len % 2 == 1) {
            Log.e(TAG, "hexString is unComplete");
            throw new Exception("hexString is unComplete");
        }
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String byteArrayToReadableHexString(byte[] bytes) {
        String result = "";
        for (byte b : bytes) {
            int v = b & 0xFF;
            char[] hexChars = new char[2];
            hexChars[0] = hexArray[v >>> 4];
            hexChars[1] = hexArray[v & 0x0F];
            result += (new String(hexChars) + " ");
        }

        return result;
    }

    public static String byteArrayToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] encodeAuthCode(byte[] authCodeBytes) {
        byte[] result = new byte[authCodeBytes.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = (byte) (0xff & (~(0xff & authCodeBytes[i]) + 0x33));
        }
        return result;
    }

    public static byte binToBCD(int hour) {
        byte result = 0;
        result |= (byte) ((hour / 10) << 4);
        result |= (byte) (hour % 10);
        return result;
    }

    public static byte[] timeToBCD(int hour, int min) throws Exception {
        if (hour < 0 || hour > 23) {
            throw new Exception("time error");
        }
        if (min < 0 || min > 59) {
            throw new Exception("time error");
        }
        byte[] result = new byte[4];
        result[0] |= (byte) ((hour / 10) << 4);
        result[0] |= (byte) (hour % 10);
        result[1] |= (byte) ((min / 10) << 4);
        result[1] |= (byte) (min % 10);
        return result;
    }

    public static int BCDByteToInt(byte b) {
        return (b & 0x0f) + ((b & 0xf0) >>> 4) * 10;
    }

}
