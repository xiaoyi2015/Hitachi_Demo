package ac.airconditionsuit.app.activity;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.entity.MyUser;
import ac.airconditionsuit.app.listener.CommonNetworkListener;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.network.HttpClient;
import ac.airconditionsuit.app.network.response.LoginResponseData;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.loopj.android.http.RequestParams;

/**
 * Created by ac on 9/19/15.
 */
public class LoginActivity extends BaseActivity {

    private EditText userNameEditText;
    private EditText passwordEditText;
    private MyOnClickListener myOnClickListener = new MyOnClickListener(){
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.login_button:
                    login();
                    break;
                case R.id.login_add_user:
                    shortStartActivity(RegisterActivity.class,Constant.IS_REGISTER,Constant.YES);
                    break;
                case R.id.forget_psd:
                    shortStartActivity(RegisterActivity.class,Constant.IS_REGISTER,Constant.NO);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        super.onCreate(savedInstanceState);

        userNameEditText = (EditText) findViewById(R.id.user_name);
        passwordEditText = (EditText) findViewById(R.id.password);

        findViewById(R.id.login_button).setOnClickListener(myOnClickListener);
        findViewById(R.id.forget_psd).setOnClickListener(myOnClickListener);
        findViewById(R.id.login_add_user).setOnClickListener(myOnClickListener);
    }

    private void login() {
        //TODO for zhulinan, 这边的用户名和密码需要check,你参照上一个项目中的accheck，写一下
        String userName = userNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        userName = "13989880921";
        password = "123456";
        //end TODO

        final RequestParams requestParams = new RequestParams();

        requestParams.put(Constant.REQUEST_PARAMS_KEY_USER_NAME, userName);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_PASSWORD, password);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_LOGIN);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_TYPE_LOGIN);

        showWaitProgress();
        HttpClient.get(requestParams, LoginResponseData.class, new HttpClient.JsonResponseHandler<LoginResponseData>() {
            @Override
            public void onSuccess(LoginResponseData response) {
                Log.i(TAG, "login success");
                MyUser user = response.getUser();
                MyApp app = MyApp.getApp();
                app.setUser(user);
                app.getLocalConfigManager().updateUser(user);
                app.initConfigManager(new CommonNetworkListener() {

                    @Override
                    public void onSuccess() {
                        dismissWaitProgress();
                        shortStartActivity(MainActivity.class);
                        finish();
                    }

                    @Override
                    public void onFailure() {
                        dismissWaitProgress();
                        //TODO for zhulinan, 这边弹一条提示信息(toast)，初始化配置文件错误。
                        //Toast相关的在MyApp类里面有:
                        //MyApp.getApp().showToast();
                    }
                });
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "login fail");
                MyApp.getApp().showToast(R.string.login_failure);
            }
        });
    }
}
