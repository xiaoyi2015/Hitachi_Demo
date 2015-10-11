package ac.airconditionsuit.app.network.socket.socketpackage;

import ac.airconditionsuit.app.network.socket.SocketManager;

import java.io.UnsupportedEncodingException;

/**
 * Created by ac on 9/24/15.
 */
public abstract class SocketPackage {
    public abstract byte[] getBytesUDP() throws Exception;
    public abstract byte[] getBytesTCP() throws UnsupportedEncodingException;
}
