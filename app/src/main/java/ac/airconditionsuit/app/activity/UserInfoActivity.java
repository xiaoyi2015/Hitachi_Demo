package ac.airconditionsuit.app.activity;

import ac.airconditionsuit.app.entity.MyUser;
import ac.airconditionsuit.app.network.response.UploadAvatar;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.network.HttpClient;
import ac.airconditionsuit.app.view.CommonButtonWithArrow;
import ac.airconditionsuit.app.view.CommonTopBar;
import ac.airconditionsuit.app.view.RoundImageView;

/**
 * Created by Administrator on 2015/9/18.
 */
public class UserInfoActivity extends BaseActivity {
    public static final int MALE = 1;
    public static final int FEMALE = 2;
    private static final int REQUEST_CODE_USER_NAME = 101;
    private static final int REQUEST_CODE_EMAIL = 102;
    private static final int REQUEST_CODE_PHONE = 103;
    private static final int PICK_FROM_GALLERY_CODE = 104;
    private static final int CAMERA_REQUEST_CODE = 105;
    private static final int CROP_REQUEST_CODE = 106;
    private MyOnClickListener myOnClickListener = new MyOnClickListener() {
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.left_icon:
                    setResult(RESULT_OK);
                    finish();
                    break;
                case R.id.nick_name:
                    shortStartActivityForResult(ChangeUserNameActivity.class, REQUEST_CODE_USER_NAME, "title", getString(R.string.nick_name));
                    break;
                case R.id.gender:
                    LayoutInflater inflater = LayoutInflater.from(UserInfoActivity.this);
                    @SuppressLint("InflateParams")
                    View v1 = inflater.inflate(R.layout.pop_up_window_gender, null);
                    final PopupWindow pop = new PopupWindow(v1, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
                    pop.setBackgroundDrawable(new BitmapDrawable());
                    pop.setOutsideTouchable(true);
                    RelativeLayout view = (RelativeLayout) findViewById(R.id.user_info_page);
                    pop.showAtLocation(view, Gravity.BOTTOM, 0, 0);

                    TextView male = (TextView) v1.findViewById(R.id.male);
                    TextView female = (TextView) v1.findViewById(R.id.female);
                    TextView cancel = (TextView) v1.findViewById(R.id.cancel);
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

                    String birthdayString = MyApp.getApp().getUser().getBirthday();
                    String[] birthday;
                    if (birthdayString == null) {
                        birthday = null;
                    } else {
                        birthday = birthdayString.split("-");
                    }
                    int year, month, day;
                    if (birthday == null || birthday.length != 3) {
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
                            final String changedBirth = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
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
                    }, year, month, day);
                    datePickerDialog.show();
                    break;
                case R.id.change_phone:
                    shortStartActivityForResult(ChangePhoneActivity.class, REQUEST_CODE_PHONE);
                    break;
                case R.id.change_email:
                    shortStartActivityForResult(ChangeUserNameActivity.class, REQUEST_CODE_EMAIL, "title", getString(R.string.change_email));
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
                                    quiteLogin();
                                }
                            }).setNegativeButton(R.string.cancel, null).setCancelable(false).show();

                    break;
                case R.id.network_icon:
                    new AlertDialog.Builder(UserInfoActivity.this).setTitle(getString(R.string.where_get_icon)).setItems(
                            new CharSequence[]{getString(R.string.album), getString(R.string.camera)}, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0) {
                                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                        photoPickerIntent.setType("image/*");
                                        startActivityForResult(photoPickerIntent, PICK_FROM_GALLERY_CODE);
                                    } else {
                                        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        intentFromCapture.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                                        intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, getAuxUri());
                                        startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
                                    }
                                    dialog.dismiss();
                                }
                            }).show();
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
    private RoundImageView userIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_setting_user_information);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.fill_user_info));
        switch (UIManager.UITYPE) {
            case 1:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_hit);
                break;
            case 2:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_dc);
                break;
            default:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_dc);
                break;
        }
        commonTopBar.setIconView(myOnClickListener, null);

        userIcon = (RoundImageView) findViewById(R.id.network_icon);

        nickName = (CommonButtonWithArrow) findViewById(R.id.nick_name);
        gender = (CommonButtonWithArrow) findViewById(R.id.gender);
        birth = (CommonButtonWithArrow) findViewById(R.id.change_birth);
        phone = (CommonButtonWithArrow) findViewById(R.id.change_phone);
        email = (CommonButtonWithArrow) findViewById(R.id.change_email);
        addHome = (CommonButtonWithArrow) findViewById(R.id.home_list);
        CommonButtonWithArrow password = (CommonButtonWithArrow) findViewById(R.id.change_password);
        CommonButtonWithArrow clause = (CommonButtonWithArrow) findViewById(R.id.common_agree_clause);
        CommonButtonWithArrow exit = (CommonButtonWithArrow) findViewById(R.id.quit_account);

        HttpClient.loadCurrentUserAvatar(MyApp.getApp().getUser().getAvatar(), userIcon);
        nickName.setOnlineTextView(MyApp.getApp().getUser().getCust_name());
        if (MyApp.getApp().getUser().getSex() == MyUser.MAIL) {
            gender.setOnlineTextView(getString(R.string.male));
        } else if (MyApp.getApp().getUser().getSex() == MyUser.FEMAIL) {
            gender.setOnlineTextView(getString(R.string.female));
        } else {
            gender.setOnlineTextView(getString(R.string.unknow));
        }
        birth.setOnlineTextView(MyApp.getApp().getUser().getBirthday());
        phone.setOnlineTextView(MyApp.getApp().getLocalConfigManager().getCurrentUserPhoneNumber());
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

        String fromActivityName = getIntent().getStringExtra(Constant.INTENT_DATA_KEY_ACTIVITY_FROM);
        if (fromActivityName != null &&
                (fromActivityName.equals(SplashActivity.class.getName())
                        || fromActivityName.equals(LoginActivity.class.getName()))) {
            password.setVisibility(View.GONE);
            clause.setVisibility(View.GONE);
            exit.setVisibility(View.GONE);
            phone.setVisibility(View.GONE);
            addHome.setVisibility(View.GONE);
            commonTopBar.setRightIconView(UIManager.getTopBarRightIconRes());
            commonTopBar.setIconView(null, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MyApp.getApp().getUser().infComplete()) {
                        shortStartActivity(MainActivity.class);
                        finish();
                        MyApp.getApp().getLocalConfigManager().saveToDisk();
                    } else {
                        MyApp.getApp().showToast(R.string.toast_inf_complete_user_inf);
                    }
                }
            });
        }
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
                case PICK_FROM_GALLERY_CODE:
                    startPhotoCrop(data.getData());
                    break;
                case CAMERA_REQUEST_CODE:
                    startPhotoCrop(auxUri);
                    break;
                case CROP_REQUEST_CODE:
                    uploadAvatar();
                    break;

            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadAvatar() {
        File avatar = new File(auxUri.getPath());
        final RequestParams requestParams = new RequestParams();
        requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_CUSTOMER);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_TYPE_SET_CUSTOMER_AVATAR);
        try {
            requestParams.put(Constant.REQUEST_PARAMS_KEY_UPLOAD_FILE, avatar);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "uploaded file can not found");
            e.printStackTrace();
            return;
        }
        HttpClient.post(requestParams, UploadAvatar.class, new HttpClient.JsonResponseHandler<UploadAvatar>() {
            @Override
            public void onSuccess(UploadAvatar response) {
                MyApp.getApp().getUser().setAvatar(response.getAvatar_url());
                HttpClient.loadCurrentUserAvatar(response.getAvatar_url(), userIcon);
                MyApp.getApp().showToast(R.string.upload_success);
            }

            @Override
            public void onFailure(Throwable throwable) {
                MyApp.getApp().showToast(R.string.upload_failure);
            }
        });

    }

    private File createImageFile() {
        // Save a file: path for use with ACTION_VIEW intents
        try {
            return File.createTempFile(
                    "temp",  /* prefix */
                    ".jpg",         /* suffix */
                    getExternalCacheDir()      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Uri auxUri = null;

    private Uri getAuxUri() {
        if (auxUri == null) {
            File imageFile = createImageFile();
            auxUri = Uri.fromFile(imageFile);
        }

        return auxUri;
    }

    public void startPhotoCrop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getAuxUri());
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }

}
