package ac.airconditionsuit.app.view;

import ac.airconditionsuit.app.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * tab indicator view on tab in main activity.
 */
public class TabIndicator extends TextView {
    private Drawable icon_normal;
    private Drawable icon_selected;
    private Context context;

    public TabIndicator(Context context) {
        super(context);
        init(context,null, 0);
    }

    public TabIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public TabIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        this.context = context;
        if (attrs == null) {
            return;
        }
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.TabIndicator, defStyle, 0);

        if (a.hasValue(R.styleable.TabIndicator_icon_normal)) {
            icon_normal = a.getDrawable(
                    R.styleable.TabIndicator_icon_normal);
            icon_normal.setCallback(this);
        }

        if (a.hasValue(R.styleable.TabIndicator_icon_selected)) {
            icon_selected = a.getDrawable(
                    R.styleable.TabIndicator_icon_selected);
            icon_selected.setCallback(this);
        }
        a.recycle();

        unSelect();
    }

    public void select(){
        setCompoundDrawablesWithIntrinsicBounds(null, icon_selected, null, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setTextColor(context.getResources().getColor(R.color.tab_indicator_text_select, null));
        }else{
            setTextColor(context.getResources().getColor(R.color.tab_indicator_text_select));
        }
    }

    public void unSelect(){
        setCompoundDrawablesWithIntrinsicBounds(null, icon_normal, null, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setTextColor(context.getResources().getColor(R.color.tab_indicator_text_normal, null));
        }else{
            setTextColor(context.getResources().getColor(R.color.tab_indicator_text_normal));
        }
    }
}
