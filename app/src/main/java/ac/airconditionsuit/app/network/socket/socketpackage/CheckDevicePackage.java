package ac.airconditionsuit.app.network.socket.socketpackage;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.network.socket.socketpackage.Tcp.TCPSendMessagePackage;
import ac.airconditionsuit.app.network.socket.socketpackage.Udp.UdpPackage;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ac on 10/9/15.
 */
public class CheckDevicePackage extends SocketPackage {
    private UdpPackage genUdpPackage() {
        UdpPackage queryStatusPackage = new UdpPackage();
        List<Byte> addressList = new ArrayList<>();
        addressList.add((byte) 0);
        queryStatusPackage.setContent(new UdpPackage.QueryStatusUdpPackageContent(addressList));
        return queryStatusPackage;
    }

    @Override
    public byte[] getBytesUDP() throws Exception {
        UdpPackage queryStatusPackage = genUdpPackage();
        return queryStatusPackage.getBytes();
    }

    @Override
    public byte[] getBytesTCP() throws UnsupportedEncodingException {
        return new TCPSendMessagePackage(genUdpPackage(),
                MyApp.getApp().getUser().getCust_id(),
                MyApp.getApp().getServerConfigManager().getCurrentChatId()).getBytes();
    }
}
