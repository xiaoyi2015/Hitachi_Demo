package ac.airconditionsuit.app.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.activity.RoomAirSettingActivity;
import ac.airconditionsuit.app.entity.Room;

public class SectionAndRoomView extends RelativeLayout {

    private List<Room> rooms;

    public SectionAndRoomView(Context context, List<Room> roomList) {
        super(context);
        this.rooms = roomList;
        init(context);
    }

    public SectionAndRoomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SectionAndRoomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.custom_section_and_room_view, this);
        ImageView arrowIconView = (ImageView) findViewById(R.id.arrow_icon);
        arrowIconView.setImageResource(R.drawable.icon_arrow_right_dc);

        //TODO set room status
        ListView listView = (ListView)findViewById(R.id.room_list);
        MyAirRoomAdapter myAirRoomAdapter = new MyAirRoomAdapter(context);
        listView.setAdapter(myAirRoomAdapter);
        setListViewHeightBasedOnChildren(listView);

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

    private class MyAirRoomAdapter extends BaseAdapter{

        private Context context;
        public MyAirRoomAdapter(Context context){
            this.context = context;
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
            if(convertView == null){
                convertView = new RoomCustomView(context);
            }
            ImageView bgBar =(ImageView)convertView.findViewById(R.id.bg_bar);
            ImageView roomMode = (ImageView)convertView.findViewById(R.id.room_mode);
            ImageView roomWindSpeed = (ImageView)convertView.findViewById(R.id.room_wind_speed);
            TextView roomTemp = (TextView)convertView.findViewById(R.id.room_temp);
            TextView roomName = (TextView)convertView.findViewById(R.id.room_name);
            LinearLayout roomView = (LinearLayout)convertView.findViewById(R.id.custom_room_view);
            roomView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(context, RoomAirSettingActivity.class);
                    intent.putExtra("title",rooms.get(position).getName());
                    context.startActivity(intent);
                }
            });

            bgBar.setImageResource(R.drawable.room_bg_bar_off_dc);
            roomMode.setImageResource(R.drawable.cool_off_dc);
            roomName.setText(rooms.get(position).getName());
            roomTemp.setText(R.string.default_temp);
            roomWindSpeed.setImageResource(R.drawable.fan_off1_dc);

            return convertView;
        }
    }

    public class RoomCustomView extends LinearLayout{

        public RoomCustomView(Context context) {
            super(context);
            init(context);
        }

        private void init(Context context) {
            inflate(context, R.layout.custom_room_view,this);
        }

    }

}
