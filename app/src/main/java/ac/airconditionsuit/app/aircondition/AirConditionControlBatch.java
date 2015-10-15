package ac.airconditionsuit.app.aircondition;

import ac.airconditionsuit.app.entity.Command;
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
        addresses.add((byte) address);

        airConditionControl = new AirConditionControl();
        switch (c.getOnoff()) {
            case Command.ON:
                airConditionControl.setOnoff(AirConditionControl.ON);
                break;
            case Command.OFF:
                airConditionControl.setOnoff(AirConditionControl.OFF);
                break;
            default:
                throw new Exception("air condition onoff error");
        }

        switch (c.getFan()) {
            case Command.WINDVELOCITY_HIGH:
                airConditionControl.setWindVelocity(AirConditionControl.WINDVELOCITY_HIGH);
                break;

            case Command.WINDVELOCITY_MIDDLE:
                airConditionControl.setWindVelocity(AirConditionControl.WINDVELOCITY_MIDDLE);
                break;

            case Command.WINDVELOCITY_LOW:
                airConditionControl.setWindVelocity(AirConditionControl.WINDVELOCITY_LOW);
                break;
            default:
                throw new Exception("wind velocity error");
        }

        switch (c.getMode()) {
            case Command.MODE_BLAST:
                airConditionControl.setMode(AirConditionControl.MODE_BLAST);
                break;

            case Command.MODE_DEHUMIDIFICATION:
                airConditionControl.setMode(AirConditionControl.MODE_DEHUMIDIFICATION);
                break;

            case Command.MODE_HEATING:
                airConditionControl.setMode(AirConditionControl.MODE_HEATING);
                break;

            case Command.MODE_REFRIGERATION:
                airConditionControl.setMode(AirConditionControl.MODE_REFRIGERATION);
                break;
            default:
                throw new Exception("mode error");
        }

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
        for (Integer address : elements) {
            if (address > 255 || address < 0) {
                throw new Exception("air condition address error");
            }
            addresses.add((byte) (address & 0xff));
        }
        this.airConditionControl = airConditionControl;
    }

    byte[] getBytes() throws Exception {
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
