package ac.airconditionsuit.app.network.socket.socketpackage.Tcp;

import ac.airconditionsuit.app.util.ByteUtil;

import java.io.UnsupportedEncodingException;

/**
 * Created by ac on 10/10/15.
 */
public class TCPHeartBeatPackage extends TcpPackage {
    public static final byte HEART_BEAT_MSG_TYPE = 0x6;
    public static final int MIDDLE_LENGTH = 9;

    private Long cust_id;

    public TCPHeartBeatPackage(Long cust_id) {
        this.cust_id = cust_id;
        msg_type = HEART_BEAT_MSG_TYPE;
    }

    @Override
    protected byte[] getMiddleBytes() throws UnsupportedEncodingException {
        byte[] result = new byte[MIDDLE_LENGTH];
        //login type always 1
        result[0] = 1;

        //cust_id
        System.arraycopy(ByteUtil.longToByteArray(cust_id), 0, result, 1, 8);

        return result;
    }

    @Override
    protected short getMiddleLength() {
        return MIDDLE_LENGTH;
    }
}
