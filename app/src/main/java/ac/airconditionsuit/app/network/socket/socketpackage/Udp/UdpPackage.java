package ac.airconditionsuit.app.network.socket.socketpackage.Udp;


import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.network.socket.UdpSocket;
import ac.airconditionsuit.app.util.ByteUtil;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ac on 9/24/15.
 * 这个类用来构建UPD的包
 */
public class UdpPackage {
    public static final String TAG = "UdpPackage";
    public static final byte AFN_YES = 0;
    public static final byte AFN_NO = 1;
    private byte framNumber;
    private Handler handler;

    public static UdpPackage genBroadcastPackage() {
        UdpPackage p = new UdpPackage();
        p.setContent(p.new BroadcastUdpPackageContent());
        return p;
    }

    public static UdpPackage genLoginPackage() throws Exception {
        UdpPackage p = new UdpPackage();
        p.setContent(p.new LoginUdpPackageContent());
        return p;
    }

    public static UdpPackage genHeartBeatPackage() {
        UdpPackage p = new UdpPackage();
        p.setContent(p.new HeartBeatPackageContent());
        return p;
    }

    public static UdpPackage genCheckDevicePackage() {
        UdpPackage p = new UdpPackage();
        List<Byte> addressList = new ArrayList<>();
        addressList.add((byte) 0);
        p.setContent(p.new QueryStatusUdpPackageContent(addressList));
        return p;
    }

    public interface Handler {
        void success();

        void fail();
    }

    private class UdpPackageContent {
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

    public class LoginUdpPackageContent extends UdpPackageContent {
        public LoginUdpPackageContent() throws Exception {
            function = AFN_LOGIN;
            content = ByteUtil.hexStringToByteArray(MyApp.getApp().getServerConfigManager().getCurrentHostMac());
            //TODO for luzheqi temp code
            content = ByteUtil.hexStringToByteArray("001EC00E1FB3");
            handler = null;
        }
    }

    public static final byte AFN_HEARTBEAT = 0x3;

    public class HeartBeatPackageContent extends UdpPackageContent {
        public HeartBeatPackageContent() {
            function = AFN_HEARTBEAT;
            handler = new Handler() {
                @Override
                public void success() {
                    MyApp.getApp().getSocketManager().heartSuccess();
                    Log.i(TAG, "udp heartbeat success");
                }

                /**
                 * heartbeat won't be fail
                 */
                @Override
                public void fail() {
                }
            };
        }
    }

    public static final byte AFN_BROADCAST = 0xf;

    public class BroadcastUdpPackageContent extends UdpPackageContent {
        public BroadcastUdpPackageContent() {
            function = AFN_BROADCAST;
            //广播包没有数据段
        }
    }

    public static final byte AFN_QUERY_STATUS = 0xf;

    public class QueryStatusUdpPackageContent extends UdpPackageContent {
        public QueryStatusUdpPackageContent(List<Byte> addresses) {
            function = AFN_QUERY_STATUS;
            content = new byte[addresses.size() + 1];
            //广播包没有数据段
            content[0] = (byte) addresses.size();
            for (int i = 0; i < addresses.size(); ++i) {
                content[i + 1] = addresses.get(i);
            }
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
        this.framNumber = (byte) (result[1] - 128);

        //数据长度
        result[2] = (byte) (contentBytes.length - 1);

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

    static byte framNumberCounter = 0;

    public static byte genControlByte() {
        byte aux = framNumberCounter;
        ++framNumberCounter;
        framNumberCounter %= 128;
        return (byte) (aux + 128);
    }

    public byte getFramNumber() {
        return framNumber;
    }

    public Handler getHandler() {
        return handler;
    }
}
