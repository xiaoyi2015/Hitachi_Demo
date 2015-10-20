package ac.airconditionsuit.app;

import ac.airconditionsuit.app.Config.ServerConfigManager;
import ac.airconditionsuit.app.Config.LocalConfigManager;
import ac.airconditionsuit.app.PushData.PushDataManager;
import ac.airconditionsuit.app.aircondition.AirConditionManager;
import ac.airconditionsuit.app.entity.MyUser;
import ac.airconditionsuit.app.listener.CommonNetworkListener;
import ac.airconditionsuit.app.network.socket.SocketManager;
import android.app.Application;
import android.os.Handler;
import android.widget.Toast;

import java.io.*;

/**
 * Created by ac on 9/19/15.
 *
 */
public class MyApp extends Application {

    private static MyApp INSTANCE;

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

        MyApp.getApp().initAirconditionManager();
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

    public void initPushDataManager(){
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
        socketManager = new SocketManager();
        socketManager.init();
    }

    public void initAirconditionManager() {
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

    public AirConditionManager getAirconditionManager() {
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
        this.user = null;
//        this.localConfigManager = null;
//        this.serverConfigManager = null;
        this.socketManager.close();
        this.socketManager = null;
    }

}
