package ac.airconditionsuit.app.service;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.network.socket.SocketManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by ac on 10/17/15.
 */
public class SocketAuxService extends Service {
    private static final String TAG = "SocketAuxService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "server start");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "close socket on service");
        try {
            new File("/sdcard/testserverdestroy").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SocketManager socketManager = MyApp.getApp().getSocketManager();
        if (socketManager != null) {
            socketManager.close();
        }
    }
}
