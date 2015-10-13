package ac.airconditionsuit.app.network.socket.socketpackage;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.entity.MyUser;
import ac.airconditionsuit.app.network.socket.socketpackage.Tcp.TCPLoginPackage;
import ac.airconditionsuit.app.network.socket.socketpackage.Udp.UdpPackage;

import java.io.UnsupportedEncodingException;

/**
 * Created by ac on 10/9/15.
 */
public class LoginPackage extends SocketPackage {

    @Override
    public byte[] getBytesUDP() throws Exception {
        udpPackage = UdpPackage.genLoginPackage();
        return udpPackage.getBytes();
    }

    @Override
    public byte[] getBytesTCP() throws UnsupportedEncodingException {
        MyUser myUser = MyApp.getApp().getUser();
        return new TCPLoginPackage(myUser.getCust_id(), myUser.getAuth()).getBytes();
    }
}
