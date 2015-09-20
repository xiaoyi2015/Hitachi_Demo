package ac.airconditionsuit.app.entity;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

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

    public UserForLocalConfig getCurrentUser() {
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

    public void addUser(MyUser user) {
        users.add(new UserForLocalConfig(user));
    }
}
