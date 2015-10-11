package ac.airconditionsuit.app.network.socket.socketpackage;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.network.socket.socketpackage.Tcp.TCPACKPackage;

import java.io.UnsupportedEncodingException;

/**
 * Created by ac on 10/9/15.
 */
public class ACKPackage extends SocketPackage {

    private byte[] msg_no;
    private byte[] msg_id;

    public ACKPackage(byte[] msg_no, byte[] msg_id) {
        this.msg_no = msg_no;
        this.msg_id = msg_id;
    }

    @Override
    public byte[] getBytesUDP() throws Exception {
        //TODO for luzheqi
        return null;
    }

    /**
     * this method won't bu called because broadcast only send by udp;
     * @return null
     */
    @Override
    public byte[] getBytesTCP() throws UnsupportedEncodingException {
        return new TCPACKPackage(MyApp.getApp().getUser().getCust_id(), msg_no, msg_id).getBytes();
    }
}
