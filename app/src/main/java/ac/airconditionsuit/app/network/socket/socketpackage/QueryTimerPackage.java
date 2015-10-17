package ac.airconditionsuit.app.network.socket.socketpackage;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.network.socket.socketpackage.Tcp.TCPSendMessagePackage;
import ac.airconditionsuit.app.network.socket.socketpackage.Udp.UdpPackage;

/**
 * Created by ac on 10/9/15.
 */
public class QueryTimerPackage extends SocketPackage {
    private int id;

    public QueryTimerPackage(int id) {
        this.id = id;
    }

    private UdpPackage genUdpPackage() throws Exception {
        udpPackage = UdpPackage.genQueryTimerPackage(id);
        return udpPackage;
    }

    @Override
    public byte[] getBytesUDP() throws Exception {
        return genUdpPackage().getBytes();
    }

    @Override
    public byte[] getBytesTCP() throws Exception {
        return new TCPSendMessagePackage(genUdpPackage(),
                MyApp.getApp().getUser().getCust_id(),
                MyApp.getApp().getServerConfigManager().getCurrentChatId()).getBytes();
    }
}
