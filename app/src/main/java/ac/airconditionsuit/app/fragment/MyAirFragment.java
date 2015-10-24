package ac.airconditionsuit.app.fragment;

import ac.airconditionsuit.app.activity.MainActivity;
import ac.airconditionsuit.app.entity.Home;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
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

    public static final int REQUEST_ROOM_DC = 2456;
    public static final int REQUEST_ROOM_HIT = 2457;
    public static final int REQUEST_ROOM_HX = 2758;
    private View view;
    private MyOnClickListener myOnClickListener = new MyOnClickListener() {
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.round_left_icon:
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), InfoPageActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };
    private MyAirSectionAdapter myAirSectionAdapter;
    private ListView listView;
    private List<Section> list;
    private CommonTopBar commonTopBar;
    private PopupWindow pop;
    private List<Home> homeList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_air, container, false);

        listView = (ListView) view.findViewById(R.id.section_view);
        myAirSectionAdapter = new MyAirSectionAdapter(getActivity(), null);
        refreshUI();
        homeList = MyApp.getApp().getLocalConfigManager().getHomeList();
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            commonTopBar.getTitleView().setOnClickListener(null);
        }
    }

    @Override
    public void setTopBar() {
        final BaseActivity baseActivity = myGetActivity();
        commonTopBar = baseActivity.getCommonTopBar();
        commonTopBar.setTitle(MyApp.getApp().getServerConfigManager().getHome().getName());
        commonTopBar.getTitleView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.pop_up_home_list, null);
                for (int i = 0; i < homeList.size(); i++) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0,-6,0,1);
                    TextView textView = new TextView(getActivity());
                    textView.setText(homeList.get(i).getName());
                    textView.setBackgroundResource(UIManager.getHomeBarRes());
                    textView.setGravity(Gravity.CENTER);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    switch (UIManager.UITYPE) {
                        case 1:
                            textView.setTextColor(myGetActivity().getResources().getColor(R.color.text_color_white));
                            break;
                        default:
                            break;
                    }
                    textView.setLayoutParams(layoutParams);
                    linearLayout.addView(textView);
                    final int finalI = i;
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MyApp.getApp().getLocalConfigManager().changeHome(finalI);
                            commonTopBar.setTitle(homeList.get(finalI).getName());
                            ((MainActivity) myGetActivity()).refreshUI();
                            pop.dismiss();
                        }
                    });
                }
                pop = new PopupWindow(linearLayout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
                pop.setBackgroundDrawable(new BitmapDrawable());
                pop.setOutsideTouchable(true);
                pop.showAsDropDown(commonTopBar);
            }
        });

        switch (UIManager.UITYPE) {
            case 1:
                commonTopBar.setRightIconView(R.drawable.top_bar_logo_hit);
                commonTopBar.setIconView(null, myOnClickListener);
                commonTopBar.getTitleView().setCompoundDrawablesWithIntrinsicBounds(null, null, null,
                        myGetActivity().getResources().getDrawable(R.drawable.top_bar_arrow_down_hit));
                break;
            case 2:
                commonTopBar.setIconView(null, null);
                commonTopBar.getTitleView().setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null,
                        myGetActivity().getResources().getDrawable(R.drawable.icon_arrow_down_dc));
                break;
            default:
                commonTopBar.setRightIconView(R.drawable.top_bar_logo_hx);
                commonTopBar.setIconView(null, myOnClickListener);
                commonTopBar.getTitleView().setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null,
                        myGetActivity().getResources().getDrawable(R.drawable.icon_arrow_down_dc));
                break;
        }
        commonTopBar.setRoundLeftIconView(myOnClickListener);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1)
            switch (requestCode) {
                case REQUEST_ROOM_DC:
                    myAirSectionAdapter.notifyDataSetChanged();
                    break;
                case REQUEST_ROOM_HIT:
                    myAirSectionAdapter.notifyDataSetChanged();
                    break;
                case REQUEST_ROOM_HX:
                    myAirSectionAdapter.notifyDataSetChanged();
                    break;
            }
    }

    private class MyAirSectionAdapter extends BaseAdapter {

        private Context context;
        List<Section> list;
        final boolean[] isCheck = new boolean[100];

        public MyAirSectionAdapter(Context context, List<Section> list) {
            this.context = context;
            this.list = list;

        }

        @Override
        public int getCount() {
            if (list == null) {
                return 0;
            } else {
                return list.size();
            }
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
                convertView = new SectionAndRoomView(context, list.get(position).getPages());
            }

            final LinearLayout sectionView = (LinearLayout) convertView.findViewById(R.id.section_item);
            TextView sectionName = (TextView) convertView.findViewById(R.id.label_text);
            sectionName.setText(list.get(position).getName());
            final ListView roomList = (ListView) convertView.findViewById(R.id.room_list);
            final ImageView arrowIcon = (ImageView) convertView.findViewById(R.id.arrow_icon);
            isCheck[position] = false;
            sectionView.setOnClickListener(new MyOnClickListener() {
                @Override
                public void onClick(View v) {
                    super.onClick(v);
                    isCheck[position] = !isCheck[position];
                    if (isCheck[position]) {
                        switch (UIManager.UITYPE) {
                            case 1:
                                arrowIcon.setImageResource(R.drawable.icon_arrow_down_hit);
                                sectionView.setBackgroundResource(R.drawable.room_section_top_box_hit);
                                break;
                            case 2:
                                arrowIcon.setImageResource(R.drawable.icon_arrow_down_dc);
                                break;
                            default:
                                arrowIcon.setImageResource(R.drawable.icon_arrow_down_hit);
                                sectionView.setBackgroundResource(R.drawable.room_section_top_box_hit);
                                break;
                        }
                        roomList.setVisibility(View.VISIBLE);
                    } else {
                        switch (UIManager.UITYPE) {
                            case 1:
                                arrowIcon.setImageResource(R.drawable.icon_arrow_right_hit);
                                sectionView.setBackgroundResource(R.drawable.room_section_box_hit);
                                break;
                            case 2:
                                arrowIcon.setImageResource(R.drawable.icon_arrow_right_dc);
                                break;
                            default:
                                arrowIcon.setImageResource(R.drawable.icon_arrow_right_hit);
                                sectionView.setBackgroundResource(R.drawable.room_section_box_hit);
                                break;
                        }
                        roomList.setVisibility(View.GONE);
                    }
                }
            });

            return convertView;
        }

        public void changeData(List<Section> list) {
            this.list = list;
            notifyDataSetChanged();
        }
    }

    @Override
    public void refreshUI() {
        if (myAirSectionAdapter == null) {
            return;
        }
        super.refreshUI();
        if (MyApp.getApp().getServerConfigManager().hasDevice()) {
            list = MyApp.getApp().getServerConfigManager().getSections();
            myAirSectionAdapter.changeData(list);
        } else {
            myAirSectionAdapter.changeData(null);
        }
    }
}
