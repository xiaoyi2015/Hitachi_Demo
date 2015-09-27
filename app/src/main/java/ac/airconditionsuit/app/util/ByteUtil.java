package ac.airconditionsuit.app.util;


/**
 * Created by ac on 9/24/15.
 */
public class ByteUtil {
    static public short byteArrayToShort(byte[] bytes, int offset){
        return (short) (bytes[offset] + bytes[offset + 1] * 256);
    }
}
