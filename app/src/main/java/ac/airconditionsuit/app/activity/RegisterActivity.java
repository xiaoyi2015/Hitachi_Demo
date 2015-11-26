package ac.airconditionsuit.app.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
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
    private MyOnClickListener myOnClickListener = new MyOnClickListener() {
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.left_icon:
                    finish();
                    break;
                case R.id.right_icon:
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
            }
        }
    };

    private CheckBox isAgreeCheckBox;
    private EditText mobilePhoneEditText;
    private EditText verifyCodeEditText;
    private EditText passwordFirstEditText;
    private EditText passwordSecondEditText;
    private String mobilePhoneNumberStart = "";
    private Button getVerifyCodeButton;
    private Handler handler;
    private final static int UPDATE_BUTTON_STATUS = 10086;
    private int remain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_register);
        super.onCreate(savedInstanceState);
        isRegister = getIntent().getStringExtra(Constant.IS_REGISTER).equals(Constant.YES);
        CommonTopBar commonTopBar = getCommonTopBar();
        LinearLayout registerAgreeClause = (LinearLayout) findViewById(R.id.register_agree_clause);
        isAgreeCheckBox = (CheckBox) findViewById(R.id.register_agree_box);
        switch (UIManager.UITYPE) {
            case 1:
                commonTopBar.setLeftIconView(R.drawable.top_bar_cancel_hit);
                commonTopBar.setRightIconView(R.drawable.top_bar_save_hit);
                break;
            case 2:
                commonTopBar.setLeftIconView(R.drawable.top_bar_cancel_dc);
                commonTopBar.setRightIconView(R.drawable.top_bar_save_dc);
                break;
            default:
                commonTopBar.setLeftIconView(R.drawable.top_bar_cancel_dc);
                commonTopBar.setRightIconView(R.drawable.top_bar_save_dc);
                break;
        }
        commonTopBar.setIconView(myOnClickListener, myOnClickListener);

        getVerifyCodeButton = (Button) findViewById(R.id.register_get_verify_code);
        getVerifyCodeButton.setOnClickListener(myOnClickListener);

        mobilePhoneEditText = (EditText) findViewById(R.id.register_phone_number_edit_text);
        verifyCodeEditText = (EditText) findViewById(R.id.register_verification_code_edit_text);
        passwordFirstEditText = (EditText) findViewById(R.id.register_password_edit_text);
        passwordSecondEditText = (EditText) findViewById(R.id.register_confirm_password_edit_text);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE_BUTTON_STATUS:
                        Integer obj = (Integer) msg.obj;
                        if (obj == 0) {
                            getVerifyCodeButton.setTag(true);
                            getVerifyCodeButton.setOnClickListener(myOnClickListener);
                            getVerifyCodeButton.setTextColor(getResources().getColor(R.color.text_color_black));
                            getVerifyCodeButton.setText(getString(R.string.get_verify_code));
                        } else {
                            getVerifyCodeButton.setText(obj + "秒");
                        }
                        break;
                    default:
                        Log.e(TAG, "unhandle case in #hangler");
                }
                return true;
            }
        });

        if (isRegister) {
            commonTopBar.setTitle(getString(R.string.register_new_user));
            TextView registerClause = (TextView) registerAgreeClause.findViewById(R.id.register_clause);
            registerClause.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            registerClause.getPaint().setFakeBoldText(true);
            registerClause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shortStartActivity(AgreementActivity.class);
                }
            });
        } else {
            commonTopBar.setTitle(getString(R.string.find_password));
            registerAgreeClause.setVisibility(View.GONE);
        }
    }

    private void submit() {
        if (isRegister) {
            if (!isAgreeCheckBox.isChecked()) {
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
        if (!isRegister) {
            requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_LOGIN);
            requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_TYPE_FIND_PASSWORD);
            requestParams.put(Constant.REQUEST_PARAMS_KEY_MOBILE_PHONE, mobilePhoneStr);
            requestParams.put(Constant.REQUEST_PARAMS_KEY_NEW_PASSWORD, password);
            requestParams.put(Constant.REQUEST_PARAMS_KEY_VALIDATE_CODE, verificationCode);
            HttpClient.get(requestParams, RegisterResponseData.class, new HttpClient.JsonResponseHandler<RegisterResponseData>() {
                @Override
                public void onSuccess(RegisterResponseData response) {
                    MyApp.getApp().showToast(R.string.set_psd_success);
                    finish();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.i(TAG, "onFailure");
                }
            });
        } else {
            requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_REGISTER);
            requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_TYPE_REGISTER);
            requestParams.put(Constant.REQUEST_PARAMS_KEY_MOBILE_PHONE, mobilePhoneStr);
            requestParams.put(Constant.REQUEST_PARAMS_KEY_PASSWORD, password);
            requestParams.put(Constant.REQUEST_PARAMS_KEY_VALIDATE_CODE, verificationCode);
            HttpClient.get(requestParams, RegisterResponseData.class, new HttpClient.JsonResponseHandler<RegisterResponseData>() {
                @Override
                public void onSuccess(RegisterResponseData response) {
                    Intent intent = new Intent();
                    intent.putExtra("userName", mobilePhoneStr);
                    intent.putExtra("password", password);
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
        disableButton(getVerifyCodeButton);
        final RequestParams requestParams = new RequestParams();
        if (!isRegister) {
            requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_LOGIN);
            requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_TYPE_VALIDATE_CODE_FOR_FIND_PASSWORD);
            requestParams.put(Constant.REQUEST_PARAMS_KEY_MOBILE_PHONE, mobilePhone);
            HttpClient.get(requestParams, String.class, new HttpClient.JsonResponseHandler<String>() {
                @Override
                public void onSuccess(String response) {
                    mobilePhoneNumberStart = mobilePhone;
                    MyApp.getApp().showToast(R.string.send_verify_code);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.i(TAG, "onFailure");
                    remain = -1;
                    getVerifyCodeButton.setTag(true);
                    getVerifyCodeButton.setOnClickListener(myOnClickListener);
                    getVerifyCodeButton.setTextColor(getResources().getColor(R.color.text_color_black));
                    getVerifyCodeButton.setText(getString(R.string.get_verify_code));
                }
            });
        } else {
            requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_REGISTER);
            requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_TYPE_VALIDATE_CODE);
            requestParams.put(Constant.REQUEST_PARAMS_KEY_MOBILE_PHONE, mobilePhone);
            HttpClient.get(requestParams, GetVerifyCodeResponse.class, new HttpClient.JsonResponseHandler<GetVerifyCodeResponse>() {
                @Override
                public void onSuccess(GetVerifyCodeResponse response) {
                    if (response.getIs_exist() == 1) {
                        MyApp.getApp().showToast(R.string.phone_already_exist);
                        remain = -1;
                        getVerifyCodeButton.setTag(true);
                        getVerifyCodeButton.setOnClickListener(myOnClickListener);
                        getVerifyCodeButton.setTextColor(getResources().getColor(R.color.text_color_black));
                        getVerifyCodeButton.setText(getString(R.string.get_verify_code));
                    } else {
                        mobilePhoneNumberStart = mobilePhone;
                        MyApp.getApp().showToast(R.string.send_verify_code);
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.i(TAG, "onFailure");
                    remain = -1;
                    getVerifyCodeButton.setTag(true);
                    getVerifyCodeButton.setOnClickListener(myOnClickListener);
                    getVerifyCodeButton.setTextColor(getResources().getColor(R.color.text_color_black));
                    getVerifyCodeButton.setText(getString(R.string.get_verify_code));
                }
            });
        }
    }

    private void enableButtonAfter60s() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                remain = 300;
                while (remain > -1) {
                    Message message = new Message();
                    message.what = UPDATE_BUTTON_STATUS;
                    message.obj = remain;
                    handler.sendMessage(message);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    remain--;
                }
            }
        }).start();
    }

    private void disableButton(final Button getVerifyCodeButton) {
        getVerifyCodeButton.setTag(false);
        getVerifyCodeButton.setOnClickListener(null);
        getVerifyCodeButton.setTextColor(getResources().getColor(R.color.text_color_white));
        enableButtonAfter60s();
    }
}
