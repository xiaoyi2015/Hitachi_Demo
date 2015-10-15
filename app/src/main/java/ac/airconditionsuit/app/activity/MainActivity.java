package ac.airconditionsuit.app.activity;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.PushData.PushDataManager;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.fragment.BaseFragment;
import ac.airconditionsuit.app.fragment.MyAirFragment;
import ac.airconditionsuit.app.fragment.SceneFragment;
import ac.airconditionsuit.app.fragment.SetClockFragment;
import ac.airconditionsuit.app.fragment.SettingFragment;
import ac.airconditionsuit.app.view.TabIndicator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;


public class MainActivity extends BaseActivity {

    //TODO 设置默认的fragment，这边可能根据需求方要求修改要改
    private static final int DEFAULT_FRAGMENT_POSITION = 3;
    private ViewPager pager;
    BaseFragment[] fragments = new BaseFragment[]{
            new MyAirFragment(),
            new SceneFragment(),
            new SetClockFragment(),
            new SettingFragment()
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
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
//                Log.i(TAG, "onPageSelected");
                changeIndicator(position);
                fragments[position].setTopBar();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        initTabIndicator();

        for (BaseFragment bf : fragments) {
            bf.setActivity(this);
        }

        pager.setCurrentItem(DEFAULT_FRAGMENT_POSITION);

        MyApp.getApp().initSocketManager();
        MyApp.getApp().initPushDataManager();
        MyApp.getApp().initAirconditionManager();

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
}
