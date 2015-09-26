package ac.airconditionsuit.app.receiver;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.network.socket.SocketManager;
import ac.airconditionsuit.app.util.NetworkConnectionStatusUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ac on 9/24/15.
 * receiver to receiver net work status change
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        SocketManager socketManager = MyApp.getApp().getSocketManager();
        int status = NetworkConnectionStatusUtil.getConnectivityStatus(context);
        if (status == NetworkConnectionStatusUtil.TYPE_WIFI_UNCONNECT) {
            socketManager.switchSocketType(SocketManager.UDP);
        } else if (status == NetworkConnectionStatusUtil.TYPE_MOBILE_CONNECT
                || status == NetworkConnectionStatusUtil.TYPE_MOBILE_CONNECT) {
            socketManager.switchSocketType(SocketManager.TCP);
        } else {
            socketManager.switchSocketType(SocketManager.UNCONNECT);
        }
    }
}
