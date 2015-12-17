package ac.airconditionsuit.app.network.socket;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.entity.Device;
import ac.airconditionsuit.app.entity.ObserveData;
import ac.airconditionsuit.app.network.socket.socketpackage.SocketPackage;
import ac.airconditionsuit.app.network.socket.socketpackage.Udp.UdpPackage;
import ac.airconditionsuit.app.util.ByteUtil;
import android.nfc.Tag;
import android.util.Log;

import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ac on 10/11/15.
 */
public class UdpSocket implements SocketWrap {
    private static final int PORT = 9002; // udp port
    private static final String TAG = "UdpSocket";
    private static DatagramSocket datagramSocket;
    private String ip;

    public void resetIpToCurrentDevice() {
        if (MyApp.getApp().getServerConfigManager() == null) {
            this.ip = null;
            return;
        }
        String curIp = MyApp.getApp().getServerConfigManager().getCurrentHostIP();
        if (curIp != null && curIp.length() > 0) {
            this.ip = curIp;
        } else {
            this.ip = null;
        }
    }

    public void connect(String ip) throws SocketException, UnknownHostException {
        if (datagramSocket == null) {
            datagramSocket = new DatagramSocket(8795);
        }
        this.ip = ip;
        Log.i(TAG, "connect to host by udp success, ip " + ip + " port: " + PORT);
    }

    @Override
    public void connect() throws SocketException, UnknownHostException {
        connect(MyApp.getApp().getServerConfigManager().getCurrentHostIP());
    }

    @Override
    synchronized public void sendMessage(SocketPackage socketPackage) {
        if (datagramSocket != null) {
            try {
                byte[] sentContent = socketPackage.getBytesUDP();
                UdpPackage udpPackage = socketPackage.getUdpPackage();
                MyApp.getApp().getSocketManager().addSentUdpPackage(udpPackage);
                DatagramPacket pack = new DatagramPacket(sentContent, sentContent.length);
                pack.setAddress(InetAddress.getByName(ip));
                pack.setPort(PORT);
                datagramSocket.send(pack);
                Log.i(TAG, "send data by udp: " + ByteUtil.byteArrayToReadableHexString(sentContent));
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
        if (datagramSocket != null) {
            datagramSocket.close();
            datagramSocket = null;
        }
    }

    @Override
    public void receiveDataAndHandle() throws IOException {
        DatagramPacket datagramPacket = new DatagramPacket(new byte[1024], 1024);
        System.out.println("receive");
        datagramSocket.receive(datagramPacket);
        byte[] receiveData = Arrays.copyOf(datagramPacket.getData(), datagramPacket.getLength());
        Log.i(TAG, "receive data " + ByteUtil.byteArrayToReadableHexString(receiveData));
        MyApp.getApp().getSocketManager().handUdpPackage(datagramPacket, receiveData);
    }

    @Override
    public boolean isConnect() {
        return datagramSocket != null;
    }
}
