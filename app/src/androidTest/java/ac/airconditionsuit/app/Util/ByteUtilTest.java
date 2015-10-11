package ac.airconditionsuit.app.Util;

import ac.airconditionsuit.app.util.ByteUtil;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by ac on 10/11/15.
 */
@RunWith(AndroidJUnit4.class)
public class ByteUtilTest extends Assert{
    @Test
    public void testAboutLong(){
        long l = -12343244323l;
        byte[] lb = ByteUtil.longToByteArray(l);
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putLong(l);
        byte[] bbb = bb.array();
        assertArrayEquals(lb, bbb);
        long ll = ByteUtil.byteArrayToLong(lb);
        assertEquals(l, ll);


//        l = 0x1212121212121212l;
//        lb = ByteUtil.longToByteArray(l);
//        ll = ByteUtil.byteArrayToLong(lb);
//        assert(l == ll);
//
//        l = 0x121212121212121l;
//        lb = ByteUtil.longToByteArray(l);
//        ll = ByteUtil.byteArrayToLong(lb);
//        assert(l == ll);
//
//        l = 0x12fff1212121l;
//        lb = ByteUtil.longToByteArray(l);
//        ll = ByteUtil.byteArrayToLong(lb);
//        assert(l == ll);
    }

    @Test
    public void testAboutShort(){
        short s = -123;
        byte[] sb = ByteUtil.shortToByteArray(s);
        short ss = ByteUtil.byteArrayToShort(sb);
        assertEquals(s, ss);

        s = 123;
        sb = ByteUtil.shortToByteArray(s);
        ss = ByteUtil.byteArrayToShort(sb);
        assertEquals(s, ss);

        s = (short) 0xffff;
        sb = ByteUtil.shortToByteArray(s);
        ss = ByteUtil.byteArrayToShort(sb);
        assertEquals(s, ss);

        s = (short) 0xfff;
        sb = ByteUtil.shortToByteArray(s);
        ss = ByteUtil.byteArrayToShort(sb);
        assertEquals(s, ss);
    }
}
