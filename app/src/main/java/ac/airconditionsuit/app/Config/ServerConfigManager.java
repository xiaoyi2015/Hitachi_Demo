package ac.airconditionsuit.app.Config;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.entity.Device;
import ac.airconditionsuit.app.entity.ServerConfig;
import ac.airconditionsuit.app.listener.CommonNetworkListener;
import ac.airconditionsuit.app.network.HttpClient;
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

    private void readFromFile() {
        if (!MyApp.getApp().isUserLogin()) {
            Log.i(TAG, "readFromFile should be call after user login");
            return;
        }

        FileInputStream fis = null;
        try {
            File serverConfigFile = MyApp.getApp().getPrivateFile(MyApp.getApp().getLocalConfigManager().getCurrentHomeConfigFileName(), Constant.CONFIG_FILE_SUFFIX);
            if (serverConfigFile == null) {
                Log.i(TAG, "can not find service config file");
                return;
            }
            fis = new FileInputStream(serverConfigFile);
            byte[] bytes = new byte[fis.available()];
            if (fis.read(bytes) != bytes.length) {
                Log.e(TAG, "read config file error");
                return;
            }
            NSDictionary root = (NSDictionary) PropertyListParser.parse(MyBase64Util.decodeToByte(bytes));
            rootJavaObj = new Gson().fromJson(PlistUtil.NSDictionaryToJsonString(root), ServerConfig.class);
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

    private void writeToFile() {
        FileOutputStream fos = null;
        try {
            File serverConfigFile = MyApp.getApp().getServerConfigFile();
            if (serverConfigFile == null) {
                Log.i(TAG, "can not find service config file");
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
        } catch (IOException e) {
            Log.e(TAG, "read server config file error");
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

        //call when write success
        uploadToServer();
    }


    Boolean isUploading = false;

    /**
     * 本类中的所有setter方法结束之后都必须调用这个函数！
     */
    public void uploadToServer() {
        if (isUploading) {
            return;
        }
        isUploading = true;

        RequestParams requestParams = new RequestParams();
        requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_CHAT);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_METHOD_CHAT);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_DEVICEID, MyApp.getApp().getLocalConfigManager().getCurrentHomeDeviceId());

    }

    /**
     * 从服务器下载配置文件。
     *
     * @param commonNetworkListener
     */
    public void downloadDeviceInformationFromServer(final CommonNetworkListener commonNetworkListener) {
        RequestParams requestParams = new RequestParams();
        requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_CHAT);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_TYPE_GET_CHATGROUPLIST);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_CUST_CLASS, Constant.REQUEST_PARAMS_VALUE_TYPE_CUST_CLASS_10008);

        HttpClient.get(requestParams, new TypeToken<List<Device.Info>>() {
        }.getType(), new HttpClient.JsonResponseHandler<List<Device.Info>>() {
            @Override
            public void onSuccess(List<Device.Info> response) {
                ArrayList<String> fileNames = new ArrayList<>(response.size());
                downloadDeviceConfigFilesFromServer(response, fileNames, commonNetworkListener);
            }

            @Override
            public void onFailure(Throwable throwable) {
                commonNetworkListener.onFailure();
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
    private void downloadDeviceConfigFilesFromServer(final List<Device.Info> response, final List<String> fileNames, final CommonNetworkListener commonNetworkListener) {
        if (response.size() != 0) {
            Long deviceId = response.remove(0).getChat_id();
            File outputFile = MyApp.getApp().getPrivateFile(deviceId.toString(), Constant.CONFIG_FILE_SUFFIX);
            HttpClient.downloadFile(HttpClient.getDownloadConfigUrl(deviceId),
                    outputFile, new HttpClient.DownloadFileHandler() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            commonNetworkListener.onFailure();
                            Log.e(TAG, MyApp.getApp().getString(R.string.toast_inf_download_file_error));
                        }

                        @Override
                        public void onSuccess(File file) {
                            fileNames.add(file.getName());
                            downloadDeviceConfigFilesFromServer(response, fileNames, commonNetworkListener);
                        }
                    });
        } else {
            //当所有的设备配置文件下载下来以后，更新设备配置文件.
            MyApp.getApp().getLocalConfigManager().updataHostDeviceConfigFile(fileNames);
            readFromFile();
            commonNetworkListener.onSuccess();
        }
    }
}
