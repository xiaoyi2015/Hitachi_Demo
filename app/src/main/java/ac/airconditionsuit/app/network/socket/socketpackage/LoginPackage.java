package ac.airconditionsuit.app.network.socket.socketpackage;

import ac.airconditionsuit.app.network.socket.socketpackage.Tcp.TcpPackage;
import ac.airconditionsuit.app.network.socket.socketpackage.Udp.UdpPackage;

/**
 * Created by ac on 10/9/15.
 */
public class LoginPackage implements SocketPackage {
    private String mac;

    public LoginPackage(String mac) {
        this.mac = mac;
    }

    @Override
    public byte[] getBytesUDP() throws Exception {
        UdpPackage udpPackage = new UdpPackage();
        udpPackage.setContent(new UdpPackage.LoginUdpPackageContent(mac));
        return udpPackage.getBytes();
    }

    @Override
    public byte[] getBytesTCP() {
        //TODO for luzheqi
        return new TcpPackage().getBytes();
    }
}
