package ac.airconditionsuit.app.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ac.airconditionsuit.app.R;
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

    private void changeColorAndSetting( int on_off, int mode, int fan, int temp) {
        roomTemp.setText(temp + getString(R.string.temp_symbol));
        if(on_off == 0){
            bgBar1.setImageResource(R.drawable.dc_bg_bar_off);
            bgBar2.setImageResource(R.drawable.dc_bg_bar_off);
            bgBar3.setImageResource(R.drawable.dc_bg_bar_off);
            bgBar4.setImageResource(R.drawable.dc_bg_bar_off);
            bgBar5.setImageResource(R.drawable.dc_bg_bar_off);
            realTemp.setTextColor(getResources().getColor(R.color.text_normal_color));
            roomOnOff.setImageResource(R.drawable.dc_onoff_off);
            roomTemp.setTextColor(getResources().getColor(R.color.text_normal_color));

            switch (mode){
                case 0:
                    roomMode.setImageResource(R.drawable.dc_cool_off);
                    roomModeText.setText(R.string.cool);
                    break;
                case 1:
                    roomMode.setImageResource(R.drawable.dc_heat_off);
                    roomModeText.setText(R.string.heat);
                    break;
                case 2:
                    roomMode.setImageResource(R.drawable.dc_dry_off);
                    roomModeText.setText(R.string.dry);
                    break;
                case 3:
                    roomMode.setImageResource(R.drawable.dc_fan_off);
                    roomModeText.setText(R.string.wind);
                    break;
            }
            switch (fan){
                case 0:
                    roomWindSpeed.setImageResource(R.drawable.dc_fan_off1);
                    roomWindText.setText(R.string.low_wind);
                    break;
                case 1:
                    roomWindSpeed.setImageResource(R.drawable.dc_fan_off3);
                    roomWindText.setText(R.string.medium_wind);
                    break;
                case 2:
                    roomWindSpeed.setImageResource(R.drawable.dc_fan_off5);
                    roomWindText.setText(R.string.high_wind);
                    break;
            }

        }else{
            switch (mode){
                case 0:
                    roomMode.setImageResource(R.drawable.dc_cool_on);
                    roomModeText.setText(R.string.cool);
                    bgBar1.setImageResource(R.drawable.dc_bg_bar_cool);
                    bgBar2.setImageResource(R.drawable.dc_bg_bar_cool);
                    bgBar3.setImageResource(R.drawable.dc_bg_bar_cool);
                    bgBar4.setImageResource(R.drawable.dc_bg_bar_cool);
                    bgBar5.setImageResource(R.drawable.dc_bg_bar_cool);
                    realTemp.setTextColor(getResources().getColor(R.color.mode_cool_blue));
                    roomOnOff.setImageResource(R.drawable.dc_onoff_cool);
                    roomTemp.setTextColor(getResources().getColor(R.color.mode_cool_blue));

                    switch (fan){
                        case 0:
                            roomWindSpeed.setImageResource(R.drawable.dc_fan_cool1);
                            roomWindText.setText(R.string.low_wind);
                            break;
                        case 1:
                            roomWindSpeed.setImageResource(R.drawable.dc_fan_cool3);
                            roomWindText.setText(R.string.medium_wind);
                            break;
                        case 2:
                            roomWindSpeed.setImageResource(R.drawable.dc_fan_cool5);
                            roomWindText.setText(R.string.high_wind);
                            break;
                    }
                    break;
                case 1:
                    roomMode.setImageResource(R.drawable.dc_heat_on);
                    roomModeText.setText(R.string.heat);
                    bgBar1.setImageResource(R.drawable.dc_bg_bar_heat);
                    bgBar2.setImageResource(R.drawable.dc_bg_bar_heat);
                    bgBar3.setImageResource(R.drawable.dc_bg_bar_heat);
                    bgBar4.setImageResource(R.drawable.dc_bg_bar_heat);
                    bgBar5.setImageResource(R.drawable.dc_bg_bar_heat);
                    realTemp.setTextColor(getResources().getColor(R.color.mode_heat_pink));
                    roomOnOff.setImageResource(R.drawable.dc_onoff_heat);
                    roomTemp.setTextColor(getResources().getColor(R.color.mode_heat_pink));

                    switch (fan){
                        case 0:
                            roomWindSpeed.setImageResource(R.drawable.dc_fan_heat1);
                            roomWindText.setText(R.string.low_wind);
                            break;
                        case 1:
                            roomWindSpeed.setImageResource(R.drawable.dc_fan_heat3);
                            roomWindText.setText(R.string.medium_wind);
                            break;
                        case 2:
                            roomWindSpeed.setImageResource(R.drawable.dc_fan_heat5);
                            roomWindText.setText(R.string.high_wind);
                            break;
                    }
                    break;

                case 2:
                    roomMode.setImageResource(R.drawable.dc_dry_on);
                    roomModeText.setText(R.string.dry);
                    bgBar1.setImageResource(R.drawable.dc_bg_bar_dry);
                    bgBar2.setImageResource(R.drawable.dc_bg_bar_dry);
                    bgBar3.setImageResource(R.drawable.dc_bg_bar_dry);
                    bgBar4.setImageResource(R.drawable.dc_bg_bar_dry);
                    bgBar5.setImageResource(R.drawable.dc_bg_bar_dry);
                    realTemp.setTextColor(getResources().getColor(R.color.mode_dry_purple));
                    roomOnOff.setImageResource(R.drawable.dc_onoff_dry);
                    roomTemp.setTextColor(getResources().getColor(R.color.mode_dry_purple));

                    switch (fan){
                        case 0:
                            roomWindSpeed.setImageResource(R.drawable.dc_fan_dry1);
                            roomWindText.setText(R.string.low_wind);
                            break;
                        case 1:
                            roomWindSpeed.setImageResource(R.drawable.dc_fan_dry3);
                            roomWindText.setText(R.string.medium_wind);
                            break;
                        case 2:
                            roomWindSpeed.setImageResource(R.drawable.dc_fan_dry5);
                            roomWindText.setText(R.string.high_wind);
                            break;
                    }
                    break;
                case 3:
                    roomMode.setImageResource(R.drawable.dc_fan_on);
                    roomModeText.setText(R.string.wind);
                    bgBar1.setImageResource(R.drawable.dc_bg_bar_fan);
                    bgBar2.setImageResource(R.drawable.dc_bg_bar_fan);
                    bgBar3.setImageResource(R.drawable.dc_bg_bar_fan);
                    bgBar4.setImageResource(R.drawable.dc_bg_bar_fan);
                    bgBar5.setImageResource(R.drawable.dc_bg_bar_fan);
                    realTemp.setTextColor(getResources().getColor(R.color.mode_fan_green));
                    roomOnOff.setImageResource(R.drawable.dc_onoff_fan);
                    roomTemp.setTextColor(getResources().getColor(R.color.mode_fan_green));

                    switch (fan){
                        case 0:
                            roomWindSpeed.setImageResource(R.drawable.dc_fan_fan1);
                            roomWindText.setText(R.string.low_wind);
                            break;
                        case 1:
                            roomWindSpeed.setImageResource(R.drawable.dc_fan_fan3);
                            roomWindText.setText(R.string.medium_wind);
                            break;
                        case 2:
                            roomWindSpeed.setImageResource(R.drawable.dc_fan_fan5);
                            roomWindText.setText(R.string.high_wind);
                            break;
                    }
                    break;
            }
        }
    }

}
