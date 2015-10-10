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
import android.widget.TextView;

import java.util.List;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.entity.ServerConfig;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.util.CheckUtil;
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
                case R.id.delete_scene:
                    TextView toDoDelete = new TextView(EditSceneActivity.this);
                    toDoDelete.setGravity(Gravity.CENTER);
                    toDoDelete.setText(getString(R.string.is_delete_scene));
                    toDoDelete.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                    new AlertDialog.Builder(EditSceneActivity.this).setView(toDoDelete).
                            setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MyApp.getApp().getServerConfigManager().deleteScene(index);
                                    MyApp.getApp().getServerConfigManager().writeToFile();
                                    Intent intent1 = new Intent();
                                    setResult(RESULT_OK, intent1);
                                    finish();
                                }
                            }).setNegativeButton(R.string.cancel, null).setCancelable(false).show();
                    break;
                case R.id.right_icon:
                    final String check_scene_name = CheckUtil.checkLength(sceneName, 10, R.string.pls_input_scene_name, R.string.scene_name_length_too_long);
                    if (check_scene_name == null)
                        return;
                    if(is_add){
                        //TODO add device setting
                        ServerConfig serverConfig = new ServerConfig();
                        ServerConfig.Scene scene = serverConfig.new Scene();
                        scene.setName(check_scene_name);
                        MyApp.getApp().getServerConfigManager().addScene(scene);
                        finish();
                    }else{
                        //TODO save device setting
                        MyApp.getApp().getServerConfigManager().getScene().get(index).setName(check_scene_name);
                        MyApp.getApp().getServerConfigManager().writeToFile();
                        Intent intent2 = new Intent();
                        intent2.putExtra("index",index);
                        intent2.putExtra("title",check_scene_name);
                        setResult(RESULT_OK, intent2);
                        finish();
                    }

                    break;

            }
        }
    };
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_edit_scene);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        index = getIntent().getIntExtra("index", -1);
        String scene_name  = getIntent().getStringExtra("title");
        is_add = scene_name.equals("");

        commonTopBar.setTitle(scene_name);
        commonTopBar.setIconView(myOnClickListener, myOnClickListener);
        sceneName = (EditText)findViewById(R.id.scene_name_text);
        sceneName.setText(scene_name);
        sceneName.setSelection(scene_name.length());

        TextView deleteScene = (TextView)findViewById(R.id.delete_scene);
        deleteScene.setOnClickListener(myOnClickListener);
        if(is_add){
            deleteScene.setVisibility(View.GONE);
        }

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
                    //TODO PICKER VIEW
                }
            });

            return convertView;
        }
    }

}
