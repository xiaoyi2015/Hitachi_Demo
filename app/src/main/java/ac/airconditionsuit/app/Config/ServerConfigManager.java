package ac.airconditionsuit.app.Config;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.entity.*;
import ac.airconditionsuit.app.listener.CommonNetworkListener;
import ac.airconditionsuit.app.network.HttpClient;
import ac.airconditionsuit.app.network.response.UploadConfigResponse;
import ac.airconditionsuit.app.util.MyBase64Util;
import ac.airconditionsuit.app.util.PlistUtil;
import android.util.Log;
import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListFormatException;
import com.dd.plist.PropertyListParser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ac on 9/19/15.
 * 与要与服务器同步的配置文件的相关操作都集中在这个类.
 * 配置文件的信息储存在{@link #rootJavaObj}，平时访问时直接从{@link #rootJavaObj}读取就行。
 * 但是写入后必须与配置文件同步，即：
 * 所有setter方法调用以后，因为改变了配置内容，所以都需要调用{@link #writeToFile()}这个方法，达到同步配置文件的目的。
 */
public class ServerConfigManager {

    public static final String TAG = "ConfigManager";

    /**
     * 这个field中以java对象的形式，储存着当前家整个配置文件的内容。其实这个家的内容和root
     */
    private ServerConfig rootJavaObj;

    public List<Section> getSections() {
        return rootJavaObj.getSections();
    }

    public List<Scene> getScene() {
        return rootJavaObj.getScenes();
    }

    public List<Timer> getTimer() {
        return rootJavaObj.getTimers();
    }

    public int getDeviceIndexFromAddress(int index){
        for(int i = 0; i < rootJavaObj.getDevices().size(); i++){
            if(index == rootJavaObj.getDevices().get(i).getIndoorindex()){
                return i;
            }
        }
        return -1;
    }

    public boolean hasDevice() {
        return rootJavaObj != null
                && rootJavaObj.getConnection() != null
                && rootJavaObj.getConnection().size() != 0;
    }

    public boolean hasHome() {
        return rootJavaObj != null
                && rootJavaObj.getHome() != null;
    }

    public void addSections(Section section) {
        List<Section> sections = rootJavaObj.getSections();
        sections.add(section);
        writeToFile();
    }

    public void addScene(Scene scene) {
        List<Scene> scenes = rootJavaObj.getScenes();
        scenes.add(scene);
        writeToFile();
    }

    public void addTimer(Timer timer) {
        List<Timer> timers = rootJavaObj.getTimers();
        timers.add(timer);
        writeToFile();
    }

    public void deleteSection(int position) {
        List<Section> sections = rootJavaObj.getSections();
        sections.remove(position);
        writeToFile();
    }

    public List<ServerConfig.Connection> getConnections() {
        return rootJavaObj.getConnection();
    }

    public String getCurrentHostMac() {
        return rootJavaObj.getConnection().get(0).getMac();
    }

    public String getCurrentHostIP() {
        return rootJavaObj.getConnection().get(0).getAddress();
    }

    public void deleteScene(int position) {
        List<Scene> scenes = rootJavaObj.getScenes();
        scenes.remove(position);
        writeToFile();
    }

    public void deleteTimer(int position) {
        List<Timer> timers = rootJavaObj.getTimers();
        timers.remove(position);
        writeToFile();
    }

    public List<DeviceFromServerConfig> getDevices() {
        return rootJavaObj.getDevices();
    }

    public Home getHome() {
        return rootJavaObj.getHome();
    }

    public void deleteRoom(int position, int index) {
        List<Section> sections = rootJavaObj.getSections();
        sections.get(position).getPages().remove(index);
        writeToFile();
    }

    public void addRoom(int position, Room room) {
        List<Section> sections = rootJavaObj.getSections();
        sections.get(position).getPages().add(room);
        writeToFile();
    }

    public void renameRoom(int position, int index, String string) {
        List<Section> sections = rootJavaObj.getSections();
        sections.get(position).getPages().get(index).setName(string);
        writeToFile();
    }

    public void submitRoomChanges(int index, List<Room> rooms) {
        rootJavaObj.getSections().get(index).setPages(rooms);
        writeToFile();
    }

    public void readFromFile() {
        if (!MyApp.getApp().isUserLogin()) {
            Log.i(TAG, "readFromFile should be call after user login");
            return;
        }

        FileInputStream fis = null;
        try {
            File serverConfigFile = MyApp.getApp().getLocalConfigManager().getCurrentHomeConfigFile();
            //配置文件名字不存在
            if (serverConfigFile == null) {
                rootJavaObj = ServerConfig.genNewConfig(Constant.NO_DEVICE_CONFIG_FILE_PREFIX + System.currentTimeMillis() + Constant.CONFIG_FILE_SUFFIX,
                        "new home");
                return;
            }
            //配置文件名字存在，文件不存在
            if (!serverConfigFile.exists()) {
                throw new IOException();
            }
            fis = new FileInputStream(serverConfigFile);
            byte[] bytes = new byte[fis.available()];
            if (fis.read(bytes) != bytes.length) {
                throw new IOException();
            }
            NSDictionary root = (NSDictionary) PropertyListParser.parse(MyBase64Util.decodeToByte(bytes));
            String json = PlistUtil.NSDictionaryToJsonString(root);
            rootJavaObj = new Gson().fromJson(json, ServerConfig.class);
            Log.i(TAG, "read server config file success");
        } catch (ParserConfigurationException | SAXException | ParseException | IOException | PropertyListFormatException e) {
            Log.e(TAG, "read server config file error");
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void writeToFile(String fileName) {
        FileOutputStream fos = null;
        try {
            File serverConfigFile = MyApp.getApp().getPrivateFile(fileName, null);
            if (serverConfigFile == null) {
                Log.i(TAG, "can not find device config file");
                return;
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            NSDictionary root = PlistUtil.JavaObjectToNSDictionary(rootJavaObj);
            PropertyListParser.saveAsXML(root, byteArrayOutputStream);
            byte[] bytes = MyBase64Util.encodeToByte(byteArrayOutputStream.toByteArray(), true);

            fos = new FileOutputStream(serverConfigFile);
            fos.write(bytes);
            fos.flush();
            fos.close();
            Log.i(TAG, "write server config file error");

            //call when write success
            uploadToServer();
        } catch (IOException e) {
            Log.e(TAG, "write server config file error");
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 本类中的所有setter方法结束之后都必须调用这个函数！
     */
    public void writeToFile() {
        String serverConfigFileName = MyApp.getApp().getLocalConfigManager().getCurrentHomeConfigFileName();
        writeToFile(serverConfigFileName);
    }


    /**
     * 当配置文件有改动时，上传配置文件当服务器
     */
    public void uploadToServer() {
        if (!hasDevice()) {
            return;
        }
        RequestParams requestParams = new RequestParams();
        requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_FILE);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_TYPE_UPLOAD_DEVICE_CONFIG_FILE);
        MyApp app = MyApp.getApp();
        requestParams.put(Constant.REQUEST_PARAMS_KEY_DEVICEID, app.getLocalConfigManager().getCurrentHomeDeviceId());
        try {
            requestParams.put(Constant.REQUEST_PARAMS_KEY_UPLOAD_FILE, Constant.X_DC, app.getLocalConfigManager().getCurrentHomeConfigFile());
        } catch (FileNotFoundException e) {
            Log.e(TAG, "uploaded file can not found");
            e.printStackTrace();
            return;
        }
        HttpClient.post(requestParams, UploadConfigResponse.class, new HttpClient.JsonResponseHandler<UploadConfigResponse>() {
            @Override
            public void onSuccess(UploadConfigResponse response) {
                Log.i(TAG, "upload host device file success");
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "upload host device file error");
            }
        });
    }

    /**
     * 从服务器下载配置文件。
     *
     * @param commonNetworkListener
     */
    public static void downloadDeviceInformationFromServer(final CommonNetworkListener commonNetworkListener) {
        final CommonNetworkListener wrapCommonNetworkListener = new CommonNetworkListener() {
            @Override
            public void onSuccess() {
                MyApp.getApp().getServerConfigManager().readFromFile();
                commonNetworkListener.onSuccess();
            }

            @Override
            public void onFailure() {
                commonNetworkListener.onFailure();
            }
        };

        RequestParams requestParams = new RequestParams();
        requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_CHAT);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_TYPE_GET_CHATGROUPLIST);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_CUST_CLASS, Constant.REQUEST_PARAMS_VALUE_TYPE_CUST_CLASS_10001);

        HttpClient.get(requestParams, new TypeToken<List<Device.Info>>() {
        }.getType(), new HttpClient.JsonResponseHandler<List<Device.Info>>() {
            @Override
            public void onSuccess(List<Device.Info> response) {
                ArrayList<String> fileNames = new ArrayList<>(response.size());
                downloadDeviceConfigFilesFromServerIter(response, fileNames, wrapCommonNetworkListener);
            }

            @Override
            public void onFailure(Throwable throwable) {
                wrapCommonNetworkListener.onFailure();
            }
        });
    }

    /**
     * 这个函数递归的下载所有设备的配置文件，一个设备对应一个家，也就是所有家的配置文件。
     *
     * @param response              所有等待下载的设备的信息。
     * @param fileNames             已经下载的设备配置文件的文件名。
     * @param commonNetworkListener
     */
    private static void downloadDeviceConfigFilesFromServerIter(final List<Device.Info> response, final List<String> fileNames, final CommonNetworkListener commonNetworkListener) {
        if (response.size() != 0) {
            Long deviceId = response.remove(0).getChat_id();
            final File outputFile = MyApp.getApp().getPrivateFile(deviceId.toString(), Constant.CONFIG_FILE_SUFFIX);
            HttpClient.downloadFile(HttpClient.getDownloadConfigUrl(deviceId),
                    outputFile, new HttpClient.DownloadFileHandler() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            Log.e(TAG, MyApp.getApp().getString(R.string.toast_inf_download_file_error));
                            fileNames.add(outputFile.getName());
                            downloadDeviceConfigFilesFromServerIter(response, fileNames, commonNetworkListener);
                        }

                        @Override
                        public void onSuccess(File file) {
                            fileNames.add(file.getName());
                            downloadDeviceConfigFilesFromServerIter(response, fileNames, commonNetworkListener);
                        }
                    });
        } else {
            //当所有的设备配置文件下载下来以后，更新设备配置文件.
            MyApp.getApp().getLocalConfigManager().updataHostDeviceConfigFile(fileNames);

            commonNetworkListener.onSuccess();
        }
    }


    public long getAdminCustId() {
        if (hasDevice()) {
            return rootJavaObj.getConnection().get(0).getCreator_cust_id();
        } else {
            return -1;
        }
    }

    public long getCurrentChatId() {
        if (hasDevice()) {
            return rootJavaObj.getConnection().get(0).getChat_id();
        } else {
            return -1;
        }
    }

    public void setRootJavaObj(ServerConfig rootJavaObj) {
        this.rootJavaObj = rootJavaObj;
    }

    public static void genNewHomeConfigFile(String configFileName, String homeName) {
        ServerConfigManager scm = new ServerConfigManager();
        scm.setRootJavaObj(ServerConfig.genNewConfig(configFileName, homeName));
        scm.writeToFile(configFileName);
    }
}
