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
    int timerid;
    List<Integer> address = new ArrayList<>();
    int fan;
    int mode;
    boolean detailenabled;
    List<Integer> week = new ArrayList<>();
    int minute;
    int hour;
    String name;

    public Timer(boolean isNew) {
        if (isNew) {
            this.timerid = 0xffffffff;
        }
    }

    public Timer() {
    }

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

    public void setOnoff(int onoff) {
        if (onoff == AirConditionControl.ON) {
            this.onoff = true;
        } else {
            this.onoff = false;
        }
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public int getTimerid() {
        return timerid;
    }

    public void setTimerid(int timerid) {
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
        this.name = name.trim();
    }


    public byte[] getBytesForUdp() throws Exception {
        byte[] result = new byte[25 + address.size()];

        //timer id
        if (timerid == 0xffffffff) {
            result[0] |= 0x7f;
            result[1] |= 0xff;
        } else {
            if (timerid > 0x7fff) {
                throw new Exception("timer id is too big");
            }
            byte[] id = ByteUtil.shortToByteArrayAsBigEndian(timerid);
            result[0] = id[0];
            result[1] = id[1];
        }

        //is enable
        if (timerenabled) {
            result[0] |= 0x80;
        }

        //time
        System.arraycopy(ByteUtil.timeToBCD(hour, minute), 0, result, 2, 2);

        //week
        for (int i : week) {
            if (i < 0 || i > 7) {
                throw new Exception("week error");
            }
            result[4] |= (1 << i);
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
        System.arraycopy(Arrays.copyOf((name + "                ").getBytes(), 16), 0,
                result, result.length - 16,
                16);

        return result;
    }

    public static Timer decodeFromByteArray(byte[] contentData) throws Exception {
        contentData = Arrays.copyOf(contentData, 26);

        Timer timer = new Timer();

        //is enable
        if ((contentData[0] & 0x80) != 0) {
            timer.setTimerenabled(true);
        } else {
            timer.setTimerenabled(false);
        }

        contentData[0] &= 0x7f;

        timer.setTimerid(ByteUtil.byteArrayToShortAsBigEndian(contentData));

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
        int controlRangeOffset = contentData.length - 20;
        for (int i = 5; i < controlRangeOffset; ++i) {
            address.add((int) contentData[i]);
        }
        timer.setAddress(address);

        //onoff
        if ((contentData[controlRangeOffset] & 0x80) == 0) {
            timer.setOnoff(false);
        } else {
            timer.setOnoff(true);
        }

        //mode
        int mode = contentData[controlRangeOffset] & 0b1111;
        if (mode < 0 || mode > 3) {
            throw new Exception("mode error");
        }
        timer.setMode(mode);

        //wind velocity
        int windVelocity = contentData[controlRangeOffset + 1];
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

        int temperature = contentData[controlRangeOffset + 3];

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
        timer.setName(new String(Arrays.copyOfRange(contentData, contentData.length - 16, contentData.length)));

        return timer;
    }

    public void addControlAircondition(int i) {
        address.add(i);
    }

    public void setWeek(int... weeks) {
        for (int week : weeks) {
            this.week.add(week);
        }
    }

    public void update(Timer timer) {
        this.temperature = timer.getTemperature();
        this.timerenabled = timer.isTimerenabled();
        this.onoff = timer.isOnoff();
        this.repeat = timer.isRepeat();
        this.timerid = timer.getTimerid();
        this.address = timer.getAddress();
        this.fan = timer.getFan();
        this.mode = timer.getMode();
        this.detailenabled = timer.isDetailenabled();
        this.week = timer.getWeek();
        this.minute = timer.getMinute();
        this.hour = timer.getHour();
        this.name = timer.getName();
    }
}
