package ac.airconditionsuit.app.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/9/18.
 */
public class UserInfoActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_setting_user_information);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.fill_user_info));

    }

    @Override
    public void onClick(View v) {

    }
}
