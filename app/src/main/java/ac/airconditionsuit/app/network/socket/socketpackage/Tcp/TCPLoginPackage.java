package ac.airconditionsuit.app.network.socket.socketpackage.Tcp;

import ac.airconditionsuit.app.util.ByteUtil;

import java.io.UnsupportedEncodingException;

/**
 * Created by ac on 10/10/15.
 */
public class TCPLoginPackage extends TcpPackage {
    public static final byte LOGIN_MSG_TYPE = 0x1;
    public static final int MIDDLE_LENGHT = 41;

    private Long cust_id;
    private String authString;

    public TCPLoginPackage(Long cust_id, String authString) {
        this.cust_id = cust_id;
        this.authString = authString;
        msg_type = LOGIN_MSG_TYPE;
    }

    @Override
    protected byte[] getMiddleBytes() throws UnsupportedEncodingException {
        byte[] result = new byte[MIDDLE_LENGHT];
        //login type always 1
        result[0] = 1;

        //cust_id
        System.arraycopy(ByteUtil.longToByteArray(cust_id), 0, result, 1, 8);

        //auth_code
        System.arraycopy(authString.getBytes("US-ASCII"), 0, result, 9, 32);

        return result;
    }

    @Override
    protected short getMiddleLength() {
        return MIDDLE_LENGHT;
    }
}
