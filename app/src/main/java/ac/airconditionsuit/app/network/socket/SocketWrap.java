package ac.airconditionsuit.app.network.socket;

import ac.airconditionsuit.app.network.socket.socketpackage.SocketPackage;

import java.io.IOException;

/**
 * Created by ac on 10/11/15.
 */
public interface SocketWrap {
    void connect() throws IOException;

    void sendMessage(SocketPackage socketPackage);

    void close() throws IOException;

    void receiveDataAndHandle() throws IOException;

    boolean isConnect();

}
