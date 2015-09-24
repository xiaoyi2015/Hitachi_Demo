package ac.airconditionsuit.app.network.socket;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.util.NetworkConnectionStatusUtil;
import android.util.Log;

import java.io.IOException;
import java.net.*;

/**
 * Created by ac on 9/18/15.
 */
public class SocketManager {
    public static final String TAG = "SocketManager";

    //能连接上互联网，以tcp形式与主机连接。
    public static final int TCP = 0;
    //不能连接上互联网，但是能连接上主机，以udp形式与主机连接。
    public static final int UDP = 1;
    public static final int UNCONNECT = 2;

    private int currentSocketType = TCP;

    interface SocketWrap {
        void connect();

        void sendMessage(SocketPackage socketPackage);

        void close();

        byte[] readMessage();

    }

    static class TcpSocket implements SocketWrap {
        private static final String IP = "114.215.83.189";//日立
        private static final int PORT = 9002;//日立

        private Socket socket;

        @Override
        public void connect() {

        }

        @Override
        public void sendMessage(SocketPackage socketPackage) {
            if (socket != null && socket.isConnected()) {
                try {
                    socket.getOutputStream().write(socketPackage.getBytes());
                } catch (IOException e) {
                    Log.e(TAG, "sendMessage by Tcp failed: socket.getOutputStream() error");
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, "sendMessage by Tcp failed: socket is null or socket.isConnected() return false");
            }
        }

        @Override
        public void close() {

        }

        @Override
        public byte[] readMessage() {

            return new byte[0];
        }
    }

    static class UdpSocket implements SocketWrap {
        private static final String HOST = "192.168.1.150";//日立
        private static final int PORT = 9002;//日立
        private DatagramSocket datagramSocket;

        UdpSocket() {
            try {
                datagramSocket = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void connect() {

        }

        @Override
        public void sendMessage(SocketPackage socketPackage) {
            if (datagramSocket != null && datagramSocket.isConnected()) {
                try {
                    byte[] sentContent = socketPackage.getBytes();
                    DatagramPacket pack = new DatagramPacket(sentContent, sentContent.length, InetAddress.getByName(HOST), PORT);
                    datagramSocket.send(pack);
                } catch (IOException e) {
                    Log.e(TAG, "sendMessage by Tcp failed: socket.getOutputStream() error");
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, "sendMessage by Tcp failed: socket is null or socket.isConnected() return false");
            }
        }

        @Override
        public void close() {

        }

        @Override
        public byte[] readMessage() {

            return new byte[0];
        }
    }

    private SocketWrap socket;

    public void sendMessage(SocketPackage socketPackage) {
        socket.sendMessage(socketPackage);
    }

    /**
     * 在退出登录，被踢下线的时候都要调用本方法。
     */
    public void close() {
        socket.close();
    }

    public void init(int status) {
        if (status == NetworkConnectionStatusUtil.TYPE_WIFI_UNCONNECT) {
            //udp
            currentSocketType = UDP;
            socket = new UdpSocket();
        } else if (status == NetworkConnectionStatusUtil.TYPE_MOBILE_CONNECT
                || status == NetworkConnectionStatusUtil.TYPE_MOBILE_CONNECT) {
            //tcp
            currentSocketType = TCP;
            socket = new TcpSocket();
        } else {
            //no_connect
            currentSocketType = UNCONNECT;

            return;
        }

        socket.connect();
        SocketPackage loginPackage = new SocketPackage();
        sendMessage(loginPackage);
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

}
