package ac.airconditionsuit.app.network.socket.socketpackage.Udp;


import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.aircondition.AirConditionControlBatch;
import ac.airconditionsuit.app.entity.DeviceFromServerConfig;
import ac.airconditionsuit.app.entity.Timer;
import ac.airconditionsuit.app.util.ByteUtil;
import android.util.Log;

import java.util.*;

/**
 * Created by ac on 9/24/15.
 * 这个类用来构建UPD的包
 */
public class UdpPackage {
    public static final String TAG = "UdpPackage";
    public static final byte AFN_YES = 0x0;
    public static final byte AFN_NO = 0x1;
    public static final byte AFN_AIR_CONDITION_STATUS_RESPONSE = 0x6;
    public static final byte AFN_TIMER_RUN_RESPONSE = 0xb;
    private byte framNumber;

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    private Handler handler;
    private byte[] msg_no;
    private static int acIndexToCheckDevice = 0;

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

    public static UdpPackage genSyncTimePackage() {
        UdpPackage p = new UdpPackage();
        p.setContent(p.new SyncTimePackageContent());
        return p;
    }

    public static UdpPackage genCheckDevicePackage() {
        UdpPackage p = new UdpPackage();
        List<Byte> addressList = new ArrayList<>();

        //按顺序选取一个空调
        List<DeviceFromServerConfig> devices_new = MyApp.getApp().getServerConfigManager().getDevices_new();
        acIndexToCheckDevice++;
        if (acIndexToCheckDevice >= devices_new.size()) acIndexToCheckDevice = 0;
        if (acIndexToCheckDevice < devices_new.size()) {
            addressList.add((byte) devices_new.get(acIndexToCheckDevice).getAddress_new());
        } else {
            addressList.add((byte) 0);
        }
        p.setContent(p.new QueryAirConditionStatusUdpPackageContent(addressList));
        return p;
    }

    public static UdpPackage genQueryAirConditionStatusPackage(List<Byte> addressList) {
        UdpPackage p = new UdpPackage();
        p.setContent(p.new QueryAirConditionStatusUdpPackageContent(addressList));
        return p;
    }

    public static UdpPackage genGetAirConditionAddressPackage() {
        UdpPackage p = new UdpPackage();
        p.setContent(p.new GetAirConditionAddressPackageContent());
        return p;
    }

    public static UdpPackage genTimerPackage(Timer timer) throws Exception {
        UdpPackage p = new UdpPackage();
        p.setContent(p.new TimerPackageContent(timer));
        return p;
    }

    public static UdpPackage genControlPackage(AirConditionControlBatch airConditionControlBatch) throws Exception {
        UdpPackage p = new UdpPackage();
        p.setContent(p.new ControlPackageContent(airConditionControlBatch));
        return p;
    }

    public static UdpPackage genDeleteTimerPackage(int id) throws Exception {
        UdpPackage p = new UdpPackage();
        p.setContent(p.new DeleteTimerPackageContent(id));
        return p;
    }

    public static UdpPackage genQueryTimerPackage(int id) throws Exception {
        UdpPackage p = new UdpPackage();
        p.setContent(p.new QueryTimerPackageContent(id));
        return p;
    }

    public void setNo(byte[] msg_no) {
        this.msg_no = msg_no;
    }

    public interface Handler {
        void success();

        void fail(int errorNo);
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
            handler = new Handler() {
                @Override
                public void success() {
                    MyApp.getApp().getSocketManager().startHeartBeat();
                    MyApp.getApp().getSocketManager().syncTimeUDP();
                    MyApp.getApp().getAirConditionManager().queryAirConditionStatus();
                }

                @Override
                public void fail(int errorNo) {
                    MyApp.getApp().getSocketManager().close();
                }
            };
        }
    }

    public static final byte AFN_SYNC_TIME = 0x7;

    public class SyncTimePackageContent extends UdpPackageContent {
        public SyncTimePackageContent() {
            function = AFN_SYNC_TIME;
            content = new byte[7];
            int year = Calendar.getInstance().get(Calendar.YEAR);
//            year = 2014;
            int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
//            month = 2;
            int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
//            day = 10;
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
//            hour = 23;
            int min = Calendar.getInstance().get(Calendar.MINUTE);
//            min = 41;
            int sec = Calendar.getInstance().get(Calendar.SECOND);
//            sec = 31;
            int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
//            week = 5;
            content[0] = ByteUtil.binToBCD(year % 100);
            content[1] = ByteUtil.binToBCD(month);
            content[2] = ByteUtil.binToBCD(day);
            content[3] = ByteUtil.binToBCD(hour);
            content[4] = ByteUtil.binToBCD(min);
            content[5] = ByteUtil.binToBCD(sec);
            content[6] = ByteUtil.binToBCD((week + 5) % 7 + 1);
        }
    }

    public static final byte AFN_HEARTBEAT = 0x3;

    public class HeartBeatPackageContent extends UdpPackageContent {
        public HeartBeatPackageContent() {
            function = AFN_HEARTBEAT;
            handler = new Handler() {
                @Override
                public void success() {
                    Log.i(TAG, "udp heart beat success");
                }

                @Override
                public void fail(int errorNo) {

                }
            };
        }
    }


    public static final byte AFN_CONTROL = 0x4;

    public class ControlPackageContent extends UdpPackageContent {
        public ControlPackageContent(AirConditionControlBatch airConditionControlBatch) throws Exception {
            function = AFN_CONTROL;
            content = airConditionControlBatch.getBytes();
        }
    }

    public static final byte AFN_QUERY_STATUS = 0x5;

    public class QueryAirConditionStatusUdpPackageContent extends UdpPackageContent {
        public QueryAirConditionStatusUdpPackageContent(List<Byte> addresses) {
            function = AFN_QUERY_STATUS;
            content = new byte[addresses.size() + 1];
            //广播包没有数据段
            content[0] = (byte) addresses.size();
            for (int i = 0; i < addresses.size(); ++i) {
                content[i + 1] = addresses.get(i);
            }
        }
    }


    public static final byte AFN_TIMER = 0x8;

    public class TimerPackageContent extends UdpPackageContent {
        public TimerPackageContent(Timer timer) throws Exception {
            function = AFN_TIMER;
            content = timer.getBytesForUdp();
        }
    }

    public static final byte AFN_DELETE_TIMER = 0x9;

    public class DeleteTimerPackageContent extends UdpPackageContent {
        public DeleteTimerPackageContent(int id) throws Exception {
            function = AFN_DELETE_TIMER;
            if (id > 32760) {
                throw new Exception("id is too big");
            }
            content = ByteUtil.shortToByteArrayAsBigEndian(id);
        }
    }

    public static final byte AFN_QUERY_TIMER = 0xa;

    public class QueryTimerPackageContent extends UdpPackageContent {
        public QueryTimerPackageContent(int id) throws Exception {
            function = AFN_QUERY_TIMER;
            if (id != 0xffff && id != 0xfffe && id > 32760) {
                throw new Exception("id is too big");
            }
            content = ByteUtil.shortToByteArrayAsBigEndian(id & 0xffff);
        }
    }


//    public static final byte AFN_GET_AIR_CONDITION_STATUS = 0x5;
//    public class GetAirConditionStatusPackageContent extends UdpPackageContent {
//        public GetAirConditionStatusPackageContent() {
//            function = AFN_GET_AIR_CONDITION_STATUS;
//            handler = new Handler() {
//                @Override
//                public void success() {
//                    Log.i(TAG, "get air status success");
//                }
//
//                @Override
//                public void fail(int errorNo) {
//                    Log.i(TAG, "get air status fail");
//                    MyApp.getApp().showToast(UdpErrorNoUtil.getMessage(errorNo));
//                }
//            };
//        }
//    }

    public static final byte AFN_GET_AIR_CONDITION_ADDRESS = 0xe;

    public class GetAirConditionAddressPackageContent extends UdpPackageContent {
        public GetAirConditionAddressPackageContent() {
            function = AFN_GET_AIR_CONDITION_ADDRESS;
            handler = null;
        }
    }

    public static final byte AFN_BROADCAST = 0xf;

    public class BroadcastUdpPackageContent extends UdpPackageContent {
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
            contentBytes = new byte[1];
            contentBytes[0] = 0;
        }

        int length = 5 + contentBytes.length;
        byte[] result = new byte[length];
        //起始字符
        result[0] = START_BYTE;

        //控制域
        if (msg_no == null) {
            result[1] = genControlByte();
            this.framNumber = (byte) (result[1] & 127);
        } else {
            result[1] = msg_no[0];
        }

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
        return (byte) (aux | 128);
    }

    public byte getFramNumber() {
        return framNumber;
    }

    public Handler getHandler() {
        return handler;
    }

    public static byte[] getContentData(byte[] receive) {
        return Arrays.copyOfRange(receive, 4, receive.length - 2);
    }
}
