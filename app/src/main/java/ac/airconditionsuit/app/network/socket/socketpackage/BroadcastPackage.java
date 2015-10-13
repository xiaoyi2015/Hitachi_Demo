package ac.airconditionsuit.app.network.socket.socketpackage;

import ac.airconditionsuit.app.network.socket.socketpackage.Udp.UdpPackage;

/**
 * Created by ac on 10/9/15.
 */
public class BroadcastPackage extends SocketPackage {

    @Override
    public byte[] getBytesUDP() throws Exception {
        udpPackage = UdpPackage.genBroadcastPackage();
        return udpPackage.getBytes();
    }

    /**
     * this method won't bu called because broadcast only send by udp;
     * @return null
     */
    @Override
    public byte[] getBytesTCP() {
        return null;
    }
}
