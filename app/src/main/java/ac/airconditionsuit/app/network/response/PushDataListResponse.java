package ac.airconditionsuit.app.network.response;

import ac.airconditionsuit.app.PushData.PushDataManager;

/**
 * Created by Administrator on 2015/10/15.
 */
public class PushDataListResponse {
    int chat_type;
    String msg_time;
    String chat_id;
    String from_cust_id;
    String to_cust_id;
    int msg_type;
    String msg_id;
    String msg_no;
    PushDataManager.PushData content;

    public PushDataManager.PushData getContent() {
        return content;
    }

}
