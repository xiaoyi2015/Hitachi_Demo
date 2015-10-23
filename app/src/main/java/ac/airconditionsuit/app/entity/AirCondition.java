package ac.airconditionsuit.app.entity;

import ac.airconditionsuit.app.aircondition.AirConditionControl;
import ac.airconditionsuit.app.aircondition.AirConditionStatusResponse;

/**
 * Created by ac on 10/15/15.
 */
public class AirCondition extends Command {
    public static final int UNFETCH = -10010;
    int realTemperature = UNFETCH;
    int warning;

    public int getRealTemperature() {
        return realTemperature;
    }

    public void setRealTemperature(int realTemperature) {
        this.realTemperature = realTemperature;
    }

    public int getWarning() {
        return warning;
    }

    public void setWarning(int warning) {
        this.warning = warning;
    }

    public void changeStatus(AirConditionStatusResponse airConditionStatusResponse) {
        mode = airConditionStatusResponse.getMode();
        onoff = airConditionStatusResponse.getOnoff();
        fan = airConditionStatusResponse.getWindVelocity();
        temperature = airConditionStatusResponse.getTemperature();
        realTemperature = airConditionStatusResponse.getHuifengTemperature();
        warning = airConditionStatusResponse.getWarning();
    }
}
