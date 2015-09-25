package ac.airconditionsuit.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.view.CommonDeviceView;

/**
 * Created by Administrator on 2015/9/25.
 */
public class DragDeviceAdapter extends BaseAdapter{
    private Context context;

    public DragDeviceAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = new CommonDeviceView(context);

        }
        TextView textView = (TextView)convertView.findViewById(R.id.bottom_name);
        ImageView imageView = (ImageView)convertView.findViewById(R.id.bg_icon);
        ImageView imageView1 = (ImageView)convertView.findViewById(R.id.right_up_icon);


        textView.setText("11");
        imageView.setImageResource(R.drawable.drag_setting_room_bar);
        imageView1.setImageResource(R.drawable.drag_setting_cancel);

        return convertView;
    }
}
