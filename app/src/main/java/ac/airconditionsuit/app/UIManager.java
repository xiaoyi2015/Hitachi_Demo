package ac.airconditionsuit.app;

/**
 * Created by ac on 10/15/15.
 */
public class UIManager {
    public static final int RILI = 1;
    public static final int HAIXIN = 2;
    public static final int D_CONTROL = 3;

    public static final int UITYPE = RILI;

    int getRoomLayout() {
        switch (UITYPE) {
            case RILI:
                return 0;
            case HAIXIN:
                return 0;
            default:
                return 0;
        }
    }

}
