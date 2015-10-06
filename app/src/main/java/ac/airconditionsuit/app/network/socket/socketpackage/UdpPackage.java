package ac.airconditionsuit.app.network.socket.socketpackage;

import ac.airconditionsuit.app.network.socket.socketpackage.SocketPackage;

/**
 * Created by ac on 9/24/15.
 * 这个类用来构建UPD的包
 */
public class UdpPackage extends SocketPackage {
    class UdpPackageContent {
        byte function;

        public byte getLen() {
            //TODO for luzheqi
            return 0;
        }

        public byte[] getBytes() {
            //TODO for luzheqi
            return new byte[10];
        }
    }

    private byte control;
    private UdpPackageContent content;

    public byte[] getBytes() {
        int length = 6 + content.getLen();
        byte[] result = new byte[length];
        //起始字符
        result[0] = 68;

        //控制域
        result[1] = control;

        //数据长度
        result[2] = content.getLen();

        byte[] contentBytes = content.getBytes();
        System.arraycopy(contentBytes, 0, result, 3, contentBytes.length);

        result[length - 2] = getCheckSum(result);
        result[length - 1] = 16;

        return result;
    }

    private byte getCheckSum(byte[] result) {
        //TODO for luzheqi
        return 0;
    }

}
