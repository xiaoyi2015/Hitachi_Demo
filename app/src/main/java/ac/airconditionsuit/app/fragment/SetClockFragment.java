package ac.airconditionsuit.app.fragment;

import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.entity.Timer;
import ac.airconditionsuit.app.network.socket.SocketManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.List;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.activity.BaseActivity;
import ac.airconditionsuit.app.activity.EditClockActivity;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/10/3.
 */
public class SetClockFragment extends BaseFragment {
    private View view;
    private static String[] weekName = new String[]{"一", "二", "三", "四", "五", "六", "日"};

    private MyOnClickListener myOnClickListener = new MyOnClickListener() {
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.right_icon:
                    int status = MyApp.getApp().getSocketManager().getStatus();
                    if (status == SocketManager.UDP_DEVICE_CONNECT
                            || status == SocketManager.TCP_DEVICE_CONNECT) {
                        Intent intent = new Intent();
                        intent.putExtra("title", "");
                        intent.setClass(getActivity(), EditClockActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_CLOCK);
                    } else {
                        MyApp.getApp().showToast(R.string.toast_inf_no_device_to_add_clock);
                    }
                    break;
            }
        }
    };
    private static final int REQUEST_CODE_CLOCK = 200;
    private static final int RESULT_OK = -1;
    private ClockSettingAdapter clockSettingAdapter;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_set_clock, container, false);
        listView = (ListView) view.findViewById(R.id.clock_list);
        clockSettingAdapter = new ClockSettingAdapter(getActivity(), null);
        listView.setAdapter(clockSettingAdapter);
        refreshUI();
        return view;
    }

    @Override
    public void setTopBar() {
        refreshUI();
        BaseActivity baseActivity = myGetActivity();
        CommonTopBar commonTopBar = baseActivity.getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.tab_label_set_time));
        commonTopBar.getTitleView().setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        switch (UIManager.UITYPE) {
            case 1:
                commonTopBar.setRightIconView(R.drawable.top_bar_add_hit);
                break;
            case 2:
                commonTopBar.setRightIconView(R.drawable.top_bar_add_dc);
                break;
            default:
                commonTopBar.setRightIconView(R.drawable.top_bar_add_dc);
                break;
        }

        commonTopBar.setIconView(null, myOnClickListener);
        commonTopBar.setRoundLeftIconView(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case REQUEST_CODE_CLOCK:
                    refreshUI();
                    break;
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class ClockSettingAdapter extends BaseAdapter {

        private Context context;
        private List<Timer> list;

        public ClockSettingAdapter(Context context, List<Timer> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            if (list == null) {
                return 0;
            } else {
                return list.size();
            }
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
            if (convertView == null) {
                convertView = new ClockCustomView(context);
            }

            final LinearLayout clockView = (LinearLayout) convertView.findViewById(R.id.clock_view);
            final TextView clockName = (TextView) convertView.findViewById(R.id.clock_name);
            final TextView clockSetting1 = (TextView) convertView.findViewById(R.id.clock_setting1);
            final TextView clockSetting2 = (TextView) convertView.findViewById(R.id.clock_setting2);
            final TextView clockTime = (TextView) convertView.findViewById(R.id.clock_time);
            final SwitchButton switchOn = (SwitchButton) convertView.findViewById(R.id.clock_on_off);
            final ImageView bgBar = (ImageView) convertView.findViewById(R.id.bg_bar);

            switchOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!switchOn.isChecked()) {
                        switchOn.setChecked(true);
                        if (UIManager.UITYPE == 2) {
                            bgBar.setImageResource(R.drawable.clock_bg_bar_on_dc);
                        }
                        clockName.setTextColor(getResources().getColor(R.color.text_normal_color));
                        clockSetting1.setTextColor(getResources().getColor(R.color.text_normal_color));
                        clockSetting2.setTextColor(getResources().getColor(R.color.text_normal_color));
                        clockTime.setTextColor(getResources().getColor(R.color.text_normal_color));
                        MyApp.getApp().getServerConfigManager().getTimer().get(position).setTimerenabled(true);
                        MyApp.getApp().getAirConditionManager().modifyTimerServer(MyApp.getApp().getServerConfigManager().getTimer().get(position));
                    } else {
                        switchOn.setChecked(false);
                        if (UIManager.UITYPE == 2) {
                            bgBar.setImageResource(R.drawable.clock_bg_bar_off_dc);
                        }
                        clockName.setTextColor(getResources().getColor(R.color.clock_off_gray));
                        clockSetting1.setTextColor(getResources().getColor(R.color.clock_off_gray));
                        clockSetting2.setTextColor(getResources().getColor(R.color.clock_off_gray));
                        clockTime.setTextColor(getResources().getColor(R.color.clock_off_gray));
                        MyApp.getApp().getServerConfigManager().getTimer().get(position).setTimerenabled(false);
                        MyApp.getApp().getAirConditionManager().modifyTimerServer(MyApp.getApp().getServerConfigManager().getTimer().get(position));
                    }
                }
            });

            if (list.get(position).isTimerenabled()) {
                switchOn.setChecked(true);
                clockName.setTextColor(getResources().getColor(R.color.text_normal_color));
                clockSetting1.setTextColor(getResources().getColor(R.color.text_normal_color));
                clockSetting2.setTextColor(getResources().getColor(R.color.text_normal_color));
                clockTime.setTextColor(getResources().getColor(R.color.text_normal_color));
                if (UIManager.UITYPE == 2) {
                    bgBar.setImageResource(R.drawable.clock_bg_bar_on_dc);
                }
            } else {
                switchOn.setChecked(false);
                clockName.setTextColor(getResources().getColor(R.color.clock_off_gray));
                clockSetting1.setTextColor(getResources().getColor(R.color.clock_off_gray));
                clockSetting2.setTextColor(getResources().getColor(R.color.clock_off_gray));
                clockTime.setTextColor(getResources().getColor(R.color.clock_off_gray));
                if (UIManager.UITYPE == 2) {
                    bgBar.setImageResource(R.drawable.clock_bg_bar_off_dc);
                }

            }
            clockName.setText(list.get(position).getName());
            String hour;
            if (list.get(position).getHour() >= 10) {
                hour = list.get(position).getHour() + ":";
            } else {
                hour = "0" + list.get(position).getHour() + ":";
            }
            if (list.get(position).getMinute() >= 10) {
                clockTime.setText(hour + list.get(position).getMinute());
            } else {
                clockTime.setText(hour + "0" + list.get(position).getMinute());
            }

            String on_off = "";
            String mode = "";
            String fan = "";
            String temp = (int) list.get(position).getTemperature() + getString(R.string.temp_symbol);
            String repeat;
            String week;

            switch (list.get(position).getMode()) {
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
            switch (list.get(position).getFan()) {
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
            if (list.get(position).isRepeat()) {
                repeat = getString(R.string.repeat);
                week = getString(R.string.week_name);
                List<Integer> week1 = list.get(position).getWeek();
                if(week1.size() == 0){
                    clockSetting2.setText(repeat);
                }else {
                    for (int i = 0; i < week1.size() - 1; i++) {
                        week = week + weekName[week1.get(i)] + "|";
                    }
                    Integer integer = week1.get(week1.size() - 1);
                    week = week + weekName[integer];
                    clockSetting2.setText(repeat + "|" + week);
                }
            } else {
                repeat = getString(R.string.not_repeat);
                week = getString(R.string.week_name);
                List<Integer> week1 = list.get(position).getWeek();
                if(week1.size() == 0){
                    clockSetting2.setText(repeat);
                }else {
                    for (int i = 0; i < week1.size() - 1; i++) {
                        week = week + weekName[week1.get(i)] + "|";
                    }
                    Integer integer = week1.get(week1.size() - 1);
                    week = week + weekName[integer];
                    clockSetting2.setText(repeat + "|" + week);
                }
            }

            if (list.get(position).isOnoff()) {
                on_off = getString(R.string.on);
            } else {
                on_off = getString(R.string.off);
            }
            clockSetting1.setText(on_off + "|" + mode + "|" + fan + "|" + temp);

            clockView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int status = MyApp.getApp().getSocketManager().getStatus();
                    if (status == SocketManager.UDP_DEVICE_CONNECT
                            || status == SocketManager.TCP_DEVICE_CONNECT) {
                        Intent intent = new Intent();
                        intent.putExtra("index", position);
                        intent.putExtra("title", list.get(position).getName());
                        intent.setClass(getActivity(), EditClockActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_CLOCK);
                    } else {
                        MyApp.getApp().showToast(R.string.toast_inf_no_device_to_modify_clock);
                    }
                }
            });

            return convertView;
        }

        public void changeData(List<Timer> timer) {
            this.list = timer;
            notifyDataSetChanged();
        }

    }

    private class ClockCustomView extends LinearLayout {
        public ClockCustomView(Context context) {
            super(context);
            init(context);
        }

        private void init(Context context) {
            switch (UIManager.UITYPE) {
                case 1:
                    inflate(context, R.layout.custom_clock_view_hit, this);
                    break;
                case 2:
                    inflate(context, R.layout.custom_clock_view, this);
                    break;
                default:
                    inflate(context, R.layout.custom_clock_view_hx, this);
                    break;
            }

        }
    }

    @Override
    public void refreshUI() {
        super.refreshUI();
        if (clockSettingAdapter == null) {
            return;
        }
        if (MyApp.getApp().getServerConfigManager().hasDevice()) {
            List<Timer> timer = MyApp.getApp().getServerConfigManager().getTimer();
            clockSettingAdapter.changeData(timer);
        } else {
            clockSettingAdapter.changeData(null);
        }

    }
}
