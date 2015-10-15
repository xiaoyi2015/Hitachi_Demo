package ac.airconditionsuit.app.aircondition;

import ac.airconditionsuit.app.entity.RootEntity;

import java.util.List;

/**
 * Created by ac on 10/14/15.
 */
public class AirConditionControl extends RootEntity{
    public static final int ON = 1;
    public static final int OFF = 0;

    /**
     * 制冷
     */
    public static final int MODE_REFRIGERATION = 0;

    /**
     * 送风
     */
    public static final int MODE_BLAST = 1;

    /**
     * 除湿
     */
    public static final int MODE_DEHUMIDIFICATION = 2;

    /**
     * 制热
     */
    public static final int MODE_HEATING = 3;




    int onoff;

    int mode;


    public static final int WINDVELOCITY_HIGH = 3;
    public static final int WINDVELOCITY_MIDDLE = 2;
    public static final int WINDVELOCITY_LOW = 1;
    int windVelocity;


    /**
     * 挡风板位置0~6
     */
    int position;

    public static final int AUTO = 1;
    public static final int NOT_AUTO = 0;
    /**
     * 风向是否自动
     */
    int auto;

    /**
     * 送风/除湿/制冷：19~30度
     * 制热：17~30度
     */
    int temperature;


    public void setOnoff(int onoff) {
        this.onoff = onoff;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setWindVelocity(int windVelocity) {
        this.windVelocity = windVelocity;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setAuto(int auto) {
        this.auto = auto;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getMode() {
        return mode;
    }

    public int getOnoff() {
        return onoff;
    }

    public int getWindVelocity() {
        return windVelocity;
    }

    public int getPosition() {
        return position;
    }

    public int getAuto() {
        return auto;
    }

    public int getTemperature() {
        return temperature;
    }

    byte[] getBytes() throws Exception {
        if (position < 0 || position > 6) {
            throw new Exception("position error");
        }

        if (mode == MODE_HEATING) {
            if (temperature < 17 || temperature > 30) {
                throw new Exception("temperature error in heating mode");
            }
        } else {
            if (temperature < 19 || temperature > 30) {
                throw new Exception("temperature error in other mode");
            }

        }
        byte[] result = new byte[4];

        //add onoff
        if (onoff == ON) {
            result[0] |= 0b10000;
        }

        //add mode
        if (mode == MODE_BLAST) {
            result[0] |= 0b1;
        } else if (mode == MODE_DEHUMIDIFICATION) {
            result[0] |= 0b10;
        } else if (mode == MODE_HEATING) {
            result[0] |= 0b11;
        }

        //wind velocity
        if (windVelocity == WINDVELOCITY_HIGH) {
            result[1] = 0b11;
        } else if (windVelocity == WINDVELOCITY_MIDDLE) {
            result[1] = 0b10;
        } else {
            result[1] = 0b1;
        }

        //wind direction
        if (auto == AUTO) {
            result[2] |= 0b10000;
        }
        result[2] |= (position << 5);

        //temp
        result[3] = (byte) temperature;

        return result;
    }

}
