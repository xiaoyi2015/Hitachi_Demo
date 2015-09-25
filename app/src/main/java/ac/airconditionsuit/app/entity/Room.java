package ac.airconditionsuit.app.entity;

import java.util.List;

/**
 * Created by ac on 9/25/15.
 */
public class Room extends RootEntity {
    String name;
    long roomidkey;
    List<Integer> elements;

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
}
