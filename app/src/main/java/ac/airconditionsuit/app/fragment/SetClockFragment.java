package ac.airconditionsuit.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;

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
        commonTopBar.setIconView(null,myOnClickListener);
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
