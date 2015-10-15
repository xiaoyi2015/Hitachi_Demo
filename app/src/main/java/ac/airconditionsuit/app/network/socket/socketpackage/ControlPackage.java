package ac.airconditionsuit.app.network.socket.socketpackage;

import ac.airconditionsuit.app.aircondition.AirConditionControl;
import ac.airconditionsuit.app.aircondition.AirConditionControlBatch;
import ac.airconditionsuit.app.entity.Command;
import ac.airconditionsuit.app.entity.Room;

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

    @Override
    public byte[] getBytesUDP() throws Exception {
        return new byte[0];
    }

    @Override
    public byte[] getBytesTCP() throws UnsupportedEncodingException {
        return new byte[0];
    }
}
