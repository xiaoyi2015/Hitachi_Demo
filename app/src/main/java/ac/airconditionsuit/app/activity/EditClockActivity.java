package ac.airconditionsuit.app.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bigkoo.pickerview.lib.WheelView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.entity.ServerConfig;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.util.CheckUtil;
import ac.airconditionsuit.app.view.AirModePickerView;
import ac.airconditionsuit.app.view.CommonButtonWithArrow;
import ac.airconditionsuit.app.view.CommonTopBar;
import ac.airconditionsuit.app.view.CommonWheelView;

/**
 * Created by Administrator on 2015/10/7.
 */
public class EditClockActivity extends BaseActivity{

    private static final int REQUEST_CODE_REPEAT = 222;
    private boolean is_add;
    private int index;
    private static String[] weekName = new String[]{"周一","周二","周三","周四","周五","周六","周日"};

    private MyOnClickListener myOnClickListener = new MyOnClickListener() {
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.left_icon:
                    finish();
                    break;
                case R.id.delete_clock:
                    TextView toDoDelete = new TextView(EditClockActivity.this);
                    toDoDelete.setGravity(Gravity.CENTER);
                    toDoDelete.setText(getString(R.string.is_delete_clock));
                    toDoDelete.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                    new AlertDialog.Builder(EditClockActivity.this).setView(toDoDelete).
                            setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MyApp.getApp().getServerConfigManager().deleteTimer(index);
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
                        //TODO add device setting
                        ServerConfig serverConfig = new ServerConfig();
                        ServerConfig.Timer timer_temp = serverConfig.new Timer();
                        timer_temp.setName(check_clock_name);
                        timer_temp.setHour(timePicker.getCurrentHour());
                        timer_temp.setMinute(timePicker.getCurrentMinute());
                        timer_temp.setMode(0);
                        timer_temp.setFan(0);
                        timer_temp.setOnoff(false);
                        timer_temp.setTemperature(25);
                        ArrayList<Integer> week_list_temp = new ArrayList<Integer>();
                        week_list_temp.clear();
                        if(flag_repeat == 1){
                            for(int i = 0; i < 7; i++){
                                if(week_list[i] == 1){
                                   week_list_temp.add(i);
                                }
                            }
                            timer_temp.setWeek(week_list_temp);
                            if(timer_temp.getWeek().size() == 0){
                                timer_temp.setRepeat(false);
                            }else{
                                timer_temp.setRepeat(true);
                            }
                        }else{
                            timer_temp.setRepeat(false);
                        }
                        MyApp.getApp().getServerConfigManager().addTimer(timer_temp);
                        finish();
                    }else{
                        //TODO save mode setting

                        ArrayList<Integer> week_list_temp1 = new ArrayList<Integer>();
                        week_list_temp1.clear();
                        if(flag_repeat == 1){
                            for(int i = 0; i < 7; i++){
                                if(week_list[i]==1){
                                    week_list_temp1.add(i);
                                }
                            }
                            MyApp.getApp().getServerConfigManager().getTimer().get(index).setWeek(week_list_temp1);
                            if(MyApp.getApp().getServerConfigManager().getTimer().get(index).getWeek().size() == 0){
                                MyApp.getApp().getServerConfigManager().getTimer().get(index).setRepeat(false);
                            }else{
                                MyApp.getApp().getServerConfigManager().getTimer().get(index).setRepeat(true);
                            }
                        }else{
                            MyApp.getApp().getServerConfigManager().getTimer().get(index).setRepeat(false);
                        }
                        MyApp.getApp().getServerConfigManager().getTimer().get(index).setName(check_clock_name);
                        MyApp.getApp().getServerConfigManager().getTimer().get(index).setHour(timePicker.getCurrentHour());
                        MyApp.getApp().getServerConfigManager().getTimer().get(index).setMinute(timePicker.getCurrentMinute());
                        MyApp.getApp().getServerConfigManager().writeToFile();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    break;

            }
        }
    };
    private EditText clockNameText;
    ServerConfig.Timer timer;
    private TimePicker timePicker;
    private CommonButtonWithArrow clockRepeat;
    private int[] week_list = new int[]{0,0,0,0,0,0,0};
    private int flag_repeat = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_edit_clock);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.tab_label_set_time));
        commonTopBar.setIconView(myOnClickListener, myOnClickListener);

        clockNameText = (EditText)findViewById(R.id.clock_name_text);
        TextView deleteClock = (TextView)findViewById(R.id.delete_clock);
        deleteClock.setOnClickListener(myOnClickListener);
        index = getIntent().getIntExtra("index",-1);
        String clock_name = getIntent().getStringExtra("title");
        is_add = clock_name.equals("");
        if(is_add){
            deleteClock.setVisibility(View.GONE);
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

        CommonButtonWithArrow clockMode = (CommonButtonWithArrow)findViewById(R.id.clock_mode);
        clockMode.getLabelTextView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
        clockMode.getOnlineTextView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);

        clockMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AirModePickerView airModePickerView = new AirModePickerView(EditClockActivity.this);
                CommonWheelView onOffView = (CommonWheelView) airModePickerView.findViewById(R.id.set_on_off);
                CommonWheelView modeView = (CommonWheelView) airModePickerView.findViewById(R.id.set_mode);
                CommonWheelView fanView = (CommonWheelView) airModePickerView.findViewById(R.id.set_fan);
                CommonWheelView tempView = (CommonWheelView) airModePickerView.findViewById(R.id.set_temp);
                airModePickerView.setMinimumHeight(400);

                new AlertDialog.Builder(EditClockActivity.this).setTitle(R.string.choose_clock_air_mode).setView(airModePickerView).
                        setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

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
                intent.putExtra("repeat",flag_repeat);
                intent.putExtra("week", week_list);
                intent.setClass(EditClockActivity.this, ChooseClockRepeatActivity.class);
                startActivityForResult(intent,REQUEST_CODE_REPEAT);
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

        String on_off = getString(R.string.off);
        String mode = getString(R.string.cool);
        String fan = getString(R.string.low_wind);
        String temp = getString(R.string.default_temp);
        String repeat = getString(R.string.not_repeat);
        String week = "";

        if(!is_add){
            timer = MyApp.getApp().getServerConfigManager().getTimer().get(index);
            if(timer.isRepeat()){
                flag_repeat = 1;
                for(int i = 0; i < timer.getWeek().size(); i++){
                    week_list[timer.getWeek().get(i)] = 1;
                }
            }
            timePicker.setCurrentHour(timer.getHour());
            timePicker.setCurrentMinute(timer.getMinute());

            if(timer.isOnoff()){
                on_off = getString(R.string.on);
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
                for(int i = 0; i < timer.getWeek().size()-1; i++){
                    week = week + weekName[timer.getWeek().get(i)] + "|";
                }
                week = week + weekName[timer.getWeek().get(timer.getWeek().size()-1)];
            }
        }else{
            timePicker.setCurrentHour(0);
            timePicker.setCurrentMinute(0);
        }

        clockMode.getOnlineTextView().setText(on_off + "|" + mode + "|" + fan + "|" + temp);
        if(week.equals("")){
            clockRepeat.getLabelTextView().setText(repeat);
        }else{
            clockRepeat.getLabelTextView().setText(repeat);
            clockRepeat.getOnlineTextView().setText(week);
        }

        ListView listView = (ListView)findViewById(R.id.air_device_list1);
        List<ServerConfig.Device> devices = MyApp.getApp().getServerConfigManager().getDevices();
        AirDeviceClockSettingAdapter airDeviceClockSettingAdapter = new AirDeviceClockSettingAdapter(EditClockActivity.this,devices);
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
                    if(flag_repeat==1 && week_list.length!=0) {
                        String week1 = "";
                        for(int i = 0; i < week_list.length; i++){
                            if(week_list[i]==1){
                                week1 = week1 + weekName[i] + "|";
                            }
                        }
                        week1.substring(0,week1.length()-1);
                        clockRepeat.getLabelTextView().setText(R.string.repeat);
                        clockRepeat.getOnlineTextView().setText(week1);
                    }else{
                        clockRepeat.getLabelTextView().setText(R.string.not_repeat);
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
        private List<ServerConfig.Device> list;
        public AirDeviceClockSettingAdapter(Context context, List<ServerConfig.Device> list) {
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
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = new CommonButtonWithArrow(context);
            }

            TextView deviceName = (TextView)convertView.findViewById(R.id.label_text);
            deviceName.setText(list.get(position).getName());
            deviceName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO choose device and set address
                }
            });
            return convertView;
        }
    }
}
