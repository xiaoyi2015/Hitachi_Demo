package ac.airconditionsuit.app.network.socket.socketpackage;

import ac.airconditionsuit.app.network.socket.SocketManager;
import ac.airconditionsuit.app.network.socket.socketpackage.Udp.UdpPackage;

import java.io.UnsupportedEncodingException;

/**
 * Created by ac on 9/24/15.
 */
public abstract class SocketPackage {
    protected UdpPackage udpPackage;
    public abstract byte[] getBytesUDP() throws Exception;
    public abstract byte[] getBytesTCP() throws Exception;

    public UdpPackage getUdpPackage() throws Exception {
        return udpPackage;
    }

}
