package ac.airconditionsuit.app.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ac on 9/19/15.
 *
 */
public class ServerConfig extends RootEntity{



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


    public void updateTimer(Timer timer) {
        for (Timer t : timers) {
            if (t.getTimerid() == timer.getTimerid()) {
                t.update(timer);
                return;
            }
        }
        timers.add(timer);
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
