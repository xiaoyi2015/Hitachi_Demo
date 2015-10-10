package ac.airconditionsuit.app.network.socket.socketpackage;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.network.socket.socketpackage.Tcp.TcpPackage;
import ac.airconditionsuit.app.network.socket.socketpackage.Udp.UdpPackage;

/**
 * Created by ac on 10/9/15.
 */
public class LoginPackage extends SocketPackage {

    @Override
    public byte[] getBytesUDP() throws Exception {
        UdpPackage udpPackage = new UdpPackage();
        udpPackage.setContent(new UdpPackage.LoginUdpPackageContent());
        return udpPackage.getBytes();
    }

    @Override
    public byte[] getBytesTCP() {
        //TODO for luzheqi
        return new TcpPackage().getBytes();
    }
}
