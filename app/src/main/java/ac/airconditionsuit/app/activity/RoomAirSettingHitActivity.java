package ac.airconditionsuit.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.aircondition.AirConditionControl;
import ac.airconditionsuit.app.entity.AirCondition;
import ac.airconditionsuit.app.entity.Room;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/10/16.
 */
public class RoomAirSettingHitActivity extends BaseActivity{

    private MyOnClickListener myOnClickListener = new MyOnClickListener() {
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.left_icon:
                    finish();
                    break;

                case R.id.temp_decrease:
                    if(mode == 1){
                        if(temp>17 && temp<=30){
                            temp --;
                            changeTemp(temp);
                        }
                    }else{
                        if(temp>19 && temp <= 30){
                            temp --;
                            changeTemp(temp);
                        }
                    }
                    break;

                case R.id.temp_increase:
                    if(mode == 1){
                        if(temp>=17 && temp<30){
                            temp ++;
                            changeTemp(temp);
                        }
                    }else{
                        if(temp>=19 && temp < 30){
                            temp ++;
                            changeTemp(temp);
                        }
                    }
                    break;

                case R.id.mode_cool:
                    mode = 0;
                    tempSeekBar.setMax(11);
                    tempSeekBar.setProgress(30);
                    changeIconColor(on_off, mode, fan);
                    break;

                case R.id.mode_heat:
                    mode = 1;
                    tempSeekBar.setMax(13);
                    tempSeekBar.setProgress(30);
                    changeIconColor(on_off,mode,fan);
                    break;

                case R.id.mode_dry:
                    mode = 2;
                    tempSeekBar.setMax(11);
                    tempSeekBar.setProgress(30);
                    changeIconColor(on_off,mode,fan);
                    break;

                case R.id.mode_fan:
                    mode = 3;
                    tempSeekBar.setMax(11);
                    tempSeekBar.setProgress(30);
                    changeIconColor(on_off,mode,fan);
                    break;

                case R.id.fan_fan1:
                    fan = 0;
                    changeIconColor(on_off,mode,fan);
                    break;

                case R.id.fan_fan3:
                    fan = 1;
                    changeIconColor(on_off,mode,fan);
                    break;

                case R.id.fan_fan5:
                    fan = 2;
                    changeIconColor(on_off,mode,fan);
                    break;

                case R.id.set_onoff:
                    if(on_off == 0){
                        on_off = 1;
                    }else {
                        on_off = 0;
                    }
                    changeIconColor(on_off,mode,fan);
                    break;

                case R.id.ok_button:
                    submit();
                    finish();
                    break;

            }
        }
    };

    private int on_off;
    private int mode;
    private int fan;
    private int temp;

    private SeekBar tempSeekBar;
    private TextView roomTemp;
    private ImageView setCool;
    private ImageView setHeat;
    private ImageView setDry;
    private ImageView setFan;
    private ImageView fan1;
    private ImageView fan3;
    private ImageView fan5;
    private ImageView setOnOff;
    private ImageView setOK;

    private Room room;
    private AirCondition airCondition;
    private AirConditionControl airConditionControl = new AirConditionControl();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_room_air_setting_hit);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getIntent().getStringExtra("title"));
        commonTopBar.setLeftIconView(R.drawable.top_bar_back_hit);
        commonTopBar.setIconView(myOnClickListener, null);

        room = new Gson().fromJson(getIntent().getStringExtra("room"),Room.class);
        airCondition = new Gson().fromJson(getIntent().getStringExtra("air"),AirCondition.class);

        ImageView tempDecrease = (ImageView)findViewById(R.id.temp_decrease);
        ImageView tempIncrease = (ImageView)findViewById(R.id.temp_increase);
        tempDecrease.setOnClickListener(myOnClickListener);
        tempIncrease.setOnClickListener(myOnClickListener);

        tempSeekBar = (SeekBar)findViewById(R.id.temp_seek_bar);
        roomTemp = (TextView)findViewById(R.id.temp_num);
        setCool = (ImageView)findViewById(R.id.mode_cool);
        setHeat = (ImageView)findViewById(R.id.mode_heat);
        setDry = (ImageView)findViewById(R.id.mode_dry);
        setFan = (ImageView)findViewById(R.id.mode_fan);
        fan1 = (ImageView)findViewById(R.id.fan_fan1);
        fan3 = (ImageView)findViewById(R.id.fan_fan3);
        fan5 = (ImageView)findViewById(R.id.fan_fan5);
        setOnOff = (ImageView)findViewById(R.id.set_onoff);
        setOK = (ImageView)findViewById(R.id.ok_button);

        setCool.setOnClickListener(myOnClickListener);
        setHeat.setOnClickListener(myOnClickListener);
        setDry.setOnClickListener(myOnClickListener);
        setFan.setOnClickListener(myOnClickListener);
        fan1.setOnClickListener(myOnClickListener);
        fan5.setOnClickListener(myOnClickListener);
        fan3.setOnClickListener(myOnClickListener);
        setOnOff.setOnClickListener(myOnClickListener);
        setOK.setOnClickListener(myOnClickListener);

        tempSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tempSeekBar.setProgress(progress);
                if(mode == 1){
                    int temp_flag = progress + 17;
                    roomTemp.setText(temp_flag + getString(R.string.temp_symbol));
                }else{
                    int temp_flag = progress + 19;
                    roomTemp.setText(temp_flag + getString(R.string.temp_symbol));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        init();

    }

    private void init() {
        if(airCondition.getOnoff() == AirConditionControl.UNKNOW){
            on_off = 0;
            mode = 0;
            fan = 0;
            temp = 30;
        }else {
            on_off = airCondition.getOnoff();
            mode = airCondition.getMode();
            fan = airCondition.getFan();
            temp = (int) airCondition.getTemperature();
        }
        if(mode == 1){
            tempSeekBar.setMax(13);
        }else{
            tempSeekBar.setMax(11);
        }
        changeTemp(temp);
        changeIconColor(on_off,mode,fan);
    }


    private void submit() {
        airConditionControl.setMode(mode);
        airConditionControl.setOnoff(on_off);
        airConditionControl.setTemperature(temp);
        airConditionControl.setWindVelocity(fan);
        try {
            MyApp.getApp().getAirconditionManager().controlRoom(room,airConditionControl);
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        } catch (Exception e) {
            MyApp.getApp().showToast(getString(R.string.control_room_fail));
            Log.e(TAG, "control room fail!");
            e.printStackTrace();
        }
    }

    private void changeTemp(int temp){
        roomTemp.setText(temp + getString(R.string.temp_symbol));
        if(mode == 1){
            tempSeekBar.setProgress(temp - 17);
        }else{
            tempSeekBar.setProgress(temp - 19);
        }

    }

    private void changeIconColor(int temp_on_off, int temp_mode, int temp_fan){
        if(temp_on_off == 0){
            setOnOff.setImageResource(R.drawable.room_icon_onoff_off_selected_hit);
            switch (temp_mode){
                case 0:
                    setCool.setImageResource(R.drawable.room_icon_cool_off_selected_hit);
                    setHeat.setImageResource(R.drawable.room_icon_heat_hit);
                    setDry.setImageResource(R.drawable.room_icon_dry_hit);
                    setFan.setImageResource(R.drawable.room_icon_fan_hit);
                    break;
                case 1:
                    setCool.setImageResource(R.drawable.room_icon_cool_hit);
                    setHeat.setImageResource(R.drawable.room_icon_heat_off_selected_hit);
                    setDry.setImageResource(R.drawable.room_icon_dry_hit);
                    setFan.setImageResource(R.drawable.room_icon_fan_hit);
                    break;
                case 2:
                    setCool.setImageResource(R.drawable.room_icon_cool_hit);
                    setHeat.setImageResource(R.drawable.room_icon_heat_hit);
                    setDry.setImageResource(R.drawable.room_icon_dry_off_selected_hit);
                    setFan.setImageResource(R.drawable.room_icon_fan_hit);
                    break;
                case 3:
                    setCool.setImageResource(R.drawable.room_icon_cool_hit);
                    setHeat.setImageResource(R.drawable.room_icon_heat_hit);
                    setDry.setImageResource(R.drawable.room_icon_dry_hit);
                    setFan.setImageResource(R.drawable.room_icon_fan_off_selected_hit);
                    break;
            }
            switch (temp_fan){
                case 0:
                    fan1.setImageResource(R.drawable.room_icon_fan1_off_selected_hit);
                    fan3.setImageResource(R.drawable.room_icon_fan3_hit);
                    fan5.setImageResource(R.drawable.room_icon_fan5_hit);
                    break;
                case 1:
                    fan1.setImageResource(R.drawable.room_icon_fan1_hit);
                    fan3.setImageResource(R.drawable.room_icon_fan3_off_selected_hit);
                    fan5.setImageResource(R.drawable.room_icon_fan5_hit);
                    break;
                case 2:
                    fan1.setImageResource(R.drawable.room_icon_fan1_hit);
                    fan3.setImageResource(R.drawable.room_icon_fan3_hit);
                    fan5.setImageResource(R.drawable.room_icon_fan5_off_selected_hit);
                    break;
            }
        }else{
            setOnOff.setImageResource(R.drawable.room_icon_onoff_on_selected_hit);
            switch (temp_mode){
                case 0:
                    setCool.setImageResource(R.drawable.room_icon_cool_on_selected_hit);
                    setHeat.setImageResource(R.drawable.room_icon_heat_hit);
                    setDry.setImageResource(R.drawable.room_icon_dry_hit);
                    setFan.setImageResource(R.drawable.room_icon_fan_hit);
                    switch (temp_fan){
                        case 0:
                            fan1.setImageResource(R.drawable.room_icon_fan1_on_blue_selected_hit);
                            fan3.setImageResource(R.drawable.room_icon_fan3_hit);
                            fan5.setImageResource(R.drawable.room_icon_fan5_hit);
                            break;
                        case 1:
                            fan1.setImageResource(R.drawable.room_icon_fan1_hit);
                            fan3.setImageResource(R.drawable.room_icon_fan3_on_blue_selected_hit);
                            fan5.setImageResource(R.drawable.room_icon_fan5_hit);
                            break;
                        case 2:
                            fan1.setImageResource(R.drawable.room_icon_fan1_hit);
                            fan3.setImageResource(R.drawable.room_icon_fan3_hit);
                            fan5.setImageResource(R.drawable.room_icon_fan5_on_blue_selected_hit);
                            break;
                    }
                    break;
                case 1:
                    setCool.setImageResource(R.drawable.room_icon_cool_hit);
                    setHeat.setImageResource(R.drawable.room_icon_heat_on_selected_hit);
                    setDry.setImageResource(R.drawable.room_icon_dry_hit);
                    setFan.setImageResource(R.drawable.room_icon_fan_hit);
                    switch (temp_fan){
                        case 0:
                            fan1.setImageResource(R.drawable.room_icon_fan1_on_red_selected_hit);
                            fan3.setImageResource(R.drawable.room_icon_fan3_hit);
                            fan5.setImageResource(R.drawable.room_icon_fan5_hit);
                            break;
                        case 1:
                            fan1.setImageResource(R.drawable.room_icon_fan1_hit);
                            fan3.setImageResource(R.drawable.room_icon_fan3_on_red_selected_hit);
                            fan5.setImageResource(R.drawable.room_icon_fan5_hit);
                            break;
                        case 2:
                            fan1.setImageResource(R.drawable.room_icon_fan1_hit);
                            fan3.setImageResource(R.drawable.room_icon_fan3_hit);
                            fan5.setImageResource(R.drawable.room_icon_fan5_on_red_selected_hit);
                            break;
                    }
                    break;
                case 2:
                    setCool.setImageResource(R.drawable.room_icon_cool_hit);
                    setHeat.setImageResource(R.drawable.room_icon_heat_hit);
                    setDry.setImageResource(R.drawable.room_icon_dry_on_selected_hit);
                    setFan.setImageResource(R.drawable.room_icon_fan_hit);
                    switch (temp_fan){
                        case 0:
                            fan1.setImageResource(R.drawable.room_icon_fan1_on_blue_selected_hit);
                            fan3.setImageResource(R.drawable.room_icon_fan3_hit);
                            fan5.setImageResource(R.drawable.room_icon_fan5_hit);
                            break;
                        case 1:
                            fan1.setImageResource(R.drawable.room_icon_fan1_hit);
                            fan3.setImageResource(R.drawable.room_icon_fan3_on_blue_selected_hit);
                            fan5.setImageResource(R.drawable.room_icon_fan5_hit);
                            break;
                        case 2:
                            fan1.setImageResource(R.drawable.room_icon_fan1_hit);
                            fan3.setImageResource(R.drawable.room_icon_fan3_hit);
                            fan5.setImageResource(R.drawable.room_icon_fan5_on_blue_selected_hit);
                            break;
                    }
                    break;
                case 3:
                    setCool.setImageResource(R.drawable.room_icon_cool_hit);
                    setHeat.setImageResource(R.drawable.room_icon_heat_hit);
                    setDry.setImageResource(R.drawable.room_icon_dry_hit);
                    setFan.setImageResource(R.drawable.room_icon_fan_on_selected_hit);
                    switch (temp_fan){
                        case 0:
                            fan1.setImageResource(R.drawable.room_icon_fan1_on_blue_selected_hit);
                            fan3.setImageResource(R.drawable.room_icon_fan3_hit);
                            fan5.setImageResource(R.drawable.room_icon_fan5_hit);
                            break;
                        case 1:
                            fan1.setImageResource(R.drawable.room_icon_fan1_hit);
                            fan3.setImageResource(R.drawable.room_icon_fan3_on_blue_selected_hit);
                            fan5.setImageResource(R.drawable.room_icon_fan5_hit);
                            break;
                        case 2:
                            fan1.setImageResource(R.drawable.room_icon_fan1_hit);
                            fan3.setImageResource(R.drawable.room_icon_fan3_hit);
                            fan5.setImageResource(R.drawable.room_icon_fan5_on_blue_selected_hit);
                            break;
                    }
                    break;
            }

        }

    }

}
