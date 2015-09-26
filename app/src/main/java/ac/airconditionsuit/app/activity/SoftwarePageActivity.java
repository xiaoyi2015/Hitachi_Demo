package ac.airconditionsuit.app.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.entity.Section;
import ac.airconditionsuit.app.listener.MyOnClickListener;
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
                    final EditText et = new EditText(SoftwarePageActivity.this);
                    new AlertDialog.Builder(SoftwarePageActivity.this).setTitle(R.string.pls_input_group_name).setView(et).
                            setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String group_new_name = et.getText().toString();
                            Section section = new Section();
                            section.setName(group_new_name);
                            MyApp.getApp().getServerConfigManager().addSections(section);
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
            TextView group_name = (TextView)convertView.findViewById(R.id.label_text);
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
                    v = inflater.inflate(R.layout.pop_up_window, null);
                    final PopupWindow pop = new PopupWindow(v, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
                    pop.setBackgroundDrawable(new BitmapDrawable());
                    pop.setOutsideTouchable(true);
                    RelativeLayout view = (RelativeLayout)findViewById(R.id.software_page_layout);
                    pop.showAtLocation(view, Gravity.BOTTOM,0,0);

                    Button cancel = (Button)v.findViewById(R.id.cancel);
                    Button delete = (Button)v.findViewById(R.id.delete_section);
                    Button change_name = (Button)v.findViewById(R.id.change_name);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pop.dismiss();
                        }
                    });
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MyApp.getApp().getServerConfigManager().deleteSection(position);
                            notifyDataSetChanged();
                            pop.dismiss();
                        }
                    });
                    change_name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final EditText et = new EditText(SoftwarePageActivity.this);
                            new AlertDialog.Builder(SoftwarePageActivity.this).setTitle(R.string.pls_input_group_name).setView(et).
                                    setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String group_new_name = et.getText().toString();
                                            MyApp.getApp().getServerConfigManager().getSections().get(position).setName(group_new_name);
                                            MyApp.getApp().getServerConfigManager().writeToFile();
                                            notifyDataSetChanged();
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
        commonTopBar.setIconView(myOnClickListener, null);
        TextView add_new_group = (TextView) findViewById(R.id.add_group);
        add_new_group.setOnClickListener(myOnClickListener);

        ListView listView = (ListView) findViewById(R.id.group_view);
        List<Section> list = MyApp.getApp().getServerConfigManager().getSections();
        RoomSectionAdapter roomSectionAdapter= new RoomSectionAdapter(this,list);
        listView.setAdapter(roomSectionAdapter);
    }

}
