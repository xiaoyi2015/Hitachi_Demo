package ac.airconditionsuit.app.entity;

import ac.airconditionsuit.app.Config.ServerConfigManager;
import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ac on 9/20/15.
 */
public class UserForLocalConfig {
    private MyUser myUser;
    private String password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
            return homeConfigFileNames.get(1);
        }
    }

    public void updateHostDeviceConfigFiles(List<String> fileNames) {
        String oldCurrentHomeFileName = getCurrentHomeConfigFileName();
        List<String> oldHomeFileNames = this.homeConfigFileNames;
        this.homeConfigFileNames = fileNames;
        if (oldCurrentHomeFileName == null) {
            if (fileNames.size() == 0) {
                //如果之前没有配置文件，与服务器同步有没有获得配置文件，那么设置currentHomeIndex为-1
                currentHomeIndex = -1;
            } else {
                currentHomeIndex = 0;
            }
            //如果之前没有配置文件，那么无论与服务器同步有没有获得配置文件有没有获得,都不需要删除旧的文件.
            return;
        }

        //以下代码只有有旧文件时才会往下执行
        //确定新的index
        //预先设置currentHomeIndex = 0; 如果新的配置文件不包含旧的，那么currentHomeIndex = 0保持不变;
        currentHomeIndex = 0;
        for (int i = 0; i < homeConfigFileNames.size(); ++i) {
            if (homeConfigFileNames.get(i).equals(oldCurrentHomeFileName)) {
                currentHomeIndex = i;
            }
        }

        List<String> temp = new ArrayList<>();
        //如果旧的有配置文件，那么把不在新的配置文件中的文件删除，删除物理上的文件
        //delete unused host device config file
        for (String oldHostDeviceConfigFile : oldHomeFileNames) {
            if (oldHostDeviceConfigFile.indexOf(Constant.NO_DEVICE_CONFIG_FILE_PREFIX) == 0) {
                //这里把没有设备的家的配置文件先保存下来，后面添加
                temp.add(oldHostDeviceConfigFile);
            } else {
                boolean needDelete = true;
                for (String newFileName : homeConfigFileNames) {
                    if (newFileName.equals(oldHostDeviceConfigFile)) {
                        needDelete = false;
                        break;
                    }
                }
                if (needDelete) {
                    deleteHostDeviceConfigFile(oldHostDeviceConfigFile);
                }
            }
        }
        //将没有设备的家的配置文件重新加入到配置文件列表
        homeConfigFileNames.addAll(temp);
    }

    private void deleteHostDeviceConfigFile(String oldHostDeviceConfigFile) {
        //warning没有处理，因为不关心删除结果。
        MyApp.getApp().getPrivateFile(oldHostDeviceConfigFile, null).delete();
    }

    public void addNewHome(String homeName) {
        String configFileName = Constant.NO_DEVICE_CONFIG_FILE_PREFIX + System.currentTimeMillis() + Constant.CONFIG_FILE_SUFFIX;
        homeConfigFileNames.add(configFileName);
        ServerConfigManager.genNewHomeConfigFile(configFileName, homeName);
    }
}
