package ac.airconditionsuit.app.util;

import ac.airconditionsuit.app.entity.RootEntity;
import android.util.Log;
import com.dd.plist.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Created by ac on 9/20/15.
 * plist util for convert to json or javaObject to plist
 */
public class PlistUtil {

    private static final String TAG = "PlistUtil";

    public static NSArray JavaListToNSArray(List<Object> list) {
        NSArray listNs = new NSArray(list.size());
        for (int i = 0; i < list.size(); i++) {
            Object object = list.get(i);
            if (object instanceof RootEntity) {
                NSDictionary value = JavaObjectToNSDictionary(object);
                listNs.setValue(i, value);
            } else {
                listNs.setValue(i, object);
            }
        }
        return listNs;
    }


    @SuppressWarnings("unchecked")
    public static void JavaObjectToNSDictionary(Object object, NSDictionary nsDictionary) {
        Field[] fields = object.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                Object fieldValue = field.get(object);
                if (fieldValue == null) {
                    nsDictionary.put(field.getName(), null);
                } else if (fieldValue instanceof List) {
                    nsDictionary.put(field.getName(), JavaListToNSArray((List)fieldValue));
                } else if (fieldValue instanceof RootEntity) {
                    nsDictionary.put(field.getName(), JavaObjectToNSDictionary(fieldValue));
                } else {
                    nsDictionary.put(field.getName(), fieldValue);
                }
            }
        } catch (IllegalAccessException e) {
            Log.e(TAG, "java Object To plist error");
            e.printStackTrace();
        }
    }

    public static NSDictionary JavaObjectToNSDictionary(Object object) {
        NSDictionary nsDictionary = new NSDictionary();
        JavaObjectToNSDictionary(object, nsDictionary);
        return nsDictionary;
    }

    public static String NSDictionaryToJsonString(NSDictionary dictionary) {
        Map<String, NSObject> map = dictionary.getHashMap();
        String result = "{";
        for (String key : map.keySet()) {
            NSObject value = map.get(key);
            result += "\"" + key + "\":";
            if (value instanceof NSDictionary) {
                result += NSDictionaryToJsonString((NSDictionary) value) + ",";
            } else if (value instanceof NSArray) {
                result += NSArrayToJsonString((NSArray) value) + ",";
            } else if (value instanceof NSString) {
                result += "\"" + ((NSString) value).getContent() + "\",";
            } else if (value instanceof NSNumber) {
                if (((NSNumber) value).isBoolean()) {
                    result += ((NSNumber) value).boolValue() ? "true," : "false,";
                } else if (((NSNumber) value).isInteger()) {
                    if (key.equals("onoff")) {//将onoff的int值转化为boolean值
                        result += ((((NSNumber) value).intValue() == 0) ? "false," : "true,");
                    }
                    else {
                        result += ((NSNumber) value).longValue() + ",";
                    }
                } else {
                    result += ((NSNumber) value).doubleValue() + ",";
                }
            }
        }

        if (result.length() > 1) {
            result = result.substring(0, result.length() - 1);
        }

        result += "}";
        return result;
    }

    public static String NSArrayToJsonString(NSArray value) {
        if (value == null) {
            return "null";
        }
        NSObject[] array = value.getArray();
        if (array.length == 0) {
            return "[]";
        }
        String result = "[";

        if (array[0] instanceof NSDictionary) {
            for (NSObject dict : array) {
                result += NSDictionaryToJsonString((NSDictionary) dict) + ",";
            }
        } else if (array[0] instanceof NSArray) {
            for (NSObject list : array) {
                result += NSArrayToJsonString((NSArray) list) + ",";
            }
        } else if (array[0] instanceof NSString) {
            for (NSObject str : array) {
                result += "\"" + ((NSString) str).getContent() + "\",";
            }
        } else if (array[0] instanceof NSNumber) {
            if (((NSNumber) array[0]).isBoolean()) {
                for (NSObject b : array) {
                    result += ((NSNumber) b).boolValue() ? "true," : "false,";
                }
            } else if (((NSNumber) array[0]).isInteger()) {
                for (NSObject i : array) {
                    result += ((NSNumber) i).longValue() + ",";
                }
            } else {
                for (NSObject d : array) {
                    result += ((NSNumber) d).doubleValue() + ",";
                }
            }
        }

        if (result.length() > 1) {
            result = result.substring(0, result.length() - 1);
        }

        result += "]";
        return result;
    }
}
