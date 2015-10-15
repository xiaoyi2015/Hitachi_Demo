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

import java.util.ArrayList;
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
                        ServerConfig serverConfig = new ServerConfig();
                        ServerConfig.Scene scene = serverConfig.new Scene();
                        ArrayList<ServerConfig.Command> commands = new ArrayList<>();
                        for(int i = 0; i < temp_on_off.size(); i++){
                            if(temp_on_off.get(i) != 2){
                                ServerConfig.Command command = serverConfig.new Command();
                                command.setAddress(MyApp.getApp().getServerConfigManager().getDevices().get(i).getIndoorindex());
                                command.setOnoff(temp_on_off.get(i));
                                command.setMode(temp_mode.get(i));
                                command.setFan(temp_fan.get(i));
                                command.setTemperature((float) (temp_temp.get(i) + 18));
                                commands.add(command);
                            }
                        }
                        scene.setCommonds(commands);
                        scene.setName(check_scene_name);
                        MyApp.getApp().getServerConfigManager().addScene(scene);
                        finish();
                    }else{
                        ServerConfig serverConfig1 = new ServerConfig();
                        MyApp.getApp().getServerConfigManager().getScene().get(index).getCommonds().clear();
                        for(int i = 0; i < temp_on_off.size(); i++){
                            if(temp_on_off.get(i) != 2){
                                ServerConfig.Command command = serverConfig1.new Command();
                                command.setAddress(MyApp.getApp().getServerConfigManager().getDevices().get(i).getIndoorindex());
                                command.setOnoff(temp_on_off.get(i));
                                command.setMode(temp_mode.get(i));
                                command.setFan(temp_fan.get(i));
                                command.setTemperature((float) (temp_temp.get(i) + 18));
                                MyApp.getApp().getServerConfigManager().getScene().get(index).getCommonds().add(command);
                            }
                        }
                        MyApp.getApp().getServerConfigManager().getScene().get(index).setName(check_scene_name);
                        MyApp.getApp().getServerConfigManager().writeToFile();
                        Intent intent2 = new Intent();
                        setResult(RESULT_OK, intent2);
                        finish();
                    }

                    break;

            }
        }
    };
    private int index;
    private ArrayList<Integer> temp_on_off = new ArrayList<>();
    private ArrayList<Integer> temp_mode = new ArrayList<>();
    private ArrayList<Integer> temp_fan = new ArrayList<>();
    private ArrayList<Integer> temp_temp = new ArrayList<>();

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
        for(int i = 0; i < MyApp.getApp().getServerConfigManager().getDevices().size(); i++){
            temp_on_off.add(2);
            temp_mode.add(0);
            temp_fan.add(0);
            temp_temp.add(7);
        }
        if(!is_add){
            for (int i = 0; i < MyApp.getApp().getServerConfigManager().getScene().get(index).getCommonds().size(); i++) {
                int temp_index_device = MyApp.getApp().getServerConfigManager().
                        getDeviceIndexFromAddress(MyApp.getApp().getServerConfigManager().getScene().get(index).getCommonds().get(i).getAddress());
                if (temp_index_device != -1) {
                    temp_on_off.set(temp_index_device, MyApp.getApp().getServerConfigManager().getScene().get(index).getCommonds().get(i).getOnoff());
                    temp_mode.set(temp_index_device, MyApp.getApp().getServerConfigManager().getScene().get(index).getCommonds().get(i).getMode());
                    temp_fan.set(temp_index_device, MyApp.getApp().getServerConfigManager().getScene().get(index).getCommonds().get(i).getFan());
                    temp_temp.set(temp_index_device, (int) (MyApp.getApp().getServerConfigManager().getScene().get(index).getCommonds().get(i).getTemperature() - 18));
                }
            }
        }

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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = new CommonButtonWithArrow(context);
            }
            TextView deviceName = (TextView)convertView.findViewById(R.id.label_text);
            deviceName.setText(list.get(position).getName());
            deviceName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);

            AirModePickerView airModePickerView1 = new AirModePickerView(context);
            airModePickerView1.setOnOffView(R.string.cancel);
            CommonWheelView onOffView1 = (CommonWheelView) airModePickerView1.findViewById(R.id.set_on_off);
            CommonWheelView modeView1 = (CommonWheelView) airModePickerView1.findViewById(R.id.set_mode);
            CommonWheelView fanView1 = (CommonWheelView) airModePickerView1.findViewById(R.id.set_fan);
            CommonWheelView tempView1 = (CommonWheelView) airModePickerView1.findViewById(R.id.set_temp);

            final TextView settingText = (TextView)convertView.findViewById(R.id.online_text);
            settingText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
            if (temp_on_off.get(position) != 2) {
                settingText.setText(onOffView1.getItemText(temp_on_off.get(position)) + "|" + modeView1.getItemText(temp_mode.get(position)) + "|"
                        + fanView1.getItemText(temp_fan.get(position)) + "|" + tempView1.getItemText(temp_temp.get(position)));
                setModeTextColor(settingText, temp_on_off.get(position), temp_mode.get(position));
            } else {
                settingText.setText("");
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final AirModePickerView airModePickerView = new AirModePickerView(context);
                    airModePickerView.setOnOffView(R.string.cancel);
                    final CommonWheelView onOffView = (CommonWheelView) airModePickerView.findViewById(R.id.set_on_off);
                    final CommonWheelView modeView = (CommonWheelView) airModePickerView.findViewById(R.id.set_mode);
                    final CommonWheelView fanView = (CommonWheelView) airModePickerView.findViewById(R.id.set_fan);
                    final CommonWheelView tempView = (CommonWheelView) airModePickerView.findViewById(R.id.set_temp);

                    onOffView.setDefault(temp_on_off.get(position));
                    modeView.setDefault(temp_mode.get(position));
                    fanView.setDefault(temp_fan.get(position));
                    tempView.setDefault(temp_temp.get(position));

                    airModePickerView.setMinimumHeight(400);

                    new AlertDialog.Builder(EditSceneActivity.this).setTitle(R.string.choose_scene_air_mode).setView(airModePickerView).
                            setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    temp_on_off.set(position, onOffView.getSelected());
                                    temp_mode.set(position, modeView.getSelected());
                                    temp_fan.set(position, fanView.getSelected());
                                    temp_temp.set(position, tempView.getSelected());

                                    if (temp_on_off.get(position) != 2) {
                                        settingText.setText(onOffView.getItemText(temp_on_off.get(position)) + "|" + modeView.getItemText(temp_mode.get(position))
                                                + "|" + fanView.getItemText(temp_fan.get(position)) + "|" + tempView.getItemText(temp_temp.get(position)));
                                        setModeTextColor(settingText,temp_on_off.get(position),temp_mode.get(position));
                                    }else{
                                        settingText.setText("");
                                    }
                                }
                            }).setNegativeButton(R.string.cancel, null).setCancelable(false).show();

                }
            });

            return convertView;
        }

        private void setModeTextColor(TextView settingText,int on_off_color,int mode_color) {
            if(on_off_color == 0){
                settingText.setTextColor(getResources().getColor(R.color.text_color_gray));
            }else{
                switch (mode_color){
                    case 0:
                        settingText.setTextColor(getResources().getColor(R.color.mode_cool_blue));
                        break;
                    case 1:
                        settingText.setTextColor(getResources().getColor(R.color.mode_heat_pink));
                        break;
                    case 2:
                        settingText.setTextColor(getResources().getColor(R.color.mode_dry_purple));
                        break;
                    case 3:
                        settingText.setTextColor(getResources().getColor(R.color.mode_fan_green));
                        break;
                }
            }
        }
    }

}
