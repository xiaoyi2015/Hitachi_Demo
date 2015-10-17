package ac.airconditionsuit.app.network.socket;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.entity.Device;
import ac.airconditionsuit.app.entity.ObserveData;
import ac.airconditionsuit.app.network.socket.socketpackage.Udp.UdpPackage;
import ac.airconditionsuit.app.util.ByteUtil;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ac on 10/17/15.
 */
public class UdpPackageHandler {
    private static final String TAG = "UdpPackageHandler";

    private Map<Byte, UdpPackage> sentPackage = new HashMap<>();

    public void addSentPackage(UdpPackage p){
        sentPackage.put(p.getFramNumber(), p);
    }

    public void handleUdpPackage(DatagramPacket datagramPacket, byte[] receiveData) throws IOException {
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

        //收到的数据没有问题，无论什么包，都可以当作心跳成功
        MyApp.getApp().getSocketManager().heartSuccess();

        //control byte
        byte control = receiveData[1];
        //1表示启动站，0表示从动站。
        byte prm = (byte) (control / 128);
        //帧序列号
        byte pfc = (byte) (control - 128 * prm);

        //数据域的功能码
        byte afn = receiveData[3];

        switch (afn) {
            case UdpPackage.AFN_BROADCAST:
                Log.i(TAG, "broadcast reply, ip: " + datagramPacket.getAddress().toString()
                        + " port: " + datagramPacket.getPort());
                Device device = new Device();
                //add ip to device
                device.getInfo().setIp(datagramPacket.getAddress().getHostAddress());

                byte[] authCodeBytes = Arrays.copyOfRange(receiveData, 6, receiveDataLength - 2);
                String authCode = ByteUtil.byteArrayToHexString(authCodeBytes);
                device.setAuthCode(authCode);
                byte[] authCodeEncodeBytes = ByteUtil.encodeAuthCode(authCodeBytes);
                device.setAuthCodeEncode(ByteUtil.byteArrayToHexString(authCodeEncodeBytes));

                //notify find device
                ObserveData od = new ObserveData(ObserveData.FIND_DEVICE_BY_UDP, device);
                MyApp.getApp().getSocketManager().notifyActivity(od);
                break;

            case UdpPackage.AFN_GET_AIR_CONDITION_ADDRESS:
                Log.i(TAG, "udp get air condition success");
                break;

            case UdpPackage.AFN_AIR_CONDITION_STATUS_RESPONSE:
                Log.i(TAG, "udp get air condition status success");
                MyApp.getApp().getAirconditionManager().updateAirconditionStatue(UdpPackage.getContentData(receiveData));
                break;

            case UdpPackage.AFN_TIMER:
                Log.i(TAG, "receive timer");
                MyApp.getApp().getAirconditionManager().updateTimerStatue(UdpPackage.getContentData(receiveData));
                break;

            case UdpPackage.AFN_TIMER_RUN_RESPONSE:
                Log.i(TAG, "receive timer run");
                MyApp.getApp().getAirconditionManager().timerRun(ByteUtil.byteArrayToShort(UdpPackage.getContentData(receiveData)));
                break;


            case UdpPackage.AFN_NO:
                String error_no = new String(Arrays.copyOfRange(receiveData, 4, 8), Charset.forName("US-ASCII"));
                Log.i(TAG, "udp error: " + error_no);
                sentPackage.get(pfc).getHandler().fail(Integer.parseInt(error_no));
                sentPackage.remove(pfc);

                break;

            case UdpPackage.AFN_YES:
                sentPackage.get(pfc).getHandler().success();
                sentPackage.remove(pfc);
                break;

            default:
                throw new IOException("udp package afn error");

        }
    }

}
