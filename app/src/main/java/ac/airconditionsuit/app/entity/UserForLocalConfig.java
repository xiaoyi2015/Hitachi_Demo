package ac.airconditionsuit.app.entity;

import ac.airconditionsuit.app.MyApp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ac on 9/20/15.
 */
public class UserForLocalConfig {
    private MyUser myUser;
    private String rememberedPassword = "";
    private String phoneNumber = "";
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


    public String getRememberedPassword() {
        return rememberedPassword;
    }

    public void setRememberedPassword(String rememberedPassword) {
        this.rememberedPassword = rememberedPassword;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public void updateHostDeviceConfigFiles(List<String> fileNames) {
        String oldCurrentHomeFileName = getCurrentHomeConfigFileName();
        List<String> oldHomeFileNames = this.homeConfigFileNames;
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
        if (oldHomeFileNames == null) {
            return;
        }

        //delete unused host device config file
        for (String oldHostDeviceConfigFile:oldHomeFileNames) {
            boolean needDelete = true;
            for (String newFileName : homeConfigFileNames){
                if (newFileName.equals(oldHostDeviceConfigFile)){
                    needDelete = false;
                    break;
                }
            }
            if (needDelete) {
                deleteHostDeviceConfigFile(oldHostDeviceConfigFile);
            }
        }
    }

    private void deleteHostDeviceConfigFile(String oldHostDeviceConfigFile) {
        //warning没有处理，因为不关心删除结果。
        MyApp.getApp().getPrivateFile(oldHostDeviceConfigFile, null).delete();
    }
}
