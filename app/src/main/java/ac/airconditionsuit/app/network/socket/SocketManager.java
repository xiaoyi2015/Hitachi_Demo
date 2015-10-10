package ac.airconditionsuit.app.network.socket;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.entity.Device;
import ac.airconditionsuit.app.network.socket.socketpackage.BroadcastPackage;
import ac.airconditionsuit.app.network.socket.socketpackage.LoginPackage;
import ac.airconditionsuit.app.network.socket.socketpackage.SocketPackage;
import ac.airconditionsuit.app.network.socket.socketpackage.Udp.UdpPackage;
import ac.airconditionsuit.app.util.ACByteQueue;
import ac.airconditionsuit.app.util.ByteUtil;
import ac.airconditionsuit.app.util.NetworkConnectionStatusUtil;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.*;
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

    interface SocketWrap {
        void connect() throws IOException;

        void sendMessage(SocketPackage socketPackage);

        void close() throws IOException;

        void receiveDataAndHandle() throws IOException;

    }

    static class TcpSocket implements SocketWrap {
        private static final String IP = "114.215.83.189";//日立
        private static final int PORT = 7000;

        private Socket socket;
        private ACByteQueue readQueue = new ACByteQueue();

        @Override
        public void connect() throws IOException {
            socket = new Socket(IP, PORT);
            Log.i(TAG, "connect to host by tcp success");
        }

        @Override
        public void sendMessage(SocketPackage socketPackage) {
            if (socket != null && socket.isConnected()) {
                try {
                    socket.getOutputStream().write(socketPackage.getBytesTCP());
                } catch (Exception e) {
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

        @Override
        public void receiveDataAndHandle() {
            //TODO for luzheqi
            return;
        }
    }

    class UdpSocket implements SocketWrap {
        //        private static final String HOST = "192.168.1.150";//日立
        private static final int PORT = 9002;//日立
        private DatagramSocket datagramSocket;
        private String currentHostIP;

        @Override
        public void connect() throws SocketException, UnknownHostException {
            datagramSocket = new DatagramSocket();
            currentHostIP = MyApp.getApp().getServerConfigManager().getCurrentHostIP();
            Log.i(TAG, "connect to host by udp success, ip " + currentHostIP + " port: " + PORT);
        }

        @Override
        public void sendMessage(SocketPackage socketPackage) {
            if (datagramSocket != null) {
                try {
                    byte[] sentContent = socketPackage.getBytesUDP();
                    Log.i(TAG, "send data by udp: " + ByteUtil.byteArrayToHexString(sentContent));
                    DatagramPacket pack = new DatagramPacket(sentContent, sentContent.length);
                    //多数情况下，getIp()获得的ip都为null, 只有在udp广播包的时候，Ip为255.255.255.255
                    if (socketPackage.getIp() != null) {
                        pack.setAddress(InetAddress.getByName(socketPackage.getIp()));
                    } else {
                        pack.setAddress(InetAddress.getByName(currentHostIP));
                    }
                    pack.setPort(PORT);

                    datagramSocket.send(pack);
                } catch (Exception e) {
                    Log.e(TAG, "sendMessage by udp failed: socket.getOutputStream() error or gen udp package error");
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, "sendMessage by udp failed: socket is null");
            }
        }

        @Override
        public void close() {
            if (datagramSocket != null && !datagramSocket.isClosed()) {
                datagramSocket.close();
            }
        }

        @Override
        public void receiveDataAndHandle() throws IOException {
            DatagramPacket datagramPacket = new DatagramPacket(new byte[1024], 1024);
            datagramSocket.receive(datagramPacket);
            byte[] receiveData = Arrays.copyOf(datagramPacket.getData(), datagramPacket.getLength());
            Log.i(TAG, "receive data after broadcast: " + ByteUtil.byteArrayToHexString(receiveData));
            int receiveDataLength = receiveData.length;

            //check head and end
            if (receiveData[0] != UdpPackage.START_BYTE
                    || receiveData[receiveDataLength - 1] != UdpPackage.END_BYTE) {
                throw new IOException("udp package start flag or end flag error!");
            }

            //check checksum
            byte checksum = UdpPackage.getCheckSum(receiveData);
            if (checksum != receiveData[receiveDataLength - 2]) {
                throw new IOException("udp package checksum error");
            }

            //check length
            byte dataLength = receiveData[2];
            if (dataLength + 6 != receiveDataLength) {
                throw new IOException("udp package length error");
            }


            //control byte
            byte control = receiveData[1];
            //1表示启动站，0表示从动站。
            byte prm = (byte) (control / 128);
            //帧序列号
            byte pfc = (byte) (control - 128);

            //数据域的功能码
            byte afn = receiveData[3];

            switch (afn) {
                case UdpPackage.AFN_BROADCAST:
                    Device device = new Device();
                    //add ip to device
                    device.getInfo().setIp(datagramPacket.getAddress().toString());

                    byte[] authCodeBytes = Arrays.copyOfRange(receiveData, 6, receiveDataLength - 2);
                    String authCode = ByteUtil.byteArrayToHexString(authCodeBytes);
                    device.setAuthCode(authCode);
                    byte[] authCodeEncodeBytes = ByteUtil.encodeAuthCode(authCodeBytes);
                    device.setAuthCodeEncode(ByteUtil.byteArrayToHexString(authCodeEncodeBytes));

                    //notify find device
                    setChanged();
                    notifyObservers(device);
                    break;

                case UdpPackage.AFN_LOGIN:

                default:
                    throw new IOException("udp package afn error");

            }
        }
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

    public int getCurrentSocketType() {
        return currentSocketType;
    }

    public void init(int status) {
        //TODO temp code for debug
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
                    socket.connect();
                    SocketPackage socketPackage = new BroadcastPackage();
                    socketPackage.setIp(BROADCAST_ADDRESS);
                    socket.sendMessage(socketPackage);

                    //搜索时间十秒
                    long currentTime = System.currentTimeMillis();
                    while (System.currentTimeMillis() < currentTime + 10 * 1000) {
                        socket.receiveDataAndHandle();
                    }

                } catch (IOException e) {
                    setChanged();
                    notifyObservers(null);
                    e.printStackTrace();
                    Log.e(TAG, "send broadcast fail!");
                }
            }
        }).start();
    }

}
