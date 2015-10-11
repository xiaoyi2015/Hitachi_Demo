package ac.airconditionsuit.app.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;

import java.util.List;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.entity.ServerConfig;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.util.CheckUtil;
import ac.airconditionsuit.app.view.CommonButtonWithArrow;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/10/7.
 */
public class EditClockActivity extends BaseActivity{

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
                    //TODO save clock setting
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
                        timer_temp.setRepeat(false);
                        timer_temp.setTemperature(25);
                        MyApp.getApp().getServerConfigManager().addTimer(timer_temp);
                        finish();
                    }else{
                        //TODO save device setting
                        MyApp.getApp().getServerConfigManager().getTimer().get(index).setName(check_clock_name);
                        MyApp.getApp().getServerConfigManager().getTimer().get(index).setHour(timePicker.getCurrentHour());
                        MyApp.getApp().getServerConfigManager().getTimer().get(index).setMinute(timePicker.getCurrentMinute());
                        MyApp.getApp().getServerConfigManager().writeToFile();
                        Intent intent = new Intent();
                        intent.putExtra("index",index);
                        intent.putExtra("title",check_clock_name);
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

        CommonButtonWithArrow clockRepeat = (CommonButtonWithArrow)findViewById(R.id.clock_repeat);
        clockRepeat.getLabelTextView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
        clockRepeat.getOnlineTextView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);

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
            temp = timer.getTemperature() + getString(R.string.temp_symbol);
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
                    //TODO choose or not
                }
            });
            //TODO
            return convertView;
        }
    }
}
