package ac.airconditionsuit.app.entity;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.network.socket.socketpackage.ControlPackage;
import ac.airconditionsuit.app.network.socket.socketpackage.Udp.UdpPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ac on 10/15/15.
 */
public class Scene extends RootEntity {
    String name;
    List<Command> commands;
    private transient final ArrayList<ControlPackage> result = new ArrayList<>();

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

    public List<ControlPackage> toSocketControlPackage(final UdpPackage.Handler handle) throws Exception {
        result.clear();
        for (Command c : commands) {
            final ControlPackage controlPackage = new ControlPackage(c);
            controlPackage.setHandle(new UdpPackage.Handler() {
                @Override
                public void success() {
                    synchronized (result) {
                        result.remove(controlPackage);
                        if (result.size() == 0) {
                            handle.success();
                        }
                    }
                }

                @Override
                public void fail(int errorNo) {
                    if (errorNo == 2000) {
                        success();
                    } else {
                        handle.fail(-1);
                    }
                }
            });
            result.add(controlPackage);
        }
        if (commands.size() != 0) {
            check(0, handle);
        } else {
            handle.success();
        }
//        if (result.size() != 0) {
//            result.get(result.size() - 1).setHandle(handle);
//        }
        return result;
    }

    private void check(final int times, final UdpPackage.Handler handle) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (result) {
                    if (result.size() != 0) {
                        if (times >= 10) {
                            handle.fail(-1);
                        } else {
                            MyApp.getApp().getSocketManager().sendMessage(result);
                            check(times + 1, handle);
                        }
                    }
                }
            }
        }, 5000);
    }
}
