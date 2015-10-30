package ac.airconditionsuit.app.aircondition;

import android.util.Log;

import ac.airconditionsuit.app.entity.RootEntity;
import ac.airconditionsuit.app.entity.Timer;

/**
 * Created by ac on 10/14/15.
 *
 */
public class AirConditionControl extends RootEntity{
    public static final int UNKNOW = -19989;
    public static final int EMPTY = 250;

    public static final int ON = 1;
    public static final int OFF = 0;
    int onoff;

    /**
     * 制冷
     */
    public static final int MODE_REFRIGERATION = 0;

    /**
     * 除湿
     */
    public static final int MODE_DEHUMIDIFICATION = 2;

    /**
     * 送风
     */
    public static final int MODE_BLAST = 3;

    /**
     * 制热
     */
    public static final int MODE_HEATING = 1;
    int mode;


    public static final int WINDVELOCITY_HIGH = 2;
    public static final int WINDVELOCITY_MIDDLE = 1;
    public static final int WINDVELOCITY_LOW = 0;
    int windVelocity;


    /**
     * 挡风板位置0~6
     */
    int position = 3;

    public static final int AUTO = 1;
    public static final int NOT_AUTO = 0;
    /**
     * 风向是否自动
     */
    int auto = AUTO;

    /**
     * 送风/除湿/制冷：19~30度
     * 制热：17~30度
     */
    int temperature;

    public AirConditionControl(Timer timer) {
        this.mode = timer.getMode();
        this.windVelocity = timer.getFan();
        this.onoff = timer.isOnoff() ? ON : OFF;
        this.temperature = (int) timer.getTemperature();
    }

    public AirConditionControl() {
    }


    public void setOnoff(int onoff) {
        this.onoff = onoff;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setWindVelocity(int windVelocity) {
        this.windVelocity = windVelocity;
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

    public int getTemperature() {
        return temperature;
    }

    public byte[] getBytes() throws Exception {
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
//        if (auto == AUTO) {
//            result[2] |= 0b10000;
//        }
//        result[2] |= (position << 5);
        result[2] = (byte) 0xff;

        //temp
        result[3] = (byte) temperature;

        Log.v("liutao", "控制空调状态 mode: " + (result[0]&0x0f) + " wind: " + result[1] + " temp: " + temperature);

        return result;
    }

}
