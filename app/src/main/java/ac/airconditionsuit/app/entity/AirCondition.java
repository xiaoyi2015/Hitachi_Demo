package ac.airconditionsuit.app.entity;

/**
 * Created by ac on 10/15/15.
 */
public class AirCondition extends Command {
    int realTemperature;
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
}
