package ac.airconditionsuit.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ac.airconditionsuit.app.R;

/**
 * Created by Administrator on 2015/10/11.
 */
public class CommonChooseView extends RelativeLayout {

    private TextView textView;
    private ImageView imageView;

    public CommonChooseView(Context context) {
        super(context);
        init(context);
    }

    public CommonChooseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CommonChooseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.custom_common_choose_view, this);
        textView = (TextView)findViewById(R.id.label_text);
        imageView = (ImageView)findViewById(R.id.choose_icon);
    }

    public void setText(String string){
        textView.setText(string);
    }

    public ImageView getIcon(){
        return imageView;
    }
}
