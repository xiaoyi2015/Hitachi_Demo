package ac.airconditionsuit.app.network.socket.socketpackage;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.entity.MyUser;
import ac.airconditionsuit.app.network.socket.socketpackage.Tcp.TCPHeartBeatPackage;
import ac.airconditionsuit.app.network.socket.socketpackage.Tcp.TCPLoginPackage;
import ac.airconditionsuit.app.network.socket.socketpackage.Udp.UdpPackage;

import java.io.UnsupportedEncodingException;

/**
 * Created by ac on 10/9/15.
 */
public class HeartBeatPackage extends SocketPackage {
    private static final String TAG = "HeartBeatPackage";

    @Override
    public byte[] getBytesUDP() throws Exception {
        if (udpPackage == null) {
            udpPackage = UdpPackage.genHeartBeatPackage();
        }
        return udpPackage.getBytes();
    }

    @Override
    public byte[] getBytesTCP() throws UnsupportedEncodingException {
        return new TCPHeartBeatPackage(MyApp.getApp().getUser().getCust_id()).getBytes();
    }
}
