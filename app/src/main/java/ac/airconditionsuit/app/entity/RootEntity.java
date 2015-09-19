package ac.airconditionsuit.app.entity;

import com.google.gson.Gson;

/**
 * Created by ac on 9/19/15.
 */
public class RootEntity {
    public String toJsonString() {
        return new Gson().toJson(this);
    }
}
