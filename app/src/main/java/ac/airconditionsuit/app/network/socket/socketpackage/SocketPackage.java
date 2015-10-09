package ac.airconditionsuit.app.network.socket.socketpackage;

import ac.airconditionsuit.app.network.socket.SocketManager;

/**
 * Created by ac on 9/24/15.
 */
public interface SocketPackage {
    byte[] getBytesUDP() throws Exception;
    byte[] getBytesTCP();
}
