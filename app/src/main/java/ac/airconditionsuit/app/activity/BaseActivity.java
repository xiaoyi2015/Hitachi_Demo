package ac.airconditionsuit.app.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by ac on 9/18/15.
 */
public class BaseActivity extends FragmentActivity {

    //Log 信息的时候使用的tag，尽量不要用system.out
    static protected String TAG;
    {
        TAG = getClass().getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
