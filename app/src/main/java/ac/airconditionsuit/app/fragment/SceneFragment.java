package ac.airconditionsuit.app.fragment;

import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.aircondition.AirConditionManager;
import ac.airconditionsuit.app.entity.Scene;
import ac.airconditionsuit.app.network.socket.socketpackage.Udp.UdpPackage;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.activity.BaseActivity;
import ac.airconditionsuit.app.activity.EditSceneActivity;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.network.socket.SocketManager;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/10/3.
 */
public class SceneFragment extends BaseFragment {

    private View view;
    private int click_num = 0;
    private CommonTopBar commonTopBar;
    private static final int RESULT_OK = -1;
    private static final int REQUEST_CODE_EDIT_SCENE = 110;

    private MyOnClickListener myOnClickListener = new MyOnClickListener() {
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.right_icon:
                    if (MyApp.getApp().getServerConfigManager().hasDevice()) {
                        if (click_num == 0) {
                            switch (UIManager.UITYPE) {
                                case 1:
                                    commonTopBar.setLeftIconView(R.drawable.top_bar_add_hit);
                                    commonTopBar.setRightIconView(R.drawable.top_bar_save_hit);
                                    break;
                                case 2:
                                    commonTopBar.setLeftIconView(R.drawable.top_bar_add_dc);
                                    commonTopBar.setRightIconView(R.drawable.top_bar_save_dc);
                                    break;
                                default:
                                    commonTopBar.setLeftIconView(R.drawable.top_bar_add_dc);
                                    commonTopBar.setRightIconView(R.drawable.top_bar_save_dc);
                                    break;
                            }

                            commonTopBar.setTitle(getString(R.string.edit_scene));
                            commonTopBar.setIconView(myOnClickListener, myOnClickListener);
                            click_num = 1;
                        } else {
                            commonTopBar.setTitle(getString(R.string.tab_label_scene_mode));
                            switch (UIManager.UITYPE) {
                                case 1:
                                    commonTopBar.setRightIconView(R.drawable.top_bar_edit_hit);
                                    break;
                                case 2:
                                    commonTopBar.setRightIconView(R.drawable.top_bar_edit_dc);
                                    break;
                                default:
                                    commonTopBar.setRightIconView(R.drawable.top_bar_edit_dc);
                                    break;
                            }

                            commonTopBar.setIconView(null, myOnClickListener);
                            click_num = 0;
                        }
                    } else {
                        MyApp.getApp().showToast("家内无控制器，无法编辑场景");
                    }
                    break;
                case R.id.left_icon:
                    if (MyApp.getApp().getServerConfigManager().getScene().size() >= 16) {
                        MyApp.getApp().showToast("场景数量不能超过16个");
                        return;
                    }
                    Intent intent = new Intent();
                    intent.putExtra("title", "");
                    intent.setClass(getActivity(), EditSceneActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_EDIT_SCENE);
                    break;
            }
        }
    };
    private SceneAdapter sceneAdapter;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_scene, container, false);
        listView = (ListView) view.findViewById(R.id.scene_list);
        sceneAdapter = new SceneAdapter(getActivity(), null);
        listView.setAdapter(sceneAdapter);
        refreshUI();
        return view;
    }

    @Override
    public void refreshUI() {
        super.refreshUI();
        if (sceneAdapter == null) {
            return;
        }
        if (MyApp.getApp().getServerConfigManager().hasDevice()) {
            List<Scene> scene_list = MyApp.getApp().getServerConfigManager().getScene();
            sceneAdapter.changeData(scene_list);
        } else {
//            MyApp.getApp().showToast("请先添加场景，再进行控制操作！");
            sceneAdapter.changeData(null);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        click_num = 0;
    }

    @Override
    public void setTopBar() {
        click_num = 0;
        refreshUI();
        BaseActivity baseActivity = myGetActivity();
        commonTopBar = baseActivity.getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.tab_label_scene_mode));
        commonTopBar.getTitleView().setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        commonTopBar.getTitleView().setOnClickListener(null);
        switch (UIManager.UITYPE) {
            case 1:
                commonTopBar.setRightIconView(R.drawable.top_bar_edit_hit);
                break;
            case 2:
                commonTopBar.setRightIconView(R.drawable.top_bar_edit_dc);
                break;
            default:
                commonTopBar.setRightIconView(R.drawable.top_bar_edit_dc);
                break;
        }

        commonTopBar.setIconView(null, myOnClickListener);
        commonTopBar.setRoundLeftIconView(null);
    }

    private class SceneAdapter extends BaseAdapter {

        private Context context;
        private List<Scene> list;

        public SceneAdapter(Context context, List<Scene> list) {
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
                convertView = new SceneCustomView(context);
            }
            TextView sceneName = (TextView) convertView.findViewById(R.id.scene_name);
            LinearLayout sceneView = (LinearLayout) convertView.findViewById(R.id.scene_view);
            sceneName.setText(list.get(position).getName());
            sceneView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MyApp.getApp().getServerConfigManager().hasDevice()) {
                        if (MyApp.getApp().getServerConfigManager().getDevices_new() != null && MyApp.getApp().
                                getServerConfigManager().getDevices_new().size() > 0) {
                            if (click_num == 1) {
                                Intent intent = new Intent();
                                intent.putExtra("index", position);
                                intent.putExtra("title", list.get(position).getName());
                                intent.setClass(getActivity(), EditSceneActivity.class);
                                startActivityForResult(intent, REQUEST_CODE_EDIT_SCENE);
                            } else {
                                TextView toDoControl = new TextView(getActivity());
                                toDoControl.setGravity(Gravity.CENTER);
                                toDoControl.setText(R.string.is_to_do_control);
                                toDoControl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                                toDoControl.setMinHeight(140);
                                new AlertDialog.Builder(getActivity()).setTitle(R.string.to_do_control_together).setView(toDoControl).
                                        setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                final ProgressDialog pd = ProgressDialog.show(getActivity(), null, "指令发送中", true, false);
                                                try {
                                                    MyApp.getApp().getAirConditionManager().controlScene(list.get(position), new UdpPackage.Handler() {
                                                        @Override
                                                        public void success() {
                                                            getActivity().runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    if (pd.isShowing()) {
                                                                        pd.dismiss();
                                                                        MyApp.getApp().showToast("指令发送成功");
                                                                        MyApp.getApp().getAirConditionManager().queryAirConditionStatus();
                                                                    }
                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void fail(int errorNo) {
                                                            getActivity().runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    MyApp.getApp().showToast("指令发送失败");
                                                                    pd.dismiss();
                                                                }
                                                            });
                                                        }
                                                    });
                                                } catch (final Exception e) {
                                                    if (pd.isShowing()) {
                                                        pd.dismiss();
                                                    }
//                                                    getActivity().runOnUiThread(new Runnable() {
//                                                        @Override
//                                                        public void run() {
//                                                            ScrollView sv = new ScrollView(getActivity());
//                                                            EditText textView = new EditText(getActivity());
//                                                            Writer writer = new StringWriter();
//                                                            PrintWriter printWriter = new PrintWriter(writer);
//                                                            e.printStackTrace(printWriter);
//                                                            String s = writer.toString();
//                                                            textView.setText(s + list.get(position).toJsonString());
//                                                            sv.addView(textView);
//                                                            new AlertDialog.Builder(getActivity()).setView(sv).show();
//                                                        }
//                                                    });
                                                    MyApp.getApp().showToast("指令发送失败");
                                                    Log.e(TAG, "control scene fail!");
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).setNegativeButton(R.string.cancel, null).setCancelable(false).show();
                            }
                        } else {
                            MyApp.getApp().showToast("请先搜索室内机");
                        }
                    } else {
                        MyApp.getApp().showToast("请先添加i-EZ控制器");
                    }
                }
            });
            return convertView;
        }

        public void changeData(List<Scene> scene_list) {
            this.list = scene_list;
            notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_EDIT_SCENE:
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
            switch (UIManager.UITYPE) {
                case 1:
                    inflate(context, R.layout.custom_scene_view_hit, this);
                    break;
                case 2:
                    inflate(context, R.layout.custom_scene_view, this);
                    break;
                default:
                    inflate(context, R.layout.custom_scene_view_hit, this);
                    break;
            }
        }

    }
}
