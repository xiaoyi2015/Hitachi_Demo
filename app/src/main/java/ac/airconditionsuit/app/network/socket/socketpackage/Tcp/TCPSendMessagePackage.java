package ac.airconditionsuit.app.network.socket.socketpackage.Tcp;

import ac.airconditionsuit.app.network.socket.socketpackage.Udp.UdpPackage;
import ac.airconditionsuit.app.util.ByteUtil;
import android.content.ContentValues;
import android.content.Context;

import java.io.UnsupportedEncodingException;

/**
 * Created by ac on 10/10/15.
 */
public class TCPSendMessagePackage extends TcpPackage {
    public static final byte SEND_MESSAGE_MSG_TYPE = 0x3;
    public static final byte SEND_MESSAGE_RETURN_MSG_TYPE = 0x4;
    private long userId;
    private long chatId;

    private byte[] content;

    public TCPSendMessagePackage(UdpPackage messagePackage, long userId, long chatId) {
        this(messagePackage.getBytes(), userId, chatId);
    }


    private TCPSendMessagePackage(byte[] content, long userId, long chatId) {
        msg_type = SEND_MESSAGE_MSG_TYPE;
        this.content = content;
        this.userId = userId;
        this.chatId = chatId;
    }

    public TCPSendMessagePackage(String json, long userId, long chatId) {
        this(json.getBytes(), userId, chatId);
    }

    @Override
    protected byte[] getMiddleBytes() throws UnsupportedEncodingException {
        byte[] result = new byte[content.length + 29];


        //chat_type, always 2
        result[0] = (byte) 2;

        //login_type, always 1
        result[1] = (byte) 1;

        //chat_id
        System.arraycopy(ByteUtil.longToByteArray(chatId), 0, result, 2 ,8);

        //from_cust_id
        System.arraycopy(ByteUtil.longToByteArray(userId), 0, result, 10 ,8);

        //to_cust_id always 0, and result init value is 0, so no need to run next line
        //System.arraycopy(ByteUtil.longToByteArray(0l), 0, result, 10 ,8);


        //content_len
        System.arraycopy(ByteUtil.shortToByteArray((short) content.length), 0, result, 26, 2);

        //content_type, always 1 because app only send bin data to server
        result[28] = 1;

        //content
        System.arraycopy(content, 0, result, 29, content.length);

        return result;
    }

    @Override
    protected short getMiddleLength() {
        return (short) (content.length + 29);
    }
}
