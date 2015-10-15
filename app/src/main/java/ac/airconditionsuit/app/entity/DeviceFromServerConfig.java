package ac.airconditionsuit.app.entity;

/**
 * Created by ac on 10/15/15.
 */
public class DeviceFromServerConfig extends RootEntity {
    String name;
    int indooraddress;
    int indoorindex;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndooraddress() {
        return indooraddress;
    }

    public void setIndooraddress(int indooraddress) {
        this.indooraddress = indooraddress;
    }

    public int getIndoorindex() {
        return indoorindex;
    }

    public void setIndoorindex(int indoorindex) {
        this.indoorindex = indoorindex;
    }
}
