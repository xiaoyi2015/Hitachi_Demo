package ac.airconditionsuit.app.activity;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.entity.Device;
import ac.airconditionsuit.app.entity.ObserveData;
import ac.airconditionsuit.app.listener.MyOnClickListener;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.view.CommonButtonWithArrow;
import ac.airconditionsuit.app.view.CommonTopBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class SearchDeviceByUdpResultActivity extends BaseActivity {

    private List<Device> devices = new ArrayList<>();
    private MyOnClickListener myOnClickListener = new MyOnClickListener() {
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.left_icon:
                    finish();
                    break;
            }
        }
    };
    private HostListAdapter hostListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search_device_by_udp_result);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.host_list));
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
        //调用这个函数以后开始在局域网中搜索主机
        showWaitProgress();
        MyApp.getApp().getSocketManager().sendBroadCast();

        ListView listView = (ListView)findViewById(R.id.host_list);
        hostListAdapter = new HostListAdapter(SearchDeviceByUdpResultActivity.this,devices);
        listView.setAdapter(hostListAdapter);

    }

    /**
     * 每搜索到一个主机，就会调用一次这个函数。注意，这个函数可能多次调用都是传入同一个主机，所以在添加的时候需要判断主机在不在
     * @param observable
     * @param data 这个就是主机
     */
    @Override
    public void update(Observable observable, Object data) {
        super.update(observable, data);
        ObserveData od = (ObserveData) data;

        dismissWaitProgress();
        switch (od.getMsg()) {
            case ObserveData.FIND_DEVICE_BY_UDP:
                //如果不为空，就表示搜索到一个设备,做相应处理
                Device device = (Device) od.getData();
                addDevice(device);
                break;
//            case ObserveData.FIND_DEVICE_BY_UDP_FAIL:
//                //如果返回会空，就表示发送广播包出现错误，做相应处理。如在界面上显示搜索失败之类的。
//                MyApp.getApp().showToast(R.string.search_host_device_failed);
//                break;
            case ObserveData.FIND_DEVICE_BY_UDP_FAIL:
            case ObserveData.FIND_DEVICE_BY_UDP_FINASH:
                if (devices == null || devices.size() == 0) {
                    Log.i(TAG, "finish search device and can not find any");
                    //todo for zhulinan对应bug第二点，这边弹一个框，样式buglist里面有的。
                }
                break;
        }
    }

    synchronized private void addDevice(final Device device) {
        for (Device d : devices) {
            if (d.getInfo().getMac().equals(device.getInfo().getMac())) {
                return;
            }
        }
        devices.add(device);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hostListAdapter.notifyDataSetChanged();
            }
        });
    }

    private class HostListAdapter extends BaseAdapter{

        private List<Device> list;
        private Context context;

        public HostListAdapter(Context context,List<Device> deviceList) {
            this.list = deviceList;
            this.context = context;
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
            ((CommonButtonWithArrow)convertView).getLabelTextView().setText(list.get(position).getInfo().getName());
            ((CommonButtonWithArrow)convertView).setOnlineTextView("添加设备");
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shortStartActivity(BindHostActivity.class, "device", list.get(position).toJsonString());
                }
            });

            return convertView;
        }
    }
}