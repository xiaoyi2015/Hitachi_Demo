package ac.airconditionsuit.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/9/18.
 */
public class AddDeviceActivity extends BaseActivity implements View.OnClickListener {

    static final private int REQUEST_QRCODE = 10086;

    private MyOnClickListener myOnClickListener = new MyOnClickListener(){
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.left_icon:
                    finish();
                    break;

                case R.id.scan_qrcode:
                    //TODO for lushixiong : import the zxing to gradle

                    //startActivityForResult(new Intent(AddDeviceActivity.this, CaptureActivity.class), REQUEST_QRCODE);
                    break;

            }

        }
    };
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
        commonTopBar.setIconView(myOnClickListener,null);
        findViewById(R.id.search_by_udp).setOnClickListener(this);
        findViewById(R.id.scan_qrcode).setOnClickListener(myOnClickListener);
    }

    @Override
    public void onClick(View v) {
        shortStartActivity(searchDeviceByUdpResultActivity.class);
    }
}
