package ac.airconditionsuit.app.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ac on 9/25/15.
 *
 */
public class Room extends RootEntity {
    //room name
    String name;

    //room id
    long roomidkey;

    //list里面的每一个integer代表一个在该房间内的空调
    List<Integer> elements = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getRoomidkey() {
        return roomidkey;
    }

    public void setRoomidkey(long roomidkey) {
        this.roomidkey = roomidkey;
    }

    public List<Integer> getElements() {
        return elements;
    }

    public void setElements(List<Integer> elements) {
        this.elements = elements;
    }

    public void addAirCondition(int index) {
        for (Integer element : elements) {
            if (element == index) {
                return;
            }
        }
        elements.add(index);
    }
}
