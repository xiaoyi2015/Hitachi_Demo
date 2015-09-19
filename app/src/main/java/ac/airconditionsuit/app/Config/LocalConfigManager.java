package ac.airconditionsuit.app.Config;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.entity.MyUser;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/**
 * Created by ac on 9/19/15.
 */
public class LocalConfigManager {

    private SharedPreferences sharePreference;

    public LocalConfigManager(Context context) {
        this.sharePreference = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public MyUser getCurrentUser() {
        String currentUserString = sharePreference.getString(Constant.PREFERENCE_KEY_CURRENT_USER, null);
        if (TextUtils.isEmpty(currentUserString)) {
            return null;
        } else {
            return MyUser.getInstanceFromJsonString(currentUserString);
        }
    }
}
