package ac.airconditionsuit.app.aircondition;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.entity.Scene;

/**
 * Created by ac on 10/15/15.
 *
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

    public void controlScene(Scene scene) throws Exception {
        MyApp.getApp().getSocketManager().sendMessage(scene.toSocketControlPackage());
    }

}
