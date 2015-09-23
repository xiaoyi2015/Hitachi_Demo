package ac.airconditionsuit.app.Config;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.entity.Device;
import ac.airconditionsuit.app.entity.ServerConfig;
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
 */
public class ServerConfigManager {

    public static final String TAG = "ConfigManager";

    //整个xml配置文件的根节点
    private NSDictionary root;
    private ServerConfig rootJavaObj;

    private void readFromFile() {
        if (!MyApp.getApp().isUserLogin()) {
            Log.i(TAG, "readFromFile should be call after user login");
            return;
        }

        FileInputStream fis = null;
        try {
            File serverConfigFile = MyApp.getApp().getPrivateFiles(MyApp.getApp().getLocalConfigManager().getCurrentHomeConfigFileName());
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
            root = (NSDictionary) PropertyListParser.parse(MyBase64Util.decodeToByte(bytes));
            rootJavaObj = new Gson().fromJson(PlistUtil.NSDictionaryToJsonString(root), ServerConfig.class);
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

    public void uploadToServer() {

    }


    /**
     * 与服务器同步配置文件，每次修改{@link ServerConfigManager#root}以后都要调用
     */
    public void downloadFromServer() {
        RequestParams requestParams = new RequestParams();
        requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_CHAT);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_TYPE_GET_CHATGROUPLIST);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_CUST_CLASS, Constant.REQUEST_PARAMS_VALUE_TYPE_CUST_CLASS_10008);

        HttpClient.get(requestParams, new TypeToken<List<Device.Info>>() {
        }.getType(), new HttpClient.JsonResponseHandler<List<Device.Info>>() {
            @Override
            public void onSuccess(List<Device.Info> response) {
                ArrayList<String> fileNames = new ArrayList<>(response.size());
                downloadConfigFiles(response, fileNames);
            }

            @Override
            public void onFailure(Throwable throwable) {
            }
        });
    }

    /**
     * 这个函数递归的下载所有设备的配置文件，一个设备对应一个家，也就是所有家的配置文件。
     * @param response 所有等待下载的设备的信息。
     * @param fileNames 已经下载的设备配置文件的文件名。
     */
    private void downloadConfigFiles(final List<Device.Info> response, final List<String> fileNames) {
        if (response.size() != 0) {
            Long deviceId = response.remove(0).getChat_id();
            File outputFile = MyApp.getApp().getPrivateFiles(deviceId.toString());
            HttpClient.downloadFile(HttpClient.getDownloadConfigUrl(deviceId),
                    outputFile, new HttpClient.DownloadFileHandler() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            MyApp.getApp().showToast(R.string.toast_inf_download_file_error);
                        }

                        @Override
                        public void onSuccess(File file) {
                            fileNames.add(file.getName());
                            downloadConfigFiles(response, fileNames);
                        }
                    });
        } else {
            //当所有的设备配置文件下载下来以后，更新设备配置文件.
            MyApp.getApp().getLocalConfigManager().updataDevice(fileNames);
            readFromFile();
        }
    }
}
