package ac.airconditionsuit.app.entity.udp;

import ac.airconditionsuit.app.entity.RootEntity;

import java.util.List;

/**
 * Created by ac on 10/14/15.
 */
public class AirConditionControl extends RootEntity{
    public static final int ON = 0;
    public static final int OFF = 1;

    /**
     * 制冷
     */
    public static final int MODE_REFRIGERATION = 1;

    /**
     * 送风
     */
    public static final int MODE_BLAST = 2;

    /**
     * 除湿
     */
    public static final int MODE_DEHUMIDIFICATION = 3;

    /**
     * 制热
     */
    public static final int MODE_HEATING = 4;

    int onoff;

    int mode;


    public static final int WINDVELOCITY_HIGH = 1;
    public static final int WINDVELOCITY_MIDDLE = 2;
    public static final int WINDVELOCITY_LOW = 3;
    int windVelocity;


    /**
     * 挡风板位置0~6
     */
    int position;

    public static final int AUTO = 0;
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


    static AirConditionControl decodeFromByteArray(byte[] input) throws Exception {
        AirConditionControl result = new AirConditionControl();
        if ((input[0] & 0b10000) > 0) {
            result.setOnoff(ON);
        } else {
            result.setOnoff(OFF);
        }


        int mode = input[0] & 0b1111;
        if (mode == 1) {
            result.setMode(MODE_BLAST);
        } else if (mode == 2) {
            result.setMode(MODE_DEHUMIDIFICATION);
        } else if (mode == 3) {
            result.setMode(MODE_HEATING);
        } else if (mode == 0){
            result.setMode(MODE_REFRIGERATION);
        } else {
            throw new Exception("mode error");
        }


        int windVelocity = input[1];
        //wind velocity
        if (windVelocity == 3) {
            result.setWindVelocity(WINDVELOCITY_HIGH);
        } else if (windVelocity == 2) {
            result.setWindVelocity(WINDVELOCITY_MIDDLE);
        } else if (windVelocity == 1) {
            result.setWindVelocity(WINDVELOCITY_LOW);
        } else {
            throw new Exception("wind velocity error");
        }


        int position = input[2] >>> 5;
        if (position < 0 || position > 6) {
            throw new Exception("position error");
        }
        result.setPosition(position);

        if ((input[2] & 0b10000) > 0) {
            result.setAuto(AUTO);
        } else {
            result.setAuto(NOT_AUTO);
        }

        int temperature = input[3];

        if (result.mode == MODE_HEATING) {
            if (temperature < 17 || temperature > 30) {
                throw new Exception("temperature error in heating mode");
            }
        } else {
            if (temperature < 19 || temperature > 30) {
                throw new Exception("temperature error in other mode");
            }
        }

        result.setTemperature(temperature);

        return result;
    }
}
