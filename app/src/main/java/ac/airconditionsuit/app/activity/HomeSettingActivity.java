package ac.airconditionsuit.app.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.view.CommonButtonWithArrow;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/9/18.
 */
public class HomeSettingActivity extends BaseActivity {

    private MyOnClickListener myOnClickListener = new MyOnClickListener(){
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.home_name:
                    shortStartActivity(ChangeHomeNameActivity.class);
                    break;
                case R.id.left_icon:
                    finish();
                    break;
                case R.id.delete_home:
                    //TODO for luzheqi delete
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_setting_home_setting);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.home));
        commonTopBar.setIconView(myOnClickListener, null);
        CommonButtonWithArrow homeName = (CommonButtonWithArrow) findViewById(R.id.home_name);
        homeName.setOnlineTextView(MyApp.getApp().getServerConfigManager().getHome().getName());
        homeName.setOnClickListener(myOnClickListener);
        TextView deleteHome = (TextView)findViewById(R.id.delete_home);
        deleteHome.setOnClickListener(myOnClickListener);

    }

}
