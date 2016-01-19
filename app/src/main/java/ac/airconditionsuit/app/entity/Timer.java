package ac.airconditionsuit.app.entity;

import android.util.Log;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.aircondition.AirConditionControl;
import ac.airconditionsuit.app.fragment.MyAirFragment;
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
    List<Integer> address_new = new ArrayList<>();
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
        this.onoff = (onoff == AirConditionControl.ON);
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

    public List<Integer> getIndexes_new_new() {
        return address_new;
    }

    public void setIndexes_new_new(List<Integer> index) {
        this.address_new = index;
    }

    public int getFan() {
        int temp_fan;
        switch (fan){
            case 1:
                temp_fan = 0;
                break;
            case 2:
                temp_fan = 1;
                break;
            default:
                temp_fan = 2;
                break;
        }
        return temp_fan;
    }

    public void setFan(int fan) {
        int temp_fan;
        switch (fan){
            case 0:
                temp_fan = 1;
                break;
            case 1:
                temp_fan = 2;
                break;
            default:
                temp_fan = 3;
                break;
        }
        this.fan = temp_fan;
    }

    public int getMode() {
        int temp_mode;
        switch (mode){
            case 1:
                temp_mode = 3;
                break;
            case 3:
                temp_mode = 1;
                break;
            default:
                temp_mode = mode;
                break;
        }
        return temp_mode;
    }

    public void setMode(int mode) {
        int temp_mode;
        switch (mode){
            case 1:
                temp_mode = 3;
                break;
            case 3:
                temp_mode = 1;
                break;
            default:
                temp_mode = mode;
                break;
        }
        this.mode = temp_mode;
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
        byte[] result = new byte[32];

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
            result[4] |= (1 << (i + 1));
        }
        if (isRepeat()) {
            result[4] += 1;
        }

        //covert local address to host address
        List<Integer> address_host = new ArrayList<>();
        List<DeviceFromServerConfig> devices_new = MyApp.getApp().getServerConfigManager().getDevices_new();
        for (Integer localIndex : this.address_new) {
            int idx = localIndex - 1;
            if (idx >= 0 && idx < devices_new.size()) {
                address_host.add(devices_new.get(idx).getIndex_new());
            }
        }


        for (int indexinconfig : address_host) {
            int index = indexinconfig;
            result[5 + index / 8] |= (1 << (index % 8));
        }

        //control
        AirConditionControl airConditionControl = new AirConditionControl(this);
        System.arraycopy(airConditionControl.getBytes(), 0, result, 13, 4);

        //name
        if (name.getBytes().length > 15) {
            throw new Exception("name too length");
        }
        System.arraycopy(Arrays.copyOf((name + "                ").getBytes(), 15), 0,
                result, 17,
                15);

        return result;
    }

    public static Timer decodeFromByteArray(byte[] contentData) throws Exception {
//        contentData = Arrays.copyOf(contentData, 26);

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
                weeks.add(i - 1);
            }
        }
        timer.setWeek(weeks);

        //address
        List<Integer> address_host = new ArrayList<>();
        int cntDev = MyApp.getApp().getServerConfigManager().getDeviceCount_new();
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if ((contentData[i + 5] & (1 << j)) != 0) {
                    int t = i * 8 + j;
                    if (t >= 1 && t <= cntDev) address_host.add(t);
                }
            }
        }
        //address, host machine, use index from 0, located in address table in host machine
        //local use index from 1 located in local address table which is resorted when obtained from host machine
        List<Integer> address_local = new ArrayList<>();
        List<DeviceFromServerConfig> devices = MyApp.getApp().getServerConfigManager().getDevices_new();
        for (Integer i_v : address_host) {
            for (int i = 0; i < devices.size(); i++) {
                if (devices.get(i).getIndex_new() == i_v) {
                    address_local.add(i + 1);
                    break;
                }
            }
        }

        timer.setIndexes_new_new(address_local);

        //onoff
        if ((contentData[13] & 0b10000) == 0) {
            timer.setOnoff(false);
        } else {
            timer.setOnoff(true);
        }

        //mode
        int mode = contentData[13] & 0b1111;
        if (mode < 0 || mode > 3) {
            throw new Exception("mode error");
        }
        switch (mode) {
            case 0:
                timer.setMode(AirConditionControl.MODE_REFRIGERATION);
                break;

            case 1:
                timer.setMode(AirConditionControl.MODE_BLAST);
                break;

            case 2:
                timer.setMode(AirConditionControl.MODE_DEHUMIDIFICATION);
                break;

            case 3:
                timer.setMode(AirConditionControl.MODE_HEATING);
                break;
        }

        //wind velocity
        int windVelocity = contentData[14];
        if (windVelocity < 1 || windVelocity > 3) {
            throw new Exception("windVelocity error");
        }
        timer.setFan(windVelocity - 1);

        int temperature = contentData[16];

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
        timer.setName(new String(Arrays.copyOfRange(contentData, 17, 32)));

        Log.v("liutao", "收到定时器 id: " + timer.getTimerid() + " name: " + timer.getName());

        return timer;
    }

    public void addControlAircondition(int i) {
        address_new.add(i);
    }

    public void setWeek(int... weeks) {
        for (int week : weeks) {
            this.week.add(week);
        }
    }

    public void update_new(Timer timer) {
        this.temperature = timer.getTemperature();
        this.timerenabled = timer.isTimerenabled();
        this.onoff = timer.isOnoff();
        this.repeat = timer.isRepeat();
        this.timerid = timer.getTimerid();
        this.address_new = timer.getIndexes_new_new();
        this.fan = timer.fan;
        this.mode = timer.mode;
        this.detailenabled = timer.isDetailenabled();
        this.week = timer.getWeek();
        this.minute = timer.getMinute();
        this.hour = timer.getHour();
        this.name = timer.getName();
    }
}
