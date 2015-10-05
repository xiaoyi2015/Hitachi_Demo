package ac.airconditionsuit.app.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/10/5.
 */
public class RoomAirSettingActivity extends BaseActivity{

    private MyOnClickListener myOnClickListener = new MyOnClickListener(){
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()){
                case R.id.left_icon:
                    finish();
                    break;

                case R.id.on_off_view:
                    break;
                case R.id.mode_view:
                    break;
                case R.id.wind_speed_view:
                    break;
                case R.id.temp_view:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_room_air_setting);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getIntent().getStringExtra("title"));
        commonTopBar.setIconView(myOnClickListener, null);
        TextView roomName = (TextView)findViewById(R.id.room_name);
        roomName.setText(getIntent().getStringExtra("title"));

        LinearLayout onOffView = (LinearLayout)findViewById(R.id.on_off_view);
        LinearLayout modeView = (LinearLayout)findViewById(R.id.mode_view);
        LinearLayout windSpeedView = (LinearLayout)findViewById(R.id.wind_speed_view);
        LinearLayout tempView = (LinearLayout)findViewById(R.id.temp_view);
        onOffView.setOnClickListener(myOnClickListener);
        modeView.setOnClickListener(myOnClickListener);
        windSpeedView.setOnClickListener(myOnClickListener);
        tempView.setOnClickListener(myOnClickListener);

    }
}
