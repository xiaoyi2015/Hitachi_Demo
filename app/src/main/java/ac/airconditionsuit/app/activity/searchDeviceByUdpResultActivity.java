package ac.airconditionsuit.app.activity;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.entity.Device;
import ac.airconditionsuit.app.entity.ObserveData;
import ac.airconditionsuit.app.network.HttpClient;
import android.os.Bundle;
import android.util.Log;
import ac.airconditionsuit.app.R;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**TODO for zhulinan
 * 点击自动搜索主机后跳转到该activity
 */
public class searchDeviceByUdpResultActivity extends BaseActivity {

    private List<Device> devices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device_by_udp_result);
        //调用这个函数以后开始在局域网中搜索主机
        MyApp.getApp().getSocketManager().sendBroadCast();
    }

    /**
     * 每搜索到一个主机，就会调用一次这个函数。注意，这个函数可能多次调用都是传入同一个主机，所以在添加的时候需要判断主机在不在
     * @param observable
     * @param data 这个就是主机
     */
    @Override
    public void update(Observable observable, Object data) {
        super.update(observable, data);
        ObserveData od = (ObserveData) data;

        switch (od.getMsg()) {
            case ObserveData.FIND_DEVICE_BY_UDP:
                //如果不为空，就表示搜索到一个设备,做相应处理
                Device device = (Device) od.getData();
                addDevice(device);
                break;
            case ObserveData.FIND_DEVICE_BY_UDP_FAIL:
                //如果返回会空，就表示发送广播包出现错误，做相应处理。如在界面上显示搜索失败之类的。
                MyApp.getApp().showToast(R.string.search_host_device_failed);
                break;
        }
    }

    /**
     * 这个方法用来绑定主机，这个方法中，我是测试用的。可以复制到用户确定绑定的地方调用。
     * @param device
     */
    private void bindDevice(final Device device) {
        RequestParams params = new RequestParams();
        params.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_REGISTER);
        params.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_TYPE_REGISTER_DEVICE);
        params.put(Constant.REQUEST_PARAMS_KEY_CUST_CLASS, Constant.REQUEST_PARAMS_VALUE_TYPE_CUST_CLASS_10001);
        params.put(Constant.REQUEST_PARAMS_KEY_DEVICE_ID, device.getInfo().getChat_id().toString());
        params.put(Constant.REQUEST_PARAMS_KEY_INTRODUCE, MyApp.getApp().getServerConfigManager().getHome().getName());
        params.put(Constant.REQUEST_PARAMS_KEY_MAC, device.getAuthCode());
        params.put(Constant.REQUEST_PARAMS_KEY_DEVICE_NAME, "deviceName");
        //always 1
        params.put(Constant.REQUEST_PARAMS_KEY_REGISTER_FROM, "1");
        params.put(Constant.REQUEST_PARAMS_KEY_DEVICE_IP, device.getInfo().getIp());
        params.put(Constant.REQUEST_PARAMS_KEY_COMMENT, device.getInfo().getChat_id().toString());

        showWaitProgress();
        HttpClient.get(params, String.class, new HttpClient.JsonResponseHandler<String>() {
            @Override
            public void onSuccess(String response) {
                dismissWaitProgress();

                Long deviceId = device.getInfo().getChat_id();
                File outputFile = MyApp.getApp().getPrivateFile(deviceId.toString(), Constant.CONFIG_FILE_SUFFIX);
                HttpClient.downloadFile(HttpClient.getDownloadConfigUrl(deviceId),
                        outputFile, new HttpClient.DownloadFileHandler() {
                            @Override
                            public void onFailure(Throwable throwable) {
                                Log.e(TAG, "下载主机配置文件失败，用新的配置文件上传服务器");
                            }

                            @Override
                            public void onSuccess(File file) {
                                Log.i(TAG, "下载主机配置文件成功，用新的配置文件上传服务器");
                                MyApp.getApp().getServerConfigManager().readFromFile();
                            }
                        });
            }

            @Override
            public void onFailure(Throwable throwable) {
                dismissWaitProgress();
            }
        });

    }

    synchronized private void addDevice(final Device device) {
        for (Device d : devices) {
            if (d.getInfo().getMac().equals(device.getInfo().getMac())) {
                return;
            }
        }
        devices.add(device);
        //adapter.notify

    }
}
