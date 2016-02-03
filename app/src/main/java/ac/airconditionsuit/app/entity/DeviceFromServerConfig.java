package ac.airconditionsuit.app.entity;

/**
 * Created by ac on 10/15/15.
 */
public class DeviceFromServerConfig extends RootEntity {
    String name;

    int indooraddress;
    int indoorindex;

//    public void reformatIndoorIndexAndAddress(boolean fromServerToLocal, int idx) {
//        if (fromServerToLocal) {
//            indoorindex = indooraddress / 16;
//            indooraddress = indooraddress % 16;
//        }
//        else  {
//            indooraddress = indoorindex * 16 + indooraddress;
//            indoorindex = idx;
//        }
//    }

    public DeviceFromServerConfig() {
    }

    public DeviceFromServerConfig(byte address) {
        indooraddress = address & 0x0f;
        indoorindex = (address & 0xf0) >>> 4;
        name = "" + indoorindex + "-" + String.format("%02d", indooraddress);
        indooraddress = indoorindex * 16 + indooraddress;
        //Log.v("liutao", "ac address: " + indoorindex + " - " + indooraddress);
    }

//    public int getIndooraddress_new() {
//        return indooraddress_new;
//    }

//    public int getIndoorindex_new() {
//        return indoorindex_new;
//    }

    public void setIndoorindex(int idx) {
        this.indoorindex = idx;
    }

    public String getFormatNameByIndoorIndexAndAddress() {
        return indooraddress / 16 + "-" + String.format("%02d", indooraddress % 16);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAddress_new(){
        return indooraddress;
    }

    public int getIndex_new() {
        return indoorindex;
    }

    public int getOldIndoorIndex() {
        return (indooraddress / 16);
    }

    public int getOldIndoorAddress() {
        return (indooraddress % 16);
    }

//    @Override
//    public boolean equals(Object o) {
//        if (o instanceof DeviceFromServerConfig) {
//            DeviceFromServerConfig temp = (DeviceFromServerConfig) o;
//            return temp.getAddress() == getAddress();
//        } else {
//            return false;
//        }
//    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        DeviceFromServerConfig c = new DeviceFromServerConfig();
        c.name = name;
        c.indooraddress = indooraddress;
        c.indoorindex = indoorindex;
        return c;
    }
}
