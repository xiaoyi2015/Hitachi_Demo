package ac.airconditionsuit.app.network.socket;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.PushData.PushDataManager;
import ac.airconditionsuit.app.entity.ObserveData;
import ac.airconditionsuit.app.network.socket.socketpackage.ACKPackage;
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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by ac on 10/11/15.
 */
class TcpSocket implements SocketWrap {
    private static final String IP = "114.215.83.189";//日立
    private static final int PORT = 7000;
    private static final String TAG = "TcpSocket";

    private Socket socket;
    private ACByteQueue readQueue = new ACByteQueue();

    @Override
    public void connect() throws IOException {
        socket = new Socket();
        socket.setKeepAlive(true);
        socket.connect(new InetSocketAddress(IP, PORT));
        Log.i(TAG, "connect to host by tcp success");
    }

    @Override
    synchronized public void sendMessage(SocketPackage socketPackage) {
        if (socket != null && socket.isConnected()) {
            try {
                byte[] dataSent = socketPackage.getBytesTCP();
                socket.getOutputStream().write(dataSent);
                Log.i(TAG, "tcp send data: " + ByteUtil.byteArrayToReadableHexString(dataSent));
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
        Log.i(TAG, "receive data from tcp: " + ByteUtil.byteArrayToReadableHexString(receiveData));

        //检查头尾标志，长度，checksum, 如果有问题就会抛出异常。
        commonCheck(receiveData);

        //收到的数据没有问题，无论什么包，都可以当作心跳成功
        MyApp.getApp().getSocketManager().heartSuccess();

        byte msg_type = receiveData[5];
        switch (msg_type) {
            case TCPLoginPackage.LOGIN_RETURN_MSG_TYPE:
                handleLoginReturn(receiveData);
                break;

            case TCPHeartBeatPackage.HEART_BEAT_MSG_TYPE:
                handleHeartBeat();
                break;

            case TCPSendMessagePackage.SEND_MESSAGE_MSG_TYPE:
                //处理从服务器接受到的消息
                handleReceiveDataFromServer(receiveData);
                break;

            case TCPSendMessagePackage.SEND_MESSAGE_RETURN_MSG_TYPE:
                //处理给服务器发消息的返回值，在这里只要做设备在线处理即可
                handleCheckDevice(receiveData);
                break;

            case TcpPackage.TICK_OFF_LINE_MSG_TYPE:
                handleOffLine(receiveData);
                break;

            default:
                throw new IOException("tcp package message type error");

        }
    }

    private void handleOffLine(byte[] receiveData) {
        if (ByteUtil.byteArrayToShort(receiveData, 15) == 700 || ByteUtil.byteArrayToShort(receiveData, 15) == 701){
            Log.i(TAG, "tcp receive offline message");
            MyApp.getApp().getSocketManager().close();
            MyApp.getApp().getSocketManager().notifyActivity(new ObserveData(ObserveData.OFFLINE));
        }
    }

    private void commonCheck(byte[] receiveData) throws IOException {
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
    }

    private void handleReceiveDataFromServer(byte[] receiveData) throws IOException {
        short contentLen = ByteUtil.byteArrayToShort(receiveData, 39);
        byte contentType = receiveData[41];
        byte[] data = Arrays.copyOfRange(receiveData, 42, 42 + contentLen);

        if (contentType == 1) {
            Log.i(TAG, "handle receive data as bin");
            long chat_id = ByteUtil.byteArrayToLong(receiveData, 7);
            if (chat_id == MyApp.getApp().getServerConfigManager().getCurrentChatId()) {
                //TODO for luzheqi, 对消息进行处理由udp的相应单元处理
            } else {
                Log.i(TAG, "receive data not for current device, ignore");
            }

        } else if (contentLen == 0) {
            Log.i(TAG, "handle receive data as json");
            PushDataManager.add(data);
        } else {
            throw new IOException("unknow content type");
        }

        byte[] msg_no = Arrays.copyOfRange(receiveData, 3, 5);
        byte[] msg_id = Arrays.copyOfRange(receiveData, 31, 39);

        sendMessage(new ACKPackage(msg_no, msg_id));

    }

    private void handleCheckDevice(byte[] receiveData) {
        short result_code = ByteUtil.byteArrayToShort(receiveData, 32);
        if (result_code == 200) {
            Log.i(TAG, "check Device success");
            MyApp.getApp().getSocketManager().checkDevice(true);
        } else {
            Log.i(TAG, "check Device fail, status code is: " + result_code);
            MyApp.getApp().getSocketManager().checkDevice(false);
        }
    }

    private void handleHeartBeat() {
        Log.i(TAG, "tcp heart beat ok");
        //check device after heartbeat
        checkDeviceConnect();
    }

    private void handleLoginReturn(byte[] receiveData) throws IOException {
        short result_code = ByteUtil.byteArrayToShort(receiveData, 15);
        if (result_code == 200) {
            Log.i(TAG, "tcp login success");
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