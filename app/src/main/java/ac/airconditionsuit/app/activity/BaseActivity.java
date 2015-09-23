package ac.airconditionsuit.app.activity;


import ac.airconditionsuit.app.Constant;
import android.app.Dialog;
import android.content.Intent;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.view.CommonTopBar;
import android.view.Window;
import android.widget.ProgressBar;

/**
 * Created by ac on 9/18/15.
 */
public class BaseActivity extends FragmentActivity {

    //Log 信息的时候使用的tag，尽量不要用system.out
    static protected String TAG;
    private Dialog waitDialog;

    {
        TAG = getClass().getName();
    }

    private CommonTopBar commonTopBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        commonTopBar = (CommonTopBar) findViewById(R.id.default_top_bar);

        //init a waitDialog for wait progress bar
        initWaitProgressBar();
    }

    private void initWaitProgressBar() {
        waitDialog = new Dialog(this);
        waitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        waitDialog.setContentView(new ProgressBar(this, null, android.R.attr.progressBarStyleLargeInverse));
        waitDialog.setCancelable(false);
        waitDialog.getWindow().setDimAmount(0.5f);
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
        intent.putExtra(Constant.INTENT_DATA_KEY_ACTIVITY_FROM, this.getClass().getName());
        startActivity(intent);
    }

    public void showWaitProgress(){
        waitDialog.show();
    }

    public void dismissWaitProgress(){
        waitDialog.dismiss();
    }

}
