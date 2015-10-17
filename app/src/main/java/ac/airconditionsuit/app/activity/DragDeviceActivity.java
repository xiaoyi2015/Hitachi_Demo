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
            switch (v.getId()){
                case R.id.left_icon:
                    finish();
                    break;
                case R.id.right_icon:
                    MyApp.getApp().getServerConfigManager().submitRoomChanges(index,dragDeviceAdapter.rooms);
                    finish();
                    break;
            }

        }
    };
    private DragDeviceAdapter dragDeviceAdapter;
    private int index;

    private class DragDeviceAdapter extends BaseAdapter {
        private Context context;
        List<Room> rooms;

        public DragDeviceAdapter(Context context,List<Room> rooms){
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
            if(convertView == null) {
                convertView = new CommonDeviceView(context);

            }
            TextView textView = (TextView)convertView.findViewById(R.id.bottom_name);
            ImageView imageView = (ImageView)convertView.findViewById(R.id.bg_icon);
            ImageView imageView1 = (ImageView)convertView.findViewById(R.id.right_up_icon);

            textView.setText(rooms.get(position).getName());
            imageView.setImageResource(R.drawable.drag_device_room_bar);
            imageView1.setImageResource(R.drawable.drag_device_delete);

            imageView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(DragDeviceActivity.this).setMessage(R.string.is_delete_room).
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
                    shortStartActivityForResult(ChangeRoomNameActivity.class,REQUEST_CODE_CHANGE_NAME,"index",String.valueOf(position));
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

        public void changeName(String name,int room_index) {
            rooms.get(room_index).setName(name);
            notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_setting_drag_device);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        switch (UIManager.UITYPE){
            case 1:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_hit);
                commonTopBar.setRightIconView(R.drawable.top_bar_save_hit);
                break;
            case 2:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_dc);
                commonTopBar.setRightIconView(R.drawable.top_bar_save_dc);
                break;
            default:
                break;
        }
        commonTopBar.setIconView(myOnClickListener,myOnClickListener);
        Intent intent = getIntent();
        String section = intent.getStringExtra("section");
        index = Integer.parseInt(intent.getStringExtra("position"));
        final Section room_info = Section.getSectionFromJsonString(section);
        commonTopBar.setTitle(room_info.getName());

        LinearLayout bottomBar = (LinearLayout)findViewById(R.id.bottom_bar);
        List<DeviceFromServerConfig> devices = MyApp.getApp().getServerConfigManager().getDevices();
        for (int i = 0; i<devices.size(); i++) {
            final CommonDeviceView commonDeviceView = new CommonDeviceView(DragDeviceActivity.this);
            commonDeviceView.setBackgroundResource(R.drawable.drag_device_room_bar);
            commonDeviceView.setBgIcon(R.drawable.drag_device_icon);
            commonDeviceView.setBottomName(devices.get(i).getName());
            commonDeviceView.setRightUpText(String .valueOf(MyApp.getApp().getServerConfigManager().getDeviceIndexFromAddress(devices.get(i).getAddress()))
                    + "-" + devices.get(i).getAddress());
            bottomBar.addView(commonDeviceView);
            commonDeviceView.setOnLongClickListener(new View.OnLongClickListener() {
                                                 @Override
                                                 public boolean onLongClick(View v) {
                                                     VibratorUtil.vibrate(DragDeviceActivity.this, 100);
                                                     ClipData.Item item = new ClipData.Item("123");
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
                        Room room = new Room();
                        room.setName(getString(R.string.new_room));
                        //MyApp.getApp().getServerConfigManager().addRoom(index, room);
                        dragDeviceAdapter.addRoom(room);
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED:
                        System.out.println(event.getResult());
                        return true;
                    default:
                        return true;
                }
            }
        });

        GridView gridView = (GridView) findViewById(R.id.receiver);
        dragDeviceAdapter = new DragDeviceAdapter(this,room_info.getPages());
        gridView.setAdapter(dragDeviceAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case REQUEST_CODE_CHANGE_NAME:
                    String room_name = data.getStringExtra("name");
                    int room_index = Integer.parseInt(data.getStringExtra("room_index"));
                    dragDeviceAdapter.changeName(room_name, room_index);
                    //MyApp.getApp().getServerConfigManager().renameRoom(index,room_index,room_name);
                    break;
            }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
