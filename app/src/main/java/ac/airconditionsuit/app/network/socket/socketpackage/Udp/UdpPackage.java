package ac.airconditionsuit.app.network.socket.socketpackage.Udp;


import ac.airconditionsuit.app.network.socket.socketpackage.SocketPackage;
import ac.airconditionsuit.app.util.ByteUtil;

/**
 * Created by ac on 9/24/15.
 * 这个类用来构建UPD的包
 */
public class UdpPackage {
    private static class UdpPackageContent {
        byte function;
        byte[] content;

        public byte getLen() {
            return (byte) (1 + content.length);
        }

        public byte[] getBytes() {
            return content;
        }
    }

    static public class LoginUdpPackageContent extends UdpPackageContent {
        public LoginUdpPackageContent(String mac) throws Exception {
            content = ByteUtil.hexStringToByteArray(mac);
        }
    }

    public void setContent(UdpPackageContent content) {
        this.content = content;
    }

    private UdpPackageContent content;

    public byte[] getBytes() {
        int length = 6 + content.getLen();
        byte[] result = new byte[length];
        //起始字符
        result[0] = 68;

        //控制域
        result[1] = genControlByte();

        //数据长度
        result[2] = content.getLen();

        byte[] contentBytes = content.getBytes();
        System.arraycopy(contentBytes, 0, result, 3, contentBytes.length);

        //check sum
        result[length - 2] = getCheckSum(result);
        //结束字符
        result[length - 1] = 16;

        return result;
    }

    private byte getCheckSum(byte[] input) {
        int res = 0;
        for (byte b : input) {
            res += b;
        }
        return (byte) (res % 256);
    }

    static byte framNO = 0;
    public static byte genControlByte() {
        byte aux = framNO;
        ++ framNO;
        framNO %= 128;
        return (byte) (aux + 128);
    }
}
