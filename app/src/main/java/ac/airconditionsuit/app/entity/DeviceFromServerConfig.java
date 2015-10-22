package ac.airconditionsuit.app.entity;

/**
 * Created by ac on 10/15/15.
 */
public class DeviceFromServerConfig extends RootEntity {
    String name;

    //todo for zhulinan,空调地址 = indoorindex * 16 + indooraddress
    int indooraddress;
    int indoorindex;

    public DeviceFromServerConfig(byte address) {
        indooraddress = address & 0xf;
        indoorindex = address >>> 4;
        name = "新空调" + indoorindex + "-" + indooraddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAddress() {
        return indoorindex * 16 + indooraddress;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DeviceFromServerConfig) {
            DeviceFromServerConfig temp = (DeviceFromServerConfig) o;
            return temp.getAddress() == getAddress();
        } else {
            return false;
        }
    }
}
