package ac.airconditionsuit.app;

import ac.airconditionsuit.app.Config.ServerConfigManager;
import ac.airconditionsuit.app.Config.LocalConfigManager;
import ac.airconditionsuit.app.PushData.PushDataManager;
import ac.airconditionsuit.app.aircondition.AirConditionManager;
import ac.airconditionsuit.app.entity.MyUser;
import ac.airconditionsuit.app.entity.ObserveData;
import ac.airconditionsuit.app.listener.CommonNetworkListener;
import ac.airconditionsuit.app.network.socket.SocketManager;

import ac.airconditionsuit.app.receiver.NetworkChangeReceiver;
import android.app.ActivityManager;
import android.app.Application;
import android.app.KeyguardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.*;
import java.util.List;

/**
 * Created by ac on 9/19/15.
 */
public class MyApp extends Application {

    private static final String TAG = "MyApp";
    private static MyApp INSTANCE;
    private long lastSuccessTime = 0;

    public static NetworkChangeReceiver getNetworkChangeReceiver() {
        return networkChangeReceiver;
    }

    public static void setNetworkChangeReceiver(NetworkChangeReceiver networkChangeReceiver) {
        MyApp.networkChangeReceiver = networkChangeReceiver;
    }

    private static NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();



    public static boolean isAppActive() {
        return appActive;
    }

    public boolean isScreenLock() {
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        boolean b = km.inKeyguardRestrictedInputMode();
        Log.e(TAG, "is scree lock: " + b);
        return b;
    }

    public static void setAppActive(boolean appActive) {
        MyApp.appActive = appActive;
    }

    private static boolean appActive = true;//第一次进入app，不检测

    private static boolean isSearchingDevices = false;
    public static void setIsSearching(boolean isSearching) {isSearchingDevices = isSearching;}
    public static boolean getIsSearching() {return isSearchingDevices;}

    private ServerConfigManager serverConfigManager;

    private LocalConfigManager localConfigManager;

    private PushDataManager pushDataManager;

    private SocketManager socketManager;

    private AirConditionManager airconditionManager;

    //user will be assigned after localConfigManager is init
    private MyUser user;
    private Handler handler;

    public Handler getHandler() {
        return handler;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;

        //init local config which is restore in default share preference.
        //after success, the configManager will init.
        initLocalConfigManager();

        handler = new Handler();

        MyApp.getApp().initAirConditionManager();

//        Log.v(TAG, getDeviceInfo(this));
    }


    public boolean isUserLogin() {
        return user != null;
    }

    public MyUser getUser() {
        return user;
    }

    private String getServerConfigFileName() {
        if (null == user) {
            return null;
        } else {
            return user.getCust_id().toString() + Constant.CONFIG_FILE_SUFFIX;
        }
    }

    public File getPrivateFile(String fileName, String suffix) {
        if (null == fileName) {
            return null;
        } else {
            return new File(getFilesDir(), fileName + (suffix == null ? "" : suffix));
        }
    }

    /**
     * init for configManager
     * this function show be call after user login.
     *
     * @param commonNetworkListener the listener call after init config from server
     */
    public void initServerConfigManager(CommonNetworkListener commonNetworkListener) {
        serverConfigManager = new ServerConfigManager();
        ServerConfigManager.downloadDeviceInformationFromServer(commonNetworkListener);
    }

    public void initPushDataManager() {
        pushDataManager = new PushDataManager();
        pushDataManager.checkPushDataFromService();
    }

    /**
     * the user get from localConfigManager maybe null;
     */
    private void initLocalConfigManager() {
        this.localConfigManager = new LocalConfigManager();
        user = localConfigManager.getCurrentUserInformation();
    }

    public void initSocketManager() {
        if (socketManager != null) {
            socketManager.close();
            socketManager.stopCheck();
            socketManager = null;
        }
        socketManager = new SocketManager();
        socketManager.init();
    }

    public void initAirConditionManager() {
        airconditionManager = new AirConditionManager();
    }

    /**
     * this method should be called after login,
     * to avoid {@link #user} is null when user first use app
     *
     * @param user the user get after login
     */
    public void setUser(MyUser user) {
        this.user = user;
    }

    public LocalConfigManager getLocalConfigManager() {
        return localConfigManager;
    }


    public ServerConfigManager getServerConfigManager() {
        return serverConfigManager;
    }

    public PushDataManager getPushDataManager() {
        return pushDataManager;
    }

    public SocketManager getSocketManager() {
        return socketManager;
    }

    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            String device_id = tm.getDeviceId();

            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);

            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }

            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            }

            json.put("device_id", device_id);

            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public AirConditionManager getAirConditionManager() {
        return airconditionManager;
    }

    /**
     * last Toast show by {@link MyApp#showToast(int)} or {@link MyApp#showToast(String)},
     * in the above two function, before show toast, the lastToast will be cancel first
     */
    private Toast lastToast;

    /**
     * call for show toast
     *
     * @param stringId string resource id
     */
    public void showToast(int stringId) {
        showToast(getString(stringId));
    }

    /**
     * call for show toast
     *
     * @param string string will be show
     */
    public void showToast(final String string) {
        if (!isAppOnForeground() || string == null || string.length() == 0 || string.contains("token 错误")) {
            return;
        }

        if (string.equals("空调控制成功")) {
            if (System.currentTimeMillis() - lastSuccessTime < 5000) {
                return;
            }else {
                lastSuccessTime = System.currentTimeMillis();
            }
        }

        if (string.contains("系统初始化未完成")) {
            MyApp.getApp().getSocketManager().notifyActivity(new ObserveData(ObserveData.SEARCH_AIR_CONDITION_CANCEL_TIMER));
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (lastToast != null) {
                    lastToast.cancel();
                }
                lastToast = Toast.makeText(MyApp.this, string, Toast.LENGTH_SHORT);
                lastToast.show();

            }
        });
    }

    public static MyApp getApp() {
        return INSTANCE;
    }

    public void offLine() {
        Log.v("liutao", "offline is called");
        this.user = null;
        this.socketManager.close();
        this.socketManager.stopCheck();
    }

//    public void quitWithoutCleaningUser() {
//        this.socketManager.close();
//        this.socketManager.stopCheck();
//    }

    public static Context context() {
        return getApp().getApplicationContext();
    }

    public static boolean isAppOnForeground() {
        // Returns a list of application processes that are running on the
        // device
        ActivityManager activityManager = (ActivityManager) context().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = MyApp.context().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

    public static void appResume() {
        if (MyApp.isAppOnForeground() && !isAppActive()) {
            setAppActive(true);
            MyApp.enterForeground();
        }
    }

    public static void appStop() {
        if (!MyApp.isAppOnForeground()) {
            setAppActive(false);
            enterBackground();
        }
    }

    private static void enterForeground() {
        Log.v("liutao", "进入前台");
        if (MyApp.getApp().pushDataManager != null) {
            MyApp.getApp().pushDataManager.checkPushDataFromService();
        }
        if (MyApp.getApp().isUserLogin()) {
            getApp().getAirConditionManager().queryTimerAll();
            getApp().getAirConditionManager().queryAirConditionStatus();
        }

        if (MyApp.getApp().getServerConfigManager() != null) {
            MyApp.getApp().getServerConfigManager().uploadToServer();
        }
    }

    private static void enterBackground() {
        ServerConfigManager serverConfigManager = MyApp.getApp().getServerConfigManager();
        if (serverConfigManager != null) {
            serverConfigManager.uploadToServer();
        }
        Log.v("liutao", "进入后台");
    }

    public void setOldUserAvatar(File file) {
        File savedFile = getCacheAvatarName();
        if (savedFile.exists()) {
            if (!savedFile.delete()) {
                Log.v(TAG, "delete old user avatar cache failed");
            }
        }
        if (!file.renameTo(savedFile)) {
            Log.v(TAG, "save user avatar cache failed");
        } else {
            Log.v(TAG, "save user avatar cache success");
        }
    }

    public File getCacheAvatarName() {
        return new File(getCacheDir(), user.getCust_id() + "userAvatar" + ".png");
    }

    public Bitmap getOldUserAvatar() {
        try {
            return BitmapFactory.decodeFile(getCacheAvatarName().getAbsolutePath());
        } catch (Exception e) {
            Log.v(TAG, "no cache avatar");
            e.printStackTrace();
            return null;
        }
    }
}
