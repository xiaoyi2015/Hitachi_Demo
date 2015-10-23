package ac.airconditionsuit.app.Config;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.entity.Home;
import ac.airconditionsuit.app.entity.LocalConfig;
import ac.airconditionsuit.app.entity.MyUser;
import ac.airconditionsuit.app.entity.UserForLocalConfig;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.File;
import java.util.ArrayList;
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
            if (localConfig == null) {
                localConfig = new LocalConfig();
                saveToDisk();
            }
        }
        return localConfig;
    }

    public UserForLocalConfig getCurrentUserConfig () {
        return getLocalConfig().getCurrentUserForLocalConfig();
    }

    public MyUser getCurrentUserInformation() {
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
        saveToDisk();
    }

    /**
     * 所有这个类中，改变了设置的方法末尾都应该调用这个函数
     */
    public void saveToDisk() {
        getSharePreference().edit().putString(Constant.PREFERENCE_KEY_LOCAL_CONFIG, localConfig.toJsonString()).apply();
    }

    public void updataHostDeviceConfigFile(List<String> fileNames) {
        getLocalConfig().updateCurrentUserHostDeviceConfigFile(fileNames);
        saveToDisk();
    }

    public String getCurrentHomeDeviceId() {
        String currentHomeConfigFileName = localConfig.getCurrentUserForLocalConfig().getCurrentHomeConfigFileName();
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

    /**
     * @param password 当传入是{@code ""}时，表示去掉记住密码的选项。
     */
    public void setCurrentUserRememberedPassword(String password){
        getLocalConfig().rememberCurrentUserPassword(password);
        saveToDisk();
    }

    public String getCurrentUserRememberedPassword(){
        return getLocalConfig().getCurrentUserRememberedPassword();
    }

    public void setCurrentPassword(String password) {
        getLocalConfig().setCurrentPassword(password);
        saveToDisk();
    }

    public String getCurrentPassword() {
        return getLocalConfig().getCurrentPassword();
    }

    public void setCurrentUserPhoneNumber(String phoneNumber){
        getLocalConfig().setCurrentUserPhoneNumber(phoneNumber);
    }

    public String getCurrentUserPhoneNumber(){
        return getLocalConfig().getCurrentUserPhoneNumber();
    }

    public void addNewHome(String homeName) {
        UserForLocalConfig user = getCurrentUserConfig();
        user.addNewHome(homeName);
        saveToDisk();
    }

    public List<Home> getHomeList() {
        List<Home> res = new ArrayList<>();
        return res;
//        for (String configFileName : getCurrentUserConfig().getHomeConfigFileNames()) {
//            ServerConfigManager serverConfigManager = new ServerConfigManager();
//            serverConfigManager.readFromFile();
//            res.add(serverConfigManager)
//        }
    }

    public void changeHome(int homeIndex) {

    }
}
