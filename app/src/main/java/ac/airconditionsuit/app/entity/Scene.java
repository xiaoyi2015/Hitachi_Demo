package ac.airconditionsuit.app.entity;

import ac.airconditionsuit.app.network.socket.socketpackage.ControlPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ac on 10/15/15.
 */
public class Scene extends RootEntity {
    String name;
    List<Command> commands;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }

    public List<ControlPackage> toSocketControlPackage() throws Exception {
        List<ControlPackage> result = new ArrayList<>();
        for (Command c : commands) {
            result.add(new ControlPackage(c));
        }
        return result;
    }
}
