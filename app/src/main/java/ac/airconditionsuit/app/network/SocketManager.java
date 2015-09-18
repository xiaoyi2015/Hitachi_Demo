package ac.airconditionsuit.app.network;

import android.app.ProgressDialog;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by ac on 9/18/15.
 */
public class SocketManager {
    interface SocketWrap {
        void connect();

        void sendMessage();

        void readMessage();
    }

    static class TcpSocket implements SocketWrap {
        private Socket socket;

        @Override
        public void connect() {

        }

        @Override
        public void sendMessage() {

        }

        @Override
        public void readMessage() {

        }
    }

    static class UdpSocket implements SocketWrap {
        private DatagramSocket datagramSocket;

        UdpSocket(String host, int port) {
            try {
                datagramSocket = new DatagramSocket(new InetSocketAddress(host, port));
                datagramSocket.connect(new InetSocketAddress(host, port));
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void connect() {

        }

        @Override
        public void sendMessage() {

        }

        @Override
        public void readMessage() {

        }
    }


    private SocketWrap socket;

}
