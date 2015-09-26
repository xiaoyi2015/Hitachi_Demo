package ac.airconditionsuit.app.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.entity.MyUser;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.view.CommonButtonWithArrow;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/9/18.
 */
public class UserInfoActivity extends BaseActivity {
    private MyOnClickListener myOnClickListener = new MyOnClickListener(){
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.left_icon:
                    finish();
                    break;
                case R.id.nick_name:
                    break;
                case R.id.gender:
                    break;
                case R.id.change_birth:
                    break;
                case R.id.change_phone:
                    break;
                case R.id.change_email:
                    break;
                case R.id.home_list:
                    break;
                case R.id.change_password:
                    break;
                case R.id.common_agree_clause:
                    break;
                case R.id.quit_account:
                    break;
                case R.id.network_icon:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_setting_user_information);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.fill_user_info));
        commonTopBar.setIconView(myOnClickListener, null);
        ImageView userIcon = (ImageView)findViewById(R.id.network_icon);
        CommonButtonWithArrow nickName = (CommonButtonWithArrow)findViewById(R.id.nick_name);
        CommonButtonWithArrow gender = (CommonButtonWithArrow)findViewById(R.id.gender);
        CommonButtonWithArrow birth = (CommonButtonWithArrow)findViewById(R.id.change_birth);
        CommonButtonWithArrow phone = (CommonButtonWithArrow)findViewById(R.id.change_phone);
        CommonButtonWithArrow email = (CommonButtonWithArrow)findViewById(R.id.change_email);
        CommonButtonWithArrow addHome = (CommonButtonWithArrow)findViewById(R.id.home_list);
        CommonButtonWithArrow password = (CommonButtonWithArrow)findViewById(R.id.change_password);
        CommonButtonWithArrow clause = (CommonButtonWithArrow)findViewById(R.id.common_agree_clause);
        CommonButtonWithArrow exit = (CommonButtonWithArrow)findViewById(R.id.quit_account);

        nickName.setOnlineTextView(MyApp.getApp().getUser().getCust_name());
        if(MyApp.getApp().getUser().getSex() == 1)
            gender.setOnlineTextView(getString(R.string.male));
        else
            gender.setOnlineTextView(getString(R.string.female));
        birth.setOnlineTextView(MyApp.getApp().getUser().getBirthday());
        phone.setOnlineTextView(MyApp.getApp().getUser().getPhone());
        email.setOnlineTextView(MyApp.getApp().getUser().getEmail());

        userIcon.setOnClickListener(myOnClickListener);
        nickName.setOnClickListener(myOnClickListener);
        gender.setOnClickListener(myOnClickListener);
        birth.setOnClickListener(myOnClickListener);
        phone.setOnClickListener(myOnClickListener);
        email.setOnClickListener(myOnClickListener);
        addHome.setOnClickListener(myOnClickListener);
        password.setOnClickListener(myOnClickListener);
        clause.setOnClickListener(myOnClickListener);
        exit.setOnClickListener(myOnClickListener);

    }

}
