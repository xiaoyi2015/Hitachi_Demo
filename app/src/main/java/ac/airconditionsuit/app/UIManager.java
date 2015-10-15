package ac.airconditionsuit.app;

/**
 * Created by ac on 10/15/15.
 */
public class UIManager {
    public static final int UI_TYPE1 = 1;
    public static final int UI_TYPE2 = 2;
    public static final int UI_TYPE3 = 3;

    private int uiType = UI_TYPE1;

    int getXXXLayout() {
        switch (uiType) {
            case UI_TYPE1:
                return 0;
            case UI_TYPE2:
                return 0;
            case UI_TYPE3:
                return 0;
        }
    }
}
