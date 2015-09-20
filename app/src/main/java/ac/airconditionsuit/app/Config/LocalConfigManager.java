package ac.airconditionsuit.app.Config;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.entity.LocalConfig;
import ac.airconditionsuit.app.entity.MyUser;
import ac.airconditionsuit.app.entity.UserForLocalConfig;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Random;

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
            localConfig = LocalConfig.getInstanceFromJsonString(sharePreference.getString(Constant.PREFERENCE_KEY_LOCAL_CONFIG, null));
            localConfig = new LocalConfig();
            asyncWithDisk();
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

    public void addUser(MyUser user) {
        getLocalConfig().addUser(user);
    }

    private String genRandomFileName() {
        return System.currentTimeMillis() + new Random().nextInt(100) + Constant.CONFIG_FILE_SUFFIX;
    }

    public void asyncWithDisk() {
        getSharePreference().edit().putString(Constant.PREFERENCE_KEY_LOCAL_CONFIG, localConfig.toJsonString()).apply();
    }
}
