package ac.airconditionsuit.app.activity;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.entity.ObserveData;
import ac.airconditionsuit.app.fragment.*;
import ac.airconditionsuit.app.view.TabIndicator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Observable;


public class MainActivity extends BaseActivity {

    private static final int DEFAULT_FRAGMENT_POSITION = 0;
    private ViewPager pager;
    BaseFragment[] fragments = new BaseFragment[]{
            new MyAirFragment(),
            new SceneFragment(),
            new SetClockFragment(),
            new SettingFragment()
    };

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        for (BaseFragment bf : fragments) {
            bf.setActivity(this);
        }

        setContentView(UIManager.getMainActivityLayout());
        super.onCreate(savedInstanceState);

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }

        });

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                changeIndicator(position);
                fragments[position].setTopBar();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        initTabIndicator();
//        pager.setCurrentItem(DEFAULT_FRAGMENT_POSITION);
        fragments[0].setTopBar();

    }

    private TabIndicator[] tabIndicators = null;

    private void initTabIndicator() {
        LinearLayout tabs = (LinearLayout) findViewById(R.id.tabs);
        tabIndicators = new TabIndicator[]{(TabIndicator) tabs.getChildAt(0),
                (TabIndicator) tabs.getChildAt(1),
                (TabIndicator) tabs.getChildAt(2),
                (TabIndicator) tabs.getChildAt(3)};
        tabIndicators[0].select();

        for (int i = 0; i < tabIndicators.length; ++i) {
            final int position = i;
            tabIndicators[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pager.setCurrentItem(position);
                }
            });
        }
    }

    private void changeIndicator(int position) {
        if (tabIndicators == null) {
            return;
        }
        for (TabIndicator tabIndicator : tabIndicators) {
            tabIndicator.unSelect();
        }
        tabIndicators[position].select();
    }

    @Override
    public void update(Observable observable, Object data) {
        super.update(observable, data);

        ObserveData od = (ObserveData) data;
        switch (od.getMsg()) {

            case ObserveData.TIMER_STATUS_RESPONSE:
            case ObserveData.AIR_CONDITION_STATUS_RESPONSE:
                refreshUI();
                break;
            case ObserveData.NETWORK_STATUS_CHANGE:
                getSettingFragment().refreshNetworkStatus();
                break;

        }
    }

    private SettingFragment getSettingFragment() {
        return (SettingFragment) fragments[3];
    }

    public void refreshUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (BaseFragment bf : fragments) {
                    bf.refreshUI();
                }
            }
        });
    }

    long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // 判断间隔时间 大于2秒就退出应用
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                String msg = "再次点击退出";
                MyApp.getApp().showToast(msg);
                exitTime = System.currentTimeMillis();
            } else {
                MyApp.getApp().quitWithoutCleaningUser();
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
