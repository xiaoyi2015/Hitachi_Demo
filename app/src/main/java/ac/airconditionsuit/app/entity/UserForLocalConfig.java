package ac.airconditionsuit.app.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ac on 9/20/15.
 */
public class UserForLocalConfig {
    private MyUser myUser;
    private boolean isRemember = false;
    List<String> homeConfigFileNames = new ArrayList<>();
    int currentHomeIndex = -1;

    public UserForLocalConfig(MyUser user) {
        myUser = user;
    }

    public MyUser getMyUser() {
        return myUser;
    }

    public void setMyUser(MyUser myUser) {
        this.myUser = myUser;
    }

    public boolean isRemember() {
        return isRemember;
    }

    public void setIsRemember(boolean isRemember) {
        this.isRemember = isRemember;
    }

    public List<String> getHomeConfigFileNames() {
        return homeConfigFileNames;
    }

    public void setHomeConfigFileNames(List<String> homeConfigFileNames) {
        this.homeConfigFileNames = homeConfigFileNames;
    }

    public int getCurrentHomeIndex() {
        return currentHomeIndex;
    }

    public void setCurrentHomeIndex(int currentHomeIndex) {
        this.currentHomeIndex = currentHomeIndex;
    }

    public String getCurrentHomeConfigFileName() {
        if (currentHomeIndex < 0 || currentHomeIndex >= homeConfigFileNames.size()) {
            return null;
        } else {
            return homeConfigFileNames.get(currentHomeIndex);
        }
    }

    public void updateDevice(List<String> fileNames) {
        String oldCurrentHomeFileName = getCurrentHomeConfigFileName();
        this.homeConfigFileNames = fileNames;
        if (oldCurrentHomeFileName == null) {
            if (fileNames.size() == 0) {
                currentHomeIndex = -1;
            } else {
                currentHomeIndex = 0;
            }
        }
        for (int i = 0; i < homeConfigFileNames.size(); ++i) {
            if (homeConfigFileNames.get(i).equals(oldCurrentHomeFileName)) {
                currentHomeIndex = i;
                return;
            }
        }
        currentHomeIndex = 0;
    }
}
