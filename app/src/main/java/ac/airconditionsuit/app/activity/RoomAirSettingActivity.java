package ac.airconditionsuit.app.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/10/5.
 */
public class RoomAirSettingActivity extends BaseActivity{

    private int on_off;
    private int mode;
    private int fan;
    private int temp;

    private MyOnClickListener myOnClickListener = new MyOnClickListener(){
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()){
                case R.id.left_icon:
                    finish();
                    break;
                //TODO change color and status, write to file
                case R.id.on_off_view:
                    on_off ++;
                    if(on_off > 1){
                        on_off = on_off - 2;
                    }
                    changeColorAndSetting(on_off,mode,fan,temp);
                    break;
                case R.id.mode_view:
                    mode ++;
                    if(mode > 3){
                        mode = mode - 4;
                    }
                    changeColorAndSetting(on_off,mode,fan,temp);
                    break;
                case R.id.wind_speed_view:
                    fan ++;
                    if(fan > 2){
                        fan = fan - 3;
                    }
                    changeColorAndSetting(on_off,mode,fan,temp);
                    break;
                case R.id.decrease_temp:
                    if(temp>18 && temp<=30){
                        temp --;
                        changeColorAndSetting(on_off,mode,fan,temp);
                    }
                    break;
                case R.id.increase_temp:
                    if(temp>=18 && temp<30){
                        temp ++;
                        changeColorAndSetting(on_off,mode,fan,temp);
                    }
                    break;
            }
        }
    };
    private ImageView bgBar1;
    private ImageView bgBar2;
    private ImageView bgBar3;
    private ImageView bgBar4;
    private ImageView bgBar5;
    private TextView realTemp;
    private ImageView roomMode;
    private ImageView roomWindSpeed;
    private TextView roomTemp;
    private TextView roomModeText;
    private TextView roomWindText;
    private ImageView roomOnOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_room_air_setting);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getIntent().getStringExtra("title"));
        commonTopBar.setLeftIconView(R.drawable.top_bar_back_dc);
        commonTopBar.setIconView(myOnClickListener, null);
        TextView roomName = (TextView)findViewById(R.id.room_name);
        roomName.setText(getIntent().getStringExtra("title"));

        LinearLayout onOffView = (LinearLayout)findViewById(R.id.on_off_view);
        LinearLayout modeView = (LinearLayout)findViewById(R.id.mode_view);
        LinearLayout windSpeedView = (LinearLayout)findViewById(R.id.wind_speed_view);
        ImageView tempDecrease = (ImageView)findViewById(R.id.decrease_temp);
        ImageView tempIncrease = (ImageView)findViewById(R.id.increase_temp);
        onOffView.setOnClickListener(myOnClickListener);
        modeView.setOnClickListener(myOnClickListener);
        windSpeedView.setOnClickListener(myOnClickListener);
        tempDecrease.setOnClickListener(myOnClickListener);
        tempIncrease.setOnClickListener(myOnClickListener);

        bgBar1 =(ImageView)findViewById(R.id.bg_bar1);
        bgBar2 =(ImageView)findViewById(R.id.bg_bar2);
        bgBar3 =(ImageView)findViewById(R.id.bg_bar3);
        bgBar4 =(ImageView)findViewById(R.id.bg_bar4);
        bgBar5 =(ImageView)findViewById(R.id.bg_bar5);

        realTemp = (TextView)findViewById(R.id.real_temp);
        roomMode = (ImageView)findViewById(R.id.mode);
        roomWindSpeed = (ImageView)findViewById(R.id.wind_speed);
        roomTemp = (TextView)findViewById(R.id.temp_number);
        roomModeText = (TextView)findViewById(R.id.mode_text);
        roomWindText = (TextView)findViewById(R.id.wind_text);
        roomOnOff = (ImageView)findViewById(R.id.on_off);

        init();

    }

    private void init() {
        on_off = 0;
        mode = 0;
        fan = 0;
        temp = 25;
        changeColorAndSetting(on_off,mode,fan,temp);
    }

    private void changeColorAndSetting( int temp_on_off, int temp_mode, int temp_fan, int temp_temp) {
        roomTemp.setText(temp_temp + getString(R.string.temp_symbol));
        if(temp_on_off == 0){
            bgBar1.setImageResource(R.drawable.room_bg_bar_off_dc);
            bgBar2.setImageResource(R.drawable.room_bg_bar_off_dc);
            bgBar3.setImageResource(R.drawable.room_bg_bar_off_dc);
            bgBar4.setImageResource(R.drawable.room_bg_bar_off_dc);
            bgBar5.setImageResource(R.drawable.room_bg_bar_off_dc);
            realTemp.setTextColor(getResources().getColor(R.color.text_normal_color));
            roomOnOff.setImageResource(R.drawable.onoff_off_dc);
            roomTemp.setTextColor(getResources().getColor(R.color.text_normal_color));

            switch (temp_mode){
                case 0:
                    roomMode.setImageResource(R.drawable.cool_off_dc);
                    roomModeText.setText(R.string.cool);
                    break;
                case 1:
                    roomMode.setImageResource(R.drawable.heat_off_dc);
                    roomModeText.setText(R.string.heat);
                    break;
                case 2:
                    roomMode.setImageResource(R.drawable.dry_off_dc);
                    roomModeText.setText(R.string.dry);
                    break;
                case 3:
                    roomMode.setImageResource(R.drawable.fan_off_dc);
                    roomModeText.setText(R.string.wind);
                    break;
            }
            switch (temp_fan){
                case 0:
                    roomWindSpeed.setImageResource(R.drawable.fan_off1_dc);
                    roomWindText.setText(R.string.low_wind);
                    break;
                case 1:
                    roomWindSpeed.setImageResource(R.drawable.fan_off3_dc);
                    roomWindText.setText(R.string.medium_wind);
                    break;
                case 2:
                    roomWindSpeed.setImageResource(R.drawable.fan_off5_dc);
                    roomWindText.setText(R.string.high_wind);
                    break;
            }

        }else{
            switch (temp_mode){
                case 0:
                    roomMode.setImageResource(R.drawable.cool_on_dc);
                    roomModeText.setText(R.string.cool);
                    bgBar1.setImageResource(R.drawable.room_bg_bar_cool_dc);
                    bgBar2.setImageResource(R.drawable.room_bg_bar_cool_dc);
                    bgBar3.setImageResource(R.drawable.room_bg_bar_cool_dc);
                    bgBar4.setImageResource(R.drawable.room_bg_bar_cool_dc);
                    bgBar5.setImageResource(R.drawable.room_bg_bar_cool_dc);
                    realTemp.setTextColor(getResources().getColor(R.color.mode_cool_blue));
                    roomOnOff.setImageResource(R.drawable.onoff_cool_dc);
                    roomTemp.setTextColor(getResources().getColor(R.color.mode_cool_blue));

                    switch (temp_fan){
                        case 0:
                            roomWindSpeed.setImageResource(R.drawable.fan_cool1_dc);
                            roomWindText.setText(R.string.low_wind);
                            break;
                        case 1:
                            roomWindSpeed.setImageResource(R.drawable.fan_cool3_dc);
                            roomWindText.setText(R.string.medium_wind);
                            break;
                        case 2:
                            roomWindSpeed.setImageResource(R.drawable.fan_cool5_dc);
                            roomWindText.setText(R.string.high_wind);
                            break;
                    }
                    break;
                case 1:
                    roomMode.setImageResource(R.drawable.heat_on_dc);
                    roomModeText.setText(R.string.heat);
                    bgBar1.setImageResource(R.drawable.room_bg_bar_heat_dc);
                    bgBar2.setImageResource(R.drawable.room_bg_bar_heat_dc);
                    bgBar3.setImageResource(R.drawable.room_bg_bar_heat_dc);
                    bgBar4.setImageResource(R.drawable.room_bg_bar_heat_dc);
                    bgBar5.setImageResource(R.drawable.room_bg_bar_heat_dc);
                    realTemp.setTextColor(getResources().getColor(R.color.mode_heat_pink));
                    roomOnOff.setImageResource(R.drawable.onoff_heat_dc);
                    roomTemp.setTextColor(getResources().getColor(R.color.mode_heat_pink));

                    switch (temp_fan){
                        case 0:
                            roomWindSpeed.setImageResource(R.drawable.fan_heat1_dc);
                            roomWindText.setText(R.string.low_wind);
                            break;
                        case 1:
                            roomWindSpeed.setImageResource(R.drawable.fan_heat3_dc);
                            roomWindText.setText(R.string.medium_wind);
                            break;
                        case 2:
                            roomWindSpeed.setImageResource(R.drawable.fan_heat5_dc);
                            roomWindText.setText(R.string.high_wind);
                            break;
                    }
                    break;

                case 2:
                    roomMode.setImageResource(R.drawable.dry_on_dc);
                    roomModeText.setText(R.string.dry);
                    bgBar1.setImageResource(R.drawable.room_bg_bar_dry_dc);
                    bgBar2.setImageResource(R.drawable.room_bg_bar_dry_dc);
                    bgBar3.setImageResource(R.drawable.room_bg_bar_dry_dc);
                    bgBar4.setImageResource(R.drawable.room_bg_bar_dry_dc);
                    bgBar5.setImageResource(R.drawable.room_bg_bar_dry_dc);
                    realTemp.setTextColor(getResources().getColor(R.color.mode_dry_purple));
                    roomOnOff.setImageResource(R.drawable.onoff_dry_dc);
                    roomTemp.setTextColor(getResources().getColor(R.color.mode_dry_purple));

                    switch (temp_fan){
                        case 0:
                            roomWindSpeed.setImageResource(R.drawable.fan_dry1_dc);
                            roomWindText.setText(R.string.low_wind);
                            break;
                        case 1:
                            roomWindSpeed.setImageResource(R.drawable.fan_dry3_dc);
                            roomWindText.setText(R.string.medium_wind);
                            break;
                        case 2:
                            roomWindSpeed.setImageResource(R.drawable.fan_dry5_dc);
                            roomWindText.setText(R.string.high_wind);
                            break;
                    }
                    break;
                case 3:
                    roomMode.setImageResource(R.drawable.fan_on_dc);
                    roomModeText.setText(R.string.wind);
                    bgBar1.setImageResource(R.drawable.room_bg_bar_fan_dc);
                    bgBar2.setImageResource(R.drawable.room_bg_bar_fan_dc);
                    bgBar3.setImageResource(R.drawable.room_bg_bar_fan_dc);
                    bgBar4.setImageResource(R.drawable.room_bg_bar_fan_dc);
                    bgBar5.setImageResource(R.drawable.room_bg_bar_fan_dc);
                    realTemp.setTextColor(getResources().getColor(R.color.mode_fan_green));
                    roomOnOff.setImageResource(R.drawable.onoff_fan_dc);
                    roomTemp.setTextColor(getResources().getColor(R.color.mode_fan_green));

                    switch (temp_fan){
                        case 0:
                            roomWindSpeed.setImageResource(R.drawable.fan_fan1_dc);
                            roomWindText.setText(R.string.low_wind);
                            break;
                        case 1:
                            roomWindSpeed.setImageResource(R.drawable.fan_fan3_dc);
                            roomWindText.setText(R.string.medium_wind);
                            break;
                        case 2:
                            roomWindSpeed.setImageResource(R.drawable.fan_fan5_dc);
                            roomWindText.setText(R.string.high_wind);
                            break;
                    }
                    break;
            }
        }
    }

}
