package ac.airconditionsuit.app.network.socket.socketpackage.Udp;


import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.aircondition.AirConditionControl;
import ac.airconditionsuit.app.aircondition.AirConditionControlBatch;
import ac.airconditionsuit.app.aircondition.AirConditionManager;
import ac.airconditionsuit.app.entity.Timer;
import ac.airconditionsuit.app.util.ByteUtil;
import ac.airconditionsuit.app.util.UdpErrorNoUtil;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

//    public static UdpPackage genGetAirConditionStatusPackage() {
//        UdpPackage p = new UdpPackage();
//        p.setContent(p.new QueryAirConditionStatusUdpPackageContent());
//        return p;
//    }

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
//                    MyApp.getApp().getSocketManager().startHeartBeat();
                    //test code todo for luzheqi
//                    MyApp.getApp().getSocketManager().getAirConditionAddressFromHostDevice();

                    //test code for control
//            Command command = new Command();
//            command.setTemperature(20);
//            command.setFan(2);
//            command.setMode(1);
//            command.setOnoff(0);
//            command.setAddress(2);
//            MyApp.getApp().getAirconditionManager().controlAirCondition(command);

                    //test code for query timer
            MyApp.getApp().getAirconditionManager().queryTimer(AirConditionManager.QUERY_ALL_TIMER);

                    //test add timer
//            Timer timer = new Timer();
//            timer.setTimerid(7);
//            timer.setMode(AirConditionControl.MODE_HEATING);
//            timer.setName("newnew");
//            timer.setTemperature(21);
//            timer.setFan(AirConditionControl.WINDVELOCITY_HIGH);
//            timer.addControlAircondition(2);
//            timer.setHour(10);
//            timer.setMinute(10);
//            timer.setOnoff(AirConditionControl.OFF);
//            timer.setRepeat(true);
//            timer.setTimerenabled(true);
//            timer.setWeek(3,4);
//            MyApp.getApp().getAirconditionManager().addTimerServer(timer);

                    //test modity timer
//            MyApp.getApp().getAirconditionManager().modityTimerServer(timer);

                    //test delete timer
//            MyApp.getApp().getAirconditionManager().deleteTimerServer(1);

//            MyApp.getApp().getAirconditionManager().queryTimer(2);


                }

                @Override
                public void fail(int errorNo) {
                    //TODO for luzheqi,这里很可能需要退出登录
                    MyApp.getApp().getSocketManager().reconnect();
                }
            };
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
    public class ControlPackageContent extends UdpPackageContent{
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
    public class TimerPackageContent extends UdpPackageContent{
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

    public static byte[] getContentData(byte[] receive) {
        return Arrays.copyOfRange(receive, 4, receive.length - 2);
    }
}
