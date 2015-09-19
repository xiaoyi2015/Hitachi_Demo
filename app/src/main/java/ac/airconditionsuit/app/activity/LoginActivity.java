package ac.airconditionsuit.app.activity;

import ac.airconditionsuit.app.Constant;
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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                login();
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


        RequestParams requestParams = new RequestParams();

        requestParams.put(Constant.REQUEST_PARAMS_KEY_USER_NAME, userName);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_PASSWORD, password);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_LOGIN);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_TYPE_LOGIN);

        HttpClient.get(this, requestParams, LoginResponseData.class, new BaseJsonHttpResponseHandler<LoginResponseData>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, LoginResponseData response) {
                //TODO
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, LoginResponseData errorResponse) {

            }

            @Override
            protected LoginResponseData parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });


    }
}
