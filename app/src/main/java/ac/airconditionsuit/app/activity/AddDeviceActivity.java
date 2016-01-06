package ac.airconditionsuit.app.activity;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.entity.Device;
import ac.airconditionsuit.app.network.HttpClient;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.view.CommonTopBar;
import com.loopj.android.http.RequestParams;

import java.io.File;

/**
 * Created by Administrator on 2015/9/18.
 */
public class AddDeviceActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_FOR_SCAN_QRCODE = 1323;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_setting_add_device);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.add_device_title));
        switch (UIManager.UITYPE) {
            case 1:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_hit);
                break;
            case 2:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_dc);
                break;
            default:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_dc);
                break;
        }
        commonTopBar.setIconView(this, null);
        findViewById(R.id.search_by_udp).setOnClickListener(this);
        findViewById(R.id.scan_qrcode).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_icon:
                finish();
                break;

            case R.id.scan_qrcode:
                shortStartActivityForResult(CustomCaptureActivity.class, REQUEST_FOR_SCAN_QRCODE);
                break;

            case R.id.search_by_udp:
                shortStartActivity(SearchDeviceByUdpResultActivity.class);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_FOR_SCAN_QRCODE:
                    String scan_result = data.getStringExtra("SCAN_RESULT");
                    Log.v(TAG, "scan qrcode success: " + scan_result);
                    try {
                        joinDevice(Device.QRCode.decodeFromJson(scan_result));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "qrcode format is not json");
                        MyApp.getApp().showToast("二维码格式错误！");
                    }
                    break;
            }

        }
    }

    private void joinDevice(final Device.QRCode qrCode) {
        RequestParams params = new RequestParams();
        params.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_CHAT);
        params.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_TYPE_APPLY_JOIN);


        long chat_id = qrCode.getChat_id();
        String t = qrCode.getT();
        if (chat_id == -1 || t == null) {
            MyApp.getApp().showToast("二维码格式错误！");
            return;
        }
        params.put(Constant.REQUEST_PARAMS_KEY_CHAT_ID, chat_id);
        params.put(Constant.REQUEST_PARAMS_KEY_T, t);

        showWaitProgress();
        HttpClient.get(params, String.class, new HttpClient.JsonResponseHandler<String>() {
            @Override
            public void onSuccess(String response) {
                dismissWaitProgress();

                final Long deviceId = qrCode.getChat_id();
                final File outputFile = MyApp.getApp().getPrivateFile(deviceId.toString(), Constant.CONFIG_FILE_SUFFIX);
                HttpClient.downloadFile(HttpClient.getDownloadConfigUrl(deviceId, qrCode.getCreator_cust_id()),
                        outputFile, new HttpClient.DownloadFileHandler() {
                            @Override
                            public void onFailure(Throwable throwable) {
                                Log.e(TAG, "下载主机配置文件失败，用新的配置文件上传服务器");
                                MyApp.getApp().getLocalConfigManager().updateCurrentServerConfigFile(deviceId + Constant.CONFIG_FILE_SUFFIX);
                                MyApp.getApp().getServerConfigManager().readFromFile();
                                Device device = new Device(qrCode);
                                MyApp.getApp().getServerConfigManager().setCurrentDevice(device);
                                MyApp.getApp().getServerConfigManager().getHome().setName(qrCode.getHome());
                                MyApp.getApp().getSocketManager().close();
                                MyApp.getApp().getSocketManager().reconnectSocket();

                                Intent intent = new Intent();
                                intent.setClass(AddDeviceActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                shortStartActivity(intent);
                            }

                            @Override
                            public void onSuccess(File file) {
                                Log.i(TAG, "下载主机配置文件成功");
                                MyApp.getApp().getLocalConfigManager().updateCurrentServerConfigFile(outputFile.getName());
                                MyApp.getApp().getServerConfigManager().readFromFile();
                                Device device = new Device(qrCode);
                                MyApp.getApp().getServerConfigManager().setCurrentDevice(device);
                                MyApp.getApp().getServerConfigManager().getHome().setName(qrCode.getHome());
                                MyApp.getApp().getSocketManager().close();
                                MyApp.getApp().getSocketManager().reconnectSocket();

                                Intent intent = new Intent();
                                intent.setClass(AddDeviceActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                shortStartActivity(intent);
                            }
                        });
            }

            @Override
            public void onFailure(Throwable throwable) {
                dismissWaitProgress();
            }
        });

    }
}
