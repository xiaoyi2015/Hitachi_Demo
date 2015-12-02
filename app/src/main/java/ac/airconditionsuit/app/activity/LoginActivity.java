package ac.airconditionsuit.app.activity;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.entity.MyUser;
import ac.airconditionsuit.app.listener.CommonNetworkListener;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.network.HttpClient;
import ac.airconditionsuit.app.network.response.LoginResponseData;
import ac.airconditionsuit.app.network.socket.TcpSocket;
import ac.airconditionsuit.app.util.CheckUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.net.Inet4Address;

/**
 * Created by ac on 9/19/15.
 */
public class LoginActivity extends BaseActivity {

    private static final int REQUEST_CODE_REGISTER = 123;
    private EditText userNameEditText;
    private CheckBox rememberCheckBox;
    private EditText passwordEditText;
    private MyOnClickListener myOnClickListener = new MyOnClickListener() {
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
                case R.id.login_info:
                    shortStartActivity(SoftwareInfoActivity.class);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(UIManager.getLoginLayout());
        super.onCreate(savedInstanceState);

        userNameEditText = (EditText) findViewById(R.id.user_name);
        passwordEditText = (EditText) findViewById(R.id.password);
        rememberCheckBox = (CheckBox) findViewById(R.id.remember_psd_box);

        findViewById(R.id.login_button).setOnClickListener(myOnClickListener);
        findViewById(R.id.forget_psd).setOnClickListener(myOnClickListener);
        findViewById(R.id.login_add_user).setOnClickListener(myOnClickListener);
        findViewById(R.id.login_info).setOnClickListener(myOnClickListener);
        findViewById(R.id.login_info).setVisibility((UIManager.UITYPE == UIManager.DC) ? View.VISIBLE : View.GONE);

        setOnclickListenerOnTextViewDrawable(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof EditText) {
                    ((EditText) v).setText("");
                }
            }
        }, passwordEditText, userNameEditText);

        if (MyApp.getApp().getLocalConfigManager().getCurrentUserConfig() != null) {
            userNameEditText.setText(MyApp.getApp().getLocalConfigManager().getCurrentUserPhoneNumber());
        }
        userNameEditText.setSelection(userNameEditText.getText().length());
        if(rememberCheckBox.isChecked()){
            passwordEditText.setText(MyApp.getApp().getLocalConfigManager().getCurrentUserRememberedPassword());
        }
        else {
            passwordEditText.setText("");
        }
        rememberCheckBox.setChecked(true);
    }

    private void login() {

        final String userName = userNameEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        String userNameCheck = CheckUtil.checkEmpty(userNameEditText, R.string.user_name_empty_info);
        if (userNameCheck == null) {
            return;
        }

        String passwordCheck = CheckUtil.checkEmpty(passwordEditText, R.string.password_empty_info);
        if (passwordCheck == null) {
            return;
        }

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
                app.getLocalConfigManager().setCurrentUserPhoneNumber(userName);
                app.getLocalConfigManager().setCurrentPassword(password);
                if (rememberCheckBox.isChecked()){
                    app.getLocalConfigManager().setCurrentUserRememberedPassword(password);
                } else {
                    app.getLocalConfigManager().setCurrentUserRememberedPassword("");
                }
                app.initServerConfigManager(new CommonNetworkListener() {

                    @Override
                    public void onSuccess() {
                        dismissWaitProgress();

                        MyUser user = MyApp.getApp().getUser();
                        MyApp.getApp().initSocketManager();
                        MyApp.getApp().initPushDataManager();

                        if (user.infComplete()) {
                            shortStartActivity(MainActivity.class);
                        } else {
                            shortStartActivity(UserInfoActivity.class);
                        }
                        finish();
                    }

                    @Override
                    public void onFailure() {
                        dismissWaitProgress();
                        MyApp.getApp().showToast(R.string.init_file_error);
                    }
                });
                registerDevice();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "login fail");
                dismissWaitProgress();
            }
        });
    }

    private void registerDevice() {
        RequestParams requestParams = new RequestParams();
        requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, "regDeviceNo");
        requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_CHAT);
        requestParams.put("device_no", "");
        HttpClient.get(requestParams, String.class, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case REQUEST_CODE_REGISTER:
                    userNameEditText.setText(data.getStringExtra("userName"));
                    passwordEditText.setText("");
                    break;

            }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
