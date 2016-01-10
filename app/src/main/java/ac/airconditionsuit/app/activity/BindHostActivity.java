package ac.airconditionsuit.app.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import java.io.File;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.entity.Device;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.network.HttpClient;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/10/22.
 */
public class BindHostActivity extends BaseActivity {

    private MyOnClickListener myOnClickListener = new MyOnClickListener() {
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.left_icon:
                    finish();
                    break;
                case R.id.right_icon:
                    bindDevice(device);
                    break;
            }
        }
    };
    private TextView changeName;
    private Device device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_bind_host);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.bind_device));
        device = new Gson().fromJson(getIntent().getStringExtra("device"), Device.class);

        switch (UIManager.UITYPE) {
            case 1:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_hit);
                commonTopBar.setRightIconView(R.drawable.top_bar_save_hit);
                break;
            case 2:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_dc);
                commonTopBar.setRightIconView(R.drawable.top_bar_save_dc);
                break;
            default:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_dc);
                commonTopBar.setRightIconView(R.drawable.top_bar_save_dc);
                break;
        }
        commonTopBar.setIconView(myOnClickListener, myOnClickListener);
        changeName = (TextView) findViewById(R.id.host_name);
        changeName.setText(device.getInfo().getName());

    }

    /**
     * 这个方法用来绑定主机，这个方法中，我是测试用的。可以复制到用户确定绑定的地方调用。
     *
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
        params.put(Constant.REQUEST_PARAMS_KEY_DEVICE_NAME, changeName.getText().toString());
        //always 1
        params.put(Constant.REQUEST_PARAMS_KEY_REGISTER_FROM, "1");
        params.put(Constant.REQUEST_PARAMS_KEY_DEVICE_IP, device.getInfo().getIp());
        params.put(Constant.REQUEST_PARAMS_KEY_COMMENT, device.getInfo().getChat_id().toString());

        showWaitProgress();
        HttpClient.get(params, String.class, new HttpClient.JsonResponseHandler<String>() {
            @Override
            public void onSuccess(String response) {
                dismissWaitProgress();

                final Long deviceId = device.getInfo().getChat_id();
                final File outputFile = MyApp.getApp().getPrivateFile(deviceId.toString(), Constant.CONFIG_FILE_SUFFIX);

                MyApp.getApp().getLocalConfigManager().updateCurrentServerConfigFile(outputFile.getName());
                MyApp.getApp().getServerConfigManager().setConfigFileAbsolutePath(outputFile.getAbsolutePath());
                MyApp.getApp().getServerConfigManager().deleteDeviceLocal();
                device.getInfo().setName(changeName.getText().toString());
                MyApp.getApp().getServerConfigManager().setCurrentDevice(device);
                MyApp.getApp().getServerConfigManager().writeToFile(true);

                MyApp.getApp().getSocketManager().close();
                MyApp.getApp().getSocketManager().reconnectSocket();

                new AlertDialog.Builder(BindHostActivity.this).setTitle(R.string.tip_bind_hostdevice_ok).setMessage(R.string.is_search_air_condition).
                        setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                shortStartActivity(SearchIndoorDeviceActivity.class,"first","yes");
                                dialog.dismiss();
                                finish();
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setClass(BindHostActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        shortStartActivity(intent);
                        finish();
                    }
                }).setCancelable(false).show();


//                HttpClient.downloadFile(HttpClient.getDownloadConfigUrl(deviceId),
//                        outputFile, new HttpClient.DownloadFileHandler() {
//                            @Override
//                            public void onFailure(Throwable throwable) {
//                                Log.e(TAG, "下载主机配置文件失败，用新的配置文件上传服务器");
//                                MyApp.getApp().getLocalConfigManager().updateCurrentServerConfigFile(deviceId + Constant.CONFIG_FILE_SUFFIX);
//                                MyApp.getApp().getServerConfigManager().readFromFile();
//                                device.getInfo().setName(changeName.getText().toString());
//                                MyApp.getApp().getServerConfigManager().setCurrentDevice(device);
//                                MyApp.getApp().getSocketManager().reconnectSocket();
//
//
//                                new AlertDialog.Builder(BindHostActivity.this).setTitle(R.string.tip_bind_hostdevice_ok).setMessage(R.string.is_search_air_condition).
//                                        setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                shortStartActivity(SearchIndoorDeviceActivity.class);
//                                                dialog.dismiss();
//                                                finish();
//                                            }
//                                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        Intent intent = new Intent();
//                                        intent.setClass(BindHostActivity.this, MainActivity.class);
//                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                        shortStartActivity(intent);
//                                        finish();
//                                    }
//                                }).setCancelable(false).show();
//                            }
//
//                            @Override
//                            public void onSuccess(File file) {
//                Log.i(TAG, "下载主机配置文件成功，用新的配置文件上传服务器");
//                MyApp.getApp().getLocalConfigManager().updateCurrentServerConfigFile(outputFile.getName());
//                MyApp.getApp().getServerConfigManager().readFromFile();
//                device.getInfo().setName(changeName.getText().toString());
//                MyApp.getApp().getServerConfigManager().setCurrentDevice(device);
//                MyApp.getApp().getSocketManager().reconnectSocket();
//
//                new AlertDialog.Builder(BindHostActivity.this).setTitle(R.string.tip_bind_hostdevice_ok).setMessage(R.string.is_search_air_condition).
//                        setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                shortStartActivity(SearchIndoorDeviceActivity.class,"first","yes");
//                                dialog.dismiss();
//                                finish();
//                            }
//                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent();
//                        intent.setClass(BindHostActivity.this, MainActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        shortStartActivity(intent);
//                        finish();
//                    }
//                }).setCancelable(false).show();
//                           }
//                        });
            }

            @Override
            public void onFailure(Throwable throwable) {
                dismissWaitProgress();
            }
        });

    }
}
