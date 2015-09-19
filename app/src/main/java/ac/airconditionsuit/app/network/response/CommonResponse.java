package ac.airconditionsuit.app.network.response;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by ac on 6/1/15.
 *
 */
public class CommonResponse {
    public static final int SUCCESS_CODE = 2000;
    public static final int TOKEN_ERROR_CODE = 2001;
    public static final int FAIL_CODE = 2002;
    public static final int MODEL_ERROR_CODE = 2003;
    public static final int METHOD_ERROR_CODE = 2004;
    public static final int TYPE_ERROR_CODE = 2005;

    int code;
    Message msg;
    int timestamp;
    JsonElement data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Message getMsg() {
        return msg;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public JsonElement getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }

    public String getCodeInf() {
        return msg.getStr();
    }

    public static class Message {
        String str;
        String dialog;

        public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }

        public String getDialog() {
            return dialog;
        }

        public void setDialog(String dialog) {
            this.dialog = dialog;
        }
    }

}
