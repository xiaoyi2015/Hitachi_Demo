package ac.airconditionsuit.app;

/**
 * Created by ac on 9/19/15.
 * PREFERENCE_KEY_*表示向{@link android.content.SharedPreferences}中存取内容用到的key
 * INTENT_DATA_KEY_*表示向{@link android.content.Intent#putExtra(String, String)}中存取内容用到的key
 * REQUEST_PARAMS_KEY_*表示HTTP请求参数的参数名
 */
public class Constant {
    public static final String PREFERENCE_KEY_CURRENT_USER = "current";

    public static final String INTENT_DATA_KEY_ACTIVITY_FROM = "activity_from" ;

    //params key
    public static final String REQUEST_PARAMS_KEY_CUST_CLASS = "cust_class";
    public static final String REQUEST_PARAMS_KEY_DEVICE_ID = "device_id";
    public static final String REQUEST_PARAMS_KEY_DEVICE_IP = "ip";
    public static final String REQUEST_PARAMS_KEY_COMMENT = "comment";
    public static final String REQUEST_PARAMS_KEY_INTRODUCE = "introduce";
    public static final String REQUEST_PARAMS_KEY_MAC = "mac";
    public static final String REQUEST_PARAMS_KEY_DEVICE_NAME = "name";
    public static final String REQUEST_PARAMS_KEY_REGISTER_FROM = "register_from";
    public static final String REQUEST_PARAMS_KEY_MOBILE_PHONE = "mobile_phone";
    public static final String REQUEST_PARAMS_KEY_PASSWORD = "password";
    public static final String REQUEST_PARAMS_KEY_USER_NAME = "user_name";
    public static final String REQUEST_PARAMS_KEY_VALIDATE_CODE = "phone_validate_code";
    public static final String REQUEST_PARAMS_KEY_LANGUAGE = "language";
    public static final String REQUEST_PARAMS_KEY_METHOD = "method";
    public static final String REQUEST_PARAMS_KEY_TYPE = "type";
    public static final String REQUEST_PARAMS_KEY_VERSION = "version";
    public static final String REQUEST_PARAMS_KEY_TOKEN = "token";
    public static final String REQUEST_PARAMS_KEY_CUST_ID = "cust_id";
    public static final String REQUEST_PARAMS_KEY_DISPLAY_ID = "display_id";

    //params value
    public static final String REQUEST_PARAMS_VALUE_METHOD_REGISTER = "eliteall.register";
    public static final String REQUEST_PARAMS_VALUE_METHOD_LOGIN = "eliteall.login";
    public static final String REQUEST_PARAMS_VALUE_METHOD_CUSTOMER = "eliteall.customer";
    public static final String REQUEST_PARAMS_VALUE_TYPE_VALIDATE_CODE = "sendRegMobileValidateCode";
    public static final String REQUEST_PARAMS_VALUE_TYPE_REGISTER = "register";
    public static final String REQUEST_PARAMS_VALUE_TYPE_FIND_PASSWORD = "verifyMobilePhone";
    public static final String REQUEST_PARAMS_VALUE_TYPE_LOGIN = "login";
    public static final String REQUEST_PARAMS_VALUE_TYPE_SAVE_CUSTOMER_INF = "saveCustInfoByField";

    //other
    public static final String CONFIG_FILE_SUFFIX = ".dc";
    public static final int FILE_DECODE_ENCODE_KEY = 0x33;
    public static final String SERVER_CONFIG_FILE_NAME = "serversetting.dc";
}