package ac.airconditionsuit.app.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.activity.BaseActivity;
import ac.airconditionsuit.app.activity.EditSceneActivity;
import ac.airconditionsuit.app.activity.InfoPageActivity;
import ac.airconditionsuit.app.entity.Section;
import ac.airconditionsuit.app.entity.ServerConfig;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/10/3.
 */
public class SceneFragment extends BaseFragment {

    private View view;
    private CommonTopBar commonTopBar;
    private int click_num = 0;
    private static final int RESULT_OK = -1;
    private static final int REQUEST_CODE_EDIT_SCENE = 110;

    private MyOnClickListener myOnClickListener = new MyOnClickListener(){
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()){
                case R.id.right_icon:
                    if(click_num == 0) {
                        commonTopBar.setLeftIconView(R.drawable.add);
                        commonTopBar.setRightIconView(R.drawable.save);
                        commonTopBar.setTitle(getString(R.string.edit_scene));
                        commonTopBar.setIconView(myOnClickListener, myOnClickListener);
                        click_num = 1;
                    }else{
                        commonTopBar.setTitle(getString(R.string.tab_label_scene_mode));
                        commonTopBar.setRightIconView(R.drawable.edit);
                        commonTopBar.setIconView(null, myOnClickListener);
                        click_num = 0;
                    }
                    break;
                case R.id.left_icon:
                    Intent intent = new Intent();
                    intent.putExtra("title", "");
                    intent.setClass(getActivity(), EditSceneActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };
    private SceneAdapter sceneAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_scene,container,false);
        ListView listView = (ListView) view.findViewById(R.id.scene_list);
        List<ServerConfig.Scene> scene_list = MyApp.getApp().getServerConfigManager().getScene();
        sceneAdapter = new SceneAdapter(getActivity(),scene_list);
        listView.setAdapter(sceneAdapter);
        return view;
    }

    @Override
    public void setTopBar() {
        BaseActivity baseActivity = myGetActivity();
        commonTopBar = baseActivity.getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.tab_label_scene_mode));
        commonTopBar.setRightIconView(R.drawable.edit);
        commonTopBar.setIconView(null,myOnClickListener);
        commonTopBar.setRoundLeftIconView(null);
    }

    private class SceneAdapter extends BaseAdapter{

        private Context context;
        private List<ServerConfig.Scene> list;
        public SceneAdapter(Context context,List<ServerConfig.Scene> list){
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
                convertView = new SceneCustomView(context);
            }
            TextView sceneName = (TextView)convertView.findViewById(R.id.scene_name);
            LinearLayout sceneView = (LinearLayout)convertView.findViewById(R.id.scene_view);
            sceneName.setText(list.get(position).getName());
            sceneView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (click_num == 1) {
                        Intent intent = new Intent();
                        intent.putExtra("index",position);
                        intent.putExtra("title", list.get(position).getName());
                        intent.setClass(getActivity(), EditSceneActivity.class);
                        startActivityForResult(intent,REQUEST_CODE_EDIT_SCENE);
                    } else {
                        TextView toDoControl = new TextView(getActivity());
                        toDoControl.setGravity(Gravity.CENTER);
                        toDoControl.setText(R.string.is_to_do_control);
                        toDoControl.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                        new AlertDialog.Builder(getActivity()).setTitle(R.string.to_do_control_together).setView(toDoControl).
                                setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //TODO submit the data to server
                                    }
                                }).setNegativeButton(R.string.cancel, null).setCancelable(false).show();
                    }
                }
            });
            return convertView;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_CODE_EDIT_SCENE:
                    sceneAdapter.list.get(data.getIntExtra("index", -1)).setName(data.getStringExtra("title"));
                    sceneAdapter.notifyDataSetChanged();
                    break;

            }
        }
    }

    private class SceneCustomView extends LinearLayout {
        public SceneCustomView(Context context) {
            super(context);
            init(context);
        }

        private void init(Context context) {
            inflate(context,R.layout.custom_scene_view,this);
        }

    }
}
