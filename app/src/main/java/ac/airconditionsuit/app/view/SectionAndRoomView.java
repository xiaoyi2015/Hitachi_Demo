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

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.activity.BaseActivity;
import ac.airconditionsuit.app.activity.MainActivity;
import ac.airconditionsuit.app.activity.RoomAirSettingActivity;
import ac.airconditionsuit.app.activity.RoomAirSettingHitActivity;
import ac.airconditionsuit.app.activity.RoomAirSettingHxActivity;
import ac.airconditionsuit.app.aircondition.AirConditionControl;
import ac.airconditionsuit.app.entity.AirCondition;
import ac.airconditionsuit.app.entity.Room;
import ac.airconditionsuit.app.fragment.BaseFragment;
import ac.airconditionsuit.app.fragment.MyAirFragment;

public class SectionAndRoomView extends RelativeLayout {

    private List<Room> rooms;
    private Context context;

    public SectionAndRoomView(Context context, List<Room> roomList) {
        super(context);
        this.rooms = roomList;
        this.context = context;
        init(this.context);
    }

    public SectionAndRoomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(this.context);
    }

    public SectionAndRoomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(this.context);
    }

    private void init(Context context) {
        switch (UIManager.UITYPE) {
            case 1:
                inflate(context, R.layout.custom_section_and_room_view_hit, this);
                break;
            case 2:
                inflate(context, R.layout.custom_section_and_room_view, this);
                break;
            default:
                inflate(context, R.layout.custom_section_and_room_view_hit, this);
                break;
        }
        ImageView arrowIconView = (ImageView) findViewById(R.id.arrow_icon);

        switch (UIManager.UITYPE) {
            case 1:
                arrowIconView.setImageResource(R.drawable.icon_arrow_right_hit);
                break;
            case 2:
                arrowIconView.setImageResource(R.drawable.icon_arrow_right_dc);
                break;
            default:
                arrowIconView.setImageResource(R.drawable.icon_arrow_right_hit);
                break;
        }

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
            ImageView roomMode = (ImageView)convertView.findViewById(R.id.room_mode);
            ImageView roomWindSpeed = (ImageView)convertView.findViewById(R.id.room_wind_speed);
            TextView roomTemp = (TextView)convertView.findViewById(R.id.room_temp);
            TextView roomName = (TextView)convertView.findViewById(R.id.room_name);
            LinearLayout roomView = (LinearLayout)convertView.findViewById(R.id.custom_room_view);
            ImageView roomOnOff;
            ImageView roomTempNone;
            final AirCondition airCondition = new AirCondition();
            if(MyApp.getApp().getAirconditionManager().getAirConditions(rooms.get(position)) == null){
                airCondition.setOnoff(AirConditionControl.EMPTY);
            }else {
                airCondition.setOnoff(MyApp.getApp().getAirconditionManager().getAirConditions(rooms.get(position)).getOnoff());
                airCondition.setMode(MyApp.getApp().getAirconditionManager().getAirConditions(rooms.get(position)).getMode());
                airCondition.setAddress(MyApp.getApp().getAirconditionManager().getAirConditions(rooms.get(position)).getAddress());
                airCondition.setFan(MyApp.getApp().getAirconditionManager().getAirConditions(rooms.get(position)).getFan());
                airCondition.setTemperature(MyApp.getApp().getAirconditionManager().getAirConditions(rooms.get(position)).getTemperature());
            }
            switch (UIManager.UITYPE){
                case 1:
                    roomOnOff = (ImageView)convertView.findViewById(R.id.room_on_off);
                    roomTempNone = (ImageView)convertView.findViewById(R.id.room_temp_none);
                    roomTemp.setText((int) airCondition.getTemperature() + getContext().getString(R.string.temp_symbol));
                    if(airCondition.getOnoff() == AirConditionControl.UNKNOW || airCondition.getOnoff() == AirConditionControl.EMPTY){
                        roomOnOff.setImageResource(R.drawable.none_hit);
                        roomMode.setImageResource(R.drawable.none_hit);
                        roomWindSpeed.setImageResource(R.drawable.none_hit);
                        roomTemp.setVisibility(GONE);
                        roomTempNone.setVisibility(VISIBLE);
                        roomTempNone.setImageResource(R.drawable.none_hit);
                    }
                    if(airCondition.getOnoff() == 0){
                        roomTemp.setVisibility(VISIBLE);
                        roomTempNone.setVisibility(GONE);
                        roomTemp.setTextColor(getResources().getColor(R.color.hit_off_gray));
                        roomOnOff.setImageResource(R.drawable.onoff_off_hit);
                        switch (airCondition.getMode()){
                            case 0:
                                roomMode.setImageResource(R.drawable.cool_off_hit);
                                break;
                            case 1:
                                roomMode.setImageResource(R.drawable.heat_off_hit);
                                break;
                            case 2:
                                roomMode.setImageResource(R.drawable.dry_off_hit);
                                break;
                            case 3:
                                roomMode.setImageResource(R.drawable.fan_off_hit);
                                break;
                        }
                        switch (airCondition.getFan()){
                            case 0:
                                roomWindSpeed.setImageResource(R.drawable.fan1_off_hit);
                                break;
                            case 1:
                                roomWindSpeed.setImageResource(R.drawable.fan3_off_hit);
                                break;
                            case 2:
                                roomWindSpeed.setImageResource(R.drawable.fan5_off_hit);
                        }
                    }
                    if(airCondition.getOnoff() == 1){
                        roomTemp.setVisibility(VISIBLE);
                        roomTempNone.setVisibility(GONE);
                        switch (airCondition.getMode()){
                            case 0:
                                roomTemp.setTextColor(getResources().getColor(R.color.hit_cool_blue));
                                roomOnOff.setImageResource(R.drawable.onoff_on_cool_dry_fan_hit);
                                roomMode.setImageResource(R.drawable.cool_on_hit);
                                switch (airCondition.getFan()){
                                    case 0:
                                        roomWindSpeed.setImageResource(R.drawable.fan1_on_cool_dry_fan_hit);
                                        break;
                                    case 1:
                                        roomWindSpeed.setImageResource(R.drawable.fan3_on_cool_dry_fan_hit);
                                        break;
                                    case 2:
                                        roomWindSpeed.setImageResource(R.drawable.fan5_on_cool_dry_fan_hit);
                                }
                                break;
                            case 1:
                                roomTemp.setTextColor(getResources().getColor(R.color.hit_heat_red));
                                roomOnOff.setImageResource(R.drawable.onoff_on_heat_hit);
                                roomMode.setImageResource(R.drawable.heat_on_hit);
                                switch (airCondition.getFan()){
                                    case 0:
                                        roomWindSpeed.setImageResource(R.drawable.fan1_on_heat_hit);
                                        break;
                                    case 1:
                                        roomWindSpeed.setImageResource(R.drawable.fan3_on_heat_hit);
                                        break;
                                    case 2:
                                        roomWindSpeed.setImageResource(R.drawable.fan5_on_heat_hit);
                                }
                                break;
                            case 2:
                                roomTemp.setTextColor(getResources().getColor(R.color.hit_cool_blue));
                                roomOnOff.setImageResource(R.drawable.onoff_on_cool_dry_fan_hit);
                                roomMode.setImageResource(R.drawable.dry_on_hit);
                                switch (airCondition.getFan()){
                                    case 0:
                                        roomWindSpeed.setImageResource(R.drawable.fan1_on_cool_dry_fan_hit);
                                        break;
                                    case 1:
                                        roomWindSpeed.setImageResource(R.drawable.fan3_on_cool_dry_fan_hit);
                                        break;
                                    case 2:
                                        roomWindSpeed.setImageResource(R.drawable.fan5_on_cool_dry_fan_hit);
                                }
                                break;
                            case 3:
                                roomTemp.setTextColor(getResources().getColor(R.color.hit_cool_blue));
                                roomOnOff.setImageResource(R.drawable.onoff_on_cool_dry_fan_hit);
                                roomMode.setImageResource(R.drawable.fan_on_hit);
                                switch (airCondition.getFan()){
                                    case 0:
                                        roomWindSpeed.setImageResource(R.drawable.fan1_on_cool_dry_fan_hit);
                                        break;
                                    case 1:
                                        roomWindSpeed.setImageResource(R.drawable.fan3_on_cool_dry_fan_hit);
                                        break;
                                    case 2:
                                        roomWindSpeed.setImageResource(R.drawable.fan5_on_cool_dry_fan_hit);
                                }
                                break;
                        }

                    }
                    roomName.setText(rooms.get(position).getName());

                    roomView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(airCondition.getOnoff() == AirConditionControl.EMPTY){
                                if(rooms.get(position).getElements() == null || rooms.get(position).getElements().size() == 0) {
                                    MyApp.getApp().showToast(getContext().getString(R.string.pls_add_air_to_room));
                                }else{
                                    MyApp.getApp().showToast(getContext().getString(R.string.fail_to_fetch_aircondition));
                                }
                            }else {
                                Intent intent = new Intent();
                                intent.putExtra("air", airCondition.toJsonString());
                                intent.putExtra("room", rooms.get(position).toJsonString());
                                intent.setClass(context, RoomAirSettingHitActivity.class);
                                intent.putExtra("title", rooms.get(position).getName());
                                ((MainActivity)context).startActivityForResult(intent,MyAirFragment.REQUEST_ROOM_HIT);
                            }
                        }
                    });
                    break;
                case 2:
                    ImageView bgBar =(ImageView)convertView.findViewById(R.id.bg_bar);
                    roomTempNone = (ImageView)convertView.findViewById(R.id.room_temp_none);
                    ImageView roomWindNone = (ImageView) convertView.findViewById(R.id.room_wind_none);

                    roomTemp.setText((int) airCondition.getTemperature() + getContext().getString(R.string.temp_symbol));
                    if(airCondition.getOnoff() == AirConditionControl.UNKNOW || airCondition.getOnoff() == AirConditionControl.EMPTY){
                        bgBar.setImageResource(R.drawable.room_bg_bar_off_dc);
                        roomMode.setImageResource(R.drawable.none_dc);
                        roomWindSpeed.setImageResource(R.drawable.none_dc);
                        roomTemp.setVisibility(GONE);
                        roomWindSpeed.setVisibility(GONE);
                        roomWindNone.setVisibility(VISIBLE);
                        roomWindNone.setImageResource(R.drawable.none_dc);
                        roomTempNone.setVisibility(VISIBLE);
                        roomTempNone.setImageResource(R.drawable.none_dc);
                    }
                    if(airCondition.getOnoff() == 0) {
                        roomTemp.setVisibility(VISIBLE);
                        roomTempNone.setVisibility(GONE);
                        roomWindNone.setVisibility(GONE);
                        roomWindSpeed.setVisibility(VISIBLE);
                        roomTemp.setTextColor(getResources().getColor(R.color.mode_off_gray));
                        bgBar.setImageResource(R.drawable.room_bg_bar_off_dc);
                        switch (airCondition.getMode()){
                            case 0:
                                roomMode.setImageResource(R.drawable.cool_off_dc);
                                break;
                            case 1:
                                roomMode.setImageResource(R.drawable.heat_off_dc);
                                break;
                            case 2:
                                roomMode.setImageResource(R.drawable.dry_off_dc);
                                break;
                            case 3:
                                roomMode.setImageResource(R.drawable.fan_off_dc);
                                break;
                        }
                        switch (airCondition.getFan()){
                            case 0:
                                roomWindSpeed.setImageResource(R.drawable.fan_off1_dc);
                                break;
                            case 1:
                                roomWindSpeed.setImageResource(R.drawable.fan_off3_dc);
                                break;
                            case 2:
                                roomWindSpeed.setImageResource(R.drawable.fan_off5_dc);
                        }
                    }
                    if(airCondition.getOnoff() == 1) {
                        roomTemp.setVisibility(VISIBLE);
                        roomTempNone.setVisibility(GONE);
                        roomWindNone.setVisibility(GONE);
                        roomWindSpeed.setVisibility(VISIBLE);
                        switch (airCondition.getMode()) {
                            case 0:
                                roomMode.setImageResource(R.drawable.cool_on_dc);
                                bgBar.setImageResource(R.drawable.room_bg_bar_cool_dc);
                                roomTemp.setTextColor(getResources().getColor(R.color.mode_cool_blue));
                                switch (airCondition.getFan()){
                                    case 0:
                                        roomWindSpeed.setImageResource(R.drawable.fan_cool1_dc);
                                        break;
                                    case 1:
                                        roomWindSpeed.setImageResource(R.drawable.fan_cool3_dc);
                                        break;
                                    case 2:
                                        roomWindSpeed.setImageResource(R.drawable.fan_cool5_dc);
                                }
                                break;
                            case 1:
                                roomMode.setImageResource(R.drawable.heat_on_dc);
                                bgBar.setImageResource(R.drawable.room_bg_bar_heat_dc);
                                roomTemp.setTextColor(getResources().getColor(R.color.mode_heat_pink));
                                switch (airCondition.getFan()){
                                    case 0:
                                        roomWindSpeed.setImageResource(R.drawable.fan_heat1_dc);
                                        break;
                                    case 1:
                                        roomWindSpeed.setImageResource(R.drawable.fan_heat3_dc);
                                        break;
                                    case 2:
                                        roomWindSpeed.setImageResource(R.drawable.fan_heat5_dc);
                                }
                                break;
                            case 2:
                                roomMode.setImageResource(R.drawable.dry_on_dc);
                                bgBar.setImageResource(R.drawable.room_bg_bar_dry_dc);
                                roomTemp.setTextColor(getResources().getColor(R.color.mode_dry_purple));
                                switch (airCondition.getFan()){
                                    case 0:
                                        roomWindSpeed.setImageResource(R.drawable.fan_dry1_dc);
                                        break;
                                    case 1:
                                        roomWindSpeed.setImageResource(R.drawable.fan_dry3_dc);
                                        break;
                                    case 2:
                                        roomWindSpeed.setImageResource(R.drawable.fan_dry5_dc);
                                }
                                break;
                            case 3:
                                roomMode.setImageResource(R.drawable.fan_on_dc);
                                bgBar.setImageResource(R.drawable.room_bg_bar_fan_dc);
                                roomTemp.setTextColor(getResources().getColor(R.color.mode_fan_green));
                                switch (airCondition.getFan()){
                                    case 0:
                                        roomWindSpeed.setImageResource(R.drawable.fan_fan1_dc);
                                        break;
                                    case 1:
                                        roomWindSpeed.setImageResource(R.drawable.fan_fan3_dc);
                                        break;
                                    case 2:
                                        roomWindSpeed.setImageResource(R.drawable.fan_fan5_dc);
                                }
                                break;
                        }
                    }

                    roomName.setText(rooms.get(position).getName());
                    roomView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(airCondition.getOnoff() == AirConditionControl.EMPTY){
                                if(rooms.get(position).getElements() == null || rooms.get(position).getElements().size() == 0) {
                                    MyApp.getApp().showToast(getContext().getString(R.string.pls_add_air_to_room));
                                }else{
                                    MyApp.getApp().showToast(getContext().getString(R.string.fail_to_fetch_aircondition));
                                }
                            }else {
                                Intent intent = new Intent();
                                intent.putExtra("air", airCondition.toJsonString());
                                intent.putExtra("room", rooms.get(position).toJsonString());
                                intent.setClass(context, RoomAirSettingActivity.class);
                                intent.putExtra("title", rooms.get(position).getName());
                                ((MainActivity)context).startActivityForResult(intent, MyAirFragment.REQUEST_ROOM_DC);
                            }

                        }
                    });
                    break;
                default:
                    roomOnOff = (ImageView)convertView.findViewById(R.id.room_on_off);
                    roomTempNone = (ImageView)convertView.findViewById(R.id.room_temp_none);
                    roomTemp.setText((int) airCondition.getTemperature() + getContext().getString(R.string.temp_symbol));
                    if(airCondition.getOnoff() == AirConditionControl.UNKNOW || airCondition.getOnoff() == AirConditionControl.EMPTY){
                        roomOnOff.setImageResource(R.drawable.none_hit);
                        roomMode.setImageResource(R.drawable.none_hit);
                        roomWindSpeed.setImageResource(R.drawable.none_hit);
                        roomTemp.setVisibility(GONE);
                        roomTempNone.setVisibility(VISIBLE);
                        roomTempNone.setImageResource(R.drawable.none_hit);
                    }
                    if(airCondition.getOnoff() == 0){
                        roomTemp.setVisibility(VISIBLE);
                        roomTempNone.setVisibility(GONE);
                        roomTemp.setTextColor(getResources().getColor(R.color.hit_off_gray));
                        roomOnOff.setImageResource(R.drawable.onoff_off_hit);
                        switch (airCondition.getMode()){
                            case 0:
                                roomMode.setImageResource(R.drawable.cool_off_hit);
                                break;
                            case 1:
                                roomMode.setImageResource(R.drawable.heat_off_hit);
                                break;
                            case 2:
                                roomMode.setImageResource(R.drawable.dry_off_hit);
                                break;
                            case 3:
                                roomMode.setImageResource(R.drawable.fan_off_hit);
                                break;
                        }
                        switch (airCondition.getFan()){
                            case 0:
                                roomWindSpeed.setImageResource(R.drawable.fan1_off_hit);
                                break;
                            case 1:
                                roomWindSpeed.setImageResource(R.drawable.fan3_off_hit);
                                break;
                            case 2:
                                roomWindSpeed.setImageResource(R.drawable.fan5_off_hit);
                        }
                    }
                    if(airCondition.getOnoff() == 1){
                        roomTemp.setVisibility(VISIBLE);
                        roomTempNone.setVisibility(GONE);
                        switch (airCondition.getMode()){
                            case 0:
                                roomTemp.setTextColor(getResources().getColor(R.color.hit_cool_blue));
                                roomOnOff.setImageResource(R.drawable.onoff_on_cool_dry_fan_hit);
                                roomMode.setImageResource(R.drawable.cool_on_hit);
                                switch (airCondition.getFan()){
                                    case 0:
                                        roomWindSpeed.setImageResource(R.drawable.fan1_on_cool_dry_fan_hit);
                                        break;
                                    case 1:
                                        roomWindSpeed.setImageResource(R.drawable.fan3_on_cool_dry_fan_hit);
                                        break;
                                    case 2:
                                        roomWindSpeed.setImageResource(R.drawable.fan5_on_cool_dry_fan_hit);
                                }
                                break;
                            case 1:
                                roomTemp.setTextColor(getResources().getColor(R.color.hit_heat_red));
                                roomOnOff.setImageResource(R.drawable.onoff_on_heat_hit);
                                roomMode.setImageResource(R.drawable.heat_on_hit);
                                switch (airCondition.getFan()){
                                    case 0:
                                        roomWindSpeed.setImageResource(R.drawable.fan1_on_heat_hit);
                                        break;
                                    case 1:
                                        roomWindSpeed.setImageResource(R.drawable.fan3_on_heat_hit);
                                        break;
                                    case 2:
                                        roomWindSpeed.setImageResource(R.drawable.fan5_on_heat_hit);
                                }
                                break;
                            case 2:
                                roomTemp.setTextColor(getResources().getColor(R.color.hit_cool_blue));
                                roomOnOff.setImageResource(R.drawable.onoff_on_cool_dry_fan_hit);
                                roomMode.setImageResource(R.drawable.dry_on_hit);
                                switch (airCondition.getFan()){
                                    case 0:
                                        roomWindSpeed.setImageResource(R.drawable.fan1_on_cool_dry_fan_hit);
                                        break;
                                    case 1:
                                        roomWindSpeed.setImageResource(R.drawable.fan3_on_cool_dry_fan_hit);
                                        break;
                                    case 2:
                                        roomWindSpeed.setImageResource(R.drawable.fan5_on_cool_dry_fan_hit);
                                }
                                break;
                            case 3:
                                roomTemp.setTextColor(getResources().getColor(R.color.hit_cool_blue));
                                roomOnOff.setImageResource(R.drawable.onoff_on_cool_dry_fan_hit);
                                roomMode.setImageResource(R.drawable.fan_on_hit);
                                switch (airCondition.getFan()){
                                    case 0:
                                        roomWindSpeed.setImageResource(R.drawable.fan1_on_cool_dry_fan_hit);
                                        break;
                                    case 1:
                                        roomWindSpeed.setImageResource(R.drawable.fan3_on_cool_dry_fan_hit);
                                        break;
                                    case 2:
                                        roomWindSpeed.setImageResource(R.drawable.fan5_on_cool_dry_fan_hit);
                                }
                                break;
                        }

                    }
                    roomName.setText(rooms.get(position).getName());
                    roomView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(airCondition.getOnoff() == AirConditionControl.EMPTY){
                                if(rooms.get(position).getElements() == null || rooms.get(position).getElements().size() == 0) {
                                    MyApp.getApp().showToast(getContext().getString(R.string.pls_add_air_to_room));
                                }else{
                                    MyApp.getApp().showToast(getContext().getString(R.string.fail_to_fetch_aircondition));
                                }
                            }else {
                                Intent intent = new Intent();
                                intent.putExtra("air", airCondition.toJsonString());
                                intent.putExtra("room", rooms.get(position).toJsonString());
                                intent.setClass(context, RoomAirSettingHxActivity.class);
                                intent.putExtra("title", rooms.get(position).getName());
                                ((MainActivity)context).startActivityForResult(intent, MyAirFragment.REQUEST_ROOM_HX);
                            }
                        }
                    });
                    break;
            }

            return convertView;
        }
    }


    public class RoomCustomView extends LinearLayout{

        public RoomCustomView(Context context) {
            super(context);
            init(context);
        }

        private void init(Context context) {
            switch (UIManager.UITYPE) {
                case 1:
                    inflate(context, R.layout.custom_room_view_hit, this);
                    break;
                case 2:
                    inflate(context, R.layout.custom_room_view,this);
                    break;
                default:
                    inflate(context, R.layout.custom_room_view_hx, this);
                    break;
            }

        }

    }

}
