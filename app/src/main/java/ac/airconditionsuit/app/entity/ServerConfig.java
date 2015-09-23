package ac.airconditionsuit.app.entity;

import java.util.List;
import java.util.Timer;

/**
 * Created by ac on 9/19/15.
 *
 */
public class ServerConfig extends RootEntity{
    public class Command extends RootEntity{
        float temperature;
        int address;
        int onoff;
        int fan;
        int mode;
    }
    public class Scene extends RootEntity{
        String name;
        List<Command> commonds;
    }

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
    }

    public class Room extends RootEntity{
        String name;
        long roomidkey;
        List<Integer> elements;
    }


    public class Section extends RootEntity{
        String name;
        List<Room> pages;
    }

    public class Home extends RootEntity{
        String name;
        String filename;
    }


    public class Device extends RootEntity{
        String name;
        int indooraddress;
        int indoorindex;
    }


    public class Setting extends RootEntity{
        String sound;
        String password;
        String pwstring;
    }


    public class Connection extends RootEntity{
        long creator_cust_id;
        String address;
        String mac;
        long cust_id;
        long chat_id;
        int state;
        String name;
    }

    List<Scene> scenes;
    List<Timer> timers;
    List<Section> sections;
    Home home;
    List<Device> devices;
    Setting settings;
    List<Connection> connection;

}
