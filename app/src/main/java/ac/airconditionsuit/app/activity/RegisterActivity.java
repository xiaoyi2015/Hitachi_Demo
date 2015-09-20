package ac.airconditionsuit.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.loopj.android.http.RequestParams;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.network.HttpClient;
import ac.airconditionsuit.app.network.response.GetVerifyCodeResponse;
import ac.airconditionsuit.app.network.response.RegisterResponseData;
import ac.airconditionsuit.app.util.CheckUtil;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/9/20.
 */
public class RegisterActivity extends BaseActivity {
    private Boolean isRegister;
    private MyOnClickListener myOnClickListener = new MyOnClickListener(){
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch(v.getId()){
                case R.id.left_cancel:
                    finish();
                    break;
                case R.id.right_yes:
                    submit();
                    break;
                case R.id.register_get_verify_code:
                    Object tag = getVerifyCodeButton.getTag();
                    if (tag == null || (Boolean) tag) {
                        getVerifyCode();
                    } else {
                        MyApp.getApp().showToast(R.string.send_verify_code);
                    }
                    break;

                case R.id.register_phone_number_edit_text:

                case R.id.register_verification_code_edit_text:

                case R.id.register_password_edit_text:

                case R.id.register_confirm_password_edit_text:

                case R.id.register_agree_clause:
            }
        }
    };

    private LinearLayout registerAgreeClause;
    private CheckBox isAgreeCheckBox;
    private ImageView registerLeftCancel;
    private ImageView registerRightYes;
    private EditText mobilePhoneEditText;
    private EditText verifyCodeEditText;
    private EditText passwordFirstEditText;
    private EditText passwordSecondEditText;
    private String mobilePhoneNumberStart = "";
    private Button getVerifyCodeButton;
    private Handler handler;
    private final static int ENABLE_BUTTON = 10086;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.login_register);
        super.onCreate(savedInstanceState);
        isRegister = getIntent().getStringExtra(Constant.IS_REGISTER).equals(Constant.YES);
        CommonTopBar commonTopBar = getCommonTopBar();
        registerAgreeClause = (LinearLayout)findViewById(R.id.register_agree_clause);
        isAgreeCheckBox = (CheckBox)findViewById(R.id.register_agree_box);
        registerLeftCancel = (ImageView)findViewById(R.id.left_cancel);
        registerLeftCancel.setOnClickListener(myOnClickListener);
        registerRightYes = (ImageView)findViewById(R.id.right_yes);
        registerRightYes.setOnClickListener(myOnClickListener);
        getVerifyCodeButton = (Button)findViewById(R.id.register_get_verify_code);
        getVerifyCodeButton.setOnClickListener(myOnClickListener);

        mobilePhoneEditText = (EditText) findViewById(R.id.register_phone_number_edit_text);
        verifyCodeEditText = (EditText) findViewById(R.id.register_verification_code_edit_text);
        passwordFirstEditText = (EditText) findViewById(R.id.register_password_edit_text);
        passwordSecondEditText = (EditText) findViewById(R.id.register_confirm_password_edit_text);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case ENABLE_BUTTON:
                        enableButton(getVerifyCodeButton);
                        break;
                    default:
                        //TODO
                }
                return true;
            }
        });

        if(isRegister) {
            commonTopBar.setTitle(getString(R.string.register_new_user));
        }
        else{
            commonTopBar.setTitle(getString(R.string.find_password));
            registerAgreeClause.setVisibility(View.GONE);
        }
    }

    private void submit() {
        if(isRegister)
        {
            if(!isAgreeCheckBox.isChecked()){
                MyApp.getApp().showToast(R.string.pls_read_clause);
                return;
            }
        }

        final String mobilePhoneStr = CheckUtil.checkMobilePhone(mobilePhoneEditText);
        if (mobilePhoneStr == null) {
            return;
        }

        if (!mobilePhoneStr.equals(mobilePhoneNumberStart)) {
            MyApp.getApp().showToast(R.string.mobile_number_is_changed);
            return;
        }

        String verificationCode = CheckUtil.checkVerificationCode(verifyCodeEditText);
        if (verificationCode == null) {
            return;
        }

        final String password = CheckUtil.checkPassword(passwordFirstEditText, passwordSecondEditText);

        if (password == null) {
            return;
        }

        final RequestParams requestParams = new RequestParams();
        if(!isRegister) {
            requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_LOGIN);
            requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.TYPE_FIND_PASSWORD);
            requestParams.put(Constant.PN_MOBILE_PHONE, mobilePhoneStr);
            requestParams.put(Constant.PN_NEW_PASSWORD, password);
            requestParams.put(Constant.PN_VALIDATE_CODE, verificationCode);
            HttpClient.get(requestParams, RegisterResponseData.class, new HttpClient.JsonResponseHandler<RegisterResponseData>() {
                @Override
                public void onSuccess(RegisterResponseData response) {
                    //这里处理请求成功
                    MyApp.getApp().showToast(R.string.set_psd_success);
                    finish();
                }
                @Override
                public void onFailure(Throwable throwable) {
                    Log.i(TAG, "onFailure");
                }
            });
        }else{
            requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.METHOD_REGISTER);
            requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.TYPE_REGISTER);
            requestParams.put(Constant.PN_MOBILE_PHONE, mobilePhoneStr);
            requestParams.put(Constant.PN_PASSWORD, password);
            requestParams.put(Constant.PN_VALIDATE_CODE, verificationCode);
            HttpClient.get(requestParams, RegisterResponseData.class, new HttpClient.JsonResponseHandler<RegisterResponseData>() {
                @Override
                public void onSuccess(RegisterResponseData response) {
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    setResult(RESULT_OK, intent);
                    finish();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.i(TAG, "onFailure");
                }
            });
        }

    }

    private void getVerifyCode() {
        final String mobilePhone = CheckUtil.checkMobilePhone(mobilePhoneEditText);
        if (mobilePhone == null) {
            return;
        }

        final RequestParams requestParams = new RequestParams();
        if(!isRegister) {
            requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_LOGIN);
            requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.TYPE_VALIDATE_CODE_FOR_FIND_PASSWORD);
            requestParams.put(Constant.PN_MOBILE_PHONE, mobilePhone);
            disableButton(getVerifyCodeButton);
            HttpClient.get(requestParams, String.class, new HttpClient.JsonResponseHandler<String>() {
                @Override
                public void onSuccess(String response) {
                    mobilePhoneNumberStart = mobilePhone;
                    MyApp.getApp().showToast(R.string.send_verify_code);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.i(TAG, "onFailure");
                    enableButton(getVerifyCodeButton);
                }
            });
        }else{
            requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.METHOD_REGISTER);
            requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.TYPE_VALIDATE_CODE);
            requestParams.put(Constant.PN_MOBILE_PHONE, mobilePhone);
            disableButton(getVerifyCodeButton);
            HttpClient.get(requestParams, GetVerifyCodeResponse.class, new HttpClient.JsonResponseHandler<GetVerifyCodeResponse>() {
                @Override
                public void onSuccess(GetVerifyCodeResponse response) {
                    if (response.getIs_exist() == 1) {
                        MyApp.getApp().showToast(R.string.phone_already_exist);
                        enableButton(getVerifyCodeButton);
                    } else {
                        mobilePhoneNumberStart = mobilePhone;
                        MyApp.getApp().showToast(R.string.send_verify_code);
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.i(TAG, "onFailure");
                    enableButton(getVerifyCodeButton);
                }
            });
        }

    }

    private void enableButton(Button getVerifyCodeButton) {
        getVerifyCodeButton.setTag(false);
        getVerifyCodeButton.setTextColor(getResources().getColor(R.color.text_color_gray));
        handler.sendEmptyMessageDelayed(ENABLE_BUTTON, 60000);
    }

    private void disableButton(Button getVerifyCodeButton) {
        getVerifyCodeButton.setTag(true);
        getVerifyCodeButton.setTextColor(getResources().getColor(R.color.white));
    }


}
