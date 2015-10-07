package ac.airconditionsuit.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.activity.BaseActivity;
import ac.airconditionsuit.app.activity.EditClockActivity;
import ac.airconditionsuit.app.activity.EditSceneActivity;
import ac.airconditionsuit.app.entity.ServerConfig;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/10/3.
 */
public class SetClockFragment extends BaseFragment {
    private View view;
    private static String[] weekName = new String[]{"一","二","三","四","五","六","日"};

    private MyOnClickListener myOnClickListener = new MyOnClickListener(){
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()){
                case R.id.right_icon:
                    Intent intent = new Intent();
                    intent.putExtra("title","");
                    intent.setClass(getActivity(), EditClockActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_set_clock,container,false);
        ListView listView = (ListView)view.findViewById(R.id.clock_list);
        ClockSettingAdapter clockSettingAdapter = new ClockSettingAdapter(getActivity(),MyApp.getApp().getServerConfigManager().getTimer());
        listView.setAdapter(clockSettingAdapter);
        return view;
    }

    @Override
    public void setTopBar() {
        BaseActivity baseActivity = myGetActivity();
        CommonTopBar commonTopBar = baseActivity.getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.tab_label_set_time));
        commonTopBar.setRightIconView(R.drawable.add);
        commonTopBar.setIconView(null, myOnClickListener);
        commonTopBar.setRoundLeftIconView(null);
    }

    private class ClockSettingAdapter extends BaseAdapter{

        private Context context;
        private List<ServerConfig.Timer> list;

        public ClockSettingAdapter(Context context,List<ServerConfig.Timer> list){
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
                convertView = new ClockCustomView(context);
            }
            ImageView bgBar = (ImageView)convertView.findViewById(R.id.bg_bar);
            TextView clockName = (TextView)convertView.findViewById(R.id.clock_name);
            TextView clockSetting1 = (TextView)convertView.findViewById(R.id.clock_setting1);
            TextView clockSetting2 = (TextView)convertView.findViewById(R.id.clock_setting2);
            TextView clockTime = (TextView)convertView.findViewById(R.id.clock_time);
            clockName.setText(list.get(position).getName());
            clockTime.setText(list.get(position).getHour()+":"+list.get(position).getMinute());
            String on_off = "";
            String mode = "";
            String fan = "";
            String temp = list.get(position).getTemperature() + getString(R.string.temp_symbol);
            String repeat;
            String week;

            switch (list.get(position).getMode()){
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
            switch (list.get(position).getFan()){
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
            if(list.get(position).isRepeat()){
                repeat = getString(R.string.repeat);
                week = getString(R.string.week_name);
                for(int i = 0; i < list.get(position).getWeek().size()-1; i++){
                    week = week + weekName[list.get(position).getWeek().get(i)] + "|";
                }
                week = week + weekName[list.get(position).getWeek().get(list.get(position).getWeek().size()-1)];
                clockSetting2.setText(repeat + "|" + week);
            }else{
                repeat = getString(R.string.not_repeat);
                clockSetting2.setText(repeat);
            }

            if(list.get(position).isOnoff()){
                on_off = getString(R.string.on);
                bgBar.setImageResource(R.drawable.dc_clock_bg_bar_on);
            }else {
                on_off = getString(R.string.off);
                bgBar.setImageResource(R.drawable.dc_clock_bg_bar_off);
            }
            clockSetting1.setText(on_off + "|" + mode + "|" + fan + "|" + temp);

            //TODO set clock info to clock_view
            return convertView;
        }
    }

    private class ClockCustomView extends LinearLayout {
        public ClockCustomView(Context context) {
            super(context);
            init(context);
        }

        private void init(Context context) {
            inflate(context,R.layout.custom_clock_view,this);
        }
    }
}
