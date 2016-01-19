package ac.airconditionsuit.app.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.entity.Room;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.util.CheckUtil;
import ac.airconditionsuit.app.view.CommonDeviceView;

/**
 * Created by Administrator on 2015/9/26.
 */
public class ChangeRoomNameActivity extends BaseActivity{
    private MyOnClickListener myOnClickListener = new MyOnClickListener(){
        @Override
        public void onClick(View v) {
            super.onClick(v);
            Intent intent = new Intent();
            switch (v.getId()){
                case R.id.add_room_page:
                    String check_name = CheckUtil.checkLength(roomName,20,R.string.room_name_empty_info,R.string.room_name_too_long_info);
                    if(check_name == null){
                        return;
                    }

                    for(int i = 0; i < nameList.size(); i++){
                        if(i != index){
                            if(nameList.get(i).equals(check_name)){
                                MyApp.getApp().showToast("房间名称不能重复");
                                return;
                            }
                        }
                    }
                    intent.putExtra("name",check_name);
                    intent.putExtra("room_index",index);
                    intent.putExtra("room",room.toJsonString());
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }
        }
    };
    private EditText roomName;
    private int index;
    private Room room;
    private ArrayList<String> nameList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_setting_change_room_name);
        super.onCreate(savedInstanceState);

        index = getIntent().getIntExtra("index",-1);
        nameList = getIntent().getStringArrayListExtra("name_list");
        room = new Gson().fromJson(getIntent().getStringExtra("room"),Room.class);
        RelativeLayout ChangeRoomNamePage = (RelativeLayout) findViewById(R.id.add_room_page);
        roomName = (EditText) findViewById(R.id.room_name);
        roomName.setText(room.getName());
        roomName.setSelection(room.getName().length());
        ChangeRoomNamePage.setOnClickListener(myOnClickListener);
        roomName.setOnClickListener(myOnClickListener);

        setOnclickListenerOnTextViewDrawable(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof EditText) {
                    ((EditText) v).setText("");
                }
            }
        }, roomName);
        GridView gridView = (GridView) findViewById(R.id.air_list);
        AirListAdapter airListAdapter = new AirListAdapter(this);
        gridView.setAdapter(airListAdapter);

    }

    private class AirListAdapter extends BaseAdapter{

        private Context context;

        public AirListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return room.getElements().size();
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
            ((CommonDeviceView)convertView).setBackgroundResource(R.drawable.drag_device_transparent_small);
            ((CommonDeviceView)convertView).setBgIcon(R.drawable.drag_device_icon);
            TextView nameView = (TextView)convertView.findViewById(R.id.bottom_name);
            TextView nameView2 = (TextView)convertView.findViewById(R.id.right_up_text);
            switch (UIManager.UITYPE){
                case 1:
                    nameView.setTextColor(getResources().getColor(R.color.text_color_black));
                    nameView2.setTextColor(getResources().getColor(R.color.text_color_white));
                    break;
                case 2:
                    nameView.setTextColor(getResources().getColor(R.color.text_color_black));
                    nameView2.setTextColor(getResources().getColor(R.color.text_color_black));
                    break;
                default:
                    nameView.setTextColor(getResources().getColor(R.color.text_color_black));
                    nameView2.setTextColor(getResources().getColor(R.color.text_color_white));
                    break;
            }
            if(UIManager.UITYPE == UIManager.HIT){
                ((CommonDeviceView)convertView).setBottomNameColor();
            }
            ((CommonDeviceView)convertView).setBottomName(MyApp.getApp().getServerConfigManager().getDevices_new().
                    get(room.getElements().get(position)).getName());
            ((CommonDeviceView) convertView).setRightUpText(MyApp.getApp().getServerConfigManager().getDevices_new().
                    get(room.getElements().get(position)).getFormatNameByIndoorIndexAndAddress());
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(ChangeRoomNameActivity.this).setTitle(R.string.tip).setMessage(getString(R.string.is_delete_device_from_room)).
                            setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    room.getElements().remove(position);
                                    notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            }).setNegativeButton(R.string.cancel, null).setCancelable(false).show();

                    return true;
                }
            });

            return convertView;
        }
    }
}
