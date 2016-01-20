package ac.airconditionsuit.app.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ac on 9/19/15.
 *
 */
public class ServerConfig extends RootEntity{

    public void clearDevice() {
        connection.clear();
        sections.clear();
        devices.clear();
        scenes.clear();
        timers.clear();
    }

    public boolean checkDevice() {
//        if (devices_new == null) return false;
        int deviceNumger = devices.size();
//        if (deviceNumger == 0) return false;
        for (Timer t : timers) {
            for (Integer index : t.getIndexes_new_new()) {
                if (index > deviceNumger)  {
                    deviceNumberChange();
                    return false;
                }
            }
        }
        for (Section s : sections) {
            for (Room r : s.getPages()) {
                for (Integer index : r.getElements()) {
                    if (index >= deviceNumger) {
                        deviceNumberChange();
                        return false;
                    }
                }
            }
        }
//        for (Scene s : scenes) {
//            for (Command c : s.getCommands()) {
//                if (!commandInDevice(c)) {
//                    deviceNumberChange();
//                    return false;
//                }
//            }
//        }
        return true;
    }

    private boolean commandInDevice_new(Command c) {
        for (DeviceFromServerConfig d : devices) {
            if (d.getAddress_new() == c.getAddress()) {
                return true;
            }
        }
        return false;
    }

    public void deviceNumberChange() {
        if (scenes != null) {
            scenes.clear();
        }

        if (sections != null) {
            sections.clear();
        }

        if (timers != null) {
            timers.clear();
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


    List<Scene> scenes = new ArrayList<>();
    List<Timer> timers = new ArrayList<>();
    List<Section> sections = new ArrayList<>();
    Home home;
    List<DeviceFromServerConfig> devices = new ArrayList<>();
    Setting settings;
    List<Connection> connection = new ArrayList<>();

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

    public List<DeviceFromServerConfig> getDevices_new() {
        if(devices == null){
            devices = new ArrayList<>();
        }
        return devices;
    }

    public void setDevices_new(List<DeviceFromServerConfig> devices) {
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
        int p = 0;
        for (Timer t : timers) {
            if (t.getTimerid() == timer.getTimerid()) {
                t.update_new(timer);
                return;
            }
            else if (t.getTimerid() > timer.getTimerid()) {
                timers.add(p, timer);
                return;
            }
            p ++;
        }
        timers.add(timer);
//      resortTimers();
    }

    public void resortTimers() {
        Collections.sort(timers, new ComparatorTimer());
        setTimers(timers);
    }

    public static ServerConfig genNewConfig(String configFileName, String homeName) {
        ServerConfig sc = new ServerConfig();
        Home home = new Home();
        home.setName(homeName);
        home.setFilename(configFileName);
        sc.setHome(home);
        return sc;
    }

    //按timerid降序排列
    public static final class ComparatorTimer implements Comparator<Timer> {
        @Override
        public int compare(Timer lhs, Timer rhs) {
            int lid = lhs.getTimerid();
            int rid = lhs.getTimerid();
            if (lid > rid) {
                return 1;
            }
            else if (lid < rid) {
                return -1;
            }
            return 0;
        }
    }
}
