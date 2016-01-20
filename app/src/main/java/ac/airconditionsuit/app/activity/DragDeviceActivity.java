package ac.airconditionsuit.app.activity;

import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.entity.DeviceFromServerConfig;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.entity.Room;
import ac.airconditionsuit.app.entity.Section;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.util.VibratorUtil;
import ac.airconditionsuit.app.view.CommonDeviceView;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/9/21.
 */
public class DragDeviceActivity extends BaseActivity {

    private static final int REQUEST_CODE_CHANGE_NAME = 100;
    private MyOnClickListener myOnClickListener = new MyOnClickListener() {
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.left_icon:
//                    MyApp.getApp().getServerConfigManager().submitRoomChanges(index, dragDeviceAdapter.rooms);
                    finish();
                    break;
                case R.id.right_icon:
                    MyApp.getApp().getServerConfigManager().submitRoomChanges(index, dragDeviceAdapter.rooms);
                    finish();
                    break;
            }

        }
    };
    private DragDeviceAdapter dragDeviceAdapter;
    private int index;
    private int room_num = 2000;

    private class DragDeviceAdapter extends BaseAdapter {
        private Context context;
        List<Room> rooms;

        public DragDeviceAdapter(Context context, List<Room> rooms) {
            this.context = context;
            this.rooms = rooms;
        }

        @Override
        public int getCount() {
            return rooms.size();
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
            if (convertView == null) {
                convertView = new CommonDeviceView(context);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.bottom_name);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.bg_icon);
            ImageView imageView1 = (ImageView) convertView.findViewById(R.id.right_up_icon);

            textView.setText(rooms.get(position).getName());
            if (rooms.get(position).getElements() == null) {
                imageView.setImageResource(R.drawable.drag_device_room_bar);
            }
            switch (rooms.get(position).getElements().size()) {
                case 0:
                    imageView.setImageResource(R.drawable.drag_device_room_bar);
                    break;
                case 1:
                    imageView.setImageResource(R.drawable.room_device1);
                    break;
                case 2:
                    imageView.setImageResource(R.drawable.room_device2);
                    break;
                case 3:
                    imageView.setImageResource(R.drawable.room_device3);
                    break;
                case 4:
                    imageView.setImageResource(R.drawable.room_device4);
                    break;
                case 5:
                    imageView.setImageResource(R.drawable.room_device5);
                    break;
                case 6:
                    imageView.setImageResource(R.drawable.room_device6);
                    break;
                case 7:
                    imageView.setImageResource(R.drawable.room_device7);
                    break;
                case 8:
                    imageView.setImageResource(R.drawable.room_device8);
                    break;
                default:
                    imageView.setImageResource(R.drawable.room_device9);
                    break;

            }
            imageView1.setImageResource(R.drawable.drag_device_delete);

            imageView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(DragDeviceActivity.this).setTitle(R.string.tip).setMessage(R.string.is_delete_room).
                            setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dragDeviceAdapter.deleteRoom(position);
                                    //MyApp.getApp().getServerConfigManager().deleteRoom(index,position);
                                }
                            }).setNegativeButton(R.string.cancel, null).setCancelable(false).show();
                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ArrayList<String> room_name_list = new ArrayList<>();

                    //房间名称在所有楼层内均不能重复
                    for (int j = 0; j < MyApp.getApp().getServerConfigManager().getSections().size(); j++) {
                        List<Room> pages = MyApp.getApp().getServerConfigManager().getSections().get(j).getPages();
                        for (int i = 0; i < pages.size(); i++) {
                            room_name_list.add(pages.get(i).getName());
                        }
                    }
                    for (int i = 0; i < dragDeviceAdapter.rooms.size(); i++) {
                        room_name_list.add(dragDeviceAdapter.rooms.get(i).getName());
                    }
                    Intent intent = new Intent();
                    intent.putExtra("index", position);
                    intent.putExtra("room", rooms.get(position).toJsonString());
                    intent.putStringArrayListExtra("name_list", room_name_list);
                    intent.setClass(DragDeviceActivity.this, ChangeRoomNameActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_CHANGE_NAME);
                }
            });

            convertView.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    switch (event.getAction()) {
                        case DragEvent.ACTION_DRAG_STARTED:
                            return true;
                        case DragEvent.ACTION_DRAG_ENTERED:
                            return true;
                        case DragEvent.ACTION_DRAG_LOCATION:
                            return true;
                        case DragEvent.ACTION_DRAG_EXITED:
                            return true;
                        case DragEvent.ACTION_DROP:
                            ClipData clipData = event.getClipData();
                            int index = Integer.parseInt(clipData.getItemAt(0).getText().toString());
                            boolean isExist = false;
                            for (int temp : rooms.get(position).getElements()) {
                                if (temp == index) {
                                    isExist = true;
                                    break;
                                }
                            }
                            if (isExist) {
                                MyApp.getApp().showToast(R.string.device_already_exist);
                            } else {
                                rooms.get(position).addAirCondition(index);
                                notifyDataSetChanged();
                            }
                            return true;
                        case DragEvent.ACTION_DRAG_ENDED:
                            return true;
                        default:
                            return true;
                    }
                }
            });


            return convertView;
        }

        private void deleteRoom(int position) {
            rooms.remove(position);
            notifyDataSetChanged();
        }

        public void addRoom(Room room) {
            rooms.add(room);
            notifyDataSetChanged();
        }

        public void changeName(String name, int room_index) {
            rooms.get(room_index).setName(name);
            notifyDataSetChanged();
        }

        public void replaceRoom(int room_index, Room room_t) {
            rooms.get(room_index).setElements(room_t.getElements());
            notifyDataSetChanged();
        }
    }

    private String getUniqueName() {
        String newName = getString(R.string.new_room) + room_num;

        boolean exist = false;
        do {
            exist = false;
            for(int j = 0; j < MyApp.getApp().getServerConfigManager().getSections().size(); j++) {
                List<Room> pages = MyApp.getApp().getServerConfigManager().getSections().get(j).getPages();
                for (int k = 0; k < pages.size(); k++) {
                    if (newName.equals(pages.get(k).getName())) {
                        exist = true;
                    }
                }
            }

            for (int i = 0; i < dragDeviceAdapter.rooms.size(); i++) {
                if (newName.equals(dragDeviceAdapter.rooms.get(i).getName())) {
                    exist = true;
                }
            }

            if (exist) {
                room_num++;
                newName = getString(R.string.new_room) + room_num;
            }
        } while (exist);

        return newName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_setting_drag_device);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        LinearLayout bottomBar = (LinearLayout) findViewById(R.id.bottom_bar);
        switch (UIManager.UITYPE) {
            case 1:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_hit);
                commonTopBar.setRightIconView(R.drawable.top_bar_save_hit);
                bottomBar.setBackgroundResource(R.drawable.under_bar_hit);
                break;
            case 2:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_dc);
                commonTopBar.setRightIconView(R.drawable.top_bar_save_dc);
                bottomBar.setBackgroundResource(R.drawable.under_bar_dc);
                break;
            default:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_dc);
                commonTopBar.setRightIconView(R.drawable.top_bar_save_dc);
                bottomBar.setBackgroundResource(R.drawable.under_bar_hit);
                break;
        }
        commonTopBar.setIconView(myOnClickListener, myOnClickListener);
        Intent intent = getIntent();
        String section = intent.getStringExtra("section");
        index = Integer.parseInt(intent.getStringExtra("position"));

        TextView sectionDeviceNum = (TextView)findViewById(R.id.section_device_num);
        int device_num;
        if(MyApp.getApp().getServerConfigManager().getDevices_new() == null || MyApp.
                getApp().getServerConfigManager().getDevices_new().size() == 0){
            device_num = 0;
        }else {
            device_num =  MyApp.getApp().getServerConfigManager().getDevices_new().size();
        }
        sectionDeviceNum.setText(getString(R.string.section_device_num1) + device_num + getString(R.string.section_device_num2));
        final Section room_info = Section.getSectionFromJsonString(section);
        commonTopBar.setTitle(room_info.getName());
        final List<DeviceFromServerConfig> devices_new = MyApp.getApp().getServerConfigManager().getDevices_new();
        for (int i = 0; i < devices_new.size(); i++) {
            final CommonDeviceView commonDeviceView = new CommonDeviceView(DragDeviceActivity.this);
            commonDeviceView.setBackgroundResource(R.drawable.drag_device_transparent_small);
            commonDeviceView.setBgIcon(R.drawable.drag_device_icon);
            TextView nameView = (TextView) commonDeviceView.findViewById(R.id.bottom_name);
            TextView nameView2 = (TextView) commonDeviceView.findViewById(R.id.right_up_text);
            switch (UIManager.UITYPE) {
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
            commonDeviceView.setBottomName(devices_new.get(i).getName());
            commonDeviceView.setRightUpText(MyApp.getApp().getServerConfigManager().getDevices_new().get(i).getFormatNameByIndoorIndexAndAddress());
            bottomBar.addView(commonDeviceView);
            final int finalI = i;
            commonDeviceView
                    .setOnLongClickListener(
                            new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    VibratorUtil.vibrate(DragDeviceActivity.this, 100);
                                    ClipData.Item item = new ClipData.Item(String.valueOf(finalI));
                                    ClipData dragData = new ClipData((CharSequence) v.getTag(),
                                            new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
                                    // Instantiates the drag shadow builder.
                                    View.DragShadowBuilder myShadow = new View.DragShadowBuilder(commonDeviceView);

                                    v.startDrag(dragData,  // the data to be dragged
                                            myShadow,  // the drag shadow builder
                                            null,      // no need to use local data
                                            0          // flags (not currently used, set to 0)
                                    );
                                    return false;
                                }
                            }
                    );
        }
        findViewById(R.id.receiver).setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        return true;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        return true;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        return true;
                    case DragEvent.ACTION_DROP:
                        if(dragDeviceAdapter.rooms.size() >= 9){
                            MyApp.getApp().showToast("房间数量不能超过9个");
                            return false;
                        }
                        ClipData clipData = event.getClipData();
                        Room room = new Room();
                        String room_check_name = getUniqueName();

                        room.setName(room_check_name);
                        room_num++;
                        room.addAirCondition(Integer.parseInt(clipData.getItemAt(0).getText().toString()));
                        dragDeviceAdapter.addRoom(room);
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED:
                        return true;
                    default:
                        return true;
                }
            }
        });

        GridView gridView = (GridView) findViewById(R.id.receiver);
        dragDeviceAdapter = new DragDeviceAdapter(this, room_info.getPages());
        gridView.setAdapter(dragDeviceAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case REQUEST_CODE_CHANGE_NAME:
                    String room_name = data.getStringExtra("name");
                    int room_index = data.getIntExtra("room_index", -1);
                    Room room_temp = new Gson().fromJson(data.getStringExtra("room"),Room.class);
                    dragDeviceAdapter.changeName(room_name, room_index);
                    dragDeviceAdapter.replaceRoom(room_index,room_temp);
                    //MyApp.getApp().getServerConfigManager().renameRoom(index,room_index,room_name);
                    break;
            }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
