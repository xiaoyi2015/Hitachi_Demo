package ac.airconditionsuit.app.aircondition;

/**
 * Created by ac on 10/15/15.
 */
public class AirconditionManager {
    public void init() {

    }

    public void updateAirconditionStatue(byte[] status) {
        try {
            AirConditionStatusResponse airConditionStatusResponse =
                    AirConditionStatusResponse.decodeFromByteArray(status);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
