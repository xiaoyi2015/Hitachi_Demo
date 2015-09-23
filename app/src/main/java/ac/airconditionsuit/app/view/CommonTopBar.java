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
import android.widget.RelativeLayout;
import android.widget.TextView;

import ac.airconditionsuit.app.R;

/**
 * document your custom view class.
 */
public class CommonTopBar extends RelativeLayout {
    private TextView titleView;
    private ImageView leftIconView;
    private ImageView leftArrowView;
    private ImageView rightImageView;
    private ImageView rightEditView;
    private ImageView rightYesView;
    private ImageView rightAddView;
    private ImageView leftCancelView;

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

        String titleLabel = a.getString(R.styleable.CommonTopBar_title);
        Drawable leftIcon = a.getDrawable(R.styleable.CommonTopBar_leftIcon);
        Drawable leftArrow = a.getDrawable(R.styleable.CommonTopBar_leftArrow);
        Drawable rightImage = a.getDrawable(R.styleable.CommonTopBar_rightImage);
        Drawable rightEdit = a.getDrawable(R.styleable.CommonTopBar_rightEdit);
        Drawable rightYes = a.getDrawable(R.styleable.CommonTopBar_rightYes);
        Drawable rightAdd = a.getDrawable(R.styleable.CommonTopBar_rightAdd);
        Drawable leftCancel = a.getDrawable(R.styleable.CommonTopBar_leftCancel);

        a.recycle();

        inflate(context, R.layout.custom_common_top_bar, this);

        titleView = (TextView) findViewById(R.id.title_label);
        leftIconView = (ImageView) findViewById(R.id.left_icon);
        leftArrowView = (ImageView) findViewById(R.id.left_arrow);
        rightImageView = (ImageView) findViewById(R.id.right_image);
        rightEditView = (ImageView) findViewById(R.id.right_edit);
        rightYesView = (ImageView) findViewById(R.id.right_yes);
        rightAddView = (ImageView) findViewById(R.id.right_add);
        leftCancelView = (ImageView) findViewById(R.id.left_cancel);

        titleView.setText(titleLabel);
        if (leftIcon == null) {
            leftIconView.setVisibility(GONE);
        } else {
            leftIconView.setImageDrawable(leftIcon);
        }
        if (leftArrow == null) {
            leftArrowView.setVisibility(GONE);
        } else {
            leftArrowView.setImageDrawable(leftArrow);
        }
        if (rightImage == null) {
            rightImageView.setVisibility(GONE);
        } else {
            rightImageView.setImageDrawable(rightImage);
        }
        if (rightEdit == null) {
            rightEditView.setVisibility(GONE);
        } else {
            rightEditView.setImageDrawable(rightEdit);
        }
        if (rightYes == null) {
            rightYesView.setVisibility(GONE);
        } else {
            rightYesView.setImageDrawable(rightYes);
        }
        if (rightAdd == null) {
            rightAddView.setVisibility(GONE);
        } else {
            rightAddView.setImageDrawable(rightAdd);
        }
        if (leftCancel == null) {
            leftCancelView.setVisibility(GONE);
        } else {
            leftCancelView.setImageDrawable(leftCancel);
        }
        setBackgroundResource(R.drawable.top_banner);
    }

    public void setTitle(String title){
        titleView.setText(title);
    }

}
