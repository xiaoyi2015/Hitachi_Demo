package ac.airconditionsuit.app.aircondition;

import android.util.Log;

/**
 * Created by ac on 10/15/15.
 */
public class AirConditionStatusResponse extends AirConditionControl {
    int address;
    byte huifengTemperature;
    byte warning;

    public int getAddress() {
        return address;
    }

    public byte getHuifengTemperature() {
        return huifengTemperature;
    }

    public byte getWarning() {
        return warning;
    }

    public static AirConditionStatusResponse decodeFromByteArray(byte[] input) throws Exception {
        AirConditionStatusResponse result = new AirConditionStatusResponse();

        result.address = input[0] & 0xff;

        if ((input[1] & 0b10000) > 0) {
            result.setOnoff(ON);
        } else {
            result.setOnoff(OFF);
        }


        int mode = input[1] & 0b1111;
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


        int windVelocity = input[2];
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


//        int position = input[3] >>> 5;
//        if (position < 0 || position > 6) {
//            throw new Exception("position error");
//        }
//        result.setPosition(position);
//
//        if ((input[3] & 0b10000) > 0) {
//            result.setAuto(AUTO);
//        } else {
//            result.setAuto(NOT_AUTO);
//        }

        int temperature = input[4];

        Log.v("liutao", "主机反馈空调状态 address: " + result.address + " mode: " + mode + " wind: " + windVelocity + " temp: " + temperature);

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

        result.huifengTemperature = input[5];

        result.warning = input[6];

        return result;
    }
}
