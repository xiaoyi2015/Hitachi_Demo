package ac.airconditionsuit.app;

/**
 * Created by ac on 10/15/15.
 */
public class UIManager {
    public static final int HIT = 1;
    public static final int DC = 2;
    public static final int HX = 3;

    public static final int UITYPE = HIT;

    public static int getLoginLayout() {
        switch (UITYPE) {
            case HIT:
                return R.layout.activity_login_hit;
            case DC:
                return R.layout.activity_login;
            default:
                return 0;
        }
    }


}
