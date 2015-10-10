package ac.airconditionsuit.app.network.socket.socketpackage.Udp;


import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.util.ByteUtil;

/**
 * Created by ac on 9/24/15.
 * 这个类用来构建UPD的包
 */
public class UdpPackage {
    private static class UdpPackageContent {
        byte function;
        byte[] content = new byte[0];

        public byte getLen() {
            return (byte) (1 + content.length);
        }

        public byte[] getBytes() {
            byte[] data = new byte[content.length + 1];
            data[0] = function;
            if (content.length != 0) {
                System.arraycopy(content, 0, data, 1, content.length);
            }
            return data;
        }
    }

    public static final byte AFN_LOGIN = 0x2;
    static public class LoginUdpPackageContent extends UdpPackageContent {
        public LoginUdpPackageContent() throws Exception {
            function = AFN_LOGIN;
            content = ByteUtil.hexStringToByteArray(MyApp.getApp().getServerConfigManager().getCurrentHostMac());
        }
    }

    public static final byte AFN_BROADCAST = 0xf;
    public static class BroadcastUdpPackageContent extends UdpPackageContent {
        public BroadcastUdpPackageContent() {
            function = AFN_BROADCAST;
            //广播包没有数据段
        }
    }

    public void setContent(UdpPackageContent content) {
        this.content = content;
    }

    private UdpPackageContent content;

    public static final byte START_BYTE = 104;
    public static final byte END_BYTE = 22;

    public byte[] getBytes() {
        byte[] contentBytes;
        if (content != null) {
            contentBytes = content.getBytes();
        } else {
            contentBytes = new byte[0];
        }

        int length = 5 + contentBytes.length;
        byte[] result = new byte[length];
        //起始字符
        result[0] = START_BYTE;

        //控制域
        result[1] = genControlByte();

        //数据长度
        result[2] = (byte) contentBytes.length;

        if (contentBytes.length != 0) {
            System.arraycopy(contentBytes, 0, result, 3, contentBytes.length);
        }

        //check sum
        result[length - 2] = getCheckSum(result);
        //结束字符
        result[length - 1] = END_BYTE;

        return result;
    }

    public static byte getCheckSum(byte[] input) {
        int res = 0;

        //校验到数据位结束，最后两位不必校验
        for (int i = 0; i < input.length - 2; ++i) {
            byte b = input[i];
            res += b;
        }
        return (byte) (res % 256);
    }

    static byte framNO = 0;

    public static byte genControlByte() {
        byte aux = framNO;
        ++framNO;
        framNO %= 128;
        return (byte) (aux + 128);
    }

}
