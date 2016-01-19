package ac.airconditionsuit.app.activity;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.entity.AirCondition;
import ac.airconditionsuit.app.entity.ObserveData;
import ac.airconditionsuit.app.util.CheckUtil;
import ac.airconditionsuit.app.view.CommonDeviceView;
import ac.airconditionsuit.app.view.CommonTopBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

import java.util.Observable;
import java.util.TimerTask;

/**
 * Created by Administrator on 2015/10/21.
 */
public class SearchIndoorDeviceActivity extends BaseActivity implements View.OnClickListener {


    private IndoorDeviceAdapter indoorDeviceAdapter;

    private TimerTask searchTimerTask;

    private int[] blingCounter = null;
    private TimerTask blingTimerTask;
    private Animation blingAnimation;

    private TimerTask readTimerTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search_indoor_device);
        super.onCreate(savedInstanceState);

        blingAnimation = AnimationUtils.loadAnimation(MyApp.getApp().getApplicationContext(), R.anim.blinganim);

        if (getIntent().getStringExtra("first") != null) {
            searchAricondition();
        }
        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.indoor_device_manage));
        RelativeLayout bottomBar = (RelativeLayout) findViewById(R.id.bottom_bar);
        TextView searchTip = (TextView) findViewById(R.id.search_tip);
        TextView searchIndoor = (TextView) findViewById(R.id.search_indoor);
        switch (UIManager.UITYPE) {
            case 1:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_hit);
                bottomBar.setBackgroundResource(R.drawable.under_bar_hit);
                searchTip.setTextColor(getResources().getColor(R.color.text_color_white));
                searchIndoor.setTextColor(getResources().getColor(R.color.text_color_white));
                break;
            case 2:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_dc);
                bottomBar.setBackgroundResource(R.drawable.under_bar_dc);
                break;
            default:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_dc);
                bottomBar.setBackgroundResource(R.drawable.under_bar_hit);
                searchTip.setTextColor(getResources().getColor(R.color.text_color_white));
                searchIndoor.setTextColor(getResources().getColor(R.color.text_color_white));
                break;
        }
        commonTopBar.setIconView(this, null);

        GridView gridView = (GridView) findViewById(R.id.indoor_device_list);
        indoorDeviceAdapter = new IndoorDeviceAdapter(SearchIndoorDeviceActivity.this);
        gridView.setAdapter(indoorDeviceAdapter);

        LinearLayout searchIndoorDevice = (LinearLayout) findViewById(R.id.search_indoor_device);
        searchIndoorDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                showWaitProgress();
//                MyApp.getApp().setIsSearching(true);
//                MyApp.getApp().getSocketManager().searchIndoorAirCondition();
//
//                searchTimerTask = new TimerTask() {
//                    @Override
//                    public void run() {
//                        dismissWaitProgress();
//                        MyApp.getApp().showToast(getString(R.string.search_indoor_failed));
//                    }
//                };
//                new java.util.Timer().schedule(searchTimerTask, 10000);
                searchAricondition();
            }
        });

//        readTimerTest = new TimerTask() {
//            @Override
//            public void run() {
//                MyApp.getApp().getAirConditionManager().queryAirConditionStatus();
//            }
//        };
//        new java.util.Timer().schedule(readTimerTest, 5000 ,10000);
    }

    private void searchAricondition() {
        new AlertDialog.Builder(SearchIndoorDeviceActivity.this).setTitle("警告").setMessage("非专业人员请勿随意搜索室内机，数量变化会导致本地楼层和场景模式被清空").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showWaitProgress();
                MyApp.getApp().setIsSearching(true);
                MyApp.getApp().getSocketManager().searchIndoorAirCondition();
                searchTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissWaitProgress();
                                new AlertDialog.Builder(SearchIndoorDeviceActivity.this).setTitle(R.string.tip).setMessage(R.string.search_indoor_failed).
                                        setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();

                                            }
                                        }).setCancelable(false).show();
                            }
                        });
                    }
                };
                new java.util.Timer().schedule(searchTimerTask, 20000);
            }
        }).setNegativeButton(R.string.cancel, null).setCancelable(false).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (blingTimerTask != null) {
            blingTimerTask.cancel();
            blingTimerTask = null;
        }
        if (searchTimerTask != null) {
            searchTimerTask.cancel();
            searchTimerTask = null;
        }
        MyApp.setIsSearching(false);
    }

    private void toastIndoorDeviceNumber() {
        dismissWaitProgress();
        MyApp.getApp().showToast("共搜索到" + MyApp.getApp().getServerConfigManager().getDeviceCount_new() + "台室内机");
    }

    private void setBlingCountDataByAcIndex(int incomingAcIndex) {
        int deviceCnt = MyApp.getApp().getServerConfigManager().getDeviceCount_new();
        if (incomingAcIndex >= 0 && incomingAcIndex < deviceCnt) {
            if (blingCounter == null || blingCounter.length != deviceCnt) {
                blingCounter = new int[deviceCnt];
            }
            blingCounter[incomingAcIndex] = 2;
        }
    }

    private void blindAc() {
        if (blingCounter == null) {
            return;
        }
        boolean hasBlingAc = false;
        for (int i = 0; i < blingCounter.length; i++) {
            int c = blingCounter[i];
            if (c > 0) {
                hasBlingAc = true;
            }
            c--;
            blingCounter[i] = (c < 0 ? 0 : c);
        }
        if (hasBlingAc) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    indoorDeviceAdapter.notifyDataSetChanged();
                }
            });
        } else {
            if (blingTimerTask != null) {
                blingTimerTask.cancel();
                blingTimerTask = null;
            }
        }
    }

    private void startBlingAcTimer() {
        if (blingTimerTask == null) {
            blingTimerTask = new TimerTask() {
                @Override
                public void run() {
                    blindAc();
                }
            };
            new java.util.Timer().schedule(blingTimerTask, 0, 100);
        }
    }


    @Override
    public void update(Observable observable, Object data) {
        super.update(observable, data);

        ObserveData od = (ObserveData) data;
        switch (od.getMsg()) {
            case ObserveData.AIR_CONDITION_STATUS_RESPONSE:
                int incomingAcIndex = MyApp.getApp().getServerConfigManager().getDeviceIndexFromAddress_new(((AirCondition) od.getData()).getAddress());
                setBlingCountDataByAcIndex(incomingAcIndex);
                startBlingAcTimer();
                break;
            case ObserveData.SEARCH_AIR_CONDITION_RESPONSE:
                if (!MyApp.getIsSearching()) {
                    break;
                }
                MyApp.setIsSearching(false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        indoorDeviceAdapter.notifyDataSetChanged();
                        if (searchTimerTask != null) {
                            searchTimerTask.cancel();
                            searchTimerTask = null;
                        }
                        toastIndoorDeviceNumber();
                    }
                });
                break;
            case ObserveData.SEARCH_AIR_CONDITION_CANCEL_TIMER:
                if (searchTimerTask != null) {
                    searchTimerTask.cancel();
                    searchTimerTask = null;
                }
                dismissWaitProgress();
                MyApp.setIsSearching(false);
                break;
            case ObserveData.SEARCH_AIR_CONDITION_NUMBERDIFFERENT:
                MyApp.setIsSearching(false);

                if (searchTimerTask != null) {
                    searchTimerTask.cancel();
                    searchTimerTask = null;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(SearchIndoorDeviceActivity.this).setTitle("警告")
                                .setMessage("扫描到的空调室内机数量发生变化，将清除所有的个性化和场景设置")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        MyApp.getApp().getServerConfigManager().airconditionNumberChange();
                                    }
                                }).setCancelable(false).show();
                    }
                });
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_icon:
                setResult(RESULT_OK);
                finish();
                break;
        }
    }

    private class IndoorDeviceAdapter extends BaseAdapter {

        private Context context;

        public IndoorDeviceAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            if (MyApp.getApp().getServerConfigManager().getDevices_new() == null)
                return 0;
            return MyApp.getApp().getServerConfigManager().getDevices_new().size();
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
                convertView = new CommonDeviceView(context);
            }
            convertView.setBackgroundResource(R.drawable.drag_device_transparent_big);
            TextView textView = (TextView) convertView.findViewById(R.id.bottom_name);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.bg_icon);
            TextView rightUpText = (TextView) convertView.findViewById(R.id.right_up_text);

            textView.setText(MyApp.getApp().getServerConfigManager().getDevices_new().get(position).getName());
            imageView.setImageResource(R.drawable.drag_device_icon);
            rightUpText.setBackgroundResource(R.drawable.drag_device_name_bar);
            rightUpText.setText(MyApp.getApp().getServerConfigManager().getDevices_new().get(position).getFormatNameByIndoorIndexAndAddress());
//            int address = MyApp.getApp().getServerConfigManager().getDevices().get(position).getAddress();
//            if(MyApp.getApp().getServerConfigManager().getDevices().get(position).getIndooraddress() < 10) {
//                rightUpText.setText(String.valueOf(MyApp.getApp().getServerConfigManager().getDevices().get(position).getIndoorIndex()) + "-0" +
//                        MyApp.getApp().getServerConfigManager().getDevices().get(position).getIndooraddress());
//            }else{
//                rightUpText.setText(String.valueOf(String.valueOf(MyApp.getApp().getServerConfigManager().getDevices().get(position).getIndoorIndex()) + "-" +
//                        MyApp.getApp().getServerConfigManager().getDevices().get(position).getIndooraddress());
//            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final EditText et = new EditText(SearchIndoorDeviceActivity.this);
                    et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    et.setBackgroundResource(R.color.text_color_white);
                    et.setMinHeight(150);
                    et.setText(MyApp.getApp().getServerConfigManager().getDevices_new().get(position).getName());
                    et.setSelection(MyApp.getApp().getServerConfigManager().getDevices_new().get(position).getName().length());
                    new AlertDialog.Builder(SearchIndoorDeviceActivity.this).setTitle(R.string.rename_indoor_device).setView(et).
                            setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String device_new_name = CheckUtil.checkLength(et, 20, R.string.device_name_empty_info, R.string.device_name_too_long_info);
                                    if (device_new_name == null) {
                                        return;
                                    }
                                    for (int i = 0; i < MyApp.getApp().getServerConfigManager().getDevices_new().size(); i++) {
                                        if (i != position) {
                                            if (device_new_name.equals(MyApp.getApp().getServerConfigManager().getDevices_new().get(i).getName())) {
                                                MyApp.getApp().showToast("空调名称不能重复");
                                                return;
                                            }
                                        }
                                    }
                                    MyApp.getApp().getServerConfigManager().getDevices_new().get(position).setName(device_new_name);
                                    MyApp.getApp().getServerConfigManager().writeToFile(true);
                                    notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            }).setNegativeButton(R.string.cancel, null).setCancelable(false).show();
                }
            });

            if (blingCounter != null && position < blingCounter.length && blingCounter[position] > 0) {
                convertView.startAnimation(blingAnimation);
            }

            return convertView;
        }
    }
}
