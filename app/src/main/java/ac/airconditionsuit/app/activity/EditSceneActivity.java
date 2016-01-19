package ac.airconditionsuit.app.activity;

import ac.airconditionsuit.app.Config.ServerConfigManager;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.entity.Command;
import ac.airconditionsuit.app.entity.DeviceFromServerConfig;
import ac.airconditionsuit.app.entity.Scene;
import ac.airconditionsuit.app.entity.Timer;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.util.CheckUtil;
import ac.airconditionsuit.app.view.AirModePickerView;
import ac.airconditionsuit.app.view.CommonButtonWithArrow;
import ac.airconditionsuit.app.view.CommonModeArrowView;
import ac.airconditionsuit.app.view.CommonTopBar;
import ac.airconditionsuit.app.view.CommonWheelView;

/**
 * Created by Administrator on 2015/10/6.
 */
public class EditSceneActivity extends BaseActivity {

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

                    new AlertDialog.Builder(EditSceneActivity.this).setTitle(R.string.tip).setMessage(getString(R.string.is_delete_scene)).
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
                    final String check_scene_name = CheckUtil.checkLength(sceneName, 20, R.string.pls_input_scene_name, R.string.scene_name_length_too_long);
                    if (check_scene_name == null)
                        return;
                    if (is_add) {
                        for (int i = 0; i < MyApp.getApp().getServerConfigManager().getScene().size(); i++) {
                            if (MyApp.getApp().getServerConfigManager().getScene().get(i).getName().equals(check_scene_name)) {
                                MyApp.getApp().showToast("已存在“" + check_scene_name + "”的场景，请输入其他名称");
                                return;
                            }
                        }
                        Scene scene = new Scene();
                        ArrayList<Command> commands = new ArrayList<>();
                        for (int i = 0; i < temp_on_off.size(); i++) {
                            if (temp_on_off.get(i) != 2) {
                                Command command = new Command();
                                command.setAddress(MyApp.getApp().getServerConfigManager().getDevices_new().get(i).getAddress_new());
                                command.setOnoff(temp_on_off.get(i));
                                command.setMode(temp_mode.get(i));
                                command.setFan(temp_fan.get(i));
                                if (temp_mode.get(i) == 1) {
                                    command.setTemperature((float) (temp_temp.get(i) + 17));
                                } else {
                                    command.setTemperature((float) (temp_temp.get(i) + 19));
                                }

                                commands.add(command);
                            }
                        }
                        scene.setCommands(commands);
                        scene.setName(check_scene_name);
                        MyApp.getApp().getServerConfigManager().addScene(scene);
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        for (int i = 0; i < MyApp.getApp().getServerConfigManager().getScene().size(); i++) {
                            if (i != index) {
                                if (MyApp.getApp().getServerConfigManager().getScene().get(i).getName().equals(check_scene_name)) {
                                    MyApp.getApp().showToast("已存在“" + check_scene_name + "”的场景，请输入其他名称");
                                    return;
                                }
                            }
                        }

                        if (MyApp.getApp().getServerConfigManager().getScene().get(index).getCommands() != null) {
                            MyApp.getApp().getServerConfigManager().getScene().get(index).getCommands().clear();
                        } else {
                            MyApp.getApp().getServerConfigManager().getScene().get(index).setCommands(new ArrayList<Command>());
                        }
                        for (int i = 0; i < temp_on_off.size(); i++) {
                            Log.v("zhulinan: onoff", temp_on_off.get(i).toString());
                            Log.v("zhulinan: adress", String.valueOf(MyApp.getApp().getServerConfigManager().getDevices_new().get(i).getAddress_new()));

                            if (temp_on_off.get(i) != 2) {
                                Command command = new Command();
                                command.setAddress(MyApp.getApp().getServerConfigManager().getDevices_new().get(i).getAddress_new());
                                command.setOnoff(temp_on_off.get(i));
                                command.setMode(temp_mode.get(i));
                                command.setFan(temp_fan.get(i));
                                if (temp_mode.get(i) == 1) {
                                    command.setTemperature((float) (temp_temp.get(i) + 17));
                                } else {
                                    command.setTemperature((float) (temp_temp.get(i) + 19));
                                }
                                MyApp.getApp().getServerConfigManager().getScene().get(index).getCommands().add(command);
                            }
                        }
                        MyApp.getApp().getServerConfigManager().getScene().get(index).setName(check_scene_name);
                        MyApp.getApp().getServerConfigManager().writeToFile(true);
                        setResult(RESULT_OK);
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
        String scene_name = getIntent().getStringExtra("title");
        is_add = scene_name.equals("");
        TextView deleteScene = (TextView) findViewById(R.id.delete_scene);
        commonTopBar.setTitle(scene_name);
        switch (UIManager.UITYPE) {
            case 1:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_hit);
                commonTopBar.setRightIconView(R.drawable.top_bar_save_hit);
                deleteScene.setBackgroundColor(getResources().getColor(R.color.delete_red_hit));
                break;
            case 2:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_dc);
                commonTopBar.setRightIconView(R.drawable.top_bar_save_dc);
                deleteScene.setBackgroundColor(getResources().getColor(R.color.switch_on_pink));
                break;
            default:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_dc);
                commonTopBar.setRightIconView(R.drawable.top_bar_save_dc);
                deleteScene.setBackgroundColor(getResources().getColor(R.color.delete_red_hx));
                break;
        }
        commonTopBar.setIconView(myOnClickListener, myOnClickListener);
        sceneName = (EditText) findViewById(R.id.scene_name_text);
        sceneName.setText(scene_name);
        sceneName.setSelection(scene_name.length());

        deleteScene.setOnClickListener(myOnClickListener);
        for (int i = 0; i < MyApp.getApp().getServerConfigManager().getDevices_new().size(); i++) {
            temp_on_off.add(2);
            temp_mode.add(0);
            temp_fan.add(0);
            temp_temp.add(6);
        }

        if (!is_add) {
            if (MyApp.getApp().getServerConfigManager().getScene().get(index).getCommands() != null) {
                Log.v("zhulinan: command_num", String.valueOf(MyApp.getApp().
                        getServerConfigManager().getScene().get(index).getCommands().size()));
                for (int i = 0; i < MyApp.getApp().getServerConfigManager().getScene().get(index).getCommands().size(); i++) {
                    Log.v("zhulinan: com_address", String.valueOf(MyApp.getApp().getServerConfigManager().getScene().
                            get(index).getCommands().get(i).getAddress()));
                    Log.v("zhulinan: com_onoff", String.valueOf(MyApp.getApp().getServerConfigManager().getScene().
                            get(index).getCommands().get(i).getOnoff()));
                    ServerConfigManager serverConfigManager = MyApp.getApp().getServerConfigManager();
                    int temp_index_device = serverConfigManager.getDeviceIndexFromAddress_new(serverConfigManager.getScene().get(index).getCommands().get(i).getAddress());
                    if (temp_index_device != -1) {
                        temp_on_off.set(temp_index_device, serverConfigManager.getScene().get(index).getCommands().get(i).getOnoff());
                        temp_mode.set(temp_index_device, serverConfigManager.getScene().get(index).getCommands().get(i).getMode());
                        temp_fan.set(temp_index_device, serverConfigManager.getScene().get(index).getCommands().get(i).getFan());
                        if (serverConfigManager.getScene().get(index).getCommands().get(i).getMode() == 1) {
                            temp_temp.set(temp_index_device, (int) (serverConfigManager.getScene().get(index).getCommands().get(i).getTemperature() - 17));
                        } else {
                            temp_temp.set(temp_index_device, (int) (serverConfigManager.getScene().get(index).getCommands().get(i).getTemperature() - 19));
                        }
                    }
                }
            }
        }

        if (is_add) {
            deleteScene.setVisibility(View.GONE);
        }
        ListView listView = (ListView) findViewById(R.id.air_device_list);
        List<DeviceFromServerConfig> devices_new = MyApp.getApp().getServerConfigManager().getDevices_new();
        AirDeviceSceneSettingAdapter airDeviceSceneSettingAdapter = new AirDeviceSceneSettingAdapter(EditSceneActivity.this, devices_new);
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

    private class AirDeviceSceneSettingAdapter extends BaseAdapter {

        private Context context;
        List<DeviceFromServerConfig> list;

        public AirDeviceSceneSettingAdapter(Context context, List<DeviceFromServerConfig> list) {
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
            TextView deviceName;
            switch (UIManager.UITYPE) {
                case 1:
                case 3:
                    if (convertView == null) {
                        convertView = new CommonModeArrowView(context);
                    }
                    deviceName = (TextView) convertView.findViewById(R.id.label_text);
                    deviceName.setText(list.get(position).getName());
                    deviceName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
                    final ImageView mode_view = (ImageView) convertView.findViewById(R.id.mode);
                    final ImageView onoff_view = (ImageView) convertView.findViewById(R.id.onoff);
                    final ImageView wind_view = (ImageView) convertView.findViewById(R.id.wind);
                    final TextView temp_text = (TextView) convertView.findViewById(R.id.temp);
                    final ImageView temp_none = (ImageView) convertView.findViewById(R.id.temp_none);

                    if (temp_on_off.get(position) != 2) {
                        temp_none.setVisibility(View.GONE);
                        temp_text.setVisibility(View.VISIBLE);
                        int flag_temp;
                        if (temp_mode.get(position) == 1) {
                            flag_temp = temp_temp.get(position) + 17;
                        } else {
                            flag_temp = temp_temp.get(position) + 19;
                        }
                        temp_text.setText(flag_temp + getString(R.string.temp_symbol));
                        if (temp_on_off.get(position) == 0) {
                            onoff_view.setImageResource(R.drawable.onoff_off_hit);
                            temp_text.setTextColor(getResources().getColor(R.color.hit_off_gray));
                            switch (temp_mode.get(position)) {
                                case 0:
                                    mode_view.setImageResource(R.drawable.cool_off_hit);
                                    break;
                                case 1:
                                    mode_view.setImageResource(R.drawable.heat_off_hit);
                                    break;
                                case 2:
                                    mode_view.setImageResource(R.drawable.dry_off_hit);
                                    break;
                                case 3:
                                    mode_view.setImageResource(R.drawable.fan_off_hit);
                                    break;
                            }
                            switch (temp_fan.get(position)) {
                                case 0:
                                    wind_view.setImageResource(R.drawable.fan1_off_hit);
                                    break;
                                case 1:
                                    wind_view.setImageResource(R.drawable.fan3_off_hit);
                                    break;
                                case 2:
                                    wind_view.setImageResource(R.drawable.fan5_off_hit);
                                    break;
                            }
                        } else {
                            onoff_view.setImageResource(R.drawable.onoff_on_heat_hit);
                            switch (temp_mode.get(position)) {
                                case 0:
                                    mode_view.setImageResource(R.drawable.cool_on_hit);
                                    temp_text.setTextColor(getResources().getColor(R.color.hit_cool_blue));
                                    switch (temp_fan.get(position)) {
                                        case 0:
                                            wind_view.setImageResource(R.drawable.fan1_on_cool_dry_fan_hit);
                                            break;
                                        case 1:
                                            wind_view.setImageResource(R.drawable.fan3_on_cool_dry_fan_hit);
                                            break;
                                        case 2:
                                            wind_view.setImageResource(R.drawable.fan5_on_cool_dry_fan_hit);
                                            break;
                                    }
                                    break;
                                case 1:
                                    mode_view.setImageResource(R.drawable.heat_on_hit);
                                    temp_text.setTextColor(getResources().getColor(R.color.hit_heat_red));
                                    switch (temp_fan.get(position)) {
                                        case 0:
                                            wind_view.setImageResource(R.drawable.fan1_on_heat_hit);
                                            break;
                                        case 1:
                                            wind_view.setImageResource(R.drawable.fan3_on_heat_hit);
                                            break;
                                        case 2:
                                            wind_view.setImageResource(R.drawable.fan5_on_heat_hit);
                                            break;
                                    }
                                    break;
                                case 2:
                                    mode_view.setImageResource(R.drawable.dry_on_hit);
                                    temp_text.setTextColor(getResources().getColor(R.color.hit_cool_blue));
                                    switch (temp_fan.get(position)) {
                                        case 0:
                                            wind_view.setImageResource(R.drawable.fan1_on_cool_dry_fan_hit);
                                            break;
                                        case 1:
                                            wind_view.setImageResource(R.drawable.fan3_on_cool_dry_fan_hit);
                                            break;
                                        case 2:
                                            wind_view.setImageResource(R.drawable.fan5_on_cool_dry_fan_hit);
                                            break;
                                    }
                                    break;
                                case 3:
                                    mode_view.setImageResource(R.drawable.fan_on_hit);
                                    temp_text.setTextColor(getResources().getColor(R.color.hit_cool_blue));
                                    switch (temp_fan.get(position)) {
                                        case 0:
                                            wind_view.setImageResource(R.drawable.fan1_on_cool_dry_fan_hit);
                                            break;
                                        case 1:
                                            wind_view.setImageResource(R.drawable.fan3_on_cool_dry_fan_hit);
                                            break;
                                        case 2:
                                            wind_view.setImageResource(R.drawable.fan5_on_cool_dry_fan_hit);
                                            break;
                                    }
                                    break;
                            }
                        }

                    } else {
                        temp_text.setVisibility(View.GONE);
                        temp_none.setVisibility(View.VISIBLE);
                        onoff_view.setImageResource(R.drawable.none_hit);
                        mode_view.setImageResource(R.drawable.none_hit);
                        wind_view.setImageResource(R.drawable.none_hit);
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

                            if (temp_mode.get(position) == 1) {
                                airModePickerView.setTempHeatList();
                                tempView.setDefault(temp_temp.get(position));
                            } else {
                                tempView.setDefault(temp_temp.get(position));
                            }

                            onOffView.setDefault(temp_on_off.get(position));

                            Log.v("liutao:    ", temp_on_off.get(position).toString());
                            modeView.setDefault(temp_mode.get(position));
                            fanView.setDefault(temp_fan.get(position));

                            airModePickerView.setMinimumHeight(400);

                            new AlertDialog.Builder(EditSceneActivity.this).setTitle(R.string.choose_scene_air_mode).setView(airModePickerView).
                                    setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            java.util.Timer timer = new java.util.Timer();
                                            timer.schedule(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            temp_on_off.set(position, onOffView.getSelected());
                                                            temp_mode.set(position, modeView.getSelected());
                                                            temp_fan.set(position, fanView.getSelected());

                                                            if (temp_on_off.get(position) != 2) {
                                                                temp_none.setVisibility(View.GONE);
                                                                temp_text.setVisibility(View.VISIBLE);
                                                                int flag_temp1;
                                                                if (temp_mode.get(position) == 1) {
                                                                    flag_temp1 = tempView.getSelected() + 17;
                                                                } else {
                                                                    flag_temp1 = tempView.getSelected() + 19;
                                                                }
                                                                //test
                                                                if (flag_temp1 > 30) {
                                                                    flag_temp1 = 30;
                                                                }
                                                                if (temp_mode.get(position) == 1) {
                                                                    if (flag_temp1 < 17) {
                                                                        flag_temp1 = 17;
                                                                    }
                                                                } else {
                                                                    if (flag_temp1 < 19) {
                                                                        flag_temp1 = 19;
                                                                    }
                                                                }
                                                                if (temp_mode.get(position) == 1) {
                                                                    temp_temp.set(position, flag_temp1 - 17);
                                                                } else {
                                                                    temp_temp.set(position, flag_temp1 - 19);
                                                                }
                                                                temp_text.setText(flag_temp1 + getString(R.string.temp_symbol));
                                                                if (temp_on_off.get(position) == 0) {
                                                                    onoff_view.setImageResource(R.drawable.onoff_off_hit);
                                                                    temp_text.setTextColor(getResources().getColor(R.color.hit_off_gray));
                                                                    switch (temp_mode.get(position)) {
                                                                        case 0:
                                                                            mode_view.setImageResource(R.drawable.cool_off_hit);
                                                                            break;
                                                                        case 1:
                                                                            mode_view.setImageResource(R.drawable.heat_off_hit);
                                                                            break;
                                                                        case 2:
                                                                            mode_view.setImageResource(R.drawable.dry_off_hit);
                                                                            break;
                                                                        case 3:
                                                                            mode_view.setImageResource(R.drawable.fan_off_hit);
                                                                            break;
                                                                    }
                                                                    switch (temp_fan.get(position)) {
                                                                        case 0:
                                                                            wind_view.setImageResource(R.drawable.fan1_off_hit);
                                                                            break;
                                                                        case 1:
                                                                            wind_view.setImageResource(R.drawable.fan3_off_hit);
                                                                            break;
                                                                        case 2:
                                                                            wind_view.setImageResource(R.drawable.fan5_off_hit);
                                                                            break;
                                                                    }
                                                                } else {
                                                                    onoff_view.setImageResource(R.drawable.onoff_on_heat_hit);
                                                                    switch (temp_mode.get(position)) {
                                                                        case 0:
                                                                            mode_view.setImageResource(R.drawable.cool_on_hit);
                                                                            temp_text.setTextColor(getResources().getColor(R.color.hit_cool_blue));
                                                                            switch (temp_fan.get(position)) {
                                                                                case 0:
                                                                                    wind_view.setImageResource(R.drawable.fan1_on_cool_dry_fan_hit);
                                                                                    break;
                                                                                case 1:
                                                                                    wind_view.setImageResource(R.drawable.fan3_on_cool_dry_fan_hit);
                                                                                    break;
                                                                                case 2:
                                                                                    wind_view.setImageResource(R.drawable.fan5_on_cool_dry_fan_hit);
                                                                                    break;
                                                                            }
                                                                            break;
                                                                        case 1:
                                                                            mode_view.setImageResource(R.drawable.heat_on_hit);
                                                                            temp_text.setTextColor(getResources().getColor(R.color.hit_heat_red));
                                                                            switch (temp_fan.get(position)) {
                                                                                case 0:
                                                                                    wind_view.setImageResource(R.drawable.fan1_on_heat_hit);
                                                                                    break;
                                                                                case 1:
                                                                                    wind_view.setImageResource(R.drawable.fan3_on_heat_hit);
                                                                                    break;
                                                                                case 2:
                                                                                    wind_view.setImageResource(R.drawable.fan5_on_heat_hit);
                                                                                    break;
                                                                            }
                                                                            break;
                                                                        case 2:
                                                                            mode_view.setImageResource(R.drawable.dry_on_hit);
                                                                            temp_text.setTextColor(getResources().getColor(R.color.hit_cool_blue));
                                                                            switch (temp_fan.get(position)) {
                                                                                case 0:
                                                                                    wind_view.setImageResource(R.drawable.fan1_on_cool_dry_fan_hit);
                                                                                    break;
                                                                                case 1:
                                                                                    wind_view.setImageResource(R.drawable.fan3_on_cool_dry_fan_hit);
                                                                                    break;
                                                                                case 2:
                                                                                    wind_view.setImageResource(R.drawable.fan5_on_cool_dry_fan_hit);
                                                                                    break;
                                                                            }
                                                                            break;
                                                                        case 3:
                                                                            mode_view.setImageResource(R.drawable.fan_on_hit);
                                                                            temp_text.setTextColor(getResources().getColor(R.color.hit_cool_blue));
                                                                            switch (temp_fan.get(position)) {
                                                                                case 0:
                                                                                    wind_view.setImageResource(R.drawable.fan1_on_cool_dry_fan_hit);
                                                                                    break;
                                                                                case 1:
                                                                                    wind_view.setImageResource(R.drawable.fan3_on_cool_dry_fan_hit);
                                                                                    break;
                                                                                case 2:
                                                                                    wind_view.setImageResource(R.drawable.fan5_on_cool_dry_fan_hit);
                                                                                    break;
                                                                            }
                                                                            break;
                                                                    }
                                                                }

                                                            } else {
                                                                temp_text.setVisibility(View.GONE);
                                                                temp_none.setVisibility(View.VISIBLE);
                                                                onoff_view.setImageResource(R.drawable.none_hit);
                                                                mode_view.setImageResource(R.drawable.none_hit);
                                                                wind_view.setImageResource(R.drawable.none_hit);
                                                            }
                                                        }
                                                    });
                                                }
                                            }, 500);

                                        }
                                    }).setNegativeButton(R.string.cancel, null).setCancelable(false).show();
                        }
                    });
                    break;
                case 2:
                    if (convertView == null) {
                        convertView = new CommonButtonWithArrow(context);
                    }
                    deviceName = (TextView) convertView.findViewById(R.id.label_text);
                    deviceName.setText(list.get(position).getName());
                    deviceName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);

                    AirModePickerView airModePickerView1 = new AirModePickerView(context);
                    airModePickerView1.setOnOffView(R.string.cancel);
                    CommonWheelView onOffView1 = (CommonWheelView) airModePickerView1.findViewById(R.id.set_on_off);
                    CommonWheelView modeView1 = (CommonWheelView) airModePickerView1.findViewById(R.id.set_mode);
                    CommonWheelView fanView1 = (CommonWheelView) airModePickerView1.findViewById(R.id.set_fan);
                    CommonWheelView tempView1 = (CommonWheelView) airModePickerView1.findViewById(R.id.set_temp);
                    if (temp_mode.get(position) == 1) {
                        airModePickerView1.setTempHeatList();
                    }

                    final TextView settingText = (TextView) convertView.findViewById(R.id.online_text);
                    settingText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
                    if (temp_on_off.get(position) != 2) {
                        if (temp_mode.get(position) == 1) {
                            settingText.setText(onOffView1.getItemText(temp_on_off.get(position)) + "|" + modeView1.getItemText(temp_mode.get(position))
                                    + "|" + fanView1.getItemText(temp_fan.get(position)) + "|" + tempView1.getItemText(temp_temp.get(position)));
                        } else {
                            settingText.setText(onOffView1.getItemText(temp_on_off.get(position)) + "|" + modeView1.getItemText(temp_mode.get(position))
                                    + "|" + fanView1.getItemText(temp_fan.get(position)) + "|" + tempView1.getItemText(temp_temp.get(position)));
                        }
                        setModeTextColor(settingText, temp_on_off.get(position), temp_mode.get(position));
                    } else {
                        settingText.setText(getString(R.string.none) + " " + getString(R.string.none) + " " +
                                getString(R.string.none) + " " + getString(R.string.none) + "  ");
                        settingText.setTextColor(getResources().getColor(R.color.text_color_gray));
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

                            if (temp_mode.get(position) == 1) {
                                airModePickerView.setTempHeatList();
                                tempView.setDefault(temp_temp.get(position));
                            } else {
                                tempView.setDefault(temp_temp.get(position));
                            }

                            onOffView.setDefault(temp_on_off.get(position));
                            modeView.setDefault(temp_mode.get(position));
                            fanView.setDefault(temp_fan.get(position));

                            airModePickerView.setMinimumHeight(400);

                            new AlertDialog.Builder(EditSceneActivity.this).setTitle(R.string.choose_scene_air_mode).setView(airModePickerView).
                                    setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (modeView.getSelected() == 1) {
                                                temp_temp.set(position, tempView.getSelected());
                                            } else {
                                                temp_temp.set(position, tempView.getSelected());
                                            }
                                            temp_on_off.set(position, onOffView.getSelected());
                                            temp_mode.set(position, modeView.getSelected());
                                            temp_fan.set(position, fanView.getSelected());

                                            if (temp_on_off.get(position) != 2) {
                                                if (temp_mode.get(position) == 1) {
                                                    settingText.setText(onOffView.getItemText(temp_on_off.get(position)) + "|" + modeView.getItemText(temp_mode.get(position))
                                                            + "|" + fanView.getItemText(temp_fan.get(position)) + "|" + tempView.getItemText(temp_temp.get(position)));
                                                } else {
                                                    settingText.setText(onOffView.getItemText(temp_on_off.get(position)) + "|" + modeView.getItemText(temp_mode.get(position))
                                                            + "|" + fanView.getItemText(temp_fan.get(position)) + "|" + tempView.getItemText(temp_temp.get(position)));
                                                }
                                                setModeTextColor(settingText, temp_on_off.get(position), temp_mode.get(position));
                                            } else {
                                                settingText.setText(getString(R.string.none) + " " + getString(R.string.none) + " " +
                                                        getString(R.string.none) + " " + getString(R.string.none) + "  ");
                                                settingText.setTextColor(getResources().getColor(R.color.text_color_gray));
                                            }
                                        }
                                    }).setNegativeButton(R.string.cancel, null).setCancelable(false).show();

                        }
                    });
                    break;
            }

            return convertView;
        }

        private void setModeTextColor(TextView settingText, int on_off_color, int mode_color) {
            if (on_off_color == 0) {
                settingText.setTextColor(getResources().getColor(R.color.text_color_gray));
            } else {
                switch (mode_color) {
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
