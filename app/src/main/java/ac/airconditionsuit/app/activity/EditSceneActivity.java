package ac.airconditionsuit.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.entity.ServerConfig;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.view.CommonButtonWithArrow;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/10/6.
 */
public class EditSceneActivity extends BaseActivity{

    private boolean is_add;
    private EditText sceneName;

    private MyOnClickListener myOnClickListener = new MyOnClickListener() {
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.left_icon:
                    finish();
                    break;
                case R.id.right_icon:
                    if(is_add){
                        //TODO add a scene
                    }else{
                        //TODO edit a scene
                    }
                    Intent intent = new Intent();
                    intent.putExtra("isAdd",is_add);
                    intent.putExtra("title",sceneName.getText());
                    setResult(RESULT_OK, intent);
                    finish();
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
        if(scene_name.equals("")){
            is_add = true;
        }else{
            is_add = false;
        }
        commonTopBar.setTitle(scene_name);
        commonTopBar.setIconView(myOnClickListener, myOnClickListener);
        sceneName = (EditText)findViewById(R.id.scene_name_text);
        sceneName.setText(scene_name);
        sceneName.setSelection(scene_name.length());

        ListView listView = (ListView)findViewById(R.id.air_device_list);
        List<ServerConfig.Device> devices = MyApp.getApp().getServerConfigManager().getDevices();
        AirDeviceSceneSettingAdapter airDeviceSceneSettingAdapter = new AirDeviceSceneSettingAdapter(EditSceneActivity.this,devices);
        listView.setAdapter(airDeviceSceneSettingAdapter);

        setListViewHeightBasedOnChildren(listView);

        setOnclickListenerOnTextViewDrawable(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof EditText) {
                    ((EditText) v).setText("");
                }
            }
        }, sceneName);
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

    private class AirDeviceSceneSettingAdapter extends BaseAdapter{

        private Context context;
        List<ServerConfig.Device> list;

        public AirDeviceSceneSettingAdapter(Context context, List<ServerConfig.Device> list){
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
            deviceName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            return convertView;
        }
    }

}
