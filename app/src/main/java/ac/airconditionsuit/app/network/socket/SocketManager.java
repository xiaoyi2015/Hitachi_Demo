package ac.airconditionsuit.app.network.socket;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.util.ACByteQueue;
import ac.airconditionsuit.app.util.ByteUtil;
import ac.airconditionsuit.app.util.NetworkConnectionStatusUtil;
import ac.airconditionsuit.app.view.TabIndicator;
import android.app.ProgressDialog;
import android.util.Log;

import java.io.IOException;
import java.net.*;
import java.util.*;

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
        void connect() throws IOException;

        void sendMessage(SocketPackage socketPackage);

        void close() throws IOException;

        byte[] readMessage() throws IOException;

    }

    static class TcpSocket implements SocketWrap {
        private static final String IP = "114.215.83.189";//日立
        private static final int PORT = 7000;

        private Socket socket;
        private ACByteQueue readQueue = new ACByteQueue();

        @Override
        public void connect() throws IOException {
            socket = new Socket(IP, PORT);
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
        public void close() throws IOException {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }

        @Override
        public byte[] readMessage() throws IOException {
            byte[] tempForRead = new byte[256];
            //先读取tcp包头部的六个字节
            while (readQueue.size() < 6) {
                int readCount = socket.getInputStream().read(tempForRead, 0, 255);
                readQueue.offer(tempForRead, 0, readCount);
            }
            byte[] header = readQueue.poll(6);


            //读取body
            int bodyLength = ByteUtil.byteArrayToShort(header, 1);
            while (readQueue.size() < bodyLength) {
                int readCount = socket.getInputStream().read(tempForRead, 0, 255);
                readQueue.offer(tempForRead, 0, readCount);
            }

            byte[] body = readQueue.poll(bodyLength);

            byte[] result = new byte[6 + bodyLength];

            //copy header to result
            System.arraycopy(result, 0, header, 0, 6);

            //copy body to result
            System.arraycopy(result, 6, body, 0, bodyLength);

            return result;
        }
    }

    static class UdpSocket implements SocketWrap {
        private static final String HOST = "192.168.1.150";//日立
        private static final int PORT = 9002;//日立
        private DatagramSocket datagramSocket;


        @Override
        public void connect() throws SocketException, UnknownHostException {
            datagramSocket = new DatagramSocket();
            datagramSocket.connect(InetAddress.getByName(HOST), PORT);
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
            if (datagramSocket != null && !datagramSocket.isClosed()) {
                datagramSocket.close();
            }
        }

        @Override
        public byte[] readMessage() throws IOException {
            DatagramPacket datagramPacket = new DatagramPacket(new byte[1024], 1024);
            datagramSocket.receive(datagramPacket);
            return Arrays.copyOf(datagramPacket.getData(), datagramPacket.getLength());
        }
    }

    class ReceiveThread extends Thread {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    byte[] data = socket.readMessage();
                    //TODO for luzheqi
                } catch (IOException e) {
                    Log.e(TAG, "read data from socket failed");
                    reconnect();
                    e.printStackTrace();
                }
            }
        }
    }

    private void reconnect() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                close();
                init();
            }
        }, 3000);
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
            socket.close();
        } catch (IOException e) {
            Log.e(TAG, "close socket error");
            reconnect();
            e.printStackTrace();
        }

        if (receiveThread != null && !receiveThread.isInterrupted()) {
            receiveThread.interrupt();
        }
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

            //如果没有联网，就不进行后面的操作了，直接return
            return;
        }

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
