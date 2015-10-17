package ac.airconditionsuit.app.activity;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.entity.MyUser;
import ac.airconditionsuit.app.listener.CommonNetworkListener;
import android.os.Bundle;

import java.io.IOException;
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
        super.onCreate(savedInstanceState);
        setContentView(UIManager.getSplashLayout());

        beginTime = System.currentTimeMillis();

        final String password = MyApp.getApp().getLocalConfigManager().getCurrentUserRememberedPassword();
        if (password != null && password.length() != 0) {
            MyApp app = MyApp.getApp();
            app.initServerConfigManager(new CommonNetworkListener() {

                @Override
                public void onSuccess() {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - beginTime < 2000) {
                        try {
                            Thread.sleep(2000 - (currentTime - beginTime));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    MyUser user = MyApp.getApp().getUser();
                    if (user.infComplete()) {
                        shortStartActivity(MainActivity.class);
                    } else {
                        //TODO
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