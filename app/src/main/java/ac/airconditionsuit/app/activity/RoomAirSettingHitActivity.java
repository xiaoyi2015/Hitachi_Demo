package ac.airconditionsuit.app.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;

import com.google.gson.Gson;

import java.util.ArrayList;

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
public class RoomAirSettingHitActivity extends BaseActivity {

    private static final int ENABLE_OK_BUTTON = 10098;
    private int flag;
    private MyOnClickListener myOnClickListener = new MyOnClickListener() {
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.left_icon:
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case R.id.temp_decrease:
                    if (mode == 1) {
                        if (temp > 17 && temp <= 30) {
                            temp--;
                            changeTemp(temp);
                        }
                    } else {
                        if (temp > 19 && temp <= 30) {
                            temp--;
                            changeTemp(temp);
                        }
                    }
                    break;

                case R.id.temp_increase:
                    if (mode == 1) {
                        if (temp >= 17 && temp < 30) {
                            temp++;
                            changeTemp(temp);
                        }
                    } else {
                        if (temp >= 19 && temp < 30) {
                            temp++;
                            changeTemp(temp);
                        }
                    }
                    break;

                case R.id.mode_cool:
                    mode = 0;
                    tempSeekBar.setMax(11);
                    if(temp < 19){
                        tempSeekBar.setProgress(0);
                        temp = 21;
                    }else {
                        tempSeekBar.setProgress(temp - 19);
                    }
                    if(flag == 1){
                        if(temp > 19) {
                            temp = temp - 2;
                        }
                        changeTemp(temp);
                    }else {
                        changeTemp(temp);
                    }
                    flag = 0;
                    changeIconColor(on_off, mode, fan);
                    break;

                case R.id.mode_heat:
                    mode = 1;
                    tempSeekBar.setMax(13);
                    tempSeekBar.setProgress(temp - 17);
                    if(flag == 0){
                        temp = temp + 2;
                        changeTemp(temp);
                    }else {
                        changeTemp(temp);
                    }
                    flag = 1;
                    changeIconColor(on_off, mode, fan);
                    break;

                case R.id.mode_dry:
                    mode = 2;
                    tempSeekBar.setMax(11);
                    if(temp < 19){
                        tempSeekBar.setProgress(0);
                        temp = 21;
                    }else {
                        tempSeekBar.setProgress(temp - 19);
                    }
                    if(flag == 1){
                        if(temp > 19) {
                            temp = temp - 2;
                        }
                        changeTemp(temp);
                    }else {
                        changeTemp(temp);
                    }
                    flag = 0;
                    changeIconColor(on_off, mode, fan);
                    break;

                case R.id.mode_fan:
                    mode = 3;
                    tempSeekBar.setMax(11);
                    if(temp < 19){
                        tempSeekBar.setProgress(0);
                        temp = 21;
                    }else {
                        tempSeekBar.setProgress(temp - 19);
                    }
                    if(flag == 1){
                        if(temp > 19) {
                            temp = temp - 2;
                        }
                        changeTemp(temp);
                    }else {
                        changeTemp(temp);
                    }
                    flag = 0;
                    changeIconColor(on_off, mode, fan);
                    break;

                case R.id.fan_fan1:
                    fan = 0;
                    changeIconColor(on_off, mode, fan);
                    break;

                case R.id.fan_fan3:
                    fan = 1;
                    changeIconColor(on_off, mode, fan);
                    break;

                case R.id.fan_fan5:
                    fan = 2;
                    changeIconColor(on_off, mode, fan);
                    break;

                case R.id.set_onoff:
                    if (on_off == 0) {
                        on_off = 1;
                    } else {
                        on_off = 0;
                    }
                    changeIconColor(on_off, mode, fan);
                    break;

                case R.id.ok_button:
                    disableButton(setOK);
                    submit();
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
    private Handler handler;
    private ImageView roomWarning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_room_air_setting_hit);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getIntent().getStringExtra("title"));
        commonTopBar.setLeftIconView(R.drawable.top_bar_back_hit);
        commonTopBar.setIconView(myOnClickListener, null);

        room = new Gson().fromJson(getIntent().getStringExtra("room"), Room.class);
        airCondition = new Gson().fromJson(getIntent().getStringExtra("air"), AirCondition.class);

        ImageView tempDecrease = (ImageView) findViewById(R.id.temp_decrease);
        ImageView tempIncrease = (ImageView) findViewById(R.id.temp_increase);
        tempDecrease.setOnClickListener(myOnClickListener);
        tempIncrease.setOnClickListener(myOnClickListener);

        tempSeekBar = (SeekBar) findViewById(R.id.temp_seek_bar);
        roomTemp = (TextView) findViewById(R.id.temp_num);
        setCool = (ImageView) findViewById(R.id.mode_cool);
        setHeat = (ImageView) findViewById(R.id.mode_heat);
        setDry = (ImageView) findViewById(R.id.mode_dry);
        setFan = (ImageView) findViewById(R.id.mode_fan);
        fan1 = (ImageView) findViewById(R.id.fan_fan1);
        fan3 = (ImageView) findViewById(R.id.fan_fan3);
        fan5 = (ImageView) findViewById(R.id.fan_fan5);
        setOnOff = (ImageView) findViewById(R.id.set_onoff);
        setOK = (ImageView) findViewById(R.id.ok_button);

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
                if (mode == 1) {
                    temp = progress + 17;
                    roomTemp.setText(temp + getString(R.string.temp_symbol));
                } else {
                    temp = progress + 19;
                    roomTemp.setText(temp + getString(R.string.temp_symbol));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case ENABLE_OK_BUTTON:
                        setOK.setImageResource(R.drawable.room_icon_ok_hit);
                        setOK.setOnClickListener(myOnClickListener);
                        break;
                    default:
                        Log.e(TAG, "unhandle case in #hangler");
                }
                return true;
            }
        });

        setOK.setImageResource(R.drawable.room_icon_ok_hit);
        roomWarning = (ImageView) findViewById(R.id.room_warning);
        final ArrayList<Integer> air_index_list = new ArrayList<>();
        final ArrayList<Integer> warning_list = new ArrayList<>();
        final ArrayList<Integer> address_list = new ArrayList<>();
        if (room.getElements() == null) {
            roomWarning.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < room.getElements().size(); i++) {
                if(MyApp.getApp().getAirConditionManager().getAirConditionByIndex_new(room.getElements().
                        get(i)) == null){
                    break;
                }else {
                    if (MyApp.getApp().getAirConditionManager().getAirConditionByIndex_new(room.getElements().
                            get(i)).getWarning() != 0) {
                        air_index_list.add(MyApp.getApp().getServerConfigManager().getDevices_new().
                                get(room.getElements().get(i)).getOldIndoorIndex());
                        warning_list.add(MyApp.getApp().getAirConditionManager().getAirConditionByIndex_new
                                (room.getElements().get(i)).getWarning());
                        address_list.add(MyApp.getApp().getServerConfigManager().getDevices_new().
                                get(room.getElements().get(i)).getOldIndoorAddress());
                    }
                }
            }
            if (air_index_list.size() > 0) {
                roomWarning.setVisibility(View.VISIBLE);
                roomWarning.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        TextView et = new TextView(RoomAirSettingHitActivity.this);
                        et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
//                        et.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
                        String warning = " \n";
                        for (int i = 0; i < air_index_list.size(); i++) {
                            if (warning_list.get(i) == -2) {
                                if (address_list.get(i) < 10) {
                                    warning = warning + "空调 " + air_index_list.get(i) + "-0" +
                                            address_list.get(i) + " 离线" + "\n";
                                } else {
                                    warning = warning + "空调 " + air_index_list.get(i) + "-" +
                                            address_list.get(i) + " 离线" + "\n";
                                }
                            } else {
                                if (address_list.get(i) < 10) {
                                    warning = warning + "空调 " + air_index_list.get(i) + "-0" +
                                            address_list.get(i) + "，报警代码：" + Integer.toHexString(warning_list.get(i)| 0xFFFFFF00).substring(6) + "\n";
                                } else {
                                    warning = warning + "空调 " + air_index_list.get(i) + "-" +
                                            address_list.get(i) + "，报警代码：" +  Integer.toHexString(warning_list.get(i)| 0xFFFFFF00).substring(6) + "\n";
                                }
                            }
                        }
                        et.setText(warning);
                        et.setGravity(Gravity.CENTER);
                        et.setTextColor(getResources().getColor(R.color.delete_red_hit));
                        ScrollView sv = new ScrollView(RoomAirSettingHitActivity.this);
                        sv.addView(et);
                        new AlertDialog.Builder(RoomAirSettingHitActivity.this).setTitle(R.string.tip).setView(sv).
                                setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).setCancelable(false).show();
                    }
                });
            } else {
                roomWarning.setVisibility(View.GONE);
            }
        }
        init();

    }

    private void enableButton() {
        handler.sendEmptyMessageDelayed(ENABLE_OK_BUTTON, 2500);
    }

    private void disableButton(ImageView imageView) {
        imageView.setImageResource(R.drawable.room_icon_ok_selected_hit);
        imageView.setOnClickListener(null);

    }

    private void init() {
        on_off = airCondition.getOnoff();
        mode = airCondition.getAirconditionMode();
        fan = airCondition.getAirconditionFan();
        temp = (int) airCondition.getTemperature();
        changeTemp(temp);
        if (mode == 1) {
            tempSeekBar.setMax(13);
            flag = 1;
        } else {
            tempSeekBar.setMax(11);
            flag = 0;
        }
        changeIconColor(on_off, mode, fan);
    }


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

    private void changeTemp(int temp) {
        roomTemp.setText(temp + getString(R.string.temp_symbol));
        if (mode == 1) {
            tempSeekBar.setProgress(temp - 17);
        } else {
            tempSeekBar.setProgress(temp - 19);
        }

    }

    private void changeIconColor(int temp_on_off, int temp_mode, int temp_fan) {
        if (temp_on_off == 0) {
            setOnOff.setImageResource(R.drawable.room_icon_onoff_off_selected_hit);
            switch (temp_mode) {
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
            switch (temp_fan) {
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
        } else {
            setOnOff.setImageResource(R.drawable.room_icon_onoff_on_selected_hit);
            switch (temp_mode) {
                case 0:
                    setCool.setImageResource(R.drawable.room_icon_cool_on_selected_hit);
                    setHeat.setImageResource(R.drawable.room_icon_heat_hit);
                    setDry.setImageResource(R.drawable.room_icon_dry_hit);
                    setFan.setImageResource(R.drawable.room_icon_fan_hit);
                    switch (temp_fan) {
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
                    switch (temp_fan) {
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
                    switch (temp_fan) {
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
                    switch (temp_fan) {
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
