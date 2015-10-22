package ac.airconditionsuit.app.activity;

import android.os.Bundle;
import android.view.View;

import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/10/22.
 */
public class RoomAirSettingHxActivity extends BaseActivity{

    private MyOnClickListener myOnClickListener = new MyOnClickListener() {
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.left_icon:
                    finish();
                    break;
            }

        }
    };

    private int on_off;
    private int mode;
    private int fan;
    private int temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_room_air_setting_hx);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getIntent().getStringExtra("title"));
        commonTopBar.setLeftIconView(R.drawable.top_bar_back_dc);
        commonTopBar.setIconView(myOnClickListener, null);
    }

}
