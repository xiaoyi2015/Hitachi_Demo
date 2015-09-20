package ac.airconditionsuit.app;

import ac.airconditionsuit.app.Config.ServerConfigManager;
import ac.airconditionsuit.app.Config.LocalConfigManager;
import ac.airconditionsuit.app.entity.MyUser;
import android.app.Application;
import android.widget.Toast;

import java.io.*;

/**
 * Created by ac on 9/19/15.
 *
 */
public class MyApp extends Application{
    private static MyApp INSTANCE;
    private ServerConfigManager serverConfigManager;


    private LocalConfigManager localConfigManager;

    //user will be assigned after localConfigManager is init
    private MyUser user;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;

        //init local config which is restore in default share preference.
        //after success, the configManager will init.
        initLocalConfigManager();

    }

    public boolean isUserLogin(){
        return user != null;
    }

    public MyUser getUser(){
        return user;
    }

    private String getServerConfigFileName(){
        if (null == user) {
            return null;
        } else {
            return user.getCust_id().toString() + Constant.CONFIG_FILE_SUFFIX;
        }
    }

    public File getServerConfigFile(){
        String fileName = getServerConfigFileName();
        if (null == fileName) {
            return null;
        } else {
            return new File(getFilesDir(), fileName);
        }
    }

    /**
     * init for configManager
     * this function show be call after user login.
     */
    public void initConfigManager() {
        serverConfigManager = new ServerConfigManager();
        serverConfigManager.downloadFromServer();
    }

    /**
     * the user get from localConfigManager maybe null;
     */
    private void initLocalConfigManager() {
        this.localConfigManager = new LocalConfigManager();
        user = localConfigManager.getCurrentUser();
    }

    /**
     * this method should be called after login,
     * to avoid {@link #user} is null when user first use app
     * @param user
     */
    public void setUser(MyUser user) {
        this.user = user;
    }

    public LocalConfigManager getLocalConfigManager() {
        return localConfigManager;
    }


    /**
     * last Toast show by {@link MyApp#showToast(int)} or {@link MyApp#showToast(String)},
     * in the above two function, before show toast, the lastToast will be cancel first
     */
    private Toast lastToast;
    /**
     * call for show toast
     * @param stringId string resource id
     */
    public void showToast(int stringId){
        showToast(getString(stringId));
    }
    /**
     * call for show toast
     * @param string string will be show
     */
    public void showToast(String string){
        if (lastToast != null) {
            lastToast.cancel();
        }
        lastToast = Toast.makeText(this, string, Toast.LENGTH_SHORT);
        lastToast.show();
    }


    public static MyApp getApp(){
        return INSTANCE;
    }

}
