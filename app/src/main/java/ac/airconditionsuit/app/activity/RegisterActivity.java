package ac.airconditionsuit.app.activity;

import android.os.Bundle;
import android.view.View;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.view.CommonButtonWithArrow;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/9/20.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener{
    private Boolean isRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.login_register);
        super.onCreate(savedInstanceState);
        isRegister = getIntent().getStringExtra(Constant.IS_REGISTER).equals(Constant.YES);
        CommonTopBar commonTopBar = getCommonTopBar();
        if(isRegister)
            commonTopBar.setTitle(getString(R.string.register_new_user));
        else
            commonTopBar.setTitle(getString(R.string.find_password));
    }

    @Override
    public void onClick(View v) {

    }
}
