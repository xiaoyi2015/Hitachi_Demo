package ac.airconditionsuit.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
 * Created by Administrator on 2015/10/22.
 */
public class RoomAirSettingHxActivity extends BaseActivity{

    private static final int ENABLE_OK_BUTTON = 10078;
    private MyOnClickListener myOnClickListener = new MyOnClickListener() {
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.left_icon:
                    Intent intent = new Intent();
                    setResult(RESULT_OK,intent);
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
                    if(temp < 19){
                        temp = 19;
                    }
                    changeTemp(temp);
                    changeIconAndStatus(on_off, mode, fan);
                    break;

                case R.id.mode_heat:
                    mode = 1;
                    changeTemp(temp);
                    changeIconAndStatus(on_off, mode, fan);
                    break;

                case R.id.mode_dry:
                    mode = 2;
                    if(temp < 19){
                        temp = 19;
                    }
                    changeTemp(temp);
                    changeIconAndStatus(on_off, mode, fan);
                    break;

                case R.id.mode_fan:
                    mode = 3;
                    if(temp < 19){
                        temp = 19;
                    }
                    changeTemp(temp);
                    changeIconAndStatus(on_off, mode, fan);
                    break;

                case R.id.fan_fan1:
                    fan = 0;
                    changeIconAndStatus(on_off, mode, fan);
                    break;

                case R.id.fan_fan3:
                    fan = 1;
                    changeIconAndStatus(on_off, mode, fan);
                    break;

                case R.id.fan_fan5:
                    fan = 2;
                    changeIconAndStatus(on_off, mode, fan);
                    break;

                case R.id.set_onoff:
                    if(on_off == 0){
                        on_off = 1;
                    }else {
                        on_off = 0;
                    }
                    changeIconAndStatus(on_off, mode, fan);
                    break;

                case R.id.ok_button:
                    disableButton(setOK);
                    submit();
                    break;
            }

        }
    };
    private Room room;
    private AirCondition airCondition;
    private AirConditionControl airConditionControl = new AirConditionControl();
    private Handler handler;

    private void submit() {
        airConditionControl.setMode(mode);
        airConditionControl.setOnoff(on_off);
        airConditionControl.setTemperature(temp);
        airConditionControl.setWindVelocity(fan);
        try {
            MyApp.getApp().getAirConditionManager().controlRoom(room, airConditionControl);
        } catch (Exception e) {
            MyApp.getApp().showToast(getString(R.string.control_room_fail));
            Log.e(TAG, "control room fail!");
            e.printStackTrace();
        }
        enableButton();
    }

    private int on_off;
    private int mode;
    private int fan;
    private int temp;

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
    private ImageView finalMode;
    private ImageView finalFan;
    private TextView finalText;
    private ImageView offView;
    private RelativeLayout panelView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_room_air_setting_hx);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getIntent().getStringExtra("title"));
        commonTopBar.setLeftIconView(R.drawable.top_bar_back_dc);
        commonTopBar.setIconView(myOnClickListener, null);

        room = new Gson().fromJson(getIntent().getStringExtra("room"),Room.class);
        airCondition = new Gson().fromJson(getIntent().getStringExtra("air"),AirCondition.class);

        ImageView tempDecrease = (ImageView)findViewById(R.id.temp_decrease);
        ImageView tempIncrease = (ImageView)findViewById(R.id.temp_increase);
        tempDecrease.setOnClickListener(myOnClickListener);
        tempIncrease.setOnClickListener(myOnClickListener);
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
        finalMode = (ImageView)findViewById(R.id.final_mode);
        finalFan = (ImageView)findViewById(R.id.final_fan);
        finalText = (TextView)findViewById(R.id.final_mode_fan_text);
        offView = (ImageView)findViewById(R.id.off_bg);
        panelView = (RelativeLayout)findViewById(R.id.panel_view);

        setCool.setOnClickListener(myOnClickListener);
        setHeat.setOnClickListener(myOnClickListener);
        setDry.setOnClickListener(myOnClickListener);
        setFan.setOnClickListener(myOnClickListener);
        fan1.setOnClickListener(myOnClickListener);
        fan5.setOnClickListener(myOnClickListener);
        fan3.setOnClickListener(myOnClickListener);
        setOnOff.setOnClickListener(myOnClickListener);
        setOK.setOnClickListener(myOnClickListener);

        handler = new Handler(new Handler.Callback(){
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case ENABLE_OK_BUTTON:
                        setOK.setImageResource(R.drawable.room_ok_button_hx);
                        setOK.setOnClickListener(myOnClickListener);
                        break;
                    default:
                        Log.e(TAG, "unhandle case in #hangler");
                }
                return true;
            }
        });
        setOK.setImageResource(R.drawable.room_ok_button_hx);
        init();

    }

    private void enableButton() {
        handler.sendEmptyMessageDelayed(ENABLE_OK_BUTTON, 1200);
    }

    private void disableButton(ImageView imageView) {
        imageView.setImageResource(R.drawable.room_ok_button_selected_hx);
        imageView.setOnClickListener(null);
    }

    private void init() {
        if(airCondition.getOnoff() == AirConditionControl.UNKNOW){
            on_off = 0;
            mode = 0;
            fan = 0;
            temp = 25;
        }else {
            on_off = airCondition.getOnoff();
            mode = airCondition.getAirconditionMode();
            fan = airCondition.getAirconditionFan();
            temp = (int) airCondition.getTemperature();
        }
        changeTemp(temp);
        changeIconAndStatus(on_off, mode, fan);
    }

    private void changeTemp(int temp_temp) {
        roomTemp.setText(String.valueOf(temp_temp));
    }

    private void changeIconAndStatus(int temp_on_off, int temp_mode, int temp_fan){
        setOnOff.setImageResource(R.drawable.onoff_on_heat_hit);
        String mode_flag = "";
        String fan_flag = "";

        if(temp_on_off == 0) {
            offView.setVisibility(View.VISIBLE);
            //setOnOff.setImageResource(R.drawable.room_off_onoff_hx);
        }else{
            offView.setVisibility(View.GONE);
            //setOnOff.setImageResource(R.drawable.onoff_on_heat_hit);
        }

        switch (temp_mode){
            case 0:
                setCool.setImageResource(R.drawable.selected_cool_hit);
                setHeat.setImageResource(R.drawable.room_off_heat_hx);
                setDry.setImageResource(R.drawable.room_off_dry_hx);
                setFan.setImageResource(R.drawable.room_off_fan_hx);
                finalMode.setImageResource(R.drawable.selected_cool_hit);
                panelView.setBackgroundResource(R.drawable.room_other_view_hx);
                mode_flag = getString(R.string.cool);
                break;
            case 1:
                setCool.setImageResource(R.drawable.room_off_cool_hx);
                setHeat.setImageResource(R.drawable.selected_heat_hit);
                setDry.setImageResource(R.drawable.room_off_dry_hx);
                setFan.setImageResource(R.drawable.room_off_fan_hx);
                finalMode.setImageResource(R.drawable.selected_heat_hit);
                panelView.setBackgroundResource(R.drawable.room_heat_view_hx);
                mode_flag = getString(R.string.heat);
                break;
            case 2:
                setCool.setImageResource(R.drawable.room_off_cool_hx);
                setHeat.setImageResource(R.drawable.room_off_heat_hx);
                setDry.setImageResource(R.drawable.selected_dry_hit);
                setFan.setImageResource(R.drawable.room_off_fan_hx);
                finalMode.setImageResource(R.drawable.selected_dry_hit);
                panelView.setBackgroundResource(R.drawable.room_other_view_hx);
                mode_flag = getString(R.string.dry);
                break;
            case 3:
                setCool.setImageResource(R.drawable.room_off_cool_hx);
                setHeat.setImageResource(R.drawable.room_off_heat_hx);
                setDry.setImageResource(R.drawable.room_off_dry_hx);
                setFan.setImageResource(R.drawable.selected_fan_hit);
                finalMode.setImageResource(R.drawable.selected_fan_hit);
                panelView.setBackgroundResource(R.drawable.room_other_view_hx);
                mode_flag = getString(R.string.wind);
                break;
        }
        switch (temp_fan){
            case 0:
                fan1.setImageResource(R.drawable.selected_fan1_hit);
                fan3.setImageResource(R.drawable.room_off_fan3_hx);
                fan5.setImageResource(R.drawable.room_off_fan5_hx);
                finalFan.setImageResource(R.drawable.selected_fan1_hit);
                fan_flag = getString(R.string.low_wind);
                break;
            case 1:
                fan1.setImageResource(R.drawable.room_off_fan1_hx);
                fan3.setImageResource(R.drawable.selected_fan3_hit);
                fan5.setImageResource(R.drawable.room_off_fan5_hx);
                finalFan.setImageResource(R.drawable.selected_fan3_hit);
                fan_flag = getString(R.string.medium_wind);
                break;
            case 2:
                fan1.setImageResource(R.drawable.room_off_fan1_hx);
                fan3.setImageResource(R.drawable.room_off_fan3_hx);
                fan5.setImageResource(R.drawable.selected_fan5_hit);
                finalFan.setImageResource(R.drawable.selected_fan5_hit);
                fan_flag = getString(R.string.high_wind);
                break;
        }
        finalText.setText(mode_flag + "|" + fan_flag);
    }

}
