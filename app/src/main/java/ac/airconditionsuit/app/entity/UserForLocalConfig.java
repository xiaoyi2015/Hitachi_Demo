package ac.airconditionsuit.app.entity;

import ac.airconditionsuit.app.Config.ServerConfigManager;
import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.network.HttpClient;
import ac.airconditionsuit.app.network.response.DeleteDeviceResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
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
            return homeConfigFileNames.get(currentHomeIndex);
        }
    }

    public void deleteNoDeviceHome() {
        String oldCurrentHomeFileName = getCurrentHomeConfigFileName();
        List<String> oldHomeFileNames = this.homeConfigFileNames;
        //首先删除没有设备的本地文件
        Iterator<String> iter = oldHomeFileNames.iterator();
        while (iter.hasNext()) {
            String next = iter.next();
            if (next.contains(Constant.NO_DEVICE_CONFIG_FILE_PREFIX)) {
                deleteHostDeviceConfigFile(next);
                iter.remove();
            }
        }
        if (oldHomeFileNames.size() == 0) {
            addNewHome(Constant.NEW_HOME_NAME, Constant.AUTO_NO_DEVICE_CONFIG_FILE_PREFIX);
            currentHomeIndex = 0;
        } else {
            for (int i = 0; i < homeConfigFileNames.size(); ++i) {
                if (homeConfigFileNames.get(i).equals(oldCurrentHomeFileName))  {
                    currentHomeIndex = i;
                    return;
                }
            }
            currentHomeIndex = 0;
        }
    }


    public void updateHostDeviceConfigFiles(List<String> fileNames) {
        String oldCurrentHomeFileName = getCurrentHomeConfigFileName();
        List<String> oldHomeFileNames = this.homeConfigFileNames;
        //首先删除没有设备的本地文件
        Iterator<String> iter = oldHomeFileNames.iterator();
        while (iter.hasNext()) {
            String next = iter.next();
            if (next.contains(Constant.NO_DEVICE_CONFIG_FILE_PREFIX) || notIn(next, fileNames)) {
                deleteHostDeviceConfigFile(next);
                iter.remove();
            }
        }

        if  (fileNames == null || fileNames.size() == 0) {
            addNewHome(Constant.NEW_HOME_NAME, Constant.AUTO_NO_DEVICE_CONFIG_FILE_PREFIX);
            currentHomeIndex = 0;
        } else {
            homeConfigFileNames = fileNames;
            for (int i = 0; i < homeConfigFileNames.size(); ++i) {
                if (homeConfigFileNames.get(i).equals(oldCurrentHomeFileName))  {
                    currentHomeIndex = i;
                    return;
                }
            }
            currentHomeIndex = 0;
        }
    }

    private boolean notIn(String next, List<String> fileNames) {
        for (String s : fileNames) {
            if (s.equals(next)) {
                return false;
            }
        }
        return true;
    }

    private void deleteHostDeviceConfigFile(String oldHostDeviceConfigFile) {
        //warning没有处理，因为不关心删除结果。
        MyApp.getApp().getPrivateFile(oldHostDeviceConfigFile, null).delete();
    }

    public void addNewHome(String homeName, String prefix) {
        String configFileName = prefix + System.currentTimeMillis() + Constant.CONFIG_FILE_SUFFIX;
        homeConfigFileNames.add(configFileName);
        if (currentHomeIndex == -1) {
            currentHomeIndex = homeConfigFileNames.size() - 1;
        }
        ServerConfigManager.genNewHomeConfigFile(configFileName, homeName);
    }

    public void changeHome(int index) {
        currentHomeIndex = index;
        MyApp.getApp().getServerConfigManager().readFromFile();
    }

    public boolean deleteCurrentHome() {
        if (homeConfigFileNames.size() <= 1) {
            return false;
        } else {
            if (MyApp.getApp().getServerConfigManager().hasDevice()) {
                MyApp.getApp().getServerConfigManager().deleteCurrentDevice(new HttpClient.JsonResponseHandler<DeleteDeviceResponse>() {
                    @Override
                    public void onSuccess(DeleteDeviceResponse response) {
                        deleteHostDeviceConfigFile(homeConfigFileNames.remove(currentHomeIndex));
                        if (currentHomeIndex >= homeConfigFileNames.size()) {
                            currentHomeIndex = homeConfigFileNames.size() - 1;
                        }
                        MyApp.getApp().getServerConfigManager().readFromFile();
                        MyApp.getApp().getSocketManager().recheckDevice();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                    }
                }, "删除家失败");
            } else {
                homeConfigFileNames.remove(currentHomeIndex);
                if (currentHomeIndex >= homeConfigFileNames.size()) {
                    currentHomeIndex = homeConfigFileNames.size() - 1;
                }
                MyApp.getApp().getServerConfigManager().readFromFile();
                MyApp.getApp().getSocketManager().recheckDevice();
            }
            return true;
        }
    }

    public void updateCurrentHostDeviceConfigFile(String newFileName) {
        if (currentHomeIndex == -1 || homeConfigFileNames.size() == 0) {
            currentHomeIndex = 0;
            homeConfigFileNames.add(newFileName);
            homeConfigFileNames.set(currentHomeIndex, newFileName);
        } else {
            String oldName = homeConfigFileNames.get(currentHomeIndex);
            if (!oldName.equals(newFileName)) {
                File oldFile = MyApp.getApp().getPrivateFile(oldName, null);
                File newFile = MyApp.getApp().getPrivateFile(newFileName, null);
                if (newFile.exists()) {
                    oldFile.delete();
                } else {
                    oldFile.renameTo(newFile);
                }
                homeConfigFileNames.set(currentHomeIndex, newFileName);
            }
        }
    }

    public void deleteDevice() {
        File oldFile = MyApp.getApp().getPrivateFile(homeConfigFileNames.get(currentHomeIndex), null);
        String configFileName = Constant.NO_DEVICE_CONFIG_FILE_PREFIX + System.currentTimeMillis() + Constant.CONFIG_FILE_SUFFIX;
        File newFile = MyApp.getApp().getPrivateFile(configFileName, null);
        if (newFile.exists()) {
            newFile.delete();
        }
        oldFile.renameTo(newFile);
        homeConfigFileNames.set(currentHomeIndex, configFileName);
        MyApp.getApp().getServerConfigManager().setConfigFileAbsolutePath(newFile.getAbsolutePath());
    }

}
