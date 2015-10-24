package ac.airconditionsuit.app.entity;

/**
 * Created by ac on 10/24/15.
 */
public class Connection extends RootEntity {
    long creator_cust_id;
    String address;
    String mac;
    long cust_id;
    long chat_id;
    int state;
    String name;

    public Connection(Device device) {
        this.creator_cust_id = device.getInfo().getCreator_cust_id();
        this.address = device.getInfo().getIp();
        this.name = device.getInfo().getName();
        this.chat_id = device.getInfo().getChat_id();
        this.mac = device.getInfo().getMac();
    }

    public long getCreator_cust_id() {
        return creator_cust_id;
    }

    public void setCreator_cust_id(long creator_cust_id) {
        this.creator_cust_id = creator_cust_id;
    }

    public String getAddress() {
        //TODO for luzheqi
//        return "192.168.1.123";
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMac() {
        //TODO for luzheqi
//        return "001EC00E1FB3";
        return mac;
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