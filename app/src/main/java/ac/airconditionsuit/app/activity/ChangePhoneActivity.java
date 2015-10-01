package ac.airconditionsuit.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
 * Created by Administrator on 2015/9/30.
 */
public class ChangePhoneActivity extends BaseActivity{

    private String mobilePhoneNumberStart = "";
    private EditText oldPasswordText;
    private EditText phoneNumberText;
    private Button getVerifyCodeButton;
    private EditText inputVerifyCodeText;
    private Handler handler;
    private final static int ENABLE_BUTTON = 10086;
    private MyOnClickListener myOnClickListener = new MyOnClickListener(){
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()){
                case R.id.left_icon:
                    finish();
                    break;
                case R.id.right_icon:
                    submit();
                    break;
                case R.id.change_phone_get_verify_code:
                    Object tag = getVerifyCodeButton.getTag();
                    if (tag == null || (Boolean) tag) {
                        getVerifyCode();
                    } else {
                        MyApp.getApp().showToast(R.string.send_verify_code);
                    }
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_setting_change_phone);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.change_phone));
        commonTopBar.setIconView(myOnClickListener, myOnClickListener);

        oldPasswordText = (EditText)findViewById(R.id.old_password_text);
        phoneNumberText = (EditText)findViewById(R.id.changed_phone_number_edit_text);
        getVerifyCodeButton =(Button)findViewById(R.id.change_phone_get_verify_code);
        inputVerifyCodeText = (EditText)findViewById(R.id.input_verification_code);

        getVerifyCodeButton.setOnClickListener(myOnClickListener);

        handler = new Handler(new Handler.Callback(){
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case ENABLE_BUTTON:
                        enableButton(getVerifyCodeButton);
                        break;
                    default:
                        Log.e(TAG, "unhandle case in #hangler");
                }
                return true;
            }
        });

        //TODO for zln
    }

    private void submit() {

        final String oldPassword = CheckUtil.checkOldPassword(oldPasswordText);
        if(oldPassword == null){
            return;
        }

        final String mobilePhoneStr = CheckUtil.checkMobilePhone(phoneNumberText);
        if (mobilePhoneStr == null) {
            return;
        }

        if (!mobilePhoneStr.equals(mobilePhoneNumberStart)) {
            MyApp.getApp().showToast(R.string.mobile_number_is_changed);
            return;
        }

        String verificationCode = CheckUtil.checkVerificationCode(inputVerifyCodeText);
        if (verificationCode == null) {
            return;
        }

        final RequestParams requestParams = new RequestParams();
        requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_REGISTER);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_TYPE_REGISTER);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_MOBILE_PHONE, mobilePhoneStr);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_VALIDATE_CODE, verificationCode);
        HttpClient.get(requestParams, RegisterResponseData.class, new HttpClient.JsonResponseHandler<RegisterResponseData>() {
            @Override
            public void onSuccess(RegisterResponseData response) {
                Intent intent = new Intent();
                intent.putExtra("userName",mobilePhoneStr);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.i(TAG, "onFailure");
            }
        });

    }

    private void getVerifyCode() {
        final String mobilePhone = CheckUtil.checkMobilePhone(phoneNumberText);
        if (mobilePhone == null) {
            return;
        }

        final RequestParams requestParams = new RequestParams();
        requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_REGISTER);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_TYPE_VALIDATE_CODE);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_MOBILE_PHONE, mobilePhone);
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

    private void enableButton(Button getVerifyCodeButton) {
        getVerifyCodeButton.setTag(false);
        //noinspection deprecation for Downward compatible
        getVerifyCodeButton.setTextColor(getResources().getColor(R.color.text_color_black));
        handler.sendEmptyMessageDelayed(ENABLE_BUTTON, 60000);
    }

    private void disableButton(Button getVerifyCodeButton) {
        getVerifyCodeButton.setTag(true);
        getVerifyCodeButton.setTextColor(getResources().getColor(R.color.white));
    }

}
