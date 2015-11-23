package ac.airconditionsuit.app.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.SwipeListView;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.PushData.PushDataManager;
import ac.airconditionsuit.app.R;

/**
 * Created by Administrator on 2015/10/3.
 */
public class InfoFragment extends Fragment{
    private SwipeListView pushDataListView;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle saveInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_info, container, false);

        pushDataListView = (SwipeListView) rootView.findViewById(R.id.pushDataList);
        pushDataListView.setAdapter(new PushDataAdapter(MyApp.getApp().getPushDataManager().readPushDataFromDatabase()));

        return rootView;
    }

    class PushDataAdapter extends BaseAdapter {

        List<PushDataManager.PushData> data;

        public PushDataAdapter(List<PushDataManager.PushData> data) {
            this.data = data;
            Collections.reverse(this.data);
        }

        public void setData(List<PushDataManager.PushData> data) {
            this.data = data;
            Collections.reverse(this.data);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (data == null) {
                return 0;
            }
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        class ViewHolder {
            TextView content;
            TextView time;
            Button deleteButton;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView;
            if (convertView != null) {
                itemView = convertView;
            } else {
                LayoutInflater li = LayoutInflater.from(InfoFragment.this.getActivity());
                itemView = li.inflate(R.layout.item_push_data, parent, false);
                ViewHolder vh = new ViewHolder();
                vh.content = (TextView) itemView.findViewById(R.id.my_content);
                vh.time = (TextView) itemView.findViewById(R.id.time);
                vh.deleteButton = (Button) itemView.findViewById(R.id.deleteButton);
                itemView.setTag(vh);
            }

            ViewHolder vh = (ViewHolder) itemView.getTag();
            final PushDataManager.PushData pushData = data.get(position);
            vh.content.setText(pushData.getContent());
            vh.time.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(pushData.getTs() * 1000)));
            vh.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pushDataListView.dismissSelected();
                    pushDataListView.closeOpenedItems();
                    MyApp.getApp().getPushDataManager().deletePushData(data.get(position));
                    data.remove(position);
                    notifyDataSetChanged();
                }
            });

            return itemView;
        }
    }

}
