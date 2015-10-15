package ac.airconditionsuit.app.entity;

/**
 * Created by ac on 10/15/15.
 */
public class Command extends RootEntity {
    float temperature;
    int address;

    public static final int ON = 1;
    public static final int OFF = 0;
    int onoff;



    public static final int WINDVELOCITY_HIGH = 2;
    public static final int WINDVELOCITY_MIDDLE = 1;
    public static final int WINDVELOCITY_LOW = 0;
    int fan;


    /**
     * 制冷
     */
    public static final int MODE_REFRIGERATION = 0;

    /**
     * 送风
     */
    public static final int MODE_BLAST = 3;

    /**
     * 除湿
     */
    public static final int MODE_DEHUMIDIFICATION = 2;

    /**
     * 制热
     */
    public static final int MODE_HEATING = 1;
    int mode;

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getOnoff() {
        return onoff;
    }

    public void setOnoff(int onoff) {
        this.onoff = onoff;
    }

    public int getFan() {
        return fan;
    }

    public void setFan(int fan) {
        this.fan = fan;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
