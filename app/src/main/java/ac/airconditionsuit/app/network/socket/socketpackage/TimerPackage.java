package ac.airconditionsuit.app.network.socket.socketpackage;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.entity.Timer;
import ac.airconditionsuit.app.network.socket.socketpackage.Tcp.TCPSendMessagePackage;
import ac.airconditionsuit.app.network.socket.socketpackage.Udp.UdpPackage;

import java.io.UnsupportedEncodingException;

/**
 * Created by ac on 10/16/15.
 */
public class TimerPackage extends SocketPackage {
    private Timer timer;

    public TimerPackage(Timer timer) {
        this.timer = timer;
    }

    private UdpPackage genUdpPackage() throws Exception {
        udpPackage = UdpPackage.genTimerPackage(timer);
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
