package ac.airconditionsuit.app.aircondition;

import ac.airconditionsuit.app.entity.RootEntity;

import java.util.List;

/**
 * Created by ac on 10/14/15.
 */
public class AirConditionControlBatch extends RootEntity{
    List<Byte> addresses;

    AirConditionControl airConditionControl;

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
