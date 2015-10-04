package ac.airconditionsuit.app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ac.airconditionsuit.app.R;

public class AdjustArrowView extends LinearLayout {

    private TextView labelTextView;
    private ImageView arrowIconView;

    public AdjustArrowView(Context context) {
        super(context);
        init(context);
    }

    public AdjustArrowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AdjustArrowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        inflate(context,R.layout.custom_common_adjust_arrow,this);
        labelTextView = (TextView)findViewById(R.id.label_text);
        arrowIconView = (ImageView)findViewById(R.id.arrow_icon);
        arrowIconView.setImageResource(R.drawable.icon_arrow_right);
    }

}
