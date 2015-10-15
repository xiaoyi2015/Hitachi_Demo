package ac.airconditionsuit.app.entity;

import ac.airconditionsuit.app.network.socket.socketpackage.ControlPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ac on 10/15/15.
 */
public class Scene extends RootEntity {
    String name;
    List<Command> commonds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Command> getCommonds() {
        return commonds;
    }

    public void setCommonds(List<Command> commonds) {
        this.commonds = commonds;
    }

    public List<ControlPackage> toSocketControlPackage() throws Exception {
        List<ControlPackage> result = new ArrayList<>();
        for (Command c : commonds) {
            result.add(new ControlPackage(c));
        }
        return result;
    }
}
