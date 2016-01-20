package ac.airconditionsuit.app.entity;

import ac.airconditionsuit.app.MyApp;
import com.google.gson.Gson;

/**
 * Created by ac on 9/19/15.
 * entity class for user,will store information for current login user
 */
public class MyUser extends RootEntity {
    public static final int MAIL = 1;
    public static final int FEMAIL = 2;

    String token;
    long cust_id;
    String cust_name; // 用户名
    int cust_class;
    int sex = -1;
    String display_id;
    int cust_status;
    String avatar_big;
    String avatar_normal;
    String avatar;
    String email;
    String birthday;
    String auth;
    String phone;


    /**
     * 获取用户头像的url
     *
     * @return
     */
    public String getAvatar() {
        if (avatar != null && avatar.length() != 0) {
            return avatar + "?random=" + System.currentTimeMillis();
        } else if (avatar_normal != null && avatar_normal.length() != 0) {
            return avatar_normal + "?random=" + System.currentTimeMillis();
        } else if (avatar_big != null && avatar_big.length() != 0) {
            return avatar_big + "?random=" + System.currentTimeMillis();
        } else {
            return null;
        }
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
        this.avatar_big = avatar;
        this.avatar_normal = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCust_status() {
        return cust_status;
    }

    public void setCust_status(int cust_status) {
        this.cust_status = cust_status;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getDisplay_id() {
        return display_id;
    }

    public void setDisplay_id(String display_id) {
        this.display_id = display_id;
    }

    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }

    public int getCust_class() {
        return cust_class;
    }

    public void setCust_class(int cust_class) {
        this.cust_class = cust_class;
    }

    public Long getCust_id() {
        return cust_id;
    }

    public void setCust_id(long cust_id) {
        this.cust_id = cust_id;
    }

    public static int getIntSex(String sex) {
        switch (sex) {
            case "男":
                return MAIL;
            case "女":
                return FEMAIL;
            default:
                return 0;
        }
    }

    public boolean infComplete() {
        return cust_name != null && cust_name.length() != 0
                && sex != -1 && sex != 0
//                && birthday != null && birthday.length() != 0
                && avatar_big != null && avatar_big.length() != 0;
    }

    public static MyUser getInstanceFromJsonString(String currentUserString) {
        if (currentUserString == null) {
            return null;
        }
        return new Gson().fromJson(currentUserString, MyUser.class);
    }

    public boolean isAdmin() {
        return cust_id == MyApp.getApp().getServerConfigManager().getAdminCustId();
    }

}
