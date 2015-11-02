package ac.airconditionsuit.app;

/**
 * Created by ac on 9/19/15.
 * PREFERENCE_KEY_*表示向{@link android.content.SharedPreferences}中存取内容用到的key
 * INTENT_DATA_KEY_*表示向{@link android.content.Intent#putExtra(String, String)}中存取内容用到的key
 * REQUEST_PARAMS_KEY_*表示HTTP请求参数的参数名
 * REQUEST_PARAMS_VALUE_*表示HTTP请求参数的value
 */
public class Constant {
    public static final String PREFERENCE_KEY_LOCAL_CONFIG = "localConfig";

    public static final String INTENT_DATA_KEY_ACTIVITY_FROM = "activity_from" ;
    public static final String INTENT_DATA_KEY_ACTIVITY_DEVICE_JSON_STRING = "device_json_string";
    public static final String INTENT_DATA_KEY_IS_FIRST = "isFirst" ;

    //params key
    public static final String REQUEST_PARAMS_KEY_CUST_CLASS = "cust_class";
    public static final String REQUEST_PARAMS_KEY_DEVICE_ID = "device_id";
    public static final String REQUEST_PARAMS_KEY_DEVICEID = "deviceId";
    public static final String REQUEST_PARAMS_KEY_UPLOAD_FILE = "uploadedFile";
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
    public static final String REQUEST_PARAMS_KEY_NEW_PASSWORD = "new_password";

    //params value
    public static final String REQUEST_PARAMS_VALUE_METHOD_REGISTER = "eliteall.register";
    public static final String REQUEST_PARAMS_VALUE_METHOD_LOGIN = "eliteall.login";
    public static final String REQUEST_PARAMS_VALUE_METHOD_CUSTOMER = "eliteall.customer";
    public static final String REQUEST_PARAMS_VALUE_METHOD_CHAT = "eliteall.chat";
    public static final String REQUEST_PARAMS_VALUE_METHOD_FILE = "eliteall.file";

    public static final String REQUEST_PARAMS_VALUE_TYPE_CANCEL = "cancel";
    public static final String REQUEST_PARAMS_VALUE_TYPE_RESET_DEVICE_CONFIG_FILE = "resetDeviceConfigfile";
    public static final String REQUEST_PARAMS_VALUE_TYPE_VALIDATE_CODE = "sendRegMobileValidateCode";
    public static final String REQUEST_PARAMS_VALUE_TYPE_REGISTER = "register";
    public static final String REQUEST_PARAMS_VALUE_TYPE_REGISTER_DEVICE = "registerDevice";
    public static final String REQUEST_PARAMS_VALUE_TYPE_FIND_PASSWORD = "verifyMobilePhone";
    public static final String REQUEST_PARAMS_VALUE_TYPE_LOGIN = "login";
    public static final String REQUEST_PARAMS_VALUE_TYPE_SAVE_CUSTOMER_INF = "saveCustInfoByField";
    public static final String REQUEST_PARAMS_VALUE_TYPE_GET_CHATGROUPLIST = "getChatGroupList";
    public static final String REQUEST_PARAMS_VALUE_TYPE_CUST_CLASS_10001 = "10001";
    public static final String REQUEST_PARAMS_VALUE_TYPE_UPLOAD_DEVICE_CONFIG_FILE = "uploadDeviceConfigFile";
    public static final String REQUEST_PARAMS_VALUE_TYPE_GET_CHAT_TOKEN = "getChatToken";
    public static final String REQUEST_PARAMS_VALUE_TYPE_APPLY_JOIN = "applyJoin";

    public static final String REQUEST_PARAMS_VALUE_TYPE_VALIDATE_CODE_FOR_FIND_PASSWORD ="sendMobileVerifyCode";

    //other
    public static final String CONFIG_FILE_SUFFIX = ".dc";
    public static final String TEMP_CONFIG_FILE_SUFFIX = ".dc.tmp";
    public static final String NO_DEVICE_CONFIG_FILE_PREFIX = "nd";
    public static final String AUTO_NO_DEVICE_CONFIG_FILE_PREFIX = "auto";
    public static final int FILE_DECODE_ENCODE_KEY = 0x33;
    public static final String SERVER_CONFIG_FILE_NAME = "serversetting.dc";
    public static final String IS_REGISTER = "is_register";
    public static final String YES = "yes";
    public static final String NO = "no";
    public static final String X_DC = "x" + CONFIG_FILE_SUFFIX;

    public static final String REQUEST_PARAMS_FIELD = "field";
    public static final String REQUEST_PARAMS_VALUE = "value";
    public static final String REQUEST_PARAMS_KEY_CUST_NAME = "cust_name";
    public static final String REQUEST_PARAMS_KEY_SEX = "sex";
    public static final String REQUEST_PARAMS_KEY_BIRTHDAY = "birthday";
    public static final String REQUEST_PARAMS_KEY_EMAIL = "email";

    public static final String REQUEST_PARAMS_TYPE_MODIFY_PASSWORD = "modifyPassword";
    public static final String REQUEST_PARAMS_TYPE_MODIFY_MOBILE = "modifyMobile";
    public static final String REQUEST_PARAMS_KEY_CONFIRM_PASSWORD = "confirm_password";
    public static final String REQUEST_PARAMS_KEY_OLD_PASSWORD = "old_password";
    public static final String REQUEST_PARAMS_TYPE_SET_CUSTOMER_AVATAR = "setCustAvatar";
    public static final String REQUEST_PARAMS_TYPE_GET_CHAT_CUST_LIST = "getChatAllCustList";
    public static final String REQUEST_PARAMS_KEY_CHAT_ID = "chat_id";
    public static final String REQUEST_PARAMS_KEY_T = "t";
    public static final String REQUEST_PARAMS_KEY_DELETE_CUST_ID = "delete_cust_id";

    public static final String REQUEST_PARAMS_VALUE_TYPE_GET_PUSHDATA = "getUnreadMsgList2";
    public static final java.lang.String REQUEST_PARAMS_VALUE_TYPE_ACK_MSG_ID = "setUnreadMsgListACK";
    public static final String REQUEST_PARAMS_KEY_PN_MSG_IDS = "msg_ids";
    public static final java.lang.String REQUEST_PARAMS_VALUE_TYPE_DELETE_CHAT_CUST = "deleteChatCust";

    public static final String NEW_HOME_NAME = "新的家";
}
