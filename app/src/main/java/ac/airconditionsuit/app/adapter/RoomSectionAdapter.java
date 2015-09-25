package ac.airconditionsuit.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.view.CommonButtonWithArrow;

/**
 * Created by Administrator on 2015/9/25.
 */
public class RoomSectionAdapter extends BaseAdapter {
    private Context context;

    public RoomSectionAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return 9;
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
        if(convertView == null){
            convertView = new CommonButtonWithArrow(context);
        }
        TextView group_name = (TextView)convertView.findViewById(R.id.label_text);
        group_name.setText("111");

        return convertView;
    }
}
