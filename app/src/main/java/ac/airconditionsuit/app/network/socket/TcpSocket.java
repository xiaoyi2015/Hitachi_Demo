package ac.airconditionsuit.app.network.socket;

import ac.airconditionsuit.app.Config.ServerConfigManager;
import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.entity.ObserveData;
import ac.airconditionsuit.app.network.socket.socketpackage.ACKPackage;
import ac.airconditionsuit.app.network.socket.socketpackage.CheckDevicePackage;
import ac.airconditionsuit.app.network.socket.socketpackage.ControlPackage;
import ac.airconditionsuit.app.network.socket.socketpackage.SocketPackage;
import ac.airconditionsuit.app.network.socket.socketpackage.Tcp.TCPHeartBeatPackage;
import ac.airconditionsuit.app.network.socket.socketpackage.Tcp.TCPLoginPackage;
import ac.airconditionsuit.app.network.socket.socketpackage.Tcp.TCPSendMessagePackage;
import ac.airconditionsuit.app.network.socket.socketpackage.Tcp.TcpPackage;
import ac.airconditionsuit.app.network.socket.socketpackage.Udp.UdpPackage;
import ac.airconditionsuit.app.util.ACByteQueue;
import ac.airconditionsuit.app.util.ByteUtil;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ac on 10/11/15.
 */
public class TcpSocket implements SocketWrap {
    public static final String IP = "114.215.83.189";//日立
    private static final int PORT = 7000;
    private static final String TAG = "TcpSocket";

    private Socket socket;
    private ACByteQueue readQueue = new ACByteQueue();
    private Map<Short, UdpPackage.Handler> handlers = new HashMap<>();

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
                UdpPackage udpPackage = socketPackage.getUdpPackage();
                MyApp.getApp().getSocketManager().addSentUdpPackage(udpPackage);
                if (socketPackage instanceof ControlPackage) {
                    ControlPackage cp = (ControlPackage) socketPackage;
                    if (cp.getHandle() != null) {
                        handlers.put(cp.getTCPMegNo(), cp.getHandle());
                    }
                }
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
            _read(tempForRead);
        }
        byte[] header = readQueue.poll(6);


        //读取body
        int bodyLength = ByteUtil.byteArrayToShort(header, 1);
        while (readQueue.size() < bodyLength) {
            _read(tempForRead);
        }

        byte[] body = readQueue.poll(bodyLength);

        byte[] result = new byte[6 + bodyLength];

        //copy header to result
        System.arraycopy(header, 0, result, 0, 6);

        //copy body to result
        System.arraycopy(body, 0, result, 6, bodyLength);

        return result;
    }

    private void _read(byte[] tempForRead) throws IOException {
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

            case TcpPackage.RECEIVE_MESSAGE_FROM_SERVER:
                //处理从服务器接受到的消息
                handleReceiveDataFromServer(receiveData);
                break;

            case TCPSendMessagePackage.SEND_MESSAGE_RETURN_MSG_TYPE:
                //处理给服务器发消息的返回值，在这里只要做设备在线处理即可
                handleCheckDevice(receiveData);
                short msg_no = ByteUtil.byteArrayToShort(receiveData, 3);
                UdpPackage.Handler handler = handlers.get(msg_no);
                if (handler != null) {
                    handler.success();
                    handlers.remove(msg_no);
                }
                break;

            case TcpPackage.TICK_OFF_LINE_MSG_TYPE:
                handleOffLine(receiveData);
                break;
            case 16:
                //ignore
                break;

            default:
                throw new IOException("tcp package message type error");

        }
    }

    @Override
    public boolean isConnect() {
        return socket != null && socket.isConnected();
    }

    private void handleOffLine(byte[] receiveData) {
        if (ByteUtil.byteArrayToShort(receiveData, 15) == 700 || ByteUtil.byteArrayToShort(receiveData, 15) == 701) {
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
            Log.i(TAG, "handle receive data as bin: " +  ByteUtil.byteArrayToReadableHexString(data));
            long chat_id = ByteUtil.byteArrayToLong(receiveData, 7);
            if (chat_id == 0 || chat_id == MyApp.getApp().getServerConfigManager().getCurrentChatId()) {
                MyApp.getApp().getSocketManager().handUdpPackage(null, data);
            } else {
                Log.i(TAG, "receive data not for current device, ignore");
            }
        } else if (contentType == 0) {
            String jsonString = new String(data);
            short msg_no = ByteUtil.byteArrayToShort(receiveData, 3);
            Log.i(TAG, "handle receive data as json: " + jsonString);
            if (jsonString.contains("空调控制成功")) {
                MyApp.getApp().getAirConditionManager().queryAirConditionStatus();
            }
            MyApp.getApp().getPushDataManager().add(jsonString, msg_no);
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

            ServerConfigManager serverConfigManager = MyApp.getApp().getServerConfigManager();
            if (serverConfigManager != null && serverConfigManager.hasDevice()) {
                MyApp.getApp().getAirConditionManager().queryAirConditionStatus();
            }

        } else if (result_code == 401) {
            Log.v("liutao", "tcp receive 401");
            MyApp.getApp().getSocketManager().close();
            MyApp.getApp().getSocketManager().notifyActivity(new ObserveData(ObserveData.OFFLINE));
        } else if (result_code == 403) {
            throw new IOException("tcp login fail");
        } else {
            throw new IOException("tcp login fail, unknown ");
        }
    }


    public void checkDeviceConnect() {
        ServerConfigManager serverConfigManager = MyApp.getApp().getServerConfigManager();
        if (serverConfigManager == null) {
            return;
        }
        if (serverConfigManager.hasDevice()) {
            CheckDevicePackage checkDevicePackage = new CheckDevicePackage();
            sendMessage(checkDevicePackage);
        }
    }
}
