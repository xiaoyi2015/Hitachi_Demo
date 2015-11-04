package ac.airconditionsuit.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ac.airconditionsuit.app.R;

/**
 * Created by Administrator on 2015/10/17.
 */
public class CommonModeArrowView extends RelativeLayout {

    public CommonModeArrowView(Context context) {
        super(context);
        init(context);
    }

    public CommonModeArrowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CommonModeArrowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.custom_common_mode_arrow_view, this);
    }

}
