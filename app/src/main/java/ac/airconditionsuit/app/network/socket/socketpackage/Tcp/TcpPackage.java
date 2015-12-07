package ac.airconditionsuit.app.network.socket.socketpackage.Tcp;


import ac.airconditionsuit.app.network.socket.socketpackage.Udp.UdpPackage;
import ac.airconditionsuit.app.util.ByteUtil;

import java.io.UnsupportedEncodingException;

/**
 * Created by ac on 9/24/15.
 * 这个类用来构建tcp的数据包
 */
public abstract class TcpPackage {
    public static final byte TICK_OFF_LINE_MSG_TYPE = 0x7;
    public static final byte RECEIVE_MESSAGE_FROM_SERVER = 0x5;

    public static final byte START_BYTE = 0x2;
    public static final byte END_BYTE = 0x3;
    public static final int HEADER_LENGTH = 6;
    protected byte msg_type;
    protected byte[] msg_no;

    public short getMsg_no_short() {
        return msg_no_short;
    }

    protected short msg_no_short;


    public byte[] getBytes() throws UnsupportedEncodingException {
        int bodyLength = getMiddleLength();
        short lengthAfterMsgType = (short) (bodyLength + 3);
        int totalLen = HEADER_LENGTH + lengthAfterMsgType;
        byte[] result = new byte[totalLen];
        //开始标志
        result[0] = START_BYTE;
        //包体总长度
        System.arraycopy(ByteUtil.shortToByteArray(lengthAfterMsgType), 0, result, 1, 2);
        //编号 在回复消息的时候是固定的。如果是发送消息，编号为null,由getMsg_no()生成。
        if (msg_no == null) {
            this.msg_no_short = getMsg_no();
            this.msg_no = ByteUtil.shortToByteArray(msg_no_short);
        }
        System.arraycopy(msg_no, 0, result, 3, 2);
        //消息类型
        result[5] = msg_type;
        //消息体
        byte[] body = getMiddleBytes();
        System.arraycopy(body, 0, result, 6, bodyLength);
        //校验码
        System.arraycopy(getCheckSum(result), 0, result, totalLen - 3, 2);
        //结束字符
        result[totalLen - 1] = END_BYTE;

        return result;
    }

    public static byte[] getCheckSum(byte[] input) {
        byte[] result = new byte[2];
        for (int i = HEADER_LENGTH; i < input.length - 3; ++i) {
            result[i % 2] ^= input[i];
        }
        return result;
    }

    private static short counter = 0;

    private static short getMsg_no() {
        return counter++;
    }

    protected abstract byte[] getMiddleBytes() throws UnsupportedEncodingException;

    protected abstract short getMiddleLength();

}
