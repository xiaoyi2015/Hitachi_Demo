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

    @Override
    public byte[] getBytesUDP() throws Exception {
        //TODO
        return null;
    }

    @Override
    public byte[] getBytesTCP() throws UnsupportedEncodingException {
        return new TCPHeartBeatPackage(MyApp.getApp().getUser().getCust_id()).getBytes();
    }
}
