package ac.airconditionsuit.app.activity;

import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.entity.DeviceFromServerConfig;
import ac.airconditionsuit.app.entity.Timer;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.util.CheckUtil;
import ac.airconditionsuit.app.view.AirModePickerView;
import ac.airconditionsuit.app.view.CommonButtonWithArrow;
import ac.airconditionsuit.app.view.CommonChooseView;
import ac.airconditionsuit.app.view.CommonTopBar;
import ac.airconditionsuit.app.view.CommonWheelView;

/**
 * Created by Administrator on 2015/10/7.
 */
public class EditClockActivity extends BaseActivity{

    private static final int REQUEST_CODE_REPEAT = 222;
    private boolean is_add;
    private int index;
    private static String[] weekName = new String[]{"一","二","三","四","五","六","日"};

    private MyOnClickListener myOnClickListener = new MyOnClickListener() {
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.left_icon:
                    finish();
                    break;
                case R.id.delete_clock:
                    new AlertDialog.Builder(EditClockActivity.this).setTitle(R.string.tip).setMessage(getString(R.string.is_delete_clock)).
                            setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MyApp.getApp().getAirConditionManager().deleteTimerServer(timer.getTimerid());
                                    Intent intent1 = new Intent();
                                    setResult(RESULT_OK, intent1);
                                    finish();
                                }
                            }).setNegativeButton(R.string.cancel, null).setCancelable(false).show();
                    break;
                case R.id.right_icon:
                    final String check_clock_name = CheckUtil.checkLength(clockNameText, 10, R.string.pls_input_clock_name, R.string.clock_name_length_too_long);
                    if (check_clock_name == null)
                        return;

                    if(is_add){
                        Timer timer_temp = new Timer();
                        timer_temp.setName(check_clock_name);
                        timer_temp.setHour(timePicker.getCurrentHour());
                        timer_temp.setMinute(timePicker.getCurrentMinute());
                        timer_temp.setMode(temp_mode);
                        timer_temp.setFan(temp_fan);
                        timer_temp.setOnoff(temp_on_off);
                        timer_temp.setTemperature(temp_temp);
                        timer_temp.setTimerenabled(true);
                        ArrayList<Integer> week_list_temp = new ArrayList<>();
                        week_list_temp.clear();
                        for(int i = 0; i < 7; i++){
                            if(week_list[i] == 1){
                                week_list_temp.add(i);
                            }
                        }
                        timer_temp.setWeek(week_list_temp);
                        if(flag_repeat == 1){
                            timer_temp.setRepeat(true);
                        }else{
                            timer_temp.setRepeat(false);
                        }

                        ArrayList<Integer> device_list_temp = new ArrayList<>();
                        device_list_temp.clear();
                        for(int i = 0; i < isDeviceChoose.size(); i++){
                            if(isDeviceChoose.get(i) == 1){
                                device_list_temp.add(i + 1);
                            }
                        }
                        timer_temp.setIndexes_new_new(device_list_temp);
                        if(device_list_temp.size() == 0){
                            MyApp.getApp().showToast("请选择定时的空调");
                            return;
                        }
                        //添加定时器时，本地不添加，等待服务器数据返回。因为本地timer没有timer id，会造成重复
                        MyApp.getApp().getAirConditionManager().addTimerServer(timer_temp);
                        //MyApp.getApp().getServerConfigManager().addTimer(timer_temp);
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }else{
                        timer.setOnoff(temp_on_off);
                        timer.setMode(temp_mode);
                        timer.setFan(temp_fan);
                        timer.setTemperature(temp_temp);
                        ArrayList<Integer> week_list_temp1 = new ArrayList<Integer>();
                        week_list_temp1.clear();
                        for(int i = 0; i < 7; i++){
                            if(week_list[i]==1){
                                week_list_temp1.add(i);
                            }
                        }
                        timer.setWeek(week_list_temp1);
                        if(flag_repeat == 1){
                            timer.setRepeat(true);
                        }else{
                            timer.setRepeat(false);
                        }
                        timer.setName(check_clock_name);
                        timer.setHour(timePicker.getCurrentHour());
                        timer.setMinute(timePicker.getCurrentMinute());
                        timer.setTimerenabled(true);
                        timer.getIndexes_new_new().clear();
                        for(int i = 0; i < isDeviceChoose.size(); i++){
                            if(isDeviceChoose.get(i) == 1){
                                timer.getIndexes_new_new().
                                        add(i + 1);
                            }
                        }
                        if(timer.getIndexes_new_new().size() == 0){
                            MyApp.getApp().showToast("请选择定时的空调");
                            return;
                        }
                        MyApp.getApp().getAirConditionManager().modifyTimerServer(timer);
                        //MyApp.getApp().getServerConfigManager().getTimer().set(index, timer);
                        //MyApp.getApp().getServerConfigManager().writeToFile();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    break;

            }
        }
    };
    private EditText clockNameText;
    Timer timer;
    private TimePicker timePicker;
    private CommonButtonWithArrow clockRepeat;
    private int[] week_list = new int[]{0,0,0,0,0,0,0};
    private int flag_repeat = 0;
    private boolean temp_on_off;
    private int temp_mode;
    private int temp_fan;
    private float temp_temp;
    private String on_off;
    private String mode;
    private String fan;
    private String temp;
    private CommonButtonWithArrow clockMode;
    private ArrayList<Integer> isDeviceChoose = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_edit_clock);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.tab_label_set_time));
        TextView deleteClock = (TextView)findViewById(R.id.delete_clock);
        switch (UIManager.UITYPE){
            case 1:
                commonTopBar.setLeftIconView(R.drawable.top_bar_cancel_hit);
                commonTopBar.setRightIconView(R.drawable.top_bar_save_hit);
                deleteClock.setBackgroundColor(getResources().getColor(R.color.delete_red_hit));
                break;
            case 2:
                commonTopBar.setLeftIconView(R.drawable.top_bar_cancel_dc);
                commonTopBar.setRightIconView(R.drawable.top_bar_save_dc);
                deleteClock.setBackgroundColor(getResources().getColor(R.color.switch_on_pink));
                break;
            default:
                commonTopBar.setLeftIconView(R.drawable.top_bar_cancel_dc);
                commonTopBar.setRightIconView(R.drawable.top_bar_save_dc);
                deleteClock.setBackgroundColor(getResources().getColor(R.color.delete_red_hx));
                break;
        }
        commonTopBar.setIconView(myOnClickListener, myOnClickListener);

        clockNameText = (EditText)findViewById(R.id.clock_name_text);
        deleteClock.setOnClickListener(myOnClickListener);
        index = getIntent().getIntExtra("index", -1);
        String clock_name = getIntent().getStringExtra("title");
        is_add = clock_name.equals("");
        if(is_add){
            deleteClock.setVisibility(View.GONE);
            temp_on_off = false;
            temp_mode = 0;
            temp_fan = 0;
            temp_temp = 25;
        }

        clockNameText.setText(clock_name);
        clockNameText.setSelection(clock_name.length());
        setOnclickListenerOnTextViewDrawable(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof EditText) {
                    ((EditText) v).setText("");
                }
            }
        }, clockNameText);

        clockMode = (CommonButtonWithArrow)findViewById(R.id.clock_mode);
        clockMode.getLabelTextView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
        clockMode.getOnlineTextView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);

        clockMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AirModePickerView airModePickerView = new AirModePickerView(EditClockActivity.this);
                final CommonWheelView onOffView = (CommonWheelView) airModePickerView.findViewById(R.id.set_on_off);
                final CommonWheelView modeView = (CommonWheelView) airModePickerView.findViewById(R.id.set_mode);
                final CommonWheelView fanView = (CommonWheelView) airModePickerView.findViewById(R.id.set_fan);
                final CommonWheelView tempView = (CommonWheelView) airModePickerView.findViewById(R.id.set_temp);

                if(temp_mode == 1){
                    airModePickerView.setTempHeatList();
                    tempView.setDefault((int) temp_temp - 17);
                } else{
                    tempView.setDefault((int) temp_temp - 19);
                }
                if (temp_on_off) {
                    onOffView.setDefault(1);
                } else {
                    onOffView.setDefault(0);
                }
                modeView.setDefault(temp_mode);
                fanView.setDefault(temp_fan);

                airModePickerView.setMinimumHeight(400);

                new AlertDialog.Builder(EditClockActivity.this).setTitle(R.string.choose_clock_air_mode).setView(airModePickerView).
                        setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (onOffView.getSelected() == 1) {
                                    temp_on_off = true;
                                }else{
                                    temp_on_off = false;
                                }
                                temp_mode = modeView.getSelected();
                                temp_fan = fanView.getSelected();
                                if(modeView.getSelected() == 1){
                                    temp_temp = (float) (tempView.getSelected() + 17);
                                }else{
                                    temp_temp = (float) (tempView.getSelected() + 19);
                                }

                                //test
                                if(temp_temp > 30){
                                    temp_temp = 30;
                                }
                                if(temp_mode == 1){
                                    if(temp_temp < 17){
                                        temp_temp = 17;
                                    }
                                }else{
                                    if(temp_temp < 19){
                                        temp_temp = 19;
                                    }
                                }

                                if(temp_on_off){
                                    on_off = getString(R.string.on);
                                }else{
                                    on_off = getString(R.string.off);
                                }
                                switch (temp_mode){
                                    case 0:
                                        mode = getString(R.string.cool);
                                        break;
                                    case 1:
                                        mode = getString(R.string.heat);
                                        break;
                                    case 2:
                                        mode = getString(R.string.dry);
                                        break;
                                    case 3:
                                        mode = getString(R.string.wind);
                                        break;
                                }
                                switch (temp_fan){
                                    case 0:
                                        fan = getString(R.string.low_wind);
                                        break;
                                    case 1:
                                        fan = getString(R.string.medium_wind);
                                        break;
                                    case 2:
                                        fan = getString(R.string.high_wind);
                                        break;
                                }
                                temp = (int)temp_temp + getString(R.string.temp_symbol);
                                clockMode.getOnlineTextView().setText(on_off + "|" + mode + "|" + fan + "|" + temp);
                            }
                        }).setNegativeButton(R.string.cancel, null).setCancelable(false).show();
            }
        });

        clockRepeat = (CommonButtonWithArrow)findViewById(R.id.clock_repeat);
        clockRepeat.getLabelTextView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
        clockRepeat.getOnlineTextView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
        clockRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("repeat", flag_repeat);
                intent.putExtra("week", week_list);
                intent.setClass(EditClockActivity.this, ChooseClockRepeatActivity.class);
                startActivityForResult(intent, REQUEST_CODE_REPEAT);
            }
        });

        timePicker = (TimePicker)findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                timePicker.setCurrentHour(hourOfDay);
                timePicker.setCurrentMinute(minute);
            }
        });

        on_off = getString(R.string.off);
        mode = getString(R.string.cool);
        fan = getString(R.string.low_wind);
        temp = getString(R.string.default_temp);
        String repeat = getString(R.string.not_repeat);
        String week = "";

        if(!is_add){
            timer = new Gson().fromJson(getIntent().getStringExtra("clock"),Timer.class);
            temp_on_off = timer.isOnoff();
            temp_mode = timer.getMode();
            temp_fan = timer.getFan();
            temp_temp = timer.getTemperature();
            if(timer.isRepeat()){
                flag_repeat = 1;
            }else{
                flag_repeat = 0;
            }
            for(int i = 0; i < timer.getWeek().size(); i++){
                week_list[timer.getWeek().get(i)] = 1;
            }
            timePicker.setCurrentHour(timer.getHour());
            timePicker.setCurrentMinute(timer.getMinute());

            if(timer.isOnoff()){
                on_off = getString(R.string.on);
            }else{
                on_off = getString(R.string.off);
            }
            switch (timer.getMode()){
                case 0:
                    mode = getString(R.string.cool);
                    break;
                case 1:
                    mode = getString(R.string.heat);
                    break;
                case 2:
                    mode = getString(R.string.dry);
                    break;
                case 3:
                    mode = getString(R.string.wind);
                    break;
            }
            switch (timer.getFan()){
                case 0:
                    fan = getString(R.string.low_wind);
                    break;
                case 1:
                    fan = getString(R.string.medium_wind);
                    break;
                case 2:
                    fan = getString(R.string.high_wind);
                    break;
            }
            temp = (int)timer.getTemperature() + getString(R.string.temp_symbol);
            if(timer.isRepeat()) {
                repeat = getString(R.string.repeat);
            }else{
                repeat = getString(R.string.not_repeat);
            }
            if(timer.getWeek().size() != 0){
                week = "周";
                for (int i = 0; i < timer.getWeek().size() - 1; i++) {
                    week = week + weekName[timer.getWeek().get(i)] + "|";
                }
                week = week + weekName[timer.getWeek().get(timer.getWeek().size() - 1)];
            }else{
                week = "";
            }

        }else{
            timePicker.setCurrentHour(0);
            timePicker.setCurrentMinute(0);
        }

        clockMode.getOnlineTextView().setText(on_off + "|" + mode + "|" + fan + "|" + temp);
        clockRepeat.getLabelTextView().setText(repeat);
        clockRepeat.getOnlineTextView().setText(week);

        if( MyApp.getApp().getServerConfigManager().getDevices_new() != null){
            for (int i = 0; i < MyApp.getApp().getServerConfigManager().getDevices_new().size(); i++){
                isDeviceChoose.add(0);
            }
            if(!is_add){
                //int num = timer.getAddress().size();
                if(timer.getIndexes_new_new().size() != 0){
                    for(int i = 0 ;i < timer.getIndexes_new_new().size(); i++){
                        isDeviceChoose.set(timer.getIndexes_new_new().get(i)-1,1);
                    }
                }
            }
        }

        ListView listView = (ListView)findViewById(R.id.air_device_list1);
        List<DeviceFromServerConfig> devices_new = MyApp.getApp().getServerConfigManager().getDevices_new();
        AirDeviceClockSettingAdapter airDeviceClockSettingAdapter = new AirDeviceClockSettingAdapter(EditClockActivity.this,devices_new);
        listView.setAdapter(airDeviceClockSettingAdapter);
        setListViewHeightBasedOnChildren(listView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_CODE_REPEAT:
                    week_list = data.getIntArrayExtra("week");
                    flag_repeat = data.getIntExtra("repeat", -1);
                    if(flag_repeat == 1) {
                        clockRepeat.getLabelTextView().setText(R.string.repeat);
                    }else{
                        clockRepeat.getLabelTextView().setText(R.string.not_repeat);
                    }
                    if(week_list.length!=0) {
                        String week1 = "";
                        int k = 0;
                        for(int i = 0; i < week_list.length; i++){
                            if(week_list[i]==1){
                                week1 = "周";
                                k++ ;
                            }
                        }
                        int p = 0;
                        for(int i = 0; i < week_list.length; i++){
                            if(week_list[i]==1){
                                p++;
                                if( p < k ) {
                                    week1 = week1 + weekName[i] + "|";
                                }else{
                                    week1 = week1 + weekName[i];
                                }
                            }
                        }
                        clockRepeat.getOnlineTextView().setText(week1);
                    }else{
                        clockRepeat.getOnlineTextView().setText("");
                    }
                    break;
            }
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private class AirDeviceClockSettingAdapter extends BaseAdapter{

        private Context context;
        private List<DeviceFromServerConfig> list;
        public AirDeviceClockSettingAdapter(Context context, List<DeviceFromServerConfig> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = new CommonChooseView(context);
            }

            TextView deviceName = (TextView)convertView.findViewById(R.id.label_text);
            deviceName.setText(list.get(position).getName());
            deviceName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
            final ImageView chooseIcon = (ImageView)convertView.findViewById(R.id.choose_icon);
            if(isDeviceChoose.get(position) == 1){
                chooseIcon.setVisibility(View.VISIBLE);
            }else{
                chooseIcon.setVisibility(View.GONE);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isDeviceChoose.get(position) == 1){
                        isDeviceChoose.set(position,0);
                        chooseIcon.setVisibility(View.GONE);
                    }else{
                        isDeviceChoose.set(position,1);
                        chooseIcon.setVisibility(View.VISIBLE);
                    }
                }
            });
            return convertView;
        }
    }
}
