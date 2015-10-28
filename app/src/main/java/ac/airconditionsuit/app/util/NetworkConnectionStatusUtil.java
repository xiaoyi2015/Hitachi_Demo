package ac.airconditionsuit.app.util;

import ac.airconditionsuit.app.network.socket.TcpSocket;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.net.Inet4Address;

/**
 * Created by ac on 9/24/15.
 */
public class NetworkConnectionStatusUtil {

    public static int TYPE_WIFI_CONNECT = 1;
    public static int TYPE_WIFI_UNCONNECT = 2;
    public static int TYPE_MOBILE_CONNECT = 3;
    public static int TYPE_MOBILE_UNCONNECT = 4;
    public static int TYPE_NOT_CONNECTED = 5;


    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                activeNetwork.isConnected();
                try {
                    if (activeNetwork.isConnected() && Inet4Address.getByName(TcpSocket.IP).isReachable(500)) {
                        return TYPE_WIFI_CONNECT;
                    } else {
                        return TYPE_WIFI_UNCONNECT;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return TYPE_WIFI_UNCONNECT;
                }
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                if (activeNetwork.isConnected()) {
                    return TYPE_MOBILE_CONNECT;
                } else {
                    return TYPE_MOBILE_UNCONNECT;
                }
            }
        }
        return TYPE_NOT_CONNECTED;
    }
}
