package ac.airconditionsuit.app.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
                if (activeNetwork.isConnected()) {
                    return TYPE_WIFI_CONNECT;
                } else {
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
