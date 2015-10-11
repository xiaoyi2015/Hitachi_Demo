package ac.airconditionsuit.app.network.socket;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.network.socket.socketpackage.CheckDevicePackage;
import ac.airconditionsuit.app.network.socket.socketpackage.SocketPackage;
import ac.airconditionsuit.app.network.socket.socketpackage.Tcp.TCPHeartBeatPackage;
import ac.airconditionsuit.app.network.socket.socketpackage.Tcp.TCPLoginPackage;
import ac.airconditionsuit.app.network.socket.socketpackage.Tcp.TCPSendMessagePackage;
import ac.airconditionsuit.app.network.socket.socketpackage.Tcp.TcpPackage;
import ac.airconditionsuit.app.util.ACByteQueue;
import ac.airconditionsuit.app.util.ByteUtil;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by ac on 10/11/15.
 */
class TcpSocket implements SocketWrap {
    private static final String IP = "114.215.83.189";//日立
    private static final int PORT = 7000;

    private Socket socket;
    private ACByteQueue readQueue = new ACByteQueue();

    @Override
    public void connect() throws IOException {
        socket = new Socket(IP, PORT);
        Log.i(SocketManager.TAG, "connect to host by tcp success");
    }

    @Override
    public void sendMessage(SocketPackage socketPackage) {
        if (socket != null && socket.isConnected()) {
            try {
                byte[] dataSent = socketPackage.getBytesTCP();
                socket.getOutputStream().write(dataSent);
                Log.i(SocketManager.TAG, "tcp send data: " + ByteUtil.byteArrayToReadableHexString(dataSent));
            } catch (Exception e) {
                Log.e(SocketManager.TAG, "sendMessage by Tcp failed: socket.getOutputStream() error");
                e.printStackTrace();
            }
        } else {
            Log.e(SocketManager.TAG, "sendMessage by Tcp failed: socket is null or socket.isConnected() return false");
        }
    }

    @Override
    public void close() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            socket = null;
        }
    }

    public byte[] readMessage() throws IOException {
        byte[] tempForRead = new byte[256];
        //先读取tcp包头部的六个字节
        while (readQueue.size() < 6) {
            if (socket != null && socket.isConnected()) {
                int readCount = socket.getInputStream().read(tempForRead, 0, 255);
                if (readCount <= 0) {
                    throw new IOException("read data return -1 or 0");
                }
                readQueue.offer(tempForRead, 0, readCount);
            } else {
                throw new IOException("read fail because socket is null or socket is close");
            }
        }
        byte[] header = readQueue.poll(6);


        //读取body
        int bodyLength = ByteUtil.byteArrayToShort(header, 1);
        while (readQueue.size() < bodyLength) {
            if (socket != null && socket.isConnected()) {
                int readCount = socket.getInputStream().read(tempForRead, 0, 255);
                if (readCount <= 0) {
                    throw new IOException("read data return -1 or 0");
                }
                readQueue.offer(tempForRead, 0, readCount);
            } else {
                throw new IOException("read fail because socket is null or socket is close");
            }
        }

        byte[] body = readQueue.poll(bodyLength);

        byte[] result = new byte[6 + bodyLength];

        //copy header to result
        System.arraycopy(header, 0, result, 0, 6);

        //copy body to result
        System.arraycopy(body, 0, result, 6, bodyLength);

        return result;
    }

    @Override
    public void receiveDataAndHandle() throws IOException {
        byte[] receiveData = readMessage();
        Log.i(SocketManager.TAG, "receive data from tcp: " + ByteUtil.byteArrayToReadableHexString(receiveData));

        int receiveDataLength = receiveData.length;

        //check head and end
        if (receiveData[0] != TcpPackage.START_BYTE
                || receiveData[receiveDataLength - 1] != TcpPackage.END_BYTE) {
            throw new IOException("udp package start flag or end flag error!");
        }

        //check checksum
        byte[] checksum = TcpPackage.getCheckSum(receiveData);
        if (!Arrays.equals(checksum, Arrays.copyOfRange(receiveData, receiveDataLength - 3, receiveDataLength - 1))) {
            throw new IOException("udp package checksum error");
        }

        //check length
        short dataLength = ByteUtil.byteArrayToShort(receiveData, 1);
        if (dataLength + 6 != receiveDataLength) {
            throw new IOException("udp package length error");
        }

        byte msg_type = receiveData[5];

        switch (msg_type) {
            case TCPLoginPackage.LOGIN_RETURN_MSG_TYPE:
                handleLoginReturn(receiveData);
                break;

            case TCPHeartBeatPackage.HEART_BEAT_MSG_TYPE:
                handleHeartBeat();
                break;

            case TCPSendMessagePackage.SEND_MESSAGE_MSG_TYPE:
                //TODO for luzheqi, 处理从服务器接受到的消息
                break;

            case TCPSendMessagePackage.SEND_MESSAGE_RETURN_MSG_TYPE:
                //处理给服务器发消息的返回值，在这里只要做设备在线处理即可
                handleCheckDevice(receiveData);
                break;

            default:
                throw new IOException("tcp package message type error");

        }
    }

    private void handleCheckDevice(byte[] receiveData) throws IOException {
        short result_code = ByteUtil.byteArrayToShort(receiveData, 32);
        if (result_code == 200) {
            Log.i(SocketManager.TAG, "check Device success");
            MyApp.getApp().getSocketManager().setStatus(SocketManager.TCP_DEVICE_CONNECT);
        } else {
            Log.i(SocketManager.TAG, "check Device fail, status code is: " + result_code);
            MyApp.getApp().getSocketManager().setStatus(SocketManager.TCP_HOST_CONNECT);
        }
    }

    private void handleHeartBeat() throws IOException {
        SocketManager socketManager = MyApp.getApp().getSocketManager();
        Log.i(SocketManager.TAG, "tcp heart beat ok");
        socketManager.setLastHeartSuccessTime(System.currentTimeMillis());
        socketManager.setStatus(SocketManager.TCP_HOST_CONNECT);
        checkDeviceConnect();
    }

    private void handleLoginReturn(byte[] receiveData) throws IOException {
        short result_code = ByteUtil.byteArrayToShort(receiveData, 15);
        if (result_code == 200) {
            Log.i(SocketManager.TAG, "tcp login success");
            MyApp.getApp().getSocketManager().startHeartBeat();
        } else if (result_code == 401) {
            throw new IOException("tcp login auth fail");
        } else if (result_code == 403) {
            throw new IOException("tcp login fail");
        } else {
            throw new IOException("tcp login fail, unknow ");
        }
    }

    private void checkDeviceConnect() {
        if (MyApp.getApp().getServerConfigManager().hasDevice()) {
            CheckDevicePackage checkDevicePackage = new CheckDevicePackage();
            sendMessage(checkDevicePackage);
        }
    }
}
