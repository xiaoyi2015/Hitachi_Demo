package ac.airconditionsuit.app.receiver;

import ac.airconditionsuit.app.Config.ServerConfigManager;
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                SocketManager socketManager = MyApp.getApp().getSocketManager();
                if (socketManager == null) {
                    return;
                }
                int status = NetworkConnectionStatusUtil.getConnectivityStatus(context);
                socketManager.switchSocketType(status);

                //log
                String logInf;
                if (status == NetworkConnectionStatusUtil.TYPE_WIFI_UNCONNECT) {
                    logInf = "network status changed, change to udp";
                } else if (status == NetworkConnectionStatusUtil.TYPE_MOBILE_CONNECT
                        || status == NetworkConnectionStatusUtil.TYPE_MOBILE_CONNECT_2G
                        || status == NetworkConnectionStatusUtil.TYPE_MOBILE_CONNECT_3G
                        || status == NetworkConnectionStatusUtil.TYPE_MOBILE_CONNECT_4G
                        || status == NetworkConnectionStatusUtil.TYPE_WIFI_CONNECT) {
                    logInf = "network status changed, change to tcp";
                    ServerConfigManager scm = MyApp.getApp().getServerConfigManager();
                    if (scm != null) {
                        scm.uploadToServer();
                    }
                } else {
                    logInf = "network status changed, close";
                }
                Log.i(TAG, logInf);

            }
        }).start();
    }
}
