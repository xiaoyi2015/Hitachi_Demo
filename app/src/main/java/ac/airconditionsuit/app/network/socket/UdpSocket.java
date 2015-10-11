package ac.airconditionsuit.app.network.socket;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.entity.Device;
import ac.airconditionsuit.app.entity.ObserveData;
import ac.airconditionsuit.app.network.response.CommonResponse;
import ac.airconditionsuit.app.network.socket.socketpackage.SocketPackage;
import ac.airconditionsuit.app.network.socket.socketpackage.Udp.UdpPackage;
import ac.airconditionsuit.app.util.ByteUtil;
import android.util.Log;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

/**
 * Created by ac on 10/11/15.
 */
class UdpSocket implements SocketWrap {
    private static final int PORT = 9002; // udp port
    private static final String TAG = "UdpSocket";
    private DatagramSocket datagramSocket;

    public void connect(String ip) throws SocketException, UnknownHostException {
        datagramSocket = new DatagramSocket();
        datagramSocket.connect(InetAddress.getByName(ip), PORT);
        Log.i(TAG, "connect to host by udp success, ip " + ip + " port: " + PORT);
    }

    @Override
    public void connect() throws SocketException, UnknownHostException {
        connect(MyApp.getApp().getServerConfigManager().getCurrentHostIP());
    }

    @Override
    public void sendMessage(SocketPackage socketPackage) {
        if (datagramSocket != null) {
            try {
                byte[] sentContent = socketPackage.getBytesUDP();
                DatagramPacket pack = new DatagramPacket(sentContent, sentContent.length);
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
        if (datagramSocket != null && !datagramSocket.isClosed()) {
            datagramSocket.close();
        }
    }

    @Override
    public void receiveDataAndHandle() throws IOException {
        DatagramPacket datagramPacket = new DatagramPacket(new byte[1024], 1024);
        datagramSocket.receive(datagramPacket);
        byte[] receiveData = Arrays.copyOf(datagramPacket.getData(), datagramPacket.getLength());
        Log.i(TAG, "receive data " + ByteUtil.byteArrayToReadableHexString(receiveData));
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
                Log.i(TAG, "broadcast reply, ip: " + datagramPacket.getAddress().toString()
                        + " port: " + datagramPacket.getPort());
                Device device = new Device();
                //add ip to device
                device.getInfo().setIp(datagramPacket.getAddress().toString());

                byte[] authCodeBytes = Arrays.copyOfRange(receiveData, 6, receiveDataLength - 4);
                String authCode = ByteUtil.byteArrayToHexString(authCodeBytes);
                device.setAuthCode(authCode);
                byte[] authCodeEncodeBytes = ByteUtil.encodeAuthCode(authCodeBytes);
                device.setAuthCodeEncode(ByteUtil.byteArrayToHexString(authCodeEncodeBytes));

                //notify find device
                ObserveData od = new ObserveData(ObserveData.FIND_DEVICE_BY_UDP, device);
                MyApp.getApp().getSocketManager().notifyActivity(od);
                break;

            case UdpPackage.AFN_LOGIN:

            default:
                throw new IOException("udp package afn error");

        }
    }
}
