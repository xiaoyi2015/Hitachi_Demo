package ac.airconditionsuit.app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import ac.airconditionsuit.app.R;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * TODO: document your custom view class.
 */
public class CommonButtonWithArrow extends LinearLayout {
    private String textLabel;
    private String textValue;
    private TextView labelTextView;
    private TextView valueTextView;

    final static public String SOME_CONSTANT = "fsdf";

    public CommonButtonWithArrow(Context context) {
        super(context);
        init(context, null, 0);
    }

    public CommonButtonWithArrow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CommonButtonWithArrow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CommonButtonWithArrow, defStyle, 0);

        textLabel = a.getString(
                R.styleable.CommonButtonWithArrow_textLabel);
        textValue = a.getString(
                R.styleable.CommonButtonWithArrow_textValue);

        a.recycle();

        inflate(context, R.layout.custom_common_button_with_arrow, this);

        labelTextView = (TextView) findViewById(R.id.label_text);
        valueTextView = (TextView) findViewById(R.id.value_text);

        labelTextView.setText(textLabel);
        valueTextView.setText(textValue);
    }

}
