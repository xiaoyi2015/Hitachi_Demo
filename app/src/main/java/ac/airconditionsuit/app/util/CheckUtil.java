package ac.airconditionsuit.app.util;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;

/**
 * Created by Administrator on 2015/9/20.
 */
public class CheckUtil {
    public static String checkMobilePhone(EditText editText) {
        String mobilePhone = editText.getText().toString();
        if (mobilePhone.length() == 0) {
            MyApp.getApp().showToast(R.string.pls_input_phone);
            return null;
        }
        if (mobilePhone.charAt(0) != '1' || mobilePhone.length() != 11) {
            MyApp.getApp().showToast(R.string.pls_input_right_phone);
            return null;
        }
        return mobilePhone;
    }

    public static String checkVerificationCode(EditText verificationCode) {
        String verificationCodeStr = verificationCode.getText().toString();
        if (verificationCodeStr.length() == 0) {
            MyApp.getApp().showToast(R.string.pls_input_verify_code);
            return null;
        }
        if (verificationCodeStr.length() != 4 || !isAllNumber(verificationCodeStr)) {
            MyApp.getApp().showToast(R.string.pls_input_right_verify_code);
            return null;
        }
        return verificationCodeStr;
    }

    public static String checkPassword(EditText passwordFirstEdtiText, EditText passwordSecondEdtiText) {
        String password = passwordFirstEdtiText.getText().toString();
        if (password.length() == 0) {
            MyApp.getApp().showToast(R.string.pls_input_psd);
            return null;
        }

        if (password.length() < 6 || password.length() > 12) {
            MyApp.getApp().showToast(R.string.pls_input_length_psd);
            return null;
        }

        String secondPassword = passwordSecondEdtiText.getText().toString();
        if (secondPassword.length() == 0) {
            MyApp.getApp().showToast(R.string.pls_input_psd_again);
            return null;
        }

        if (!password.equals(secondPassword)) {
            MyApp.getApp().showToast(R.string.differ_two_psd);
            return null;
        }

        if (password.contains(" ")) {
            MyApp.getApp().showToast(R.string.forbid_psd_blank);
            return null;
        }

        return password;
    }

    private static boolean isAllNumber(String verificationCode) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(verificationCode).matches();
    }

    private static boolean isEmail(String email) {
        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        return pattern.matcher(email).matches();
    }

    public static String checkEmail(EditText emailEditText) {
        String emailStr = emailEditText.getText().toString();
        if (emailEditText.length() == 0) {
            MyApp.getApp().showToast(R.string.pls_input_email);
            return null;
        }
        if (!isEmail(emailStr)) {
            MyApp.getApp().showToast(R.string.pls_input_right_email);
            return null;
        }
        return emailStr;
    }


}
