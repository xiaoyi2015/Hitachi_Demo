package ac.airconditionsuit.app.aircondition;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.entity.*;
import ac.airconditionsuit.app.network.socket.socketpackage.ControlPackage;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ac on 10/15/15.
 *
 */
public class AirConditionManager {

    List<AirCondition> airConditions = new ArrayList<>();

    public void init() {
        MyApp.getApp().getSocketManager().getAirConditionStatusFromHostDevice(
                MyApp.getApp().getServerConfigManager().getDevices()
        );
    }

    public void updateAirconditionStatue(byte[] status) {
        try {
            AirConditionStatusResponse airConditionStatusResponse =
                    AirConditionStatusResponse.decodeFromByteArray(status);

            AirCondition airCondition = getAirCondition(airConditionStatusResponse.getAddress());

            switch (airConditionStatusResponse.getMode()) {
                case AirConditionControl.MODE_BLAST:
                    airCondition.setMode(AirCondition.MODE_BLAST);
                    break;

                case AirConditionControl.MODE_DEHUMIDIFICATION:
                    airCondition.setMode(AirCondition.MODE_DEHUMIDIFICATION);
                    break;

                case AirConditionControl.MODE_HEATING:
                    airCondition.setMode(AirCondition.MODE_HEATING);
                    break;

                case AirConditionControl.MODE_REFRIGERATION:
                    airCondition.setMode(AirCondition.MODE_REFRIGERATION);
                    break;
            }

            switch (airConditionStatusResponse.getOnoff()) {
                case AirConditionControl.ON:
                    airCondition. setOnoff(AirCondition.ON);
                    break;

                case AirConditionControl.OFF:
                    airCondition. setOnoff(AirCondition.OFF);
                    break;
            }

            switch (airConditionStatusResponse.getWindVelocity()) {
                case AirConditionStatusResponse.WINDVELOCITY_HIGH:
                    airCondition.setFan(AirCondition.WINDVELOCITY_HIGH);
                    break;

                case AirConditionStatusResponse.WINDVELOCITY_MIDDLE:
                    airCondition.setFan(AirCondition.WINDVELOCITY_MIDDLE);
                    break;

                case AirConditionStatusResponse.WINDVELOCITY_LOW:
                    airCondition.setFan(AirCondition.WINDVELOCITY_LOW);
                    break;
            }

            airCondition.setTemperature(airConditionStatusResponse.getTemperature());
            airCondition.setRealTemperature(airConditionStatusResponse.getHuifengTemperature());
            airCondition.setWarning(airConditionStatusResponse.getWarning());

            MyApp.getApp().getSocketManager().notifyActivity(new ObserveData(ObserveData.AIR_CONDITION_STATUS_RESPONSE, airCondition));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void controlScene(Scene scene) throws Exception {
        MyApp.getApp().getSocketManager().sendMessage(scene.toSocketControlPackage());
    }

    public void controlRoom(Room room, AirConditionControl airConditionControl) throws Exception {
        MyApp.getApp().getSocketManager().sendMessage(new ControlPackage(room, airConditionControl));
    }

    /**
     * @param address 待查找的空调的地址
     * @return 可能为空
     */
    public AirCondition getAirCondition(int address){
        for (AirCondition airCondition : airConditions) {
            if (airCondition.getAddress() == address) {
                return airCondition;
            }
        }
        return null;
    }

}
