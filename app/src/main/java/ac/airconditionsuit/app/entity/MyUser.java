package ac.airconditionsuit.app.entity;

import com.google.gson.Gson;

/**
 * Created by ac on 9/19/15.
 * entity class for user,will store information for current login user
 */
public class MyUser extends RootEntity{
    public static MyUser getInstanceFromJsonString(String jsonString){
        return new Gson().fromJson(jsonString, MyUser.class);
    }
}
