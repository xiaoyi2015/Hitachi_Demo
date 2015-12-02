package ac.airconditionsuit.app.view;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.network.HttpClient;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * document your custom view class.
 */
public class CommonTopBar extends RelativeLayout {
    private TextView titleView;
    private ImageView leftIconView;
    private ImageView rightIconView;
    private RoundImageView roundLeftIconView;

    public CommonTopBar(Context context) {
        super(context);
        init(context,null, 0);
    }

    public CommonTopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CommonTopBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context,attrs, defStyle);
    }

    private void init(Context context,AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CommonTopBar, defStyle, 0);

        String titleLabel = a.getString(R.styleable.CommonTopBar_titleText);
        Drawable leftIcon = a.getDrawable(R.styleable.CommonTopBar_leftIcon);
        Drawable rightIcon = a.getDrawable(R.styleable.CommonTopBar_rightIcon);

        a.recycle();

        inflate(context, R.layout.custom_common_top_bar, this);

        titleView = (TextView) findViewById(R.id.title_label);
        leftIconView = (ImageView) findViewById(R.id.left_icon);
        rightIconView = (ImageView) findViewById(R.id.right_icon);
        roundLeftIconView = (RoundImageView)findViewById(R.id.round_left_icon);

        titleView.setText(titleLabel);
        leftIconView.setImageDrawable(leftIcon);
        rightIconView.setImageDrawable(rightIcon);
        switch (UIManager.UITYPE) {
            case 1:
                setBackgroundResource(R.drawable.top_banner_hit);
                titleView.setTextColor(getResources().getColor(R.color.text_color_white));
                break;
            case 2:
                setBackgroundResource(R.drawable.top_banner_dc);
                break;
            default:
                setBackgroundResource(R.drawable.top_banner_hx);
                break;
        }

    }

    public void setTitle(String title){
        titleView.setText(title);
    }

    public void setIconView(OnClickListener leftListener, OnClickListener rightListener){
        if(leftListener == null)
            leftIconView.setVisibility(GONE);
        else
            leftIconView.setOnClickListener(leftListener);
        if(rightListener == null)
            rightIconView.setVisibility(GONE);
        else
            rightIconView.setOnClickListener(rightListener);
    }

    public void setRightIconView(int id){
        rightIconView.setImageResource(id);
        rightIconView.setVisibility(VISIBLE);
    }

    public void setLeftIconView(int id){
        leftIconView.setImageResource(id);
        leftIconView.setVisibility(VISIBLE);
    }

    public void setRoundLeftIconView(OnClickListener listener){
        if(listener == null){
            roundLeftIconView.setVisibility(GONE);
        }else {
            HttpClient.loadCurrentUserAvatar(MyApp.getApp().getUser().getAvatar(),roundLeftIconView);
            roundLeftIconView.setOnClickListener(listener);
            roundLeftIconView.setVisibility(VISIBLE);
        }

    }

    public TextView getTitleView(){
        return titleView;
    }

}
