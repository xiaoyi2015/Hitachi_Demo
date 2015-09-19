package ac.airconditionsuit.app;

import ac.airconditionsuit.app.Config.ConfigManager;
import ac.airconditionsuit.app.entity.MyUser;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/**
 * Created by ac on 9/19/15.
 *
 */
public class MyApp extends Application{
    private ConfigManager configManager;
    private MyUser user;

    @Override
    public void onCreate() {
        super.onCreate();
        initThingsForSpecificUser();
        initConfigManager();
    }

    private void initThingsForSpecificUser() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String currentUserString = sp.getString(Constant.PREFERENCE_KEY_CURRENT_USER, null);
        if (!TextUtils.isEmpty(currentUserString)) {
            user = MyUser.getInstanceFromJsonString(currentUserString);
        }
    }

    /**
     * TODO init for configManager
     */
    private void initConfigManager() {
    }
}
