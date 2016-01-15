package ac.airconditionsuit.app.activity;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.entity.MyUser;
import ac.airconditionsuit.app.listener.CommonNetworkListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import java.io.File;
import java.util.*;

/**
 * Created by ac on 8/13/15.
 * splash
 */
public class SplashActivity extends BaseActivity {
    private long beginTime;
    private final static int DELAY = 2000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(UIManager.getSplashLayout());

        beginTime = System.currentTimeMillis();

        final String password = MyApp.getApp().getLocalConfigManager().getCurrentUserRememberedPassword();
        MyApp.getApp().setUser(MyApp.getApp().getLocalConfigManager().getCurrentUserInformation());
        if (password != null && password.length() != 0) {
            MyApp app = MyApp.getApp();
            app.initServerConfigManager(new CommonNetworkListener() {

                @Override
                public void onSuccess() {
                    goToNextPage();
                }

                @Override
                public void onFailure() {
                    goToNextPage();
                }
            });
        } else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    shortStartActivity(LoginActivity.class);
                    finish();
                }
            }, DELAY);
        }

        File cacheDir = getCacheDir();
        for (File file : cacheDir.listFiles()) {
            System.out.println(file.getAbsoluteFile());
        }
    }

    private void goToNextPage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                if (currentTime - beginTime < DELAY) {
                    try {
                        Thread.sleep(DELAY - (currentTime - beginTime));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
                });
            }
        }).start();
    }
}