package ac.airconditionsuit.app.activity;

import ac.airconditionsuit.app.network.HttpClient;
import ac.airconditionsuit.app.network.response.DeleteDeviceResponse;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.view.CommonButtonWithArrow;
import ac.airconditionsuit.app.view.CommonModeArrowView;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/10/20.
 */
public class HostDeviceActivity extends BaseActivity{

    private MyOnClickListener myOnClickListener = new MyOnClickListener() {
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.left_icon:
                    finish();
                    break;

                case R.id.host_device_qr_code:
                    shortStartActivity(QRCodeActivity.class);
                    break;

                case R.id.delete_host_device:
                    if(MyApp.getApp().getUser().isAdmin()) {
                        new AlertDialog.Builder(HostDeviceActivity.this).setTitle(R.string.tip).setMessage(R.string.is_delete_host_device).
                                setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // final long chat_id = MyApp.getApp().getServerConfigManager().getConnections().get(0).getChat_id();
                                        deleteDevice();
                                    }
                                }).setNegativeButton(R.string.cancel, null).setCancelable(false).show();
                    }else{
                        new AlertDialog.Builder(HostDeviceActivity.this).setTitle(R.string.tip).setMessage(R.string.is_delete_host_device2).
                                setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // final long chat_id = MyApp.getApp().getServerConfigManager().getConnections().get(0).getChat_id();
                                        deleteDevice();
                                    }
                                }).setNegativeButton(R.string.cancel, null).setCancelable(false).show();
                    }
                    break;

                case R.id.scan_indoor_device:
                    shortStartActivityForResult(SearchIndoorDeviceActivity.class, 1234);
                    break;

            }
        }
    };
    private CommonButtonWithArrow scanIndoorDevice;

    private void deleteDevice() {
        showWaitProgress();
        MyApp.getApp().getServerConfigManager().deleteCurrentDevice(new HttpClient.JsonResponseHandler<DeleteDeviceResponse>() {

            @Override
            public void onSuccess(DeleteDeviceResponse response) {
                MyApp.getApp().getLocalConfigManager().currentHomeDeleteDevice();
                dismissWaitProgress();
                finish();
            }

            @Override
            public void onFailure(Throwable throwable) {
                dismissWaitProgress();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_host_device);
        super.onCreate(savedInstanceState);

        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.device_manage));
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
        commonTopBar.setIconView(myOnClickListener, null);

        RelativeLayout qrCode = (RelativeLayout)findViewById(R.id.host_device_qr_code);
        qrCode.setOnClickListener(myOnClickListener);
        ImageView imageView1 = (ImageView)qrCode.findViewById(R.id.onoff);
        ImageView imageView2 = (ImageView)qrCode.findViewById(R.id.mode);
        ImageView imageView3 = (ImageView)qrCode.findViewById(R.id.wind);
        TextView textView = (TextView)qrCode.findViewById(R.id.temp);
        TextView textView1 = (TextView)qrCode.findViewById(R.id.label_text);
        ImageView imageView4 = (ImageView)qrCode.findViewById(R.id.temp_none);
        ImageView imageView5 = (ImageView)qrCode.findViewById(R.id.arrow_right);
        TextView manageLabelText = (TextView)findViewById(R.id.manager_label_text);

        CommonButtonWithArrow hostDeviceName = (CommonButtonWithArrow)findViewById(R.id.host_device_name);
        CommonButtonWithArrow hostDeviceIP = (CommonButtonWithArrow)findViewById(R.id.host_device_ip);
        scanIndoorDevice = (CommonButtonWithArrow)findViewById(R.id.scan_indoor_device);
        LinearLayout deleteView = (LinearLayout)findViewById(R.id.delete_host_device_view);
        TextView deleteText = (TextView)deleteView.findViewById(R.id.label_text);
        deleteText.setTextColor(getResources().getColor(R.color.hit_heat_red));
        findViewById(R.id.delete_host_device).setOnClickListener(myOnClickListener);
        hostDeviceName.setOnlineTextView(MyApp.getApp().getServerConfigManager().getConnections().get(0).getName());
        hostDeviceIP.setOnlineTextView(MyApp.getApp().getServerConfigManager().getCurrentHostIP());
        scanIndoorDevice.setOnClickListener(myOnClickListener);
        scanIndoorDevice.setOnlineTextView(MyApp.getApp().getServerConfigManager().getDevices_new().size() + getString(R.string.device_symbol));

        if(MyApp.getApp().getUser().isAdmin()) {
            manageLabelText.setText(getString(R.string.manager_text));
            qrCode.setVisibility(View.VISIBLE);
        }else{
            manageLabelText.setText(getString(R.string.manager_text3));
            qrCode.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            switch (requestCode) {
                case 1234:
                    scanIndoorDevice.setOnlineTextView(MyApp.getApp().getServerConfigManager().getDevices_new().size() + getString(R.string.device_symbol));
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
