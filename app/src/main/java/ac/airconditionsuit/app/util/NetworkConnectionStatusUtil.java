package ac.airconditionsuit.app.util;

import ac.airconditionsuit.app.network.socket.TcpSocket;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.io.IOException;
import java.net.Inet4Address;

/**
 * Created by ac on 9/24/15.
 */
public class NetworkConnectionStatusUtil {

    public static int TYPE_WIFI_CONNECT = 1;
    public static int TYPE_WIFI_UNCONNECT = 2;
    public static int TYPE_MOBILE_CONNECT = 3;
    public static int TYPE_MOBILE_CONNECT_2G = 7;
    public static int TYPE_MOBILE_CONNECT_3G = 8;
    public static int TYPE_MOBILE_CONNECT_4G = 9;
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
                    TelephonyManager mTelephonyManager = (TelephonyManager)
                            context.getSystemService(Context.TELEPHONY_SERVICE);
                    int networkType = mTelephonyManager.getNetworkType();
                    switch (networkType) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return TYPE_MOBILE_CONNECT_2G;
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return TYPE_MOBILE_CONNECT_3G;
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return TYPE_MOBILE_CONNECT_4G;
                        default:
                            return TYPE_MOBILE_CONNECT;
                    }
                } else {
                    return TYPE_MOBILE_UNCONNECT;
                }
            }
        }
        return TYPE_NOT_CONNECTED;
    }
}
