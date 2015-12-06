package ac.airconditionsuit.app.entity;

/**
 * Created by ac on 10/11/15.
 */
public class ObserveData {
    public static final int FIND_DEVICE_BY_UDP = 0;
    public static final int FIND_DEVICE_BY_UDP_FAIL = 1;

    public static final int OFFLINE = 2;

    public static final int TIMER_STATUS_RESPONSE = 6;
    public static final int AIR_CONDITION_STATUS_RESPONSE = 3;

    public static final int SEARCH_AIR_CONDITION_RESPONSE = 4;
    public static final int SEARCH_AIR_CONDITION_NUMBERDIFFERENT = 3843;
    public static final int SEARCH_AIR_CONDITION_CANCEL_TIMER= 3844;
    public static final int NETWORK_STATUS_CHANGE = 5;
    public static final int FIND_DEVICE_BY_UDP_FINASH = 7;

    int msg;
    Object data;

    public ObserveData(int msg) {
        this(msg, null);
    }

    public ObserveData(int msg, Object data) {
        this.msg = msg;
        this.data = data;
    }

    public int getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }
}
