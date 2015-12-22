package ac.airconditionsuit.app.activity;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.entity.ObserveData;
import ac.airconditionsuit.app.network.socket.SocketManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.view.CommonTopBar;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.umeng.analytics.MobclickAgent;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by ac on 9/18/15.
 */
public class BaseActivity extends FragmentActivity implements Observer {

    //Log 信息的时候使用的tag，尽量不要用system.out
    protected String TAG;
    private Dialog waitDialog;

    {
        TAG = getClass().getName();
    }

    private CommonTopBar commonTopBar;

    private static final int SHOW_WAIT_PROGRESS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        commonTopBar = (CommonTopBar) findViewById(R.id.default_top_bar);

        //init a waitDialog for wait progress bar
        initWaitProgressBar();

    }

    private void initWaitProgressBar() {
        waitDialog = new Dialog(this);
        waitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        waitDialog.setContentView(new ProgressBar(this, null, android.R.attr.progressBarStyleLargeInverse));
        waitDialog.setCancelable(false);
        waitDialog.getWindow().setDimAmount(0.5f);
    }

    public CommonTopBar getCommonTopBar() {
        return commonTopBar;
    }

    public void shortStartActivity(Class c, String... keyAndValue) {
        Intent intent = new Intent(this, c);
        int keyAndValueLength = keyAndValue.length;
        for (int i = 0; i < keyAndValueLength / 2; ++i) {
            intent.putExtra(keyAndValue[i * 2], keyAndValue[i * 2 + 1]);
        }
        intent.putExtra(Constant.INTENT_DATA_KEY_ACTIVITY_FROM, TAG);
        startActivity(intent);
    }

    public void shortStartActivity(Intent intent) {
        intent.putExtra(Constant.INTENT_DATA_KEY_ACTIVITY_FROM, TAG);
        startActivity(intent);
    }

    public void shortStartActivityForResult(Class c, int requsetCode, String... keyAndValue) {
        Intent intent = new Intent(this, c);
        int keyAndValueLength = keyAndValue.length;
        for (int i = 0; i < keyAndValueLength / 2; ++i) {
            intent.putExtra(keyAndValue[i * 2], keyAndValue[i * 2 + 1]);
        }
        intent.putExtra(Constant.INTENT_DATA_KEY_ACTIVITY_FROM, TAG);
        startActivityForResult(intent, requsetCode);
    }

    public void showWaitProgress() {
        if (!waitDialog.isShowing()) {
            waitDialog.show();
        }
    }

    public void dismissWaitProgress() {
        if (waitDialog.isShowing()) {
            waitDialog.dismiss();
        }
    }

    protected void setOnclickListenerOnTextViewDrawable(final View.OnClickListener onClickListener, EditText... editTexts) {
        for (final EditText editText : editTexts) {
            editText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= editText.getRight() - editText.getTotalPaddingRight()) {
                            onClickListener.onClick(v);
                            return false;
                        }
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        ObserveData od = (ObserveData) data;
        switch (od.getMsg()) {
            case ObserveData.OFFLINE:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(BaseActivity.this).setMessage("您已经在其他地方登录，请重新登录")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        quiteLogin();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    }
                });
                break;
        }
    }

    public void quiteLogin() {
        finishAffinity();
        shortStartActivity(LoginActivity.class);
        MyApp.getApp().getLocalConfigManager().setCurrentUserRememberedPassword("");
        MyApp.getApp().offLine();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        SocketManager socketManager = MyApp.getApp().getSocketManager();
        if (socketManager != null) {
            socketManager.addObserver(this);
//            socketManager.onResume(this);
        }

        MyApp.appResume();

        registerNetworkStatusChangeReceiver();
    }

    private void registerNetworkStatusChangeReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        this.registerReceiver(MyApp.getNetworkChangeReceiver(), filter);
    }

    private void unRegisterNetworkStatusChangeReceiver() {
        this.unregisterReceiver(MyApp.getNetworkChangeReceiver());
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyApp.appStop();

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        SocketManager socketManager = MyApp.getApp().getSocketManager();
        if (socketManager != null) {
            socketManager.deleteObserver(this);
//            socketManager.onPause(this);
        }
        unRegisterNetworkStatusChangeReceiver();
    }

}
