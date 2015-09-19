package ac.airconditionsuit.app.activity;

import ac.airconditionsuit.app.MyApp;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by ac on 9/18/15.
 */
public class BaseActivity extends FragmentActivity {

    //Log 信息的时候使用的tag，尽量不要用system.out
    static protected String TAG;
    {
        TAG = getClass().getName();
    }

    private CommonTopBar commonTopBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        commonTopBar = (CommonTopBar) findViewById(R.id.default_top_bar);
    }

    public CommonTopBar getCommonTopBar() {
        return commonTopBar;
    }

    public MyApp getApp(){
        return (MyApp) getApplication();
    }
}
