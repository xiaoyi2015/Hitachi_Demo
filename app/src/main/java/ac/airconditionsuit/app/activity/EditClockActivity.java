package ac.airconditionsuit.app.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
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
import ac.airconditionsuit.app.view.CommonButtonWithArrow;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/10/7.
 */
public class EditClockActivity extends BaseActivity{

    private static String[] weekName = new String[]{"周一","周二","周三","周四","周五","周六","周日"};

    private MyOnClickListener myOnClickListener = new MyOnClickListener() {
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.left_icon:
                    finish();
                    break;
                case R.id.right_icon:
                    //TODO save clock setting
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_edit_clock);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.tab_label_set_time));
        commonTopBar.setIconView(myOnClickListener, myOnClickListener);

        EditText clockNameText = (EditText)findViewById(R.id.clock_name_text);
        String clock_name  = getIntent().getStringExtra("title");
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

        TimePicker timePicker = (TimePicker)findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(6);
        timePicker.setCurrentMinute(12);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                //TODO
            }
        });

        CommonButtonWithArrow clockMode = (CommonButtonWithArrow)findViewById(R.id.clock_mode);
        clockMode.getLabelTextView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
        clockMode.getOnlineTextView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);

        CommonButtonWithArrow clockRepeat = (CommonButtonWithArrow)findViewById(R.id.clock_repeat);
        clockRepeat.getLabelTextView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
        clockRepeat.getOnlineTextView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);

        ServerConfig.Timer timer;
        String on_off = getString(R.string.off);
        String mode = getString(R.string.cool);
        String fan = getString(R.string.low_wind);
        String temp = getString(R.string.default_temp);
        String repeat = getString(R.string.not_repeat);
        String week = "";

        if(!clock_name.equals("")){
            timer = new Gson().fromJson(getIntent().getStringExtra("data"), ServerConfig.Timer.class);

            if(timer.isOnoff()){
                on_off = getString(R.string.on);
            }
            switch (timer.getMode()){
                case 0:
                    mode = getString(R.string.cool);
                    break;
                case 1:
                    mode = getString(R.string.hot);
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
        }

        clockMode.getOnlineTextView().setText(on_off + "|" + mode + "|" + fan + "|" + temp);
        if(week.equals("")){
            clockRepeat.getLabelTextView().setText(repeat);
        }else{
            clockRepeat.getLabelTextView().setText(repeat);
            clockRepeat.getOnlineTextView().setText(week);
        }
        //TODO color set and delete

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

            //TODO
            return convertView;
        }
    }
}
