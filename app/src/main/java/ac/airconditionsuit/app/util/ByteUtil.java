package ac.airconditionsuit.app.util;


import android.util.Log;

/**
 * Created by ac on 9/24/15.
 */
public class ByteUtil {
    public static final String TAG = "ByteUtil";

    static public short byteArrayToShort(byte[] bytes, int offset) {
        return (short) (bytes[offset] + bytes[offset + 1] * 256);
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
}
