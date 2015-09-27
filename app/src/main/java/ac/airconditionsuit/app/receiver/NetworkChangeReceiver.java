package ac.airconditionsuit.app.receiver;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.network.socket.SocketManager;
import ac.airconditionsuit.app.util.NetworkConnectionStatusUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by ac on 9/24/15.
 * receiver to receiver net work status change
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    public static final String TAG = "NetworkChangeReceiver";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        SocketManager socketManager = MyApp.getApp().getSocketManager();
        int status = NetworkConnectionStatusUtil.getConnectivityStatus(context);
        String logInf;
        if (status == NetworkConnectionStatusUtil.TYPE_WIFI_UNCONNECT) {
            socketManager.switchSocketType(SocketManager.UDP);
            logInf = "network status changed, change to udp";
        } else if (status == NetworkConnectionStatusUtil.TYPE_MOBILE_CONNECT
                || status == NetworkConnectionStatusUtil.TYPE_WIFI_CONNECT) {
            socketManager.switchSocketType(SocketManager.TCP);
            logInf = "network status changed, change to tcp";
        } else {
            socketManager.switchSocketType(SocketManager.UNCONNECT);
            logInf = "network status changed, close";
        }
        Log.i(TAG, logInf);
    }
}
