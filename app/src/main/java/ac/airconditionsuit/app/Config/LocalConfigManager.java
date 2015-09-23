package ac.airconditionsuit.app.Config;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.entity.LocalConfig;
import ac.airconditionsuit.app.entity.MyUser;
import ac.airconditionsuit.app.entity.UserForLocalConfig;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.File;
import java.util.List;

/**
 * Created by ac on 9/19/15.
 * manager for local config
 */
public class LocalConfigManager {

    private LocalConfig localConfig;

    private SharedPreferences sharePreference;

    public SharedPreferences getSharePreference() {
        if (sharePreference == null) {
            sharePreference = PreferenceManager.getDefaultSharedPreferences(MyApp.getApp());
        }
        return sharePreference;
    }

    public LocalConfig getLocalConfig() {
        if (localConfig == null) {
            localConfig = LocalConfig.getInstanceFromJsonString(getSharePreference().getString(Constant.PREFERENCE_KEY_LOCAL_CONFIG, null));
            localConfig = new LocalConfig();
            saveToDisk();
        }
        return localConfig;
    }

    public UserForLocalConfig getCurrentUserConfig () {
        return getLocalConfig().getCurrentUser();
    }

    public MyUser getCurrentUser() {
        if (getCurrentUserConfig() == null) {
            return null;
        }

        return getCurrentUserConfig().getMyUser();
    }

    /**
     * if user first login in, add user;
     * else update user;
     * @param user
     */
    public void updateUser(MyUser user) {
        getLocalConfig().updateUser(user);
    }

    public void saveToDisk() {
        getSharePreference().edit().putString(Constant.PREFERENCE_KEY_LOCAL_CONFIG, localConfig.toJsonString()).apply();
    }

    public void updataHostDeviceConfigFile(List<String> fileNames) {
        getLocalConfig().updateHostDeviceConfigFile(fileNames);
        saveToDisk();
    }

    public String getCurrentHomeDeviceId() {
        String currentHomeConfigFileName = localConfig.getCurrentUser().getCurrentHomeConfigFileName();
        return currentHomeConfigFileName.substring(0, currentHomeConfigFileName.lastIndexOf(Constant.CONFIG_FILE_SUFFIX));
    }

    public String getCurrentHomeConfigFileName() {
        UserForLocalConfig user = getCurrentUserConfig();
        if (user == null) {
            return null;
        }
        return user.getCurrentHomeConfigFileName();
    }

    public File getCurrentHomeConfigFile() {
        return MyApp.getApp().getPrivateFile(getCurrentHomeConfigFileName(), null);
    }
}
