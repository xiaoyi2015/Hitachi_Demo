package ac.airconditionsuit.app.network.socket.socketpackage;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.aircondition.AirConditionControl;
import ac.airconditionsuit.app.aircondition.AirConditionControlBatch;
import ac.airconditionsuit.app.entity.Command;
import ac.airconditionsuit.app.entity.Room;
import ac.airconditionsuit.app.network.socket.socketpackage.Tcp.TCPSendMessagePackage;
import ac.airconditionsuit.app.network.socket.socketpackage.Udp.UdpPackage;

import java.io.UnsupportedEncodingException;

/**
 * Created by ac on 10/15/15.
 *
 */
public class ControlPackage extends SocketPackage {
    private AirConditionControlBatch airConditionControlBatch;
    public ControlPackage(Command c) throws Exception {
        airConditionControlBatch = new AirConditionControlBatch(c);
    }

    public ControlPackage(Room room, AirConditionControl airConditionControl) throws Exception {
        airConditionControlBatch = new AirConditionControlBatch(room.getElements(), airConditionControl);
    }

    private UdpPackage genUdpPackage() throws Exception {
        udpPackage = UdpPackage.genControlPackage(airConditionControlBatch);
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
