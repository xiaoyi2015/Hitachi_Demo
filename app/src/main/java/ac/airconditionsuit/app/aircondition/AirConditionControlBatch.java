package ac.airconditionsuit.app.aircondition;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.entity.Command;
import ac.airconditionsuit.app.entity.DeviceFromServerConfig;
import ac.airconditionsuit.app.entity.RootEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ac on 10/14/15.
 */
public class AirConditionControlBatch extends RootEntity {
    List<Byte> addresses;

    AirConditionControl airConditionControl;

    public AirConditionControlBatch(Command c) throws Exception {
        addresses = new ArrayList<>(1);
        int address = c.getAddress();
        if (address > 255 || address < 0) {
            throw new Exception("air condition address error");
        }
//        address = 254;
        addresses.add((byte) address);

        airConditionControl = new AirConditionControl();
        airConditionControl.setOnoff(c.getOnoff());
        airConditionControl.setWindVelocity(c.getFan());
        airConditionControl.setMode(c.getMode());

        int temperature = (int) c.getTemperature();
        if (airConditionControl.getMode() == AirConditionControl.MODE_HEATING) {
            if (temperature < 17 || temperature > 30) {
                throw new Exception("temperature error in heating mode");
            }
        } else {
            if (temperature < 19 || temperature > 30) {
                throw new Exception("temperature error in other mode");
            }

        }

        airConditionControl.setTemperature(temperature);
    }

    public AirConditionControlBatch(List<Integer> elements, AirConditionControl airConditionControl) throws Exception {
        addresses = new ArrayList<>();
        for (Integer index : elements) {
            List<DeviceFromServerConfig> devices_new = MyApp.getApp().getServerConfigManager().getDevices_new();
            if (devices_new.size() <= index) {
                throw new Exception("air condition index is to large");
            }
            DeviceFromServerConfig deviceFromServerConfig = devices_new.get(index);
            int address = deviceFromServerConfig.getAddress_new();
            if (address > 255 || address < 0) {
                throw new Exception("air condition address error");
            }
            addresses.add((byte) (address & 0xff));
        }
        this.airConditionControl = airConditionControl;
    }

    public byte[] getBytes() throws Exception {
        if (addresses.size() > 255) {
            throw new Exception("addresses size can not be more than 255");
        }

        //add address
        byte[] result = new byte[addresses.size() + 5];
        result[0] = (byte) addresses.size();
        for (int i = 0; i < addresses.size(); ++i) {
            result[i + 1] = addresses.get(i);
        }

        System.arraycopy(airConditionControl.getBytes(), 0, result, result.length - 4, 4);

        return result;
    }
}
