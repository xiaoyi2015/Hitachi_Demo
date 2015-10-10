package ac.airconditionsuit.app.activity;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.entity.Device;
import ac.airconditionsuit.app.entity.ServerConfig;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import ac.airconditionsuit.app.R;
import android.widget.TextView;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**TODO for zhulinan
 * 点击自动搜索主机后跳转到该activity
 */
public class searchDeviceByUdpResultActivity extends ObserverSocketManagerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device_by_udp_result);
        //调用这个函数以后开始在局域网中搜索主机
        MyApp.getApp().getSocketManager().sendBroadCast();
    }

    /**
     * 没搜索到一个主机，就会调用一次这个函数。
     * @param observable
     * @param data 这个就是主机
     */
    @Override
    public void update(Observable observable, Object data) {
        if (data != null) {
            //如果不为空，就表示搜索到一个设备,做相应处理
            Device device = (Device) data;
        } else {
            //如果返回会空，就表示发送广播包出现错误，做相应处理。如在界面上显示搜索失败之类的。

        }
    }
}
