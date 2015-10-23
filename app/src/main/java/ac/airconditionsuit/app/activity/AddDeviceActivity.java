package ac.airconditionsuit.app.activity;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.entity.Device;
import ac.airconditionsuit.app.network.HttpClient;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;

import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.view.CommonTopBar;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.security.PrivilegedAction;

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
        commonTopBar.setTitle(getString(R.string.add_device));
        switch (UIManager.UITYPE){
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
        commonTopBar.setIconView(this,null);
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
                shortStartActivity(searchDeviceByUdpResultActivity.class);
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
                    joinDevice(Device.QRCode.decodeFromJson(scan_result));
                    break;
            }

        }
    }

    private void joinDevice(final Device.QRCode qrCode) {
        RequestParams params = new RequestParams();
        params.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_CHAT);
        params.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_TYPE_APPLY_JOIN);


        String chat_id = qrCode.getChat_id();
        String t = qrCode.getT();
        if (chat_id == null || t == null) {
            MyApp.getApp().showToast("二维码格式错误！");
            return;
        }
        params.put(Constant.REQUEST_PARAMS_KEY_CHAT_ID, chat_id.toString());
        params.put(Constant.REQUEST_PARAMS_KEY_T, t);

        showWaitProgress();
        HttpClient.get(params, String.class, new HttpClient.JsonResponseHandler<String>() {
            @Override
            public void onSuccess(String response) {
                dismissWaitProgress();

                String deviceId = qrCode.getChat_id();
                File outputFile = MyApp.getApp().getPrivateFile(deviceId, Constant.CONFIG_FILE_SUFFIX);
                HttpClient.downloadFile(HttpClient.getDownloadConfigUrl(Long.parseLong(deviceId)),
                        outputFile, new HttpClient.DownloadFileHandler() {
                            @Override
                            public void onFailure(Throwable throwable) {
                                Log.e(TAG, "下载主机配置文件失败，用新的配置文件上传服务器");
                            }

                            @Override
                            public void onSuccess(File file) {
                                Log.i(TAG, "下载主机配置文件成功，用新的配置文件上传服务器");
                                Intent intent = new Intent();
                                intent.setClass(AddDeviceActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                                MyApp.getApp().getServerConfigManager().readFromFile();
                            }
                        });
            }

            @Override
            public void onFailure(Throwable throwable) {
                dismissWaitProgress();
                MyApp.getApp().showToast("加入设备失败！");
            }
        });

    }
}
