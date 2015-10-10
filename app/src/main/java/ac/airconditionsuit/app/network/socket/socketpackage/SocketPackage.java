package ac.airconditionsuit.app.network.socket.socketpackage;

import ac.airconditionsuit.app.network.socket.SocketManager;

/**
 * Created by ac on 9/24/15.
 */
public abstract class SocketPackage {
    public abstract byte[] getBytesUDP() throws Exception;
    public abstract byte[] getBytesTCP();

    String ip;
    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getIp() {
        return ip;
    }

}
