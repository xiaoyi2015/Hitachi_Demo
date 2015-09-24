package ac.airconditionsuit.app.activity;

import ac.airconditionsuit.app.Config.LocalConfigManager;
import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.entity.MyUser;
import ac.airconditionsuit.app.listener.CommonNetworkListener;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.network.HttpClient;
import ac.airconditionsuit.app.network.response.LoginResponseData;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.loopj.android.http.RequestParams;

/**
 * Created by ac on 9/19/15.
 */
public class LoginActivity extends BaseActivity {

    private static final int REQUEST_CODE_REGISTER = 123;
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
                    shortStartActivityForResult(RegisterActivity.class, REQUEST_CODE_REGISTER, Constant.IS_REGISTER, Constant.YES);
                    break;
                case R.id.forget_psd:
                    shortStartActivity(RegisterActivity.class, Constant.IS_REGISTER, Constant.NO);
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

        /**TODO for zhulinan
         * 这里要把之前登录的手机号码填充上。
         * 具体步骤如下，先取得当前用户{@link LocalConfigManager#getCurrentUserConfig()}
         * 如果为空，说明是第一次使用本软件。则什么都不用干。如果不为空，进行下一步：
         * 取得当前用户的登录手机号码 {@link LocalConfigManager#getCurrentUserPhoneNumber()}
         * 填入到对应的EdtiText。
         */
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
                        /**TODO for zhulinan
                         * 这边查看记住密码的checkbox,做相应操作。
                         * 相关函数为{@link LocalConfigManager#setCurrentUserRememberedPassword(String)}
                         * 如果不用记住密码，则传入空字符串
                         * 无论是不是记住密码，都要把登录手机好设置到LocalConfig {@link LocalConfigManager#setCurrentUserPhoneNumber(String)},这个函数在注册成功时也要调用。
                         */
                        shortStartActivity(MainActivity.class);
                        finish();
                    }

                    @Override
                    public void onFailure() {
                        dismissWaitProgress();
                        MyApp.getApp().showToast(R.string.init_file_error);
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

    /**TODO for zhulinan
     * 看一下{@link android.app.Activity#startActivityForResult(Intent, int)}相关内容，把注册成功后的的情况处理一下：
     * 暂时将用户名密码填上。让用户可以点击登录。
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CODE_REGISTER:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
