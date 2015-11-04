package ac.airconditionsuit.app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import ac.airconditionsuit.app.R;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * a customer view for button with arrow
 */
public class CommonButtonWithArrow extends LinearLayout {
    private TextView labelTextView;
    private TextView onlineTextView;

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

        String textLabel = a.getString(
                R.styleable.CommonButtonWithArrow_textLabel);
        Boolean rightArrow = a.getBoolean(
                R.styleable.CommonButtonWithArrow_rightArrow,true);

        a.recycle();

        inflate(context, R.layout.custom_common_button_with_arrow, this);


        labelTextView = (TextView)findViewById(R.id.label_text);
        onlineTextView = (TextView)findViewById(R.id.online_text);

        if(!rightArrow)
        {
            onlineTextView.setCompoundDrawables(null,null,null,null);
        }
        labelTextView.setText(textLabel);
    }

    public void setOnlineTextView(String string){
        onlineTextView.setText(string);
    }

    public void setOnlineTextView(int stringId){
        onlineTextView.setText(stringId);
    }

    public void setLabelTextView(String string) {
        labelTextView.setText(string);
    }

    public void setLabelTextView(int stringId) {
        labelTextView.setText(stringId);
    }

    public TextView getLabelTextView(){
        return labelTextView;
    }

    public TextView getOnlineTextView(){
        return onlineTextView;
    }

}
