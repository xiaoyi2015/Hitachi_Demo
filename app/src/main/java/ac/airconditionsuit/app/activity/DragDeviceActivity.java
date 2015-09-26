package ac.airconditionsuit.app.activity;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
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

    private static final int REQUEST_CODE_ADD_ROOM = 100;
    private MyOnClickListener myOnClickListener = new MyOnClickListener() {
        @Override
        public void onClick(View v) {
            super.onClick(v);

        }
    };

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
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = new CommonDeviceView(context);

            }
            TextView textView = (TextView)convertView.findViewById(R.id.bottom_name);
            ImageView imageView = (ImageView)convertView.findViewById(R.id.bg_icon);
            ImageView imageView1 = (ImageView)convertView.findViewById(R.id.right_up_icon);

            textView.setText(rooms.get(position).getName());
            imageView.setImageResource(R.drawable.drag_setting_room_bar);
            imageView1.setImageResource(R.drawable.drag_setting_cancel);

            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_setting_drag_device);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        Intent intent = getIntent();
        String section = intent.getStringExtra("section");
        final Section room_info = Section.getSectionFromJsonString(section);
        commonTopBar.setTitle(room_info.getName());

        final CommonDeviceView drag_view = (CommonDeviceView) findViewById(R.id.device1);
        drag_view.setOnLongClickListener(new View.OnLongClickListener() {
                                             @Override
                                             public boolean onLongClick(View v) {
                                                 VibratorUtil.vibrate(DragDeviceActivity.this, 100);
                                                 ClipData.Item item = new ClipData.Item("123");
                                                 ClipData dragData = new ClipData((CharSequence) v.getTag(),
                                                         new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
                                                 // Instantiates the drag shadow builder.
                                                 View.DragShadowBuilder myShadow = new View.DragShadowBuilder(drag_view);

                                                 v.startDrag(dragData,  // the data to be dragged
                                                         myShadow,  // the drag shadow builder
                                                         null,      // no need to use local data
                                                         0          // flags (not currently used, set to 0)
                                                 );
                                                 return false;
                                             }
                                         }
        );

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
                        room.setName("111");
                        MyApp.getApp().getServerConfigManager().addRoom(room_info, room);
                        shortStartActivityForResult(AddRoomActivity.class, REQUEST_CODE_ADD_ROOM);
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
        DragDeviceAdapter dragDeviceAdapter = new DragDeviceAdapter(this,room_info.getPages());
        gridView.setAdapter(dragDeviceAdapter);

    }
    /**
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case REQUEST_CODE_ADD_ROOM:
                    break;

            }
        super.onActivityResult(requestCode, resultCode, data);
    }**/
}
