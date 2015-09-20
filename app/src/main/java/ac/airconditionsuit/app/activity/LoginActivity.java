package ac.airconditionsuit.app.activity;

import ac.airconditionsuit.app.Config.LocalConfigManager;
import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.entity.MyUser;
import ac.airconditionsuit.app.network.HttpClient;
import ac.airconditionsuit.app.network.response.LoginResponseData;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.Header;

/**
 * Created by ac on 9/19/15.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText userNameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        super.onCreate(savedInstanceState);

        userNameEditText = (EditText) findViewById(R.id.user_name);
        passwordEditText = (EditText) findViewById(R.id.password);

        findViewById(R.id.login_button).setOnClickListener(this);
        findViewById(R.id.forget_psd).setOnClickListener(this);
        findViewById(R.id.login_add_user).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
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

    private void login() {
        //TODO for zhulinan, 这边的用户名和密码需要check,你参照上一个项目中的accheck，写一下
        String userName = userNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        //temp code
        userName = "13989880921";
        password = "123456";


        final RequestParams requestParams = new RequestParams();

        requestParams.put(Constant.REQUEST_PARAMS_KEY_USER_NAME, userName);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_PASSWORD, password);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_LOGIN);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_TYPE_LOGIN);

        HttpClient.get(requestParams, LoginResponseData.class, new HttpClient.JsonResponseHandler<LoginResponseData>() {
            @Override
            public void onSuccess(LoginResponseData response) {
                Log.i(TAG, "onSuccess");
                MyUser user = response.getUser();
                MyApp app = MyApp.getApp();
                app.getLocalConfigManager().saveUser(user);
                app.initConfigManager();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.i(TAG, "onFailure");
            }
        });
    }
}
