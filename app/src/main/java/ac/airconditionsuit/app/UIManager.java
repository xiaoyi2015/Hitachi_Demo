package ac.airconditionsuit.app;

/**
 * Created by ac on 10/15/15.
 */
public class UIManager {
    public static final int HIT = 1;
    public static final int DC = 2;
    public static final int HX = 3;

    public static final int UITYPE = DC;

    public static int getLoginLayout() {
        switch (UITYPE) {
            case HIT:
                return R.layout.activity_login_hit;
            case DC:
                return R.layout.activity_login;
            default:
                return R.layout.activity_login_hx;
        }
    }

    public static int getMainActivityLayout() {
        switch (UITYPE) {
            case HIT:
                return R.layout.activity_main_hit;
            case DC:
                return R.layout.activity_main;
            default:
                return R.layout.activity_main_hit;
        }
    }

    public static int getSoftwareInfoLayout() {
        switch (UITYPE) {
            case HIT:
                return R.layout.fragment_setting_software_info_hit;
            case DC:
                return R.layout.fragment_setting_software_info;
            default:
                return R.layout.fragment_setting_software_info_hx;
        }
    }

    public static int getSplashLayout() {
        switch (UITYPE) {
            case HIT:
                return R.layout.activity_splash_hit;
            case DC:
                return R.layout.activity_splash;
            default:
                return R.layout.activity_splash_hx;
        }
    }

    public static int getTopBarRightIconRes() {
        switch (UIManager.UITYPE){
            case HIT:
                return R.drawable.top_bar_save_hit;
            case DC:
                return R.drawable.top_bar_save_dc;
            default:
                return R.drawable.top_bar_save_dc;
        }
    }

    public static int getHomeBarRes() {
        switch (UIManager.UITYPE){
            case HIT:
                return R.drawable.top_banner_hit;
            case DC:
                return R.drawable.top_banner_dc;
            default:
                return R.drawable.top_banner_hx;
        }
    }

    public static int getArrowRight() {
        switch (UIManager.UITYPE){
            case HIT:
                return R.drawable.icon_arrow_right_hit;
            case DC:
                return R.drawable.icon_arrow_right_dc;
            default:
                return R.drawable.icon_arrow_right_hit;
        }
    }

    public static int getRefreshColor(){
        switch (UIManager.UITYPE){
            case HIT:
                return android.R.color.holo_red_dark;
            case DC:
                return R.color.text_normal_color;
            default:
                return R.color.delete_red_hx;
        }
    }

}
