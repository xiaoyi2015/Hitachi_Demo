package ac.airconditionsuit.app.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;

import ac.airconditionsuit.app.R;

public class SegmentControlView extends LinearLayout {

    private TextView textView1 = null;
    private TextView textView2 = null;
    private onSegmentControlViewClickListener listener;

    public SegmentControlView(Context context) {
        super(context);
        initView();
    }

    public SegmentControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SegmentControlView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        textView1 = new TextView(getContext());
        textView2 = new TextView(getContext());

        textView1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        textView2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        setSegmentText(0, getContext().getString(R.string.info_text));
        setSegmentText(1, getContext().getString(R.string.family_text));
        setSegmentTextSize(16);

        XmlPullParser xrp = getResources().getXml(R.xml.seg_text_color_selector);
        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(), xrp);
            textView1.setTextColor(csl);
            textView2.setTextColor(csl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        textView1.setGravity(Gravity.CENTER);
        textView2.setGravity(Gravity.CENTER);
        textView1.setPadding(3, 6, 3, 6);
        textView2.setPadding(3, 6, 3, 6);
        textView1.setBackgroundResource(R.drawable.seg_left);
        textView2.setBackgroundResource(R.drawable.seg_right);

        this.removeAllViews();
        this.addView(textView1);
        this.addView(textView2);
        this.invalidate();

        textView1.setSelected(true);
        textView1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textView1.isSelected()) {
                    return;
                }
                textView1.setSelected(true);
                textView2.setSelected(false);
                if (listener != null) {
                    listener.onSegmentControlViewClick(textView1, 0);
                }
            }
        });
        textView2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textView2.isSelected()) {
                    return;
                }
                textView2.setSelected(true);
                textView1.setSelected(false);
                if (listener != null) {
                    listener.onSegmentControlViewClick(textView2, 1);
                }
            }
        });

    }

    //set text size
    public void setSegmentTextSize(int dp) {
        textView1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, dp);
        textView2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, dp);
    }

    //set text
    public void setSegmentText(int position, CharSequence text) {
        if (position == 0) {
            textView1.setText(text);
        }
        if (position == 1) {
            textView2.setText(text);
        }
    }

    //set click listener
    public void setOnSegmentControlViewClickListener(onSegmentControlViewClickListener listener) {
        this.listener = listener;
    }

    public interface onSegmentControlViewClickListener {
        void onSegmentControlViewClick(View v, int position);
    }

    //text transform
    private static int dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}
