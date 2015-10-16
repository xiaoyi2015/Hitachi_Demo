package ac.airconditionsuit.app.entity;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.aircondition.AirConditionControl;
import ac.airconditionsuit.app.util.ByteUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ac on 10/16/15.
 */
public class Timer extends RootEntity {
    float temperature;
    boolean timerenabled;
    boolean onoff;
    boolean repeat;
    long timerid;
    List<Integer> address;
    int fan;
    int mode;
    boolean detailenabled;
    List<Integer> week;
    int minute;
    int hour;
    String name;

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public boolean isTimerenabled() {
        return timerenabled;
    }

    public void setTimerenabled(boolean timerenabled) {
        this.timerenabled = timerenabled;
    }

    public boolean isOnoff() {
        return onoff;
    }

    public void setOnoff(boolean onoff) {
        this.onoff = onoff;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public long getTimerid() {
        return timerid;
    }

    public void setTimerid(long timerid) {
        this.timerid = timerid;
    }

    public List<Integer> getAddress() {
        return address;
    }

    public void setAddress(List<Integer> address) {
        this.address = address;
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

    public boolean isDetailenabled() {
        return detailenabled;
    }

    public void setDetailenabled(boolean detailenabled) {
        this.detailenabled = detailenabled;
    }

    public List<Integer> getWeek() {
        return week;
    }

    public void setWeek(List<Integer> week) {
        this.week = week;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public byte[] getBytesForUdp() throws Exception {
        byte[] result = new byte[25 + address.size()];

        //is enable
        if (timerenabled) {
            result[0] |= 0x80;
        }

        //timer id
        if (timerid > 0x7fff) {
            throw new Exception("timer id is too big");
        }
        result[0] |= timerid / 256;
        result[1] |= timerid % 256;

        //time
        System.arraycopy(ByteUtil.timeToBCD(hour, minute), 0, result, 2, 2);

        //week
        for (int i : week) {
            if (i < 0 || i > 7) {
                throw new Exception("week error");
            }
            result[4] |= (i << i);
        }
        if (isRepeat()) {
            result[4] += 1;
        }

        //address
        for (int i = 0; i < address.size(); ++i) {
            int integer = address.get(i);
            if (integer < 0 || integer > 255) {
                throw new Exception("address error");
            }
            result[5 + i] = (byte) integer;
        }

        //control
        AirConditionControl airConditionControl = new AirConditionControl(this);
        System.arraycopy(airConditionControl.getBytes(), 0, result, result.length - 20, 4);

        //name
        if (name.getBytes().length > 16) {
            throw new Exception("name too length");
        }
        name += "                ";
        System.arraycopy(Arrays.copyOf(name.getBytes(), 16), 0,
                result, result.length - 16,
                16);

        return result;
    }

    public static Timer decodeFromByteArray(byte[] contentData) throws Exception {
        Timer timer = new Timer();

        //is enable
        if ((contentData[0] & 0x80) != 0) {
            timer.setTimerenabled(true);
        } else {
            timer.setTimerenabled(false);
        }

        contentData[0] &= 0x7f;

        timer.setTimerid(ByteUtil.byteArrayToShort(contentData));

        timer.setHour(ByteUtil.BCDByteToInt(contentData[2]));
        timer.setMinute(ByteUtil.BCDByteToInt(contentData[3]));

        if ((contentData[4] & 1) != 0) {
            timer.setRepeat(true);
        } else {
            timer.setRepeat(false);
        }

        //week
        List<Integer> weeks = new ArrayList<>();
        for (int i = 1; i <= 7; ++i) {
            if ((contentData[4] & (1 << i)) != 0) {
                weeks.add(i);
            }
        }
        timer.setWeek(weeks);

        //address
        List<Integer> address = new ArrayList<>();
        for (int i = 5; i < contentData.length - 20; ++i) {
            address.add((int) contentData[i]);
        }
        timer.setAddress(address);

        //mode
        int mode = contentData[1] & 0b1111;
        if (mode < 0 || mode > 3) {
            throw new Exception("mode error");
        }
        timer.setMode(mode);

        //wind velocity
        int windVelocity = contentData[2];
        if (windVelocity < 1 || windVelocity > 3) {
            throw new Exception("windVelocity error");
        }
        timer.setFan(windVelocity);

//        int position = contentData[3] >>> 5;
//        if (position < 0 || position > 6) {
//            throw new Exception("position error");
//        }
//        result.setPosition(position);
//
//        if ((contentData[3] & 0b10000) > 0) {
//            result.setAuto(AUTO);
//        } else {
//            result.setAuto(NOT_AUTO);
//        }

        int temperature = contentData[4];

        if (timer.getMode() == AirConditionControl.MODE_HEATING) {
            if (temperature < 17 || temperature > 30) {
                throw new Exception("temperature error in heating mode");
            }
        } else {
            if (temperature < 19 || temperature > 30) {
                throw new Exception("temperature error in other mode");
            }
        }
        timer.setTemperature(temperature);

        //name
        timer.setName(new String(Arrays.copyOfRange(contentData, contentData.length - 20, contentData.length)));

        return timer;
    }
}
