package ac.airconditionsuit.app.network.socket;

import ac.airconditionsuit.app.Constant;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by ac on 9/24/15.
 */
public class UdpPackage {
    class UdpPackageContent {
        byte function;

        public byte getLen() {
            return 0;
        }
    }

    private byte control;
    private UdpPackageContent content;

    public byte[] build() {
        byte[] result = new byte[6 + content.getLen()];
        //起始字符
        result[0] = 68;

        //控制域
        result[1] = control;

        //数据长度
        result[2] = content.getLen();



        return result;
    }

}
