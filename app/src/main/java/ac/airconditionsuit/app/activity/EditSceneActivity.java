package ac.airconditionsuit.app.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.entity.Device;
import ac.airconditionsuit.app.entity.ServerConfig;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.view.CommonButtonWithArrow;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/10/6.
 */
public class EditSceneActivity extends BaseActivity{

    private MyOnClickListener myOnClickListener = new MyOnClickListener() {
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.left_icon:
                    finish();
                    break;
                case R.id.right_icon:
                    //TODO submit the commands
                    break;

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_edit_scene);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        String scene_name  = getIntent().getStringExtra("title");
        commonTopBar.setTitle(scene_name);
        commonTopBar.setIconView(myOnClickListener, myOnClickListener);
        EditText sceneName = (EditText)findViewById(R.id.scene_name_text);
        sceneName.setText(scene_name);
        sceneName.setSelection(scene_name.length());

        ListView listView = (ListView)findViewById(R.id.air_device_list);
        List<ServerConfig.Device> devices = MyApp.getApp().getServerConfigManager().getDevices();
        AirDeviceSettingAdapter airDeviceSettingAdapter = new AirDeviceSettingAdapter(EditSceneActivity.this,devices);
        listView.setAdapter(airDeviceSettingAdapter);

        setOnclickListenerOnTextViewDrawable(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof EditText) {
                    ((EditText) v).setText("");
                }
            }
        }, sceneName);
    }

    private class AirDeviceSettingAdapter extends BaseAdapter{

        private Context context;
        List<ServerConfig.Device> list;

        public AirDeviceSettingAdapter(Context context,List<ServerConfig.Device> list){
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
            TextView settingText = (TextView)convertView.findViewById(R.id.online_text);
            deviceName.setText(list.get(position).getName());
            deviceName.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            return convertView;
        }
    }

}
