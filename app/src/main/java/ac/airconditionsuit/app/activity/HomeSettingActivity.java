package ac.airconditionsuit.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.view.CommonButtonWithArrow;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/9/18.
 */
public class HomeSettingActivity extends BaseActivity implements View.OnClickListener {

    private CommonButtonWithArrow homeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_setting_home_setting);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.home));
        homeName = (CommonButtonWithArrow)findViewById(R.id.home_name);
        homeName.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_name:
                shortStartActivity(ChangeHomeNameActivity.class);
                break;
        }
    }
}
