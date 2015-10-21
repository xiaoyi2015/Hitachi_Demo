package ac.airconditionsuit.app.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.entity.DeviceFromServerConfig;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.view.CommonDeviceView;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/10/21.
 */
public class SearchIndoorDeviceActivity extends BaseActivity {

    private MyOnClickListener myOnClickListener = new MyOnClickListener() {
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.left_icon:
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search_indoor_device);
        super.onCreate(savedInstanceState);

        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.indoor_device_manage));
        switch (UIManager.UITYPE){
            case 1:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_hit);
                break;
            case 2:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_dc);
                break;
            default:
                break;
        }
        commonTopBar.setIconView(myOnClickListener, null);

        GridView gridView = (GridView) findViewById(R.id.indoor_device_list);
        IndoorDeviceAdapter indoorDeviceAdapter = new IndoorDeviceAdapter(SearchIndoorDeviceActivity.this);
        gridView.setAdapter(indoorDeviceAdapter);

    }

    private class IndoorDeviceAdapter extends BaseAdapter{

        private Context context;

        public IndoorDeviceAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            if(MyApp.getApp().getServerConfigManager().getDevices() == null)
                return 0;
            return MyApp.getApp().getServerConfigManager().getDevices().size();
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
            if(convertView == null) {
                convertView = new CommonDeviceView(context);
            }
            convertView.setBackgroundResource(R.drawable.drag_device_transparent_big);
            TextView textView = (TextView)convertView.findViewById(R.id.bottom_name);
            ImageView imageView = (ImageView)convertView.findViewById(R.id.bg_icon);
            TextView rightUpText = (TextView)convertView.findViewById(R.id.right_up_text);
            LinearLayout searchIndoorDevice = (LinearLayout)findViewById(R.id.search_indoor_device);
            searchIndoorDevice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO search indoor device
                }
            });

            textView.setText(MyApp.getApp().getServerConfigManager().getDevices().get(position).getName());
            imageView.setImageResource(R.drawable.drag_device_icon);
            rightUpText.setBackgroundResource(R.drawable.drag_device_name_bar);
            int address = MyApp.getApp().getServerConfigManager().getDevices().get(position).getAddress();
            rightUpText.setText(String.valueOf(MyApp.getApp().getServerConfigManager().getDeviceIndexFromAddress(address)) + "-" + address);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final EditText et = new EditText(SearchIndoorDeviceActivity.this);
                    et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    et.setBackgroundResource(R.color.text_color_white);
                    et.setMinHeight(150);
                    et.setHint(MyApp.getApp().getServerConfigManager().getDevices().get(position).getName());
                    new AlertDialog.Builder(SearchIndoorDeviceActivity.this).setTitle(R.string.rename_indoor_device).setView(et).
                            setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String device_new_name = et.getText().toString();
                                    MyApp.getApp().getServerConfigManager().getDevices().get(position).setName(device_new_name);
                                    MyApp.getApp().getServerConfigManager().writeToFile();
                                    notifyDataSetChanged();
                                }
                            }).setNegativeButton(R.string.cancel, null).setCancelable(false).show();
                }
            });

            return convertView;
        }
    }
}
