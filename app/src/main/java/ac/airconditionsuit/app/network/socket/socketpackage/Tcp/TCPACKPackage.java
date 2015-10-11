package ac.airconditionsuit.app.network.socket.socketpackage.Tcp;

import ac.airconditionsuit.app.network.socket.socketpackage.Udp.UdpPackage;
import ac.airconditionsuit.app.util.ByteUtil;

import java.io.UnsupportedEncodingException;

/**
 * Created by ac on 10/10/15.
 */
public class TCPACKPackage extends TcpPackage {
    public static final byte ACK_MESSAGE_MSG_TYPE = 0x8;
    private static final int MIDDLE_LENGTH = 16;
    private byte[] msg_id;
    private long userId;

    public TCPACKPackage(long userId, byte[] msg_no, byte[] msg_id) {
        msg_type = ACK_MESSAGE_MSG_TYPE;
        this.userId = userId;
        this.msg_no = msg_no;
        this.msg_id = msg_id;
    }

    @Override
    protected byte[] getMiddleBytes() throws UnsupportedEncodingException {
        byte[] result = new byte[MIDDLE_LENGTH];

        //user_id
        System.arraycopy(ByteUtil.longToByteArray(userId), 0, result, 0 ,8);

        //msg_id
        System.arraycopy(msg_id, 0, result, 8 ,8);

        return result;
    }

    @Override
    protected short getMiddleLength() {
        return MIDDLE_LENGTH;
    }
}
