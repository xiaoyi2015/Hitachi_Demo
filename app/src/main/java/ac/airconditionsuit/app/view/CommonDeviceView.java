package ac.airconditionsuit.app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ac.airconditionsuit.app.R;

/**
    document your custom view class.
 */
public class CommonDeviceView extends RelativeLayout {

    private TextView bottomName;
    private ImageView bgIcon;
    private ImageView rightUpIcon;
    private TextView rightUpText;

    public CommonDeviceView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public CommonDeviceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CommonDeviceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context,AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CommonDeviceView, defStyle, 0);
        String bottom_name = a.getString(R.styleable.CommonDeviceView_bottom_name);
        Drawable bg_icon = a.getDrawable(R.styleable.CommonDeviceView_bg_icon);
        Drawable right_up_icon = a.getDrawable(R.styleable.CommonDeviceView_right_up_icon);
        String right_up_text = a.getString(R.styleable.CommonDeviceView_right_up_text);
        Drawable right_up_text_bg = a.getDrawable(R.styleable.CommonDeviceView_right_up_text_bg);

        a.recycle();
        inflate(context, R.layout.custom_device_view, this);

        bottomName = (TextView)findViewById(R.id.bottom_name);
        bgIcon = (ImageView)findViewById(R.id.bg_icon);
        rightUpIcon = (ImageView)findViewById(R.id.right_up_icon);
        rightUpText = (TextView)findViewById(R.id.right_up_text);

        bottomName.setText(bottom_name);
        bgIcon.setImageDrawable(bg_icon);
        rightUpIcon.setImageDrawable(right_up_icon);
        rightUpText.setBackgroundDrawable(right_up_text_bg);
        rightUpText.setText(right_up_text);

    }

    public void setBgIcon(int drawable){
        bgIcon.setImageResource(drawable);
    }

    public void setBottomName(String string){
        bottomName.setText(string);
    }

    public void setRightUpText(String string){
        rightUpText.setText(string);
        rightUpText.setBackgroundResource(R.drawable.drag_device_name_bar);
    }

    public void setBottomNameColor(){
        bottomName.setTextColor(getResources().getColor(R.color.text_color_black));
    }

}
