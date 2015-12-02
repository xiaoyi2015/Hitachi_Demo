package ac.airconditionsuit.app.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.network.HttpClient;
import ac.airconditionsuit.app.network.response.RegisterResponseData;
import ac.airconditionsuit.app.util.CheckUtil;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/9/30.
 */
public class ChangePasswordActivity extends BaseActivity{

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
                case R.id.forget_psd:
                    shortStartActivity(RegisterActivity.class, Constant.IS_REGISTER, Constant.NO);
                    break;
            }
        }
    };
    private EditText oldPasswordText;
    private EditText newPassword;
    private EditText newPasswordAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_setting_change_password);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.change_password));
        switch (UIManager.UITYPE){
            case 1:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_hit);
                commonTopBar.setRightIconView(R.drawable.top_bar_save_hit);
                break;
            case 2:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_dc);
                commonTopBar.setRightIconView(R.drawable.top_bar_save_dc);
                break;
            default:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_dc);
                commonTopBar.setRightIconView(R.drawable.top_bar_save_dc);
                break;
        }
        commonTopBar.setIconView(myOnClickListener, myOnClickListener);

        TextView forgetPsd = (TextView) findViewById(R.id.forget_psd);
        forgetPsd.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        forgetPsd.getPaint().setFakeBoldText(true);

        oldPasswordText = (EditText)findViewById(R.id.old_password_text);
        newPassword = (EditText)findViewById(R.id.new_password);
        TextView forgetPassword = (TextView) findViewById(R.id.forget_psd);
        newPasswordAgain = (EditText)findViewById(R.id.new_password_again);

        forgetPassword.setOnClickListener(myOnClickListener);
    }

    private void submit() {

        final String oldPassword = CheckUtil.checkOldPassword(oldPasswordText);
        if(oldPassword == null){
            return;
        }

        final String password = CheckUtil.checkPassword(newPassword, newPasswordAgain);
        if (password == null) {
            return;
        }

        if (password.equals(oldPassword)) {
            MyApp.getApp().showToast(getString(R.string.oldPassWordEqualNewPassword));
            return;
        }

        final RequestParams requestParams = new RequestParams();
        requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_LOGIN);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_TYPE_MODIFY_PASSWORD);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_NEW_PASSWORD, password);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_CONFIRM_PASSWORD, password);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_OLD_PASSWORD, oldPassword);
        HttpClient.get(requestParams, String.class, new HttpClient.JsonResponseHandler<String>() {

            @Override
            public void onSuccess(String response) {
                MyApp.getApp().offLine();
                Intent intent = new Intent();
                intent.setClass(ChangePasswordActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.i(TAG, "onFailure");
            }
        });

    }

}
