package ac.airconditionsuit.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;

import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.view.CommonTopBar;

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
                    //todo for zhulinan
                    Log.v(TAG, "scan qrcode success: " + data.getStringExtra("SCAN_RESULT"));
                    break;
            }

        }
    }
}
