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
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends FragmentActivity {

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
                changeIndicator(position);
                return fragments[position];
            }

            @Override
            public int getCount() {
                return 4;
            }

        });

        initTabIndicator();
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
