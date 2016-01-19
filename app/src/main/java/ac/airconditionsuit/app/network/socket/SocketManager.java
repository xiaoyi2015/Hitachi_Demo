package ac.airconditionsuit.app.network.socket;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.entity.DeviceFromServerConfig;
import ac.airconditionsuit.app.entity.ObserveData;
import ac.airconditionsuit.app.network.socket.socketpackage.*;
import ac.airconditionsuit.app.network.socket.socketpackage.Udp.UdpPackage;
import ac.airconditionsuit.app.util.NetworkConnectionStatusUtil;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.*;

/**
 * Created by ac on 9/18/15.
 */
public class SocketManager extends Observable {
    public static final String TAG = "SocketManager";

    //connection status
    public static final int TCP_HOST_CONNECT = 0;
    public static final int TCP_DEVICE_CONNECT = 1;
    public static final int UDP_DEVICE_CONNECT = 2;
    public static final int TCP_UDP_ALL_UNCONNECT = 3;

    //socket type

    public static final int TCP = 0;
    public static final int UDP = 1;
    public static final int NONE = 2;


    public static final int HEART_BEAT_PERIOD_TCP = 60000;
    public static final int HEART_BEAT_INVALID_TIME_TCP = 70000;

    public static final int HEART_BEAT_PERIOD_UDP = 15000;
    public static final int HEART_BEAT_INVALID_TIME_UDP = 20000;

    public static final int CHECK_PERIOD = 5000;

    private boolean isTcpHostConnect = false;
    private boolean isTcpDeviceConnect = false;
    private boolean isUdpDeviceConnect = false;

    private long lastHeartSuccessTime = 0;
    private Timer heartBeatTimer;
    private Timer checkTimer;

    public SocketManager() {
        udpPackageHandler = new UdpPackageHandler();
    }

    public void setLastHeartSuccessTime(long lastHeartSuccessTime) {
        this.lastHeartSuccessTime = lastHeartSuccessTime;
    }

    public boolean shouldSendPacketsToQuery() {
        if (!MyApp.getApp().getServerConfigManager().hasDevice()) {
            return false;
        }
        int status = getStatus();
        return status == UDP_DEVICE_CONNECT || status == TCP_DEVICE_CONNECT;
    }

    public void heartSuccess() {
        setLastHeartSuccessTime(System.currentTimeMillis());
        if (socket == null) {
            statusAllFalse();
            return;
        }
        if (socket instanceof TcpSocket) {
            statusTcpServerConnect();
        } else {
            statusUdpConnect();
        }
    }

    public void statusTcpServerConnectDeviceNot() {
        isTcpHostConnect = true;
        isTcpDeviceConnect = false;
        notifyActivity(new ObserveData(ObserveData.NETWORK_STATUS_CHANGE));
    }

    public void statusTcpServerConnect() {
        isTcpHostConnect = true;
//        isTcpDeviceConnect = false;
        notifyActivity(new ObserveData(ObserveData.NETWORK_STATUS_CHANGE));
    }

    public void statusUdpConnect() {
        isTcpHostConnect = false;
        isUdpDeviceConnect = true;
        isTcpDeviceConnect = false;
        notifyActivity(new ObserveData(ObserveData.NETWORK_STATUS_CHANGE));
    }

    public void statusAllFalse() {
        isTcpHostConnect = false;
        isTcpDeviceConnect = false;
        isUdpDeviceConnect = false;
        notifyActivity(new ObserveData(ObserveData.NETWORK_STATUS_CHANGE));
    }

    private void statusTcpDeviceConnect() {
        isTcpDeviceConnect = true;
        isTcpHostConnect = true;
        isUdpDeviceConnect = false;
        notifyActivity(new ObserveData(ObserveData.NETWORK_STATUS_CHANGE));
    }

    public void checkDevice(boolean success) {
        setLastHeartSuccessTime(System.currentTimeMillis());
        if (socket == null) {
            statusAllFalse();
            return;
        }
        if (success) {
            statusTcpDeviceConnect();
            if (socket instanceof TcpSocket) {
                statusTcpDeviceConnect();
            } else {
                Log.i(TAG, "fucking udp socket receive a tcp package");
            }
        } else {
            if (socket instanceof TcpSocket) {
                statusTcpServerConnectDeviceNot();
            } else {
                Log.i(TAG, "fucking udp socket receive a tcp package");
            }
        }
    }

    public int getStatus() {
        if (isTcpDeviceConnect) {
            return TCP_DEVICE_CONNECT;
        }
        if (isTcpHostConnect) {
            return TCP_HOST_CONNECT;
        }
        if (isUdpDeviceConnect) {
            return UDP_DEVICE_CONNECT;
        }
        return TCP_UDP_ALL_UNCONNECT;
    }

    public void startHeartBeat() {
        int heartbeatPeriod;
        if (socket instanceof TcpSocket) {
            heartbeatPeriod = HEART_BEAT_PERIOD_TCP;
        } else {
            heartbeatPeriod = HEART_BEAT_PERIOD_UDP;
        }

        heartBeatTimer = new Timer();
        heartBeatTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SocketPackage heartBeatSocket = new HeartBeatPackage();
                sendMessage(heartBeatSocket);
                MyApp.getApp().getAirConditionManager().queryAirConditionStatus();
            }
        }, 0, heartbeatPeriod);

    }

    public void notifyActivity(ObserveData od) {
        setChanged();
        notifyObservers(od);
    }

    public void sendMessage(final List<ControlPackage> controlPackages) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (controlPackages) {
                    for (ControlPackage p : controlPackages) {
                        sendMessage(p);
                    }
                }
            }
        }).run();
    }

    private long lastQueryTime = 0;
    public void getAllAirConditionStatusFromHostDevice(List<DeviceFromServerConfig> devices) throws Exception {
        if (System.currentTimeMillis() - lastQueryTime < 10000) {
            return;
        } else {
            lastQueryTime = System.currentTimeMillis();
        }
        List<Integer> addresses = new ArrayList<>();
        for (DeviceFromServerConfig d : devices) {
            Integer address = d.getAddress_new();
            if (address < 0 || address > 255) {
                throw new Exception("air condition address error");
            }
            addresses.add(address);
        }
        QueryAirConditionStatusPackage queryAirConditionStatusPackage = new QueryAirConditionStatusPackage(addresses);
        Log.v("liutao", "主动发包读所有空调状态");
        sendMessage(queryAirConditionStatusPackage);
    }

    public void handUdpPackage(DatagramPacket datagramPacket, byte[] receiveData) throws IOException {
        udpPackageHandler.handleUdpPackage(datagramPacket, receiveData);
    }

    public void addSentUdpPackage(UdpPackage udpPackage) {
        if (udpPackage != null) {
            udpPackageHandler.addSentPackage(udpPackage);
        }
    }

    public void searchIndoorAirCondition() {
        sendMessage(new QueryAirConditionAddressPackage());
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    UdpSocket socket = new UdpSocket();
//                    socket.connect();
//                    SocketPackage socketPackage = new QueryAirConditionAddressPackage();
//                    socket.sendMessage(socketPackage);
//
//                    //搜索时间十秒
//                    long currentTime = System.currentTimeMillis();
//                    while (System.currentTimeMillis() < currentTime + 10 * 1000) {
//                        socket.receiveDataAndHandle();
//                    }
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Log.e(TAG, "search indoor air condition failed");
//                }
//            }
//        }).start();
    }

    public void sendUdpACK(byte[] no) {
        sendMessage(new ACKPackage(no, null));
    }

    public void syncTimeUDP() {
        sendMessage(new SyncTimePackage());
    }


    class ReceiveThread extends Thread {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    if (socket == null || !socket.isConnect()) {
                        return;
                    }
                    socket.receiveDataAndHandle();
                } catch (IOException e) {
                    if (Thread.interrupted()) {
                        Log.e(TAG, "receive thread interrupted");
                    } else {
                        Log.e(TAG, "read data from socket failed");
                    }
                    e.printStackTrace();
                    close();
                    break;
                }
            }
        }
    }

    public void setDeviceOffline() {
        //如果是tcp连接，不关闭tcp，只是重新检查设备
        if (isTcpHostConnect && isTcpDeviceConnect) {
            isTcpDeviceConnect = false;
        } else if (isUdpDeviceConnect) {
            isUdpDeviceConnect = false;
        }
        notifyActivity(new ObserveData(ObserveData.NETWORK_STATUS_CHANGE));
    }


    public void recheckDevice() {
        //如果是tcp连接，不关闭tcp，只是重新检查设备
        if (MyApp.getApp().getServerConfigManager().hasDevice()) {
            if (socket == null) {
                reconnectSocket();
                return;
            }
            if (socket instanceof TcpSocket) {
                ((TcpSocket) socket).checkDeviceConnect();
            } else {
                ((UdpSocket) socket).resetIpToCurrentDevice();
                LoginPackage loginPackage = new LoginPackage();
                sendMessage(loginPackage);
            }

        }
        notifyActivity(new ObserveData(ObserveData.NETWORK_STATUS_CHANGE));
    }

    public void reconnectSocket() {
        close();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                init();
            }
        }, 500);
//        Log.i(TAG, "reconnect");
    }

    private SocketWrap socket;
    private UdpPackageHandler udpPackageHandler;
    private Thread receiveThread;

    public void sendMessage(final SocketPackage socketPackage) {
        if (socket == null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (socket != null) {
                    socket.sendMessage(socketPackage);
                }
            }
        }).start();
    }

    /**
     * 在退出登录，被踢下线的时候都要调用本方法。
     */
    public void close() {
        if (heartBeatTimer != null) {
            heartBeatTimer.cancel();
            heartBeatTimer = null;
        }

        if (receiveThread != null && !receiveThread.isInterrupted()) {
            receiveThread.interrupt();
            receiveThread = null;
        }

        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
            Log.e(TAG, "close socket error");
            e.printStackTrace();
        }

        statusAllFalse();
    }

    public void stopCheck() {
        if (checkTimer != null) {
            checkTimer.cancel();
        }
    }

    synchronized public void init(final int status) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (!MyApp.getApp().isUserLogin()) {
                    return;
                }

                final int socketType = getSocketType(status);
                if (socket != null && socket.isConnect()
                        && (socket instanceof TcpSocket && socketType == TCP)) {
                    return;
                }

                statusAllFalse();
                lastHeartSuccessTime = 0;

                if (socketType == UDP) {
                    socket = new UdpSocket();
                } else if (socketType == TCP) {
                    socket = new TcpSocket();
                } else {
                    socket = null;
                    //如果没有联网，就不进行后面的操作了，直接return
//                    Log.i(TAG, "init socket manager failed due to no network");
                    return;
                }

                //network task should be run on background
                try {
                    if (socket != null) {
                        socket.connect();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "establish socket connect failed!");
                    close();
                    e.printStackTrace();
                }

                //socket链接后开始读取
                receiveThread = new ReceiveThread();
                receiveThread.start();

                //登录
                LoginPackage loginPackage = new LoginPackage();
                sendMessage(loginPackage);


                if (checkTimer == null) {

                    final int heartBeatInvalidTimeTcp;
                    int checkPeriod;
                    if (socket instanceof TcpSocket) {
                        heartBeatInvalidTimeTcp = HEART_BEAT_INVALID_TIME_TCP;
                    } else {
                        heartBeatInvalidTimeTcp = HEART_BEAT_INVALID_TIME_UDP;
                    }

                    checkTimer = new Timer();
                    checkTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            //checkout error for socket
                            if (socket == null || !socket.isConnect()) {
                                reconnectSocket();
                                return;
                            }

                            //checkout heartbeat
                            if (System.currentTimeMillis() - lastHeartSuccessTime > heartBeatInvalidTimeTcp) {
                                reconnectSocket();
                                Log.v(TAG, "socket check lose heart beat");
                                return;
                            }

                            int socketType = getSocketType(NetworkConnectionStatusUtil.getConnectivityStatus(MyApp.getApp()));
                            if (socketType != NONE &&
                                    (socket instanceof TcpSocket && socketType == UDP)
                                    || (socket instanceof UdpSocket && socketType == TCP)) {
                                reconnectSocket();
                                Log.v(TAG, "socket switch socket type");
                            }
                        }
                    }, CHECK_PERIOD, CHECK_PERIOD);
                }

            }
        }).start();
    }

    public void init() {
        //check to used tcp or udp
        new Thread(new Runnable() {
            @Override
            public void run() {
                int status = NetworkConnectionStatusUtil.getConnectivityStatus(MyApp.getApp());
                init(status);
            }
        }).start();
    }

    public static int getSocketType(int status) {
//        status = NetworkConnectionStatusUtil.TYPE_WIFI_UNCONNECT;
        if (status == NetworkConnectionStatusUtil.TYPE_WIFI_UNCONNECT) {
            //udp
            //udp还要判断是否有设备
            if (!MyApp.getApp().getServerConfigManager().hasDevice()) {
                return NONE;
            } else {
                return UDP;
            }
        } else if (status == NetworkConnectionStatusUtil.TYPE_MOBILE_CONNECT
                || status == NetworkConnectionStatusUtil.TYPE_MOBILE_CONNECT_2G
                || status == NetworkConnectionStatusUtil.TYPE_MOBILE_CONNECT_3G
                || status == NetworkConnectionStatusUtil.TYPE_MOBILE_CONNECT_4G
                || status == NetworkConnectionStatusUtil.TYPE_WIFI_CONNECT) {
            //tcp
            return TCP;
        } else {
            return NONE;
        }

    }

    public void switchSocketType(int connectionStatus) {
        //当socket可用，且当前socket类型无需变换时，直接return
        int socketType = getSocketType(connectionStatus);
        if (socket != null
                && socket.isConnect()
                && ((socket instanceof TcpSocket && socketType == TCP)
                || (socket instanceof UdpSocket && socketType == UDP))) {
            return;
        }
        close();
        //当所需socketType为none时，不需要连接socket;
        if (socketType != NONE) {
            init(connectionStatus);
        }
    }

    private static final String BROADCAST_ADDRESS = "255.255.255.255";
    private static final int BROADCAST_PORT = 9002;

    interface findDeviceListener {
        void findDevice(String authCode, String authCodeForShow, String ip);
    }


//    public void getAirConditionAddressFromHostDevice() {
//        sendMessage(new QueryAirConditionAddressPackage());
//    }

    public UdpSocket broadCastSocket;

    public void sendBroadCast() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (broadCastSocket == null) {
                        broadCastSocket = new UdpSocket();
                    }
                    broadCastSocket.connect(BROADCAST_ADDRESS);
                    SocketPackage socketPackage = new BroadcastPackage();
                    //重发三遍，主机偶尔会没有应答
                    broadCastSocket.sendMessage(socketPackage);
                    broadCastSocket.sendMessage(socketPackage);
                    broadCastSocket.sendMessage(socketPackage);

                    //搜索时间十秒
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
//                            broadCastSocket.close();
                            ObserveData od = new ObserveData(ObserveData.FIND_DEVICE_BY_UDP_FINASH);
                            notifyActivity(od);
                        }
                    }, 10 * 1000);
                    long currentTime = System.currentTimeMillis();
                    while (System.currentTimeMillis() < currentTime + 10 * 1000) {
                        broadCastSocket.receiveDataAndHandle();
                    }
                    ObserveData od = new ObserveData(ObserveData.FIND_DEVICE_BY_UDP_FINASH);
                    notifyActivity(od);
                } catch (IOException e) {
                    ObserveData od = new ObserveData(ObserveData.FIND_DEVICE_BY_UDP_FAIL);
                    notifyActivity(od);
                    e.printStackTrace();
                    Log.e(TAG, "send broadcast fail!");
                }
            }
        }).start();
    }

}
