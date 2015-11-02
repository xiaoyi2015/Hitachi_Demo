package ac.airconditionsuit.app.activity;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.network.HttpClient;
import ac.airconditionsuit.app.network.response.CommonResponse;
import ac.airconditionsuit.app.network.response.DeleteDeviceResponse;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.entity.Device;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.view.CommonButtonWithArrow;
import ac.airconditionsuit.app.view.CommonChooseView;
import ac.airconditionsuit.app.view.CommonModeArrowView;
import ac.airconditionsuit.app.view.CommonTopBar;
import com.android.volley.Response;
import com.loopj.android.http.RequestParams;

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
                    new AlertDialog.Builder(HostDeviceActivity.this).setTitle(R.string.tip).setMessage(R.string.is_delete_host_device).
                            setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//                                    final long chat_id = MyApp.getApp().getServerConfigManager().getConnections().get(0).getChat_id();
                                    showWaitProgress();
                                    MyApp.getApp().getServerConfigManager().deleteCurrentDevice(new HttpClient.JsonResponseHandler<DeleteDeviceResponse>() {

                                        @Override
                                        public void onSuccess(DeleteDeviceResponse response) {
                                            dismissWaitProgress();
                                            finish();
                                        }

                                        @Override
                                        public void onFailure(Throwable throwable) {
                                            dismissWaitProgress();
                                        }
                                    });
                                    dialog.dismiss();
                                }
                            }).setNegativeButton(R.string.cancel, null).setCancelable(false).show();
                    break;

                case R.id.scan_indoor_device:
                    if(MyApp.getApp().getUser().isAdmin()) {
                        shortStartActivity(SearchIndoorDeviceActivity.class);
                    }else{
                        MyApp.getApp().showToast("只有设备的管理者才能扫描室内机");
                        return;
                    }
                    break;

            }
        }
    };


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

        CommonModeArrowView qrCode = (CommonModeArrowView)findViewById(R.id.host_device_qr_code);
        qrCode.setOnClickListener(myOnClickListener);
        ImageView imageView1 = (ImageView)qrCode.findViewById(R.id.onoff);
        ImageView imageView2 = (ImageView)qrCode.findViewById(R.id.mode);
        ImageView imageView3 = (ImageView)qrCode.findViewById(R.id.wind);
        TextView textView = (TextView)qrCode.findViewById(R.id.temp);
        TextView textView1 = (TextView)qrCode.findViewById(R.id.label_text);
        ImageView imageView4 = (ImageView)qrCode.findViewById(R.id.temp_none);
        ImageView imageView5 = (ImageView)qrCode.findViewById(R.id.arrow_right);
        TextView manageLabelText = (TextView)findViewById(R.id.manager_label_text);
        TextView manageLabelText2 = (TextView)findViewById(R.id.manager_label_text2);

        CommonButtonWithArrow hostDeviceName = (CommonButtonWithArrow)findViewById(R.id.host_device_name);
        CommonButtonWithArrow hostDeviceIP = (CommonButtonWithArrow)findViewById(R.id.host_device_ip);
        CommonButtonWithArrow scanIndoorDevice = (CommonButtonWithArrow)findViewById(R.id.scan_indoor_device);
        LinearLayout deleteView = (LinearLayout)findViewById(R.id.delete_host_device_view);
        TextView deleteText = (TextView)deleteView.findViewById(R.id.label_text);
        deleteText.setTextColor(getResources().getColor(R.color.hit_heat_red));
        findViewById(R.id.delete_host_device).setOnClickListener(myOnClickListener);
        hostDeviceName.setOnlineTextView(MyApp.getApp().getServerConfigManager().getConnections().get(0).getName());
        hostDeviceIP.setOnlineTextView(MyApp.getApp().getServerConfigManager().getCurrentHostIP());
        scanIndoorDevice.setOnClickListener(myOnClickListener);
        scanIndoorDevice.setOnlineTextView(MyApp.getApp().getServerConfigManager().getDevices().size() + getString(R.string.device_symbol));

        if(MyApp.getApp().getUser().isAdmin()) {
            manageLabelText.setText(getString(R.string.manager_text));
            manageLabelText2.setVisibility(View.VISIBLE);
            qrCode.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
            imageView1.setVisibility(View.INVISIBLE);
            imageView2.setVisibility(View.INVISIBLE);
            imageView3.setVisibility(View.INVISIBLE);
            imageView4.setVisibility(View.VISIBLE);
            imageView4.setImageResource(R.drawable.common_2d_code);
            imageView5.setImageResource(R.drawable.icon_arrow_right_dc);
            textView1.setText(getString(R.string.qr_code));
            textView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);

        }else{
            manageLabelText.setText(getString(R.string.manager_text3));
            manageLabelText2.setVisibility(View.GONE);
            qrCode.setVisibility(View.INVISIBLE);
        }
    }
}
