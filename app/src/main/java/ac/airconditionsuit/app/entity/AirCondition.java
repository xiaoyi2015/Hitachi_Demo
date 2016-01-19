package ac.airconditionsuit.app.entity;

import ac.airconditionsuit.app.aircondition.AirConditionControl;
import ac.airconditionsuit.app.aircondition.AirConditionManager;
import ac.airconditionsuit.app.aircondition.AirConditionStatusResponse;

/**
 * Created by ac on 10/15/15.
 */
public class AirCondition extends Command {
    public static final int UNFETCH = -10010;
    int realTemperature = UNFETCH;
    int warning;
    int flag = 0;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public AirCondition(AirConditionStatusResponse airConditionStatusResponse) {
        super();
        this.warning = airConditionStatusResponse.getWarning();
        this.address = airConditionStatusResponse.getAddress();
        this.realTemperature = airConditionStatusResponse.getHuifengTemperature();
        this.mode = airConditionStatusResponse.getMode();
        this.onoff = airConditionStatusResponse.getOnoff() == 1;
        this.temperature = airConditionStatusResponse.getTemperature();
        this.fan = airConditionStatusResponse.getWindVelocity();
    }

    public AirCondition() {
        this.warning = AirConditionControl.UNKNOW;
        this.address = AirConditionControl.UNKNOW;
        this.realTemperature = AirConditionControl.UNKNOW;
        this.mode = AirConditionControl.UNKNOW;
        this.onoff = false;
        this.temperature = AirConditionControl.UNKNOW;
        this.fan = AirConditionControl.UNKNOW;
    }

    public AirCondition(DeviceFromServerConfig dev) {
        this.warning = 0;
        this.address = dev.getAddress_new();
        this.realTemperature = AirConditionControl.UNKNOW;
        this.mode = AirConditionControl.UNKNOW;
        this.onoff = false;
        this.temperature = AirConditionControl.UNKNOW;
        this.fan = AirConditionControl.UNKNOW;
    }

    public AirCondition(AirCondition ac) {
        this.warning = ac.warning;
        this.address = ac.address;
        this.realTemperature = ac.realTemperature;
        this.mode = ac.mode;
        this.onoff = ac.onoff;
        this.temperature = ac.temperature;
        this.fan = ac.fan;
    }

    private boolean variableWrong(int t) {
        if (t == AirConditionControl.UNKNOW || t == AirConditionControl.EMPTY || t == UNFETCH) {
            return true;
        }
        return false;
    }

    public void repair() {
        if (variableWrong(mode)) {
            mode = 0;
        }
        if (variableWrong(fan)) {
            fan = 1;
        }

        if (variableWrong((int) temperature)) {
            temperature = 25;
        }

        if (variableWrong(realTemperature)) {
            realTemperature = 25;
        }
    }

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
        onoff = airConditionStatusResponse.getOnoff() == 1;
        fan = airConditionStatusResponse.getWindVelocity();
        temperature = airConditionStatusResponse.getTemperature();
        realTemperature = airConditionStatusResponse.getHuifengTemperature();
        warning = airConditionStatusResponse.getWarning();
    }
}
