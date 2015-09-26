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
import android.widget.ListView;
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
                    shortStartActivity(DragDeviceActivity.class,"section",section);
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
