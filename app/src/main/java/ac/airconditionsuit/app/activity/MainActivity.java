package ac.airconditionsuit.app.activity;

import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.fragment.BaseFragment;
import ac.airconditionsuit.app.fragment.SettingFragment;
import ac.airconditionsuit.app.view.TabIndicator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends BaseActivity {

    //TODO 设置默认的fragment，这边可能根据需求方要求修改要改
    private static final int DEFAULT_FRAGMENT_POSITION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            BaseFragment[] fragments = new BaseFragment[]{
                    new BaseFragment(),
                    new BaseFragment(),
                    new BaseFragment(),
                    new SettingFragment()
            };


            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                // 这里Destroy的是Fragment的视图层次，并不是Destroy Fragment对象
                super.destroyItem(container, position, object);
                Log.i(TAG, "Destroy Item...");
            }

        });

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                changeIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        initTabIndicator();

        pager.setCurrentItem(DEFAULT_FRAGMENT_POSITION);

    }

    private TabIndicator[] tabIndicators = null;

    private void initTabIndicator() {
        LinearLayout tabs = (LinearLayout) findViewById(R.id.tabs);
        tabIndicators = new TabIndicator[]{(TabIndicator) tabs.getChildAt(0),
                (TabIndicator) tabs.getChildAt(1),
                (TabIndicator) tabs.getChildAt(2),
                (TabIndicator) tabs.getChildAt(3)};
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
