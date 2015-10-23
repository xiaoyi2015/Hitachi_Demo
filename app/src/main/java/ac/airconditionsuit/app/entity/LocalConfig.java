package ac.airconditionsuit.app.entity;

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

    public List<UserForLocalConfig> getUsers() {
        return users;
    }

    public void setUsers(List<UserForLocalConfig> users) {
        this.users = users;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
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
            UserForLocalConfig tempuser = users.get(i);
            if (Objects.equals(tempuser.getMyUser().getCust_id(), user.getCust_id())) {
                tempuser.setMyUser(user);
                currentIndex = i;
                return;
            }

        }
        users.add(new UserForLocalConfig(user));
        //set currentIndex = latest user
        currentIndex = users.size() - 1;
    }

    public void updateCurrentUserHostDeviceConfigFile(List<String> fileNames) {
        UserForLocalConfig user = getCurrentUserForLocalConfig();
        user.updateHostDeviceConfigFiles(fileNames);
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
        getCurrentUserForLocalConfig().setRememberedPassword(password);
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
