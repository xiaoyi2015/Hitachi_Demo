package ac.airconditionsuit.app.activity;


import android.content.Intent;
import ac.airconditionsuit.app.MyApp;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by ac on 9/18/15.
 */
public class BaseActivity extends FragmentActivity {

    private static final String ACTIVITY_FROM = "activity_from" ;
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


    public void shortStartActivity(Class c, String... keyAndValue) {
        Intent intent = new Intent(this, c);
        int keyAndValueLength = keyAndValue.length;
        for (int i = 0; i < keyAndValueLength / 2; ++i) {
            intent.putExtra(keyAndValue[i * 2], keyAndValue[i * 2 + 1]);
        }
        intent.putExtra(ACTIVITY_FROM, this.getClass().getName());
        startActivity(intent);
    }

    public MyApp getApp(){
        return (MyApp) getApplication();
    }
}
