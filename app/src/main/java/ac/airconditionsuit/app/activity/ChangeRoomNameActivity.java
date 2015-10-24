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

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.entity.Room;
import ac.airconditionsuit.app.listener.MyOnClickListener;
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
                    intent.putExtra("name",roomName.getText().toString());
                    intent.putExtra("room_index",index);
                    intent.putExtra("room",room.toJsonString());
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }
        }
    };
    private EditText roomName;
    private String index;
    private Room room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_setting_change_room_name);
        super.onCreate(savedInstanceState);

        index = getIntent().getStringExtra("index");
        room = new Gson().fromJson(getIntent().getStringExtra("room"),Room.class);
        RelativeLayout ChangeRoomNamePage = (RelativeLayout) findViewById(R.id.add_room_page);
        roomName = (EditText) findViewById(R.id.room_name);
        roomName.setText(getString(R.string.new_room));
        roomName.setSelection(getString(R.string.new_room).length());
        ChangeRoomNamePage.setOnClickListener(myOnClickListener);
        roomName.setOnClickListener(myOnClickListener);

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
                    nameView.setTextColor(getResources().getColor(R.color.text_color_white));
                    nameView2.setTextColor(getResources().getColor(R.color.text_color_white));
                    break;
                case 2:
                    break;
                default:
                    nameView.setTextColor(getResources().getColor(R.color.text_color_white));
                    nameView2.setTextColor(getResources().getColor(R.color.text_color_white));
                    break;
            }
            if(UIManager.UITYPE == UIManager.HIT){
                ((CommonDeviceView)convertView).setBottomNameColor();
            }
            ((CommonDeviceView)convertView).setBottomName(MyApp.getApp().getServerConfigManager().getDevices().
                    get(room.getElements().get(position)).getName());
            if(MyApp.getApp().getServerConfigManager().getDevices().get(room.getElements().get(position)).getIndooraddress()<10) {
                ((CommonDeviceView) convertView).setRightUpText(String.valueOf(room.getElements().get(position))
                        + "-0" + MyApp.getApp().getServerConfigManager().getDevices().get(room.getElements().get(position)).getIndooraddress());
            }else{
                ((CommonDeviceView) convertView).setRightUpText(String.valueOf(room.getElements().get(position))
                        + "-" + MyApp.getApp().getServerConfigManager().getDevices().get(room.getElements().get(position)).getIndooraddress());
            }
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
