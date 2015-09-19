package ac.airconditionsuit.app;

import ac.airconditionsuit.app.Config.ConfigManager;
import ac.airconditionsuit.app.Config.LocalConfigManager;
import ac.airconditionsuit.app.entity.MyUser;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;
import org.w3c.dom.ls.LSException;

/**
 * Created by ac on 9/19/15.
 *
 */
public class MyApp extends Application{
    private static MyApp INSTANCE;
    private ConfigManager configManager;
    private LocalConfigManager localConfigManager;

    //user will be assigned after localConfigManager is init
    private MyUser user;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;

        //init local config which is restore in default share preference,
        initPreferenceManager();

        initConfigManager();
    }

    public boolean isUserLogin(){
        return user != null;
    }

    public MyUser getUser(){
        return user;
    }

    public String getConfigFileName(){
        if (null == user) {
            return null;
        } else {
            return user.getCust_id().toString() + Constant.CONFIG_FILE_SUFFIX;
        }
    }

    /**
     * TODO init for configManager
     */
    private void initConfigManager() {
    }

    private void initPreferenceManager() {
        this.localConfigManager = new LocalConfigManager(this);
        user = localConfigManager.getCurrentUser();
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
     * @param stringId string resource id
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
