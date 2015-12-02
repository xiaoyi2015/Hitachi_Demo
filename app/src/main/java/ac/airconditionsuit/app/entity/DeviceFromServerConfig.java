package ac.airconditionsuit.app.entity;

import android.util.Log;

/**
 * Created by ac on 10/15/15.
 */
public class DeviceFromServerConfig extends RootEntity {
    String name;

    int indooraddress;
    int indoorindex;

    public void reformatIndoorIndexAndAddress(boolean fromServerToLocal, int idx) {
        if (fromServerToLocal) {
            indoorindex = indooraddress / 16;
            indooraddress = indooraddress % 16;
        }
        else  {
            indooraddress = indoorindex * 16 + indooraddress;
            indoorindex = idx;
        }
    }

    public DeviceFromServerConfig(byte address) {
        indooraddress = address & 0x0f;
        indoorindex = (address & 0xf0) >>> 4;
        name = "" + indoorindex + "-" + String.format("%02d", indooraddress);
        //Log.v("liutao", "ac address: " + indoorindex + " - " + indooraddress);
    }

    public int getIndooraddress() {
        return indooraddress;
    }

    public int getIndoorindex() {
        return indoorindex;
    }

    public String getFormatNameByIndoorIndexAndAddress() {
        return indoorindex + "-" + String.format("%02d", indooraddress);
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
