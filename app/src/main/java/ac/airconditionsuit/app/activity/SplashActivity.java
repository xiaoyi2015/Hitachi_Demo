package ac.airconditionsuit.app.activity;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.entity.MyUser;
import ac.airconditionsuit.app.listener.CommonNetworkListener;
import android.os.Bundle;
import com.umeng.analytics.MobclickAgent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ac on 8/13/15.
 * splash
 */
public class SplashActivity extends BaseActivity {
    private long beginTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        MobclickAgent.setDebugMode(true);
        MobclickAgent.reportError(this, "error");
        super.onCreate(savedInstanceState);
        setContentView(UIManager.getSplashLayout());

//        new AsyncHttpClient().get(this, "http://www.baidu.com", new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                System.out.println(statusCode);
//                System.out.println(new String(responseBody));
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//
//            }
//        });

        beginTime = System.currentTimeMillis();

        final String password = MyApp.getApp().getLocalConfigManager().getCurrentUserRememberedPassword();
        if (password != null && password.length() != 0) {
            MyApp app = MyApp.getApp();
            app.initServerConfigManager(new CommonNetworkListener() {

                @Override
                public void onSuccess() {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - beginTime < 2000) {
//                        try {
//                            Thread.sleep(2000 - (currentTime - beginTime));
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                    }

                    MyUser user = MyApp.getApp().getUser();
                    if (user.infComplete()) {
                        MyApp.getApp().initSocketManager();
                        MyApp.getApp().initPushDataManager();
                        shortStartActivity(MainActivity.class);
                    } else {
                        shortStartActivity(UserInfoActivity.class);
                    }
                    finish();
                }

                @Override
                public void onFailure() {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - beginTime < 2000) {
                        try {
                            Thread.sleep(2000 - (currentTime - beginTime));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    shortStartActivity(LoginActivity.class);
                    finish();
                }
            });
        } else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    shortStartActivity(LoginActivity.class);
                    finish();
                }
            }, 2000);
        }
    }
}