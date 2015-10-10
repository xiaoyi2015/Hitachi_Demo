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

public class searchDeviceByUdpResultActivity extends ObserverSocketManagerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device_by_udp_result);
        MyApp.getApp().getSocketManager().sendBroadCast();
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data != null) {
            Device device = (Device) data;
            TextView textView = (TextView) findViewById(R.id.test);
            textView.setText(textView.getText() + device.getAuthCode() + ",");
        }
    }
}
