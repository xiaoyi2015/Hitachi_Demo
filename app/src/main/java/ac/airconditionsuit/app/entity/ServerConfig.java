package ac.airconditionsuit.app.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ac on 9/19/15.
 *
 */
public class ServerConfig extends RootEntity{

    public class Timer extends RootEntity{
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
    }


    public class Setting extends RootEntity{
        String sound;
        String password;
        String pwstring;

        public String getSound() {
            return sound;
        }

        public void setSound(String sound) {
            this.sound = sound;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPwstring() {
            return pwstring;
        }

        public void setPwstring(String pwstring) {
            this.pwstring = pwstring;
        }
    }


    public class Connection extends RootEntity{
        long creator_cust_id;
        String address;
        String mac;
        long cust_id;
        long chat_id;
        int state;
        String name;

        public long getCreator_cust_id() {
            return creator_cust_id;
        }

        public void setCreator_cust_id(long creator_cust_id) {
            this.creator_cust_id = creator_cust_id;
        }

        public String getAddress() {
            //TODO for luzheqi
            return "192.168.1.123";
//            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getMac() {
            //TODO for luzheqi
            return "001EC00E1FB3";
//            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public long getCust_id() {
            return cust_id;
        }

        public void setCust_id(long cust_id) {
            this.cust_id = cust_id;
        }

        public long getChat_id() {
            return chat_id;
        }

        public void setChat_id(long chat_id) {
            this.chat_id = chat_id;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    List<Scene> scenes;
    List<Timer> timers;
    List<Section> sections;
    Home home;
    List<DeviceFromServerConfig> devices;
    Setting settings;
    List<Connection> connection;

    public List<Scene> getScenes() {
        return scenes;
    }

    public void setScenes(List<Scene> scenes) {
        this.scenes = scenes;
    }

    public List<Timer> getTimers() {
        return timers;
    }

    public void setTimers(List<Timer> timers) {
        this.timers = timers;
    }

    public List<Section> getSections() {
        if (sections == null) {
            sections = new ArrayList<>();
        }
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public Home getHome() {
        return home;
    }

    public void setHome(Home home) {
        this.home = home;
    }

    public List<DeviceFromServerConfig> getDevices() {
        if(devices == null){
            devices = new ArrayList<>();
        }
        return devices;
    }

    public void setDevices(List<DeviceFromServerConfig> devices) {
        this.devices = devices;
    }

    public Setting getSettings() {
        return settings;
    }

    public void setSettings(Setting settings) {
        this.settings = settings;
    }

    public List<Connection> getConnection() {
        return connection;
    }

    public void setConnection(List<Connection> connection) {
        this.connection = connection;
    }

    public static ServerConfig genNewConfig(String configFileName, String homeName) {
        ServerConfig sc = new ServerConfig();
        Home home = new Home();
        home.setName(homeName);
        home.setFilename(configFileName);
        sc.setHome(home);
        return sc;
    }
}
