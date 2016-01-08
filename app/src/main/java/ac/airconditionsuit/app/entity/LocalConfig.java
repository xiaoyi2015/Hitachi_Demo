package ac.airconditionsuit.app.entity;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by ac on 9/20/15.
 */
public class LocalConfig extends RootEntity {

    List<UserForLocalConfig> users = new ArrayList<>();
    int currentIndex = -1;

    public int getHomeNum(){
        if(getCurrentUserForLocalConfig().getCurrentHomeConfigFileName() == null){
            return 0;
        }
        return getCurrentUserForLocalConfig().getHomeConfigFileNames().size();
    }

    public UserForLocalConfig getCurrentUserForLocalConfig() {
        if (currentIndex < 0 || currentIndex >= users.size()) {
            return null;
        }
        return users.get(currentIndex);
    }

    public static LocalConfig getInstanceFromJsonString(String currentUserString) {
        if (currentUserString == null) {
            return null;
        }
        return new Gson().fromJson(currentUserString, LocalConfig.class);
    }

    public void updateUser(MyUser user) {
        for (int i = 0; i < users.size(); ++i) {
            UserForLocalConfig tempUser = users.get(i);
            if (Objects.equals(tempUser.getMyUser().getCust_id(), user.getCust_id())) {
                tempUser.setMyUser(user);
                currentIndex = i;
                return;
            }
        }

        UserForLocalConfig newUser = new UserForLocalConfig(user);
        users.add(newUser);
        //set currentIndex = latest user
        currentIndex = users.size() - 1;
        newUser.addNewHome(Constant.NEW_HOME_NAME, Constant.AUTO_NO_DEVICE_CONFIG_FILE_PREFIX);
    }

    public void updateCurrentUserHostDeviceConfigFile(List<String> fileNames) {
        UserForLocalConfig user = getCurrentUserForLocalConfig();
        user.updateHostDeviceConfigFiles(fileNames);
    }

    public void deleteNoDeviceHome() {
        UserForLocalConfig user = getCurrentUserForLocalConfig();
        user.deleteNoDeviceHome();
    }

    public void setCurrentPassword(String password) {
        getCurrentUserForLocalConfig().setPassword(password);
    }

    public String getCurrentPassword() {
        UserForLocalConfig currentUserForLocalConfig = getCurrentUserForLocalConfig();
        if (currentUserForLocalConfig == null) {
            return null;
        } else {
            return currentUserForLocalConfig.getPassword();
        }
    }

    public void rememberCurrentUserPassword(String password){
        UserForLocalConfig currentUserForLocalConfig = getCurrentUserForLocalConfig();
        if (currentUserForLocalConfig != null) {
            currentUserForLocalConfig.setRememberedPassword(password);
        }
    }

    public String getCurrentUserRememberedPassword(){
        UserForLocalConfig currentUserForLocalConfig = getCurrentUserForLocalConfig();
        if (currentUserForLocalConfig == null) {
            return null;
        } else {
            return currentUserForLocalConfig.getRememberedPassword();
        }
    }


    public void setCurrentUserPhoneNumber(String phoneNumber){
        getCurrentUserForLocalConfig().setPhoneNumber(phoneNumber);
    }

    public String getCurrentUserPhoneNumber(){
        UserForLocalConfig currentUserForLocalConfig = getCurrentUserForLocalConfig();
        if (currentUserForLocalConfig == null) {
            return null;
        } else {
            return currentUserForLocalConfig.getPhoneNumber();
        }
    }

}
