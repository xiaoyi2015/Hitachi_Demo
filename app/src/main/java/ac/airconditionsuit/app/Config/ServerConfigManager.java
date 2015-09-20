package ac.airconditionsuit.app.Config;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.entity.Device;
import ac.airconditionsuit.app.network.HttpClient;
import ac.airconditionsuit.app.util.MyBase64Util;
import android.util.Log;
import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListFormatException;
import com.dd.plist.PropertyListParser;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.text.ParseException;
import java.util.List;

/**
 * Created by ac on 9/19/15.
 */
public class ServerConfigManager {

    public static final String TAG = "ConfigManager";

    //整个xml配置文件的根节点
    private NSDictionary root;

    private void readFromFile() {
        if (!MyApp.getApp().isUserLogin()) {
            Log.i(TAG, "readFromFile should be call after user login");
            return;
        }

        FileInputStream fis = null;
        try {
            File serverConfigFile = MyApp.getApp().getServerConfigFile();
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
                Log.d(TAG, String.valueOf(response.size()));
//                if (s == null) {
//                    s = new ArrayList<>();
//                }
//                updateHomeByNewDeviceInf(s);
//                ACSettingManager.getPmng().load();
//                if (downloadListener != null) {
//                    downloadListener.onSuccess();
//                }
            }

            @Override
            public void onFailure(Throwable throwable) {
            }
        });
        HttpClient.downloadFile(HttpClient.getDownloadConfigUrl(), MyApp.getApp().getServerConfigFile(), null);
    }


}
