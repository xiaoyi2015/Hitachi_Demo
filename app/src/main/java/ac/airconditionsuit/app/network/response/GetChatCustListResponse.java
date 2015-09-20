package ac.airconditionsuit.app.network.response;

import ac.airconditionsuit.app.entity.MyUser;

import java.util.List;

/**
 * Created by ac on 6/1/15.
 *
 */
public class GetChatCustListResponse {
    int last_update_id;

    List<MyUser> cust_list;

    public List<MyUser> getCust_list() {
        return cust_list;
    }

    public void setCust_list(List<MyUser> cust_list) {
        this.cust_list = cust_list;
    }

}
