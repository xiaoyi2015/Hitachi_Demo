package ac.airconditionsuit.app.network.socket;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.entity.Device;
import ac.airconditionsuit.app.entity.ObserveData;
import ac.airconditionsuit.app.network.socket.socketpackage.*;
import ac.airconditionsuit.app.util.NetworkConnectionStatusUtil;
import android.util.Log;

import java.io.IOException;
import java.util.*;

/**
 * Created by ac on 9/18/15.
 */
public class SocketManager extends Observable {
    public static final String TAG = "SocketManager";

    //能连接上互联网，以tcp形式与主机连接。
    public static final int TCP = 0;
    //不能连接上互联网，但是能连接上主机，以udp形式与主机连接。
    public static final int UDP = 1;
    public static final int UDP_NODEVICE = 3;
    public static final int UNCONNECT = 2;

    private int currentSocketType = TCP;

    public static final int TCP_HOST_CONNECT = 0;
    public static final int TCP_DEVICE_CONNECT = 1;
    public static final int UDP_CONNECT = 2;
    public static final int TCP_UDP_ALL_UNCONNECT = 3;
    private int status = UNCONNECT;


    public static final int HEART_BEAT_PERIOD = 60000;
    public static final int HEART_BEAT_INVAILD_TIME = 70000;
    private long lastHeartSuccessTime = 0;
    private Timer heartBeatTimer;

    public void setLastHeartSuccessTime(long lastHeartSuccessTime) {
        this.lastHeartSuccessTime = lastHeartSuccessTime;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void startHeartBeat() {
        heartBeatTimer = new Timer();
        heartBeatTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SocketPackage heartBeatSocket = new HeartBeatPackage();
                sendMessage(heartBeatSocket);
            }
        }, 0, HEART_BEAT_PERIOD);

    }

    public void notifyActivity(ObserveData od) {
        setChanged();
        notifyObservers(od);
    }

    class ReceiveThread extends Thread {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    socket.receiveDataAndHandle();
                } catch (IOException e) {
                    Log.e(TAG, "read data from socket failed");
                    reconnect();
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    private void reconnect() {
        close();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                init();
            }
        }, 5000);
        Log.i(TAG, "reconnect");
    }

    private SocketWrap socket;
    private Thread receiveThread;

    public void sendMessage(SocketPackage socketPackage) {
        socket.sendMessage(socketPackage);
    }

    /**
     * 在退出登录，被踢下线的时候都要调用本方法。
     */
    public void close() {
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
            Log.e(TAG, "close socket error");
            e.printStackTrace();
        }

        if (receiveThread != null && !receiveThread.isInterrupted()) {
            receiveThread.interrupt();
            receiveThread = null;
        }

        if (heartBeatTimer != null) {
            heartBeatTimer.cancel();
            heartBeatTimer = null;
        }
    }

    public int getCurrentSocketType() {
        return currentSocketType;
    }

    public void init(int status) {
        if (!MyApp.getApp().isUserLogin()) {
            return;
        }

        status = NetworkConnectionStatusUtil.TYPE_WIFI_UNCONNECT;
        if (status == NetworkConnectionStatusUtil.TYPE_WIFI_UNCONNECT) {
            //udp
            //udp还要判断是否有设备
            if (!MyApp.getApp().getServerConfigManager().hasDevice()) {
                currentSocketType = UDP_NODEVICE;
                return;
            }
            currentSocketType = UDP;
            socket = new UdpSocket();
        } else if (status == NetworkConnectionStatusUtil.TYPE_MOBILE_CONNECT
                || status == NetworkConnectionStatusUtil.TYPE_WIFI_CONNECT) {
            //tcp
            currentSocketType = TCP;
            socket = new TcpSocket();
        } else {
            //no_connect
            currentSocketType = UNCONNECT;

            //如果没有联网，就不进行后面的操作了，直接return
            Log.i(TAG, "init socket manager failed due to no network");
            return;
        }

        //network task should be run on background
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket.connect();
                } catch (IOException e) {
                    Log.e(TAG, "establish socket connect failed!");
                    reconnect();
                    e.printStackTrace();
                }

                //socket链接后开始读取
                receiveThread = new ReceiveThread();
                receiveThread.start();

                //登录
                LoginPackage loginPackage = new LoginPackage();
                sendMessage(loginPackage);
            }
        }).start();
    }

    public void init() {
        //check to used tcp or udp
        int status = NetworkConnectionStatusUtil.getConnectivityStatus(MyApp.getApp());
        init(status);
    }

    public void switchSocketType(int socketType) {
        close();
        init(socketType);
    }

    private static final String BROADCAST_ADDRESS = "255.255.255.255";
    private static final int BROADCAST_PORT = 9002;

    interface findDeviceListener {
        void findDevice(String authCode, String authCodeForShow, String ip);
    }

    public void sendBroadCast() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UdpSocket socket = new UdpSocket();
//                    socket.connect(BROADCAST_ADDRESS);
                    socket.connect("192.168.1.123");
                    SocketPackage socketPackage = new BroadcastPackage();
                    //重发三遍，主机偶尔会没有应答
                    socket.sendMessage(socketPackage);
                    socket.sendMessage(socketPackage);
                    socket.sendMessage(socketPackage);

                    //搜索时间十秒
                    long currentTime = System.currentTimeMillis();
                    while (System.currentTimeMillis() < currentTime + 10 * 1000) {
                        socket.receiveDataAndHandle();
                    }

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
