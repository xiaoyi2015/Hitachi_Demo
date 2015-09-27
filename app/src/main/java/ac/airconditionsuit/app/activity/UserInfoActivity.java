package ac.airconditionsuit.app.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.entity.MyUser;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.network.HttpClient;
import ac.airconditionsuit.app.network.response.LoginResponseData;
import ac.airconditionsuit.app.view.CommonButtonWithArrow;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/9/18.
 */
public class UserInfoActivity extends BaseActivity {

    private static final int REQUEST_CODE_USER_NAME = 101;
    private MyOnClickListener myOnClickListener = new MyOnClickListener(){
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.left_icon:
                    finish();
                    break;
                case R.id.nick_name:
                    shortStartActivityForResult(ChangeUserNameActivity.class,REQUEST_CODE_USER_NAME,"title",getString(R.string.nick_name));
                    break;
                case R.id.gender:
                    LayoutInflater inflater = LayoutInflater.from(UserInfoActivity.this);
                    View v1 = inflater.inflate(R.layout.gender_pop_up_window, null);
                    final PopupWindow pop = new PopupWindow(v1, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
                    pop.setBackgroundDrawable(new BitmapDrawable());
                    pop.setOutsideTouchable(true);
                    RelativeLayout view = (RelativeLayout)findViewById(R.id.user_info_page);
                    pop.showAtLocation(view,Gravity.BOTTOM,0,0);

                    TextView male = (TextView)v1.findViewById(R.id.male);
                    final TextView female = (TextView)v1.findViewById(R.id.female);
                    TextView cancel = (TextView)v1.findViewById(R.id.cancel);
                    male.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            pop.dismiss();
                        }
                    });

                    female.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final RequestParams requestParams = new RequestParams();
                            requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_CUSTOMER);
                            requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_TYPE_SAVE_CUSTOMER_INF);
                            requestParams.put(Constant.REQUEST_PARAMS_FIELD, Constant.REQUEST_PARAMS_KEY_SEX);
                            requestParams.put(Constant.REQUEST_PARAMS_VALUE, R.string.female);

                            HttpClient.post(requestParams, String.class, new HttpClient.JsonResponseHandler<String>() {
                                @Override
                                public void onSuccess(String response) {

                                }

                                @Override
                                public void onFailure(Throwable throwable) {
                                    MyApp.getApp().showToast(R.string.change_user_sex_failure);
                                }
                            });
                            pop.dismiss();
                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pop.dismiss();
                        }
                    });

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
    private CommonButtonWithArrow nickName;
    private CommonButtonWithArrow gender;
    private CommonButtonWithArrow birth;
    private CommonButtonWithArrow phone;
    private CommonButtonWithArrow email;
    private CommonButtonWithArrow addHome;
    private CommonButtonWithArrow password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_setting_user_information);
        super.onCreate(savedInstanceState);

        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.fill_user_info));
        commonTopBar.setIconView(myOnClickListener, null);

        ImageView userIcon = (ImageView)findViewById(R.id.network_icon);

        nickName = (CommonButtonWithArrow)findViewById(R.id.nick_name);
        gender = (CommonButtonWithArrow)findViewById(R.id.gender);
        birth = (CommonButtonWithArrow)findViewById(R.id.change_birth);
        phone = (CommonButtonWithArrow)findViewById(R.id.change_phone);
        email = (CommonButtonWithArrow)findViewById(R.id.change_email);
        addHome = (CommonButtonWithArrow)findViewById(R.id.home_list);
        password = (CommonButtonWithArrow)findViewById(R.id.change_password);
        CommonButtonWithArrow clause = (CommonButtonWithArrow)findViewById(R.id.common_agree_clause);
        CommonButtonWithArrow exit = (CommonButtonWithArrow)findViewById(R.id.quit_account);

        HttpClient.loadImage(MyApp.getApp().getUser().getAvatar_big(),userIcon);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case REQUEST_CODE_USER_NAME:
                    String user_name = data.getStringExtra("userName");
                    nickName.setOnlineTextView(user_name);
                    MyApp.getApp().getUser().setCust_name(user_name);
                    break;

            }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
