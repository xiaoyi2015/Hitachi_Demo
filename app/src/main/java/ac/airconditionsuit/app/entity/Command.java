package ac.airconditionsuit.app.entity;

/**
 * Created by ac on 10/15/15.
 */
public class Command extends RootEntity {
    float temperature;
    int address;
    boolean onoff;
    int fan;
    int mode;

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getOnoff() {
        return onoff ? 1 : 0;
    }

    public void setOnoff(int onoff) {
        this.onoff = onoff == 1;
    }

    public int getAirconditionFan(){
        return fan;
    }

    public int getAirconditionMode(){
        return mode;
    }

    public void setAirconditionFan(int fan){
        this.fan = fan;
    }

    public void setAirconditionMode(int mode){
        this.mode = mode;
    }

    public int getFan() {
        int temp_fan;
        switch (fan) {
            case 1:
                temp_fan = 0;
                break;
            case 2:
                temp_fan = 1;
                break;
            default:
                temp_fan = 2;
                break;
        }
        return temp_fan;
    }

    public void setFan(int fan) {
        int temp_fan;
        switch (fan) {
            case 0:
                temp_fan = 1;
                break;
            case 1:
                temp_fan = 2;
                break;
            case 2:
                temp_fan = 3;
                break;
            default:
                temp_fan = fan;
                break;
        }
        this.fan = temp_fan;
    }

    public int getMode() {
        int temp_mode;
        switch (mode) {
            case 1:
                temp_mode = 3;
                break;
            case 3:
                temp_mode = 1;
                break;
            default:
                temp_mode = mode;
                break;
        }
        return temp_mode;
    }

    public void setMode(int mode) {
        int temp_mode;
        switch (mode) {
            case 1:
                temp_mode = 3;
                break;
            case 3:
                temp_mode = 1;
                break;
            default:
                temp_mode = mode;
                break;
        }
        this.mode = temp_mode;
    }
}
