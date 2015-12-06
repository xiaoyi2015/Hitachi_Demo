package ac.airconditionsuit.app.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.entity.Section;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.util.CheckUtil;
import ac.airconditionsuit.app.view.CommonButtonWithArrow;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/9/18.
 */
public class SoftwarePageActivity extends BaseActivity {
    private MyOnClickListener myOnClickListener = new MyOnClickListener(){
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()){
                case R.id.add_group:
                    if(MyApp.getApp().getServerConfigManager().getSections().size() >= 16){
                        MyApp.getApp().showToast("群组数量不能超过16个");
                        return;
                    }
                    final EditText et = new EditText(SoftwarePageActivity.this);
                    et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    et.setMinHeight(200);
                    et.setBackgroundResource(R.color.text_color_white);
                    new AlertDialog.Builder(SoftwarePageActivity.this).setTitle(R.string.pls_input_group_name).setView(et).
                            setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String group_new_name = CheckUtil.checkLength(et, 20, R.string.group_name_empty_info, R.string.group_name_too_long_info);
                            if(group_new_name == null){
                                return;
                            }
                            for(int i = 0; i < MyApp.getApp().getServerConfigManager().getSections().size(); i++){
                                if(MyApp.getApp().getServerConfigManager().getSections().get(i).getName().equals(group_new_name)){
                                    MyApp.getApp().showToast("已存在“" + group_new_name + "”的楼层，请输入其他名称");
                                    return;
                                }
                            }
                            Section section = new Section();
                            section.setName(group_new_name);
                            MyApp.getApp().getServerConfigManager().addSections(section);
                            dialog.dismiss();
                        }
                    }).setNegativeButton(R.string.cancel, null).setCancelable(false).show();

                    break;
                case R.id.left_icon:
                    finish();
                    break;

            }
        }
    };

    private class RoomSectionAdapter extends BaseAdapter {
        private Context context;
        List<Section> list;

        public RoomSectionAdapter(Context context, List<Section> list){
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
                convertView = new CommonButtonWithArrow(context);
            }
            final TextView group_name = (TextView)convertView.findViewById(R.id.label_text);
            group_name.setText(list.get(position).getName());

            convertView.setOnClickListener(new MyOnClickListener() {
                @Override
                public void onClick(View v) {
                    super.onClick(v);
                    String section = list.get(position).toJsonString();
                    shortStartActivity(DragDeviceActivity.class, "section", section,"position",String.valueOf(position));
                }
            });

            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    LayoutInflater inflater = LayoutInflater.from(SoftwarePageActivity.this);
                    View v1 = inflater.inflate(R.layout.pop_up_window_section, null);
                    final PopupWindow pop = new PopupWindow(v1, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
                    pop.setBackgroundDrawable(new BitmapDrawable());
                    pop.setOutsideTouchable(true);
                    RelativeLayout view = (RelativeLayout) findViewById(R.id.software_page_layout);
                    pop.showAtLocation(view, Gravity.BOTTOM, 0, 0);

                    TextView cancel = (TextView) v1.findViewById(R.id.cancel);
                    TextView delete = (TextView) v1.findViewById(R.id.delete_section);
                    TextView change_name = (TextView) v1.findViewById(R.id.change_name);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pop.dismiss();
                        }
                    });
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new AlertDialog.Builder(SoftwarePageActivity.this).setTitle(R.string.tip).setMessage(getString(R.string.is_delete_section)).
                                    setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            MyApp.getApp().getServerConfigManager().deleteSection(position);
                                            notifyDataSetChanged();
                                            dialog.dismiss();
                                        }
                                    }).setNegativeButton(R.string.cancel, null).setCancelable(false).show();
                            pop.dismiss();
                        }
                    });
                    change_name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final EditText et = new EditText(SoftwarePageActivity.this);
                            et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                            et.setBackgroundResource(R.color.text_color_white);
                            et.setMinHeight(200);
                            et.setText(list.get(position).getName());
                            et.setSelection(list.get(position).getName().length());
                            new AlertDialog.Builder(SoftwarePageActivity.this).setTitle(R.string.pls_input_group_name).setView(et).
                                    setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String group_new_name = CheckUtil.checkLength(et, 20, R.string.group_name_empty_info, R.string.group_name_too_long_info);
                                            if (group_new_name == null) {
                                                return;
                                            }
                                            for (int i = 0; i < MyApp.getApp().getServerConfigManager().getSections().size(); i++) {
                                                if (i != position) {
                                                    if (MyApp.getApp().getServerConfigManager().getSections().get(i).getName().equals(group_new_name)) {
                                                        MyApp.getApp().showToast("已存在“" + group_new_name + "”的楼层，请输入其他名称");
                                                        return;
                                                    }
                                                }
                                            }
                                            MyApp.getApp().getServerConfigManager().getSections().get(position).setName(group_new_name);
                                            MyApp.getApp().getServerConfigManager().writeToFile(true);
                                            notifyDataSetChanged();
                                            dialog.dismiss();
                                        }
                                    }).setNegativeButton(R.string.cancel, null).setCancelable(false).show();
                            pop.dismiss();
                        }
                    });

                    return true;

                }
            });

            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_setting_software_page);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.software_page));
        switch (UIManager.UITYPE){
            case 1:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_hit);
                break;
            case 2:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_dc);
                break;
            default:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_dc);
                break;
        }
        commonTopBar.setIconView(myOnClickListener, null);

        ListView listView = (ListView) findViewById(R.id.group_view);
        List<Section> list = MyApp.getApp().getServerConfigManager().getSections();
        RoomSectionAdapter roomSectionAdapter= new RoomSectionAdapter(this,list);
        listView.setAdapter(roomSectionAdapter);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.footer_add_group, null);
        TextView add_new_group = (TextView) view.findViewById(R.id.add_group);
        add_new_group.setOnClickListener(myOnClickListener);
        listView.addFooterView(view);
        //setListViewHeightBasedOnChildren(listView);
    }
/*
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
*/
}
