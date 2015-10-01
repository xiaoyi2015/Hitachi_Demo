package ac.airconditionsuit.app.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.network.HttpClient;
import ac.airconditionsuit.app.view.CommonButtonWithArrow;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/9/18.
 */
public class UserInfoActivity extends BaseActivity {
    public static final int MALE = 1;
    public static final int FEMALE = 2;
    private static final int REQUEST_CODE_USER_NAME = 101;
    private static final int REQUEST_CODE_EMAIL = 102;
    private static final int REQUEST_CODE_PHONE = 103;
    private MyOnClickListener myOnClickListener = new MyOnClickListener(){
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.left_icon:
                    finish();
                    break;
                case R.id.nick_name:
                    shortStartActivityForResult(ChangeUserNameActivity.class,REQUEST_CODE_USER_NAME,"title",getString(R.string.nick_name));
                    break;
                case R.id.gender:
                    LayoutInflater inflater = LayoutInflater.from(UserInfoActivity.this);
                    View v1 = inflater.inflate(R.layout.pop_up_window_gender, null);
                    final PopupWindow pop = new PopupWindow(v1, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
                    pop.setBackgroundDrawable(new BitmapDrawable());
                    pop.setOutsideTouchable(true);
                    RelativeLayout view = (RelativeLayout)findViewById(R.id.user_info_page);
                    pop.showAtLocation(view,Gravity.BOTTOM,0,0);

                    TextView male = (TextView)v1.findViewById(R.id.male);
                    TextView female = (TextView)v1.findViewById(R.id.female);
                    TextView cancel = (TextView)v1.findViewById(R.id.cancel);
                    male.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final RequestParams requestParams = new RequestParams();
                            requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_CUSTOMER);
                            requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_TYPE_SAVE_CUSTOMER_INF);
                            requestParams.put(Constant.REQUEST_PARAMS_FIELD, Constant.REQUEST_PARAMS_KEY_SEX);
                            requestParams.put(Constant.REQUEST_PARAMS_VALUE, MALE);

                            HttpClient.post(requestParams, String.class, new HttpClient.JsonResponseHandler<String>() {
                                @Override
                                public void onSuccess(String response) {
                                    gender.setOnlineTextView(getString(R.string.male));
                                    MyApp.getApp().getUser().setSex(MALE);
                                    MyApp.getApp().getLocalConfigManager().updateUser(MyApp.getApp().getUser());
                                }

                                @Override
                                public void onFailure(Throwable throwable) {
                                    MyApp.getApp().showToast(R.string.change_user_sex_failure);
                                }
                            });
                            pop.dismiss();
                        }
                    });

                    female.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final RequestParams requestParams = new RequestParams();
                            requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_CUSTOMER);
                            requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_TYPE_SAVE_CUSTOMER_INF);
                            requestParams.put(Constant.REQUEST_PARAMS_FIELD, Constant.REQUEST_PARAMS_KEY_SEX);
                            requestParams.put(Constant.REQUEST_PARAMS_VALUE, FEMALE);

                            HttpClient.post(requestParams, String.class, new HttpClient.JsonResponseHandler<String>() {
                                @Override
                                public void onSuccess(String response) {
                                    gender.setOnlineTextView(getString(R.string.female));
                                    MyApp.getApp().getUser().setSex(FEMALE);
                                    MyApp.getApp().getLocalConfigManager().updateUser(MyApp.getApp().getUser());
                                }

                                @Override
                                public void onFailure(Throwable throwable) {
                                    MyApp.getApp().showToast(R.string.change_user_sex_failure);
                                }
                            });
                            pop.dismiss();
                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pop.dismiss();
                        }
                    });
                    break;
                case R.id.change_birth:

                    String[] birthday = MyApp.getApp().getUser().getBirthday().split("-");
                    int year, month, day;
                    if (birthday.length != 3) {
                        year = 1991;
                        month = 0;
                        day = 1;
                    } else {
                        try {
                            year = Integer.valueOf(birthday[0]);
                            month = Integer.valueOf(birthday[1]) - 1;
                            day = Integer.valueOf(birthday[2]);
                        } catch (Exception e) {
                            year = 1991;
                            month = 0;
                            day = 1;
                            e.printStackTrace();
                        }
                    }
                    DatePickerDialog datePickerDialog = new DatePickerDialog(UserInfoActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            final String changedBirth = year + "-" + (monthOfYear+1) + "-" + dayOfMonth;
                            final RequestParams requestParams = new RequestParams();
                            requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_CUSTOMER);
                            requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_TYPE_SAVE_CUSTOMER_INF);
                            requestParams.put(Constant.REQUEST_PARAMS_FIELD, Constant.REQUEST_PARAMS_KEY_BIRTHDAY);
                            requestParams.put(Constant.REQUEST_PARAMS_VALUE, changedBirth);

                            HttpClient.post(requestParams, String.class, new HttpClient.JsonResponseHandler<String>() {
                                @Override
                                public void onSuccess(String response) {
                                    birth.setOnlineTextView(changedBirth);
                                    MyApp.getApp().getUser().setBirthday(changedBirth);
                                    MyApp.getApp().getLocalConfigManager().updateUser(MyApp.getApp().getUser());
                                }

                                @Override
                                public void onFailure(Throwable throwable) {
                                    MyApp.getApp().showToast(R.string.change_user_birthday_failure);
                                }
                            });

                        }
                    },year,month,day );
                    datePickerDialog.show();
                    break;
                case R.id.change_phone:
                    shortStartActivityForResult(ChangePhoneActivity.class,REQUEST_CODE_PHONE);
                    break;
                case R.id.change_email:
                    shortStartActivityForResult(ChangeUserNameActivity.class,REQUEST_CODE_EMAIL,"title",getString(R.string.change_email));
                    break;
                case R.id.home_list:
                    shortStartActivity(AddHomeActivity.class);
                    break;
                case R.id.change_password:
                    shortStartActivity(ChangePasswordActivity.class);
                    break;
                case R.id.common_agree_clause:
                    shortStartActivity(AgreementActivity.class);
                    break;
                case R.id.quit_account:
                    new AlertDialog.Builder(UserInfoActivity.this).setMessage(R.string.is_quit_account).
                            setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    intent.setClass(UserInfoActivity.this,LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }).setNegativeButton(R.string.cancel, null).setCancelable(false).show();

                    break;
                case R.id.network_icon:
                    //TODO for zln
                    break;
            }
        }
    };
    private CommonButtonWithArrow nickName;
    private CommonButtonWithArrow gender;
    private CommonButtonWithArrow birth;
    private CommonButtonWithArrow phone;
    private CommonButtonWithArrow email;
    private CommonButtonWithArrow addHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_setting_user_information);
        super.onCreate(savedInstanceState);

        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.fill_user_info));
        commonTopBar.setIconView(myOnClickListener, null);

        ImageView userIcon = (ImageView)findViewById(R.id.network_icon);

        nickName = (CommonButtonWithArrow)findViewById(R.id.nick_name);
        gender = (CommonButtonWithArrow)findViewById(R.id.gender);
        birth = (CommonButtonWithArrow)findViewById(R.id.change_birth);
        phone = (CommonButtonWithArrow)findViewById(R.id.change_phone);
        email = (CommonButtonWithArrow)findViewById(R.id.change_email);
        addHome = (CommonButtonWithArrow)findViewById(R.id.home_list);
        CommonButtonWithArrow password = (CommonButtonWithArrow) findViewById(R.id.change_password);
        CommonButtonWithArrow clause = (CommonButtonWithArrow)findViewById(R.id.common_agree_clause);
        CommonButtonWithArrow exit = (CommonButtonWithArrow)findViewById(R.id.quit_account);

        HttpClient.loadImage(MyApp.getApp().getUser().getAvatar_big(), userIcon);
        nickName.setOnlineTextView(MyApp.getApp().getUser().getCust_name());
        if(MyApp.getApp().getUser().getSex() == 1)
            gender.setOnlineTextView(getString(R.string.male));
        else
            gender.setOnlineTextView(getString(R.string.female));
        birth.setOnlineTextView(MyApp.getApp().getUser().getBirthday());
        phone.setOnlineTextView(MyApp.getApp().getUser().getPhone());
        email.setOnlineTextView(MyApp.getApp().getUser().getEmail());

        userIcon.setOnClickListener(myOnClickListener);
        nickName.setOnClickListener(myOnClickListener);
        gender.setOnClickListener(myOnClickListener);
        birth.setOnClickListener(myOnClickListener);
        phone.setOnClickListener(myOnClickListener);
        email.setOnClickListener(myOnClickListener);
        addHome.setOnClickListener(myOnClickListener);
        password.setOnClickListener(myOnClickListener);
        clause.setOnClickListener(myOnClickListener);
        exit.setOnClickListener(myOnClickListener);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case REQUEST_CODE_USER_NAME:
                    String user_name = data.getStringExtra("userName");
                    nickName.setOnlineTextView(user_name);
                    MyApp.getApp().getUser().setCust_name(user_name);
                    MyApp.getApp().getLocalConfigManager().updateUser(MyApp.getApp().getUser());
                    break;
                case REQUEST_CODE_EMAIL:
                    String email_text = data.getStringExtra("email");
                    email.setOnlineTextView(email_text);
                    MyApp.getApp().getUser().setEmail(email_text);
                    MyApp.getApp().getLocalConfigManager().updateUser(MyApp.getApp().getUser());
                    break;
                case REQUEST_CODE_PHONE:
                    String phone_text = data.getStringExtra("userName");
                    phone.setOnlineTextView(phone_text);
                    MyApp.getApp().getUser().setPhone(phone_text);
                    MyApp.getApp().getLocalConfigManager().updateUser(MyApp.getApp().getUser());
                    break;

            }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
