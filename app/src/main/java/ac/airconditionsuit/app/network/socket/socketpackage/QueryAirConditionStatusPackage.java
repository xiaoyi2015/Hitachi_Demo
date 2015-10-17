package ac.airconditionsuit.app.network.socket.socketpackage;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.network.socket.socketpackage.Tcp.TCPSendMessagePackage;
import ac.airconditionsuit.app.network.socket.socketpackage.Udp.UdpPackage;
import ac.airconditionsuit.app.util.ByteUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ac on 10/9/15.
 */
public class QueryAirConditionStatusPackage extends SocketPackage {

    List<Byte> airConditionAddresses = new ArrayList<>();

    public QueryAirConditionStatusPackage(List<Integer> airConditionAddresses) {
        for (int i : airConditionAddresses) {
            this.airConditionAddresses.add((byte) i);
        }
    }

    private UdpPackage genUdpPackage() {
        udpPackage = UdpPackage.genQueryAirConditionStatusPackage(airConditionAddresses);
        return udpPackage;
    }

    @Override
    public byte[] getBytesUDP() throws Exception {
        return genUdpPackage().getBytes();
    }

    @Override
    public byte[] getBytesTCP() throws UnsupportedEncodingException {
        return new TCPSendMessagePackage(genUdpPackage(),
                MyApp.getApp().getUser().getCust_id(),
                MyApp.getApp().getServerConfigManager().getCurrentChatId()).getBytes();
    }
}
