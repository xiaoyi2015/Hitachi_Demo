package ac.airconditionsuit.app.view;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import ac.airconditionsuit.app.R;

/**
 * Created by Administrator on 2015/10/5.
 */
public class RoomCustomView extends LinearLayout{

    public RoomCustomView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.custom_room_view,this);
    }

}
