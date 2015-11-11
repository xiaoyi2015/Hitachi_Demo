package ac.airconditionsuit.app.Config;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.entity.*;
import ac.airconditionsuit.app.listener.CommonNetworkListener;
import ac.airconditionsuit.app.network.HttpClient;
import ac.airconditionsuit.app.network.response.DeleteDeviceResponse;
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
import java.util.TimerTask;

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
    private String fileName;
    private TimerTask writeToServerTask;

    public List<Section> getSections() {
        List<Section> sections = rootJavaObj.getSections();
        if (sections == null) {
            sections = new ArrayList<>();
            rootJavaObj.setSections(sections);
        }
        return sections;
    }

    public List<Scene> getScene() {
        return rootJavaObj.getScenes();
    }

    public List<Timer> getTimer() {
        return rootJavaObj.getTimers();
    }

    public void clearTimer() {
        rootJavaObj.setTimers(new ArrayList<Timer>());
        MyApp.getApp().getSocketManager().notifyActivity(new ObserveData(ObserveData.TIMER_STATUS_RESPONSE, null));
        writeToFile();
    }

    public int getDeviceIndexFromAddress(int address) {
        for (int i = 0; i < rootJavaObj.getDevices().size(); i++) {
            if (address == rootJavaObj.getDevices().get(i).getAddress()) {
                return i;
            }
        }
        return -1;
    }

    public ServerConfig getRootJavaObj() {
        return rootJavaObj;
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

    public List<Connection> getConnections() {
        List<Connection> connection = rootJavaObj.getConnection();
        if (connection == null) {
            rootJavaObj.setConnection(new ArrayList<Connection>());
        }
        return rootJavaObj.getConnection();
    }

    public String getCurrentHostMac() {
        return rootJavaObj.getConnection().get(0).getMac();
    }

    public Connection getCurrentHostDeviceInfo() {
        return rootJavaObj.getConnection().get(0);
    }

    public String getCurrentHostIP() {
        return rootJavaObj.getConnection().get(0).getAddress();
    }

    public void deleteScene(int position) {
        List<Scene> scenes = rootJavaObj.getScenes();
        scenes.remove(position);
        writeToFile();
    }

    public void deleteTimerByPosition(int position) {
        List<Timer> timers = rootJavaObj.getTimers();
        if (timers.size() <= position) {
            Log.v(TAG, "timer is already delete");
            return;
        }
        timers.remove(position);
        writeToFile();
    }

    public void deleteTimerById(int id) {
        List<Timer> timers = rootJavaObj.getTimers();
        for (Timer t : timers) {
            if (t.getTimerid() == id) {
                timers.remove(t);
                break;
            }
        }
        MyApp.getApp().getSocketManager().notifyActivity(new ObserveData(ObserveData.TIMER_STATUS_RESPONSE, null));
        writeToFile();
    }

    public List<DeviceFromServerConfig> getDevices() {
        return rootJavaObj.getDevices();
    }

    public Integer getDeviceCount() {
        if (rootJavaObj.getDevices() == null) return 0;
        return rootJavaObj.getDevices().size();
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

    private void readFromFile(File serverConfigFile) {
        if (!MyApp.getApp().isUserLogin()) {
            Log.i(TAG, "readFromFile should be call after user login");
            return;
        }

        FileInputStream fis = null;
        try {
            //配置文件名字不存在
            if (serverConfigFile == null) {
                String configFileName = Constant.NO_DEVICE_CONFIG_FILE_PREFIX
                        + System.currentTimeMillis()
                        + Constant.CONFIG_FILE_SUFFIX;
                fileName = MyApp.getApp().getPrivateFile(configFileName, null).getAbsolutePath();
                rootJavaObj = ServerConfig.genNewConfig(configFileName, "新的家");
                writeToFile();
                return;
            }
            fileName = serverConfigFile.getAbsolutePath();
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
            rootJavaObj = switchAddressAndIndexFileToObj(new Gson().fromJson(json, ServerConfig.class), true);
            Log.v(TAG, "read server config file success");
        } catch (ParserConfigurationException | SAXException | ParseException | IOException | PropertyListFormatException e) {
            rootJavaObj = ServerConfig.genNewConfig(serverConfigFile.getName(), "新的家");
            writeToFile();
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

    private ServerConfig switchAddressAndIndexFileToObj(ServerConfig serverConfig, boolean flag) {
        List<DeviceFromServerConfig> devices = serverConfig.getDevices();
        if (devices == null || devices.size() == 0) {
            return serverConfig;
        }
        List<Section> sections = serverConfig.getSections();
        if (sections != null) {
            for (Section section : sections) {
                List<Room> rooms = section.getPages();
                if (rooms == null) {
                    continue;
                }
                for (Room room : rooms) {
                    List<Integer> elements = room.getElements();
                    if (elements == null) {
                        continue;
                    }
                    List<Integer> newElements = new ArrayList<>();
                    for (Integer integer : elements) {
                        if (flag) {
//                            newElements.add(integer + 1);
                            newElements.add(integer);
                        } else {
//                            newElements.add(integer - 1);
                            newElements.add(integer);
                        }
                    }
                    room.setElements(newElements);
                }
            }
        }

        List<Scene> scenes = serverConfig.getScenes();
        if (scenes != null) {
            for (Scene scene : scenes) {
                List<Command> commands = scene.getCommands();
                if (commands == null) {
                    continue;
                }
                for (Command command : commands) {
                    if (flag) {
                        if (command.getAddress() >= 32) {
                            command.setAddress(0);
                        } else {
                            int realAddress = devices.get(command.getAddress()).getAddress();
                            command.setAddress(realAddress);
                        }
                    } else {
                        int realAddress = command.getAddress();
                        for (int i = 0; i < devices.size(); ++i) {
                            if (devices.get(i).getAddress() == realAddress) {
                                command.setAddress(i);
                                break;
                            }
                        }
                    }
                }
            }
        }

        serverConfig.resortTimers();
        List<Timer> timers = serverConfig.getTimers();
        serverConfig.setTimers(new ArrayList<Timer>());
        if (timers != null) {
            for (Timer timer : timers) {
                List<Integer> indexes = timer.getIndexes();
                if (indexes == null) {
                    continue;
                }
                List<Integer> newIndexes = new ArrayList<>();
                for (Integer index : indexes) {
                    if (flag) {
                        newIndexes.add(index + 1);
                    } else {
                        newIndexes.add(index - 1);
                    }
                }
                timer.setIndexes(newIndexes);
            }
        }


        return serverConfig;
    }

    public void readFromFile(String fileName) {
        File serverConfigFile = MyApp.getApp().getLocalConfigManager().getHomeConfigFile(fileName);
        readFromFile(serverConfigFile);
    }

    public void readFromFile() {
        File serverConfigFile = MyApp.getApp().getLocalConfigManager().getCurrentHomeConfigFile();
        readFromFile(serverConfigFile);
    }

    public void writeToFile() {
        writeToFile(fileName);
    }


    /**
     * 当配置文件有改动时，上传配置文件当服务器
     */
    public void uploadToServer() {
        Log.v("liutao", "上传配置文件到服务器");
        if (!hasDevice() || MyApp.getApp().getUser() == null) {
            return;
        }
        File currentHomeConfigFile = new File(fileName);
        if (currentHomeConfigFile.getName().contains(Constant.AUTO_NO_DEVICE_CONFIG_FILE_PREFIX)
                || currentHomeConfigFile.getName().contains(Constant.NO_DEVICE_CONFIG_FILE_PREFIX)) {
            return;
        }
        final RequestParams requestParams = new RequestParams();
        requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_FILE);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_TYPE_UPLOAD_DEVICE_CONFIG_FILE);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_DEVICEID, currentHomeConfigFile.getName().substring(0,
                currentHomeConfigFile.getName().lastIndexOf(Constant.CONFIG_FILE_SUFFIX)));
        try {
            requestParams.put(Constant.REQUEST_PARAMS_KEY_UPLOAD_FILE, Constant.X_DC, currentHomeConfigFile);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "uploaded file can not found");
            e.printStackTrace();
            return;
        }

        MyApp.getApp().getHandler().post(new Runnable() {
            @Override
            public void run() {
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
            final Device.Info deviceInfo = response.remove(0);
            Long deviceId = deviceInfo.getChat_id();
            final File outputFile = MyApp.getApp().getPrivateFile(deviceId.toString(), Constant.CONFIG_FILE_SUFFIX);
            HttpClient.downloadFile(HttpClient.getDownloadConfigUrl(deviceId),
                    outputFile, new HttpClient.DownloadFileHandler() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            Log.e(TAG, MyApp.getApp().getString(R.string.toast_inf_download_file_error));
                            fileNames.add(outputFile.getName());
                            if (outputFile.exists()) {
                                ServerConfigManager scm = new ServerConfigManager();
                                scm.readFromFile(outputFile);
                                scm.setCurrentDevice(deviceInfo);
                            } else {
                                ServerConfigManager scm = ServerConfigManager.genNewHomeConfigFile(outputFile.getName(), "新的家");
                                scm.setCurrentDevice(deviceInfo);
                            }
                            downloadDeviceConfigFilesFromServerIter(response, fileNames, commonNetworkListener);
                        }

                        @Override
                        public void onSuccess(File file) {
                            fileNames.add(file.getName());
                            ServerConfigManager scm = new ServerConfigManager();
                            scm.readFromFile(file);
                            scm.updateCurrentConnection(deviceInfo);
                            downloadDeviceConfigFilesFromServerIter(response, fileNames, commonNetworkListener);
                        }
                    });
        } else {
            //当所有的设备配置文件下载下来以后，更新设备配置文件.
            MyApp.getApp().getLocalConfigManager().updateHostDeviceConfigFile(fileNames);

            commonNetworkListener.onSuccess();
        }
    }

    private void updateCurrentConnection(Device.Info deviceInfo) {
        List<Connection> connections = getConnections();
        if (connections.size() == 0) {
            connections.add(new Connection(deviceInfo));
        } else {
            connections.set(0, new Connection(deviceInfo));
        }
        writeToFile();
    }

    public void deleteCurrentDevice(final HttpClient.JsonResponseHandler<DeleteDeviceResponse> handler) {
        RequestParams params = new RequestParams();
        params.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_REGISTER);
        params.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_TYPE_CANCEL);
        final long currentChatId = getCurrentChatId();
        params.put(Constant.REQUEST_PARAMS_KEY_DEVICE_ID, currentChatId);

        HttpClient.get(params, DeleteDeviceResponse.class, new HttpClient.JsonResponseHandler<DeleteDeviceResponse>() {
            @Override
            public void onSuccess(DeleteDeviceResponse response) {
                rootJavaObj.setConnection(null);
                rootJavaObj.setSections(null);
                rootJavaObj.setDevices(null);
                rootJavaObj.setScenes(null);
                rootJavaObj.setTimers(null);
                MyApp.getApp().getSocketManager().setDeviceOffline();
                writeToFile();
                //删除设备后，不应关闭tcp链接
                if (handler != null) {
                    handler.onSuccess(response);
                }

                //删除配置文件
                final RequestParams requestParams = new RequestParams();
                requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_FILE);
                requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_TYPE_RESET_DEVICE_CONFIG_FILE);
                requestParams.put(Constant.REQUEST_PARAMS_KEY_TOKEN, MyApp.getApp().getUser().getToken());
                requestParams.put(Constant.REQUEST_PARAMS_KEY_CUST_ID, MyApp.getApp().getUser().getCust_id());
                requestParams.put(Constant.REQUEST_PARAMS_KEY_DISPLAY_ID, MyApp.getApp().getUser().getDisplay_id());
                requestParams.put(Constant.REQUEST_PARAMS_KEY_DEVICEID, currentChatId);

                HttpClient.get(requestParams, String.class, new HttpClient.JsonResponseHandler<String>() {

                    @Override
                    public void onSuccess(String response) {
                        Log.i(TAG, "删除配置文件成功");
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.i(TAG, "删除配置文件失败");
                    }
                });
            }

            @Override
            public void onFailure(Throwable throwable) {
                MyApp.getApp().showToast(R.string.toast_inf_delete_device_failed);
                if (handler != null) {
                    handler.onFailure(throwable);
                }
            }
        });
    }

    public void deleteDevice(final long chat_id, final HttpClient.JsonResponseHandler handler, final boolean deleteHomeOrDevice) {
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

    public String getCurrentChatIdStringForMenuInMyAirFragment() {
        long t = getCurrentChatId();
        if (t == -1) return "";
//        if (t == MyApp.getApp().getServerConfigManager().getCurrentChatId()) return " (当前)";
        if (t == MyApp.getApp().getServerConfigManager().getCurrentChatId()) return "";
//        return " (" + t + ")";
        return "";
    }

    public void setRootJavaObj(ServerConfig rootJavaObj) {
        this.rootJavaObj = rootJavaObj;
    }

    public static ServerConfigManager genNewHomeConfigFile(String configFileName, String homeName) {
        ServerConfigManager scm = new ServerConfigManager();
        scm.setRootJavaObj(ServerConfig.genNewConfig(configFileName, homeName));
        String absolutePath = MyApp.getApp().getPrivateFile(configFileName, null).getAbsolutePath();
        scm.writeToFile(absolutePath);
        scm.setFileName(absolutePath);
        return scm;
    }

    private void setFileName(String absolutePath) {
        fileName = absolutePath;
    }

    private void writeToFile(String configFileName) {
        FileOutputStream fos = null;
        try {
            File serverConfigFile = new File(configFileName);
            if (serverConfigFile == null) {
                Log.e(TAG, "can not find device config file");
                return;
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ServerConfig rightServerConfig = switchAddressAndIndexFileToObj(new Gson().fromJson(rootJavaObj.toJsonString(), ServerConfig.class), false);
            NSDictionary root = PlistUtil.JavaObjectToNSDictionary(rightServerConfig);
            PropertyListParser.saveAsXML(root, byteArrayOutputStream);
            byte[] bytes = MyBase64Util.encodeToByte(byteArrayOutputStream.toByteArray(), true);

            fos = new FileOutputStream(configFileName);
            fos.write(bytes);
            fos.flush();
            fos.close();
            Log.v(TAG, "write server config file success");

            //call when write success
            uploadToServerAfterDelay();
//            uploadToServer();
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

    private void uploadToServerAfterDelay() {
        if (writeToServerTask != null) {
            writeToServerTask.cancel();
            writeToServerTask = null;
            //Log.v("lt", "测试定时任务，取消");
        }

        writeToServerTask = new TimerTask() {
            public void run() {
                uploadToServer();
                //Log.v("lt", "测试定时任务，执行");
            }
        };
        java.util.Timer writeToServerTimer = new java.util.Timer();
        writeToServerTimer.schedule(writeToServerTask, 3000);//3s
//            Log.v("lt", "测试定时任务，开始计时");
    }

    public void updateTimer(Timer timer) {
        rootJavaObj.updateTimer(timer);
        writeToFile();
    }

    public void updateAirCondition(byte[] contentData) {
        int count = contentData[0] & 0xff;
        if (contentData.length != count + 1) {
            Log.e(TAG, "decode air condition address fail");
        }
        List<DeviceFromServerConfig> newDevices = new ArrayList<>();
        for (int i = 1; i < contentData.length; ++i) {
            DeviceFromServerConfig tempDevice = new DeviceFromServerConfig(contentData[i]);
            newDevices.add(tempDevice);
        }

        List<DeviceFromServerConfig> oldDevices = getRootJavaObj().getDevices();
        if (oldDevices != null) {
            for (DeviceFromServerConfig oldDevice : oldDevices) {
                for (DeviceFromServerConfig newDevice : newDevices){
                    if (newDevice.getAddress() == oldDevice.getAddress()) {
                        newDevice.setName(oldDevice.getName());
                    }
                }
            }
        }
        getRootJavaObj().setDevices(newDevices);

        ObserveData od = new ObserveData(ObserveData.SEARCH_AIR_CONDITION_RESPONSE, getDevices());
        MyApp.getApp().getSocketManager().notifyActivity(od);

        if (oldDevices != null && oldDevices.size() != 0 && oldDevices.size() != newDevices.size()) {
            MyApp.getApp().getSocketManager().notifyActivity(new ObserveData(ObserveData.SEARCH_AIR_CONDITION_NUMBERDIFFERENT));
        }
    }


    private void setCurrentDevice(Device.Info deviceInfo) {
        List<Connection> connections = rootJavaObj.getConnection();
        if (connections == null) {
            connections = new ArrayList<>();
            rootJavaObj.setConnection(connections);
        }
        Connection connection = new Connection(deviceInfo);
        connections.clear();
        connections.add(connection);
        writeToFile();
    }

    public void setCurrentDevice(Device device) {
        setCurrentDevice(device.getInfo());
    }

    public void deleteAllTimer() {
        rootJavaObj.setTimers(new ArrayList<Timer>());
        writeToFile();
    }

    public void airconditionNumberChange() {
        if (rootJavaObj.getScenes() != null) {
            rootJavaObj.getScenes().clear();
        }

        if (rootJavaObj.getSections() != null) {
            rootJavaObj.getSections().clear();
        }

        writeToFile();
    }
}
