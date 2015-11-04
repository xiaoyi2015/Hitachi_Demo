package ac.airconditionsuit.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.loopj.android.http.RequestParams;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.network.HttpClient;
import ac.airconditionsuit.app.util.CheckUtil;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/9/27.
 */
public class ChangeUserNameActivity extends BaseActivity{

    private MyOnClickListener myOnClickListener = new MyOnClickListener(){
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.left_icon:
                    finish();
                    break;
                case R.id.right_icon:
                    if(title.equals(getString(R.string.nick_name))) {
                        final String user_name = CheckUtil.checkLength(changeUserName, 20, R.string.pls_input_nickname, R.string.nickname_length_too_long);
                        if (user_name == null)
                            return;
                        final RequestParams requestParams = new RequestParams();
                        requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_CUSTOMER);
                        requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_TYPE_SAVE_CUSTOMER_INF);
                        requestParams.put(Constant.REQUEST_PARAMS_FIELD, Constant.REQUEST_PARAMS_KEY_CUST_NAME);
                        requestParams.put(Constant.REQUEST_PARAMS_VALUE, user_name);
                        showWaitProgress();
                        HttpClient.post(requestParams, String.class, new HttpClient.JsonResponseHandler<String>() {
                            @Override
                            public void onSuccess(String response) {
                                Intent intent = new Intent();
                                intent.putExtra("userName", user_name);
                                setResult(RESULT_OK, intent);
                                finish();
                                dismissWaitProgress();
                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                MyApp.getApp().showToast(R.string.change_user_name_failure);
                                dismissWaitProgress();
                            }
                        });
                    }else{
                        final String email = CheckUtil.checkEmail(changeUserName);
                        if (email == null)
                            return;
                        final RequestParams requestParams = new RequestParams();
                        requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_CUSTOMER);
                        requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_TYPE_SAVE_CUSTOMER_INF);
                        requestParams.put(Constant.REQUEST_PARAMS_FIELD, Constant.REQUEST_PARAMS_KEY_EMAIL);
                        requestParams.put(Constant.REQUEST_PARAMS_VALUE, email);

                        showWaitProgress();
                        HttpClient.post(requestParams, String.class, new HttpClient.JsonResponseHandler<String>() {
                            @Override
                            public void onSuccess(String response) {
                                Intent intent = new Intent();
                                intent.putExtra("email", email);
                                setResult(RESULT_OK, intent);
                                finish();
                                dismissWaitProgress();
                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                MyApp.getApp().showToast(R.string.change_email_failure);
                                dismissWaitProgress();
                            }
                        });
                    }

                    break;
            }
        }
    };
    private EditText changeUserName;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_setting_change_user_name);
        super.onCreate(savedInstanceState);
        title = getIntent().getStringExtra("title");
        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(title);
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
        changeUserName = (EditText)findViewById(R.id.edit_user_name);

        String textLabel;
        if(title.equals(getString(R.string.nick_name))) {
            textLabel = MyApp.getApp().getUser().getCust_name();
        } else{
            textLabel = MyApp.getApp().getUser().getEmail();
        }
        changeUserName.setText(textLabel);
        if (textLabel == null) {
            changeUserName.setSelection(0);
        } else {
            changeUserName.setSelection(textLabel.length());
        }
    }
}
