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

    public Connection(Device.Info info) {
        this.creator_cust_id = info.getCreator_cust_id();
        this.address = info.getIp();
        this.name = info.getName();
        this.chat_id = info.getChat_id();
        this.mac = info.getMac().substring(0, 12);
    }

    public long getCreator_cust_id() {
        return creator_cust_id;
    }

    public void setCreator_cust_id(long creator_cust_id) {
        this.creator_cust_id = creator_cust_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMac() {
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
