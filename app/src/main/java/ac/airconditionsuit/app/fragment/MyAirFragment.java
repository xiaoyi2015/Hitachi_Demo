package ac.airconditionsuit.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.activity.BaseActivity;
import ac.airconditionsuit.app.activity.InfoPageActivity;
import ac.airconditionsuit.app.entity.Section;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.view.SectionAndRoomView;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/10/3.
 */
public class MyAirFragment extends BaseFragment {

    private View view;
    private MyOnClickListener myOnClickListener = new MyOnClickListener(){
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()){
                case R.id.round_left_icon:
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), InfoPageActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_air, container, false);

        ListView listView = (ListView) view.findViewById(R.id.section_view);

        //这边也要判断一下有没有设备
        if (MyApp.getApp().getServerConfigManager().hasDevice()) {
            List<Section> list = MyApp.getApp().getServerConfigManager().getSections();
            MyAirSectionAdapter myAirSectionAdapter= new MyAirSectionAdapter(getActivity(),list);
            listView.setAdapter(myAirSectionAdapter);
        } else {
            //TODO for zhulinan,没有设备做相应处理
        }

        return view;
    }

    @Override
    public void setTopBar() {
        BaseActivity baseActivity = myGetActivity();
        CommonTopBar commonTopBar = baseActivity.getCommonTopBar();
        commonTopBar.setTitle(MyApp.getApp().getServerConfigManager().getHome().getName());
        commonTopBar.setIconView(null, null);
        commonTopBar.setRoundLeftIconView(myOnClickListener);
    }

    private class MyAirSectionAdapter extends BaseAdapter{

        private Context context;
        List<Section> list;
        final boolean[] isCheck = new boolean[100];

        public MyAirSectionAdapter(Context context,List<Section> list){
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
                convertView = new SectionAndRoomView(context,list.get(position).getPages());
            }
            //TODO read the room status and set to UI

            LinearLayout sectionView = (LinearLayout)convertView.findViewById(R.id.section_item);
            TextView sectionName = (TextView)convertView.findViewById(R.id.label_text);
            sectionName.setText(list.get(position).getName());
            final ListView roomList = (ListView)convertView.findViewById(R.id.room_list);
            final ImageView arrowIcon = (ImageView)convertView.findViewById(R.id.arrow_icon);
            isCheck[position] = false;
            sectionView.setOnClickListener(new MyOnClickListener() {
                @Override
                public void onClick(View v) {
                    super.onClick(v);
                    isCheck[position] = !isCheck[position];
                    if (isCheck[position]) {
                        arrowIcon.setImageResource(R.drawable.icon_arrow_down);
                        roomList.setVisibility(View.VISIBLE);
                    } else {
                        arrowIcon.setImageResource(R.drawable.icon_arrow_right);
                        roomList.setVisibility(View.GONE);
                    }
                }
            });

            return convertView;
        }
    }
}
