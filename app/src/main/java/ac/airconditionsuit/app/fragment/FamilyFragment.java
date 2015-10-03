package ac.airconditionsuit.app.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.activity.BaseActivity;
import ac.airconditionsuit.app.entity.MyUser;
import ac.airconditionsuit.app.network.HttpClient;
import ac.airconditionsuit.app.network.response.GetChatCustListResponse;
import ac.airconditionsuit.app.view.RoundImageView;

/**
 * Created by Administrator on 2015/10/3.
 */
public class FamilyFragment extends Fragment{

    BaseActivity baseActivity;
    private View view;
    private RoundImageView userPicture;
    private boolean admin;
    private CustomAdapter customAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseActivity = (BaseActivity) getActivity();
        view = inflater.inflate(R.layout.fragment_family,container,false);
        TextView adminText = (TextView) view.findViewById(R.id.text_view);
        TextView userName = (TextView) view.findViewById(R.id.user_name);
        userPicture = (RoundImageView) view.findViewById(R.id.current_user);
        if(false)              //TODO isAdmin
        {
            admin = false;
            adminText.setVisibility(View.GONE);
            userName.setText(MyApp.getApp().getUser().getCust_name());
        }else{
            admin = true;
            userName.setText(MyApp.getApp().getUser().getCust_name());
        }
        HttpClient.loadImage(MyApp.getApp().getUser().getAvatar_normal(), userPicture);
        initDataFromInternet();

        return view;
    }

    private void initDataFromInternet() {

        final RequestParams requestParams = new RequestParams();
        requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_CHAT);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_TYPE_GET_CHAT_CUST_LIST);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_CUST_ID, 111);   //TODO getCurrentDeviceId()

        HttpClient.get(requestParams, GetChatCustListResponse.class, new HttpClient.JsonResponseHandler<GetChatCustListResponse>() {
            @Override
            public void onSuccess(GetChatCustListResponse response) {
                inflaterUI(response.getCust_list());
                //int n = response.getCust_list().size();
            }

            @Override
            public void onFailure(Throwable throwable) {
                MyApp.getApp().showToast("111");
            }
        });
    }

    private void inflaterUI(List<MyUser> cust_list) {
        if(cust_list == null)
        {
            cust_list = new ArrayList<>();
        }
        List<MyUser> customers1 = new ArrayList<>();

        for ( int i = 0; i < cust_list.size();i++ )
        {
            if((cust_list.get(i).getCust_id() != MyApp.getApp().getUser().getCust_id()) && (cust_list.get(i).getCust_name() != ""))
            {

                if(cust_list.get(i).getCust_id() <= 0x01000000000000l && cust_list.get(i).getCust_id() >= 0)
                    customers1.add(cust_list.get(i));
            }
        }

        //int n = cust_list.size();
        ListView listView = (ListView) view.findViewById(R.id.family_list);
        customAdapter = new CustomAdapter(baseActivity,customers1,admin);
        listView.setAdapter(customAdapter);
    }

    private class CustomAdapter extends BaseAdapter{

        private Context context;
        private List<MyUser> customers;
        private boolean isAdmin;
        private LayoutInflater listContainer;
        private boolean findAdmin;

        public final class ListItemView{
            public RoundImageView image1;
            public TextView name1;
            public RoundImageView image2;
            public TextView name2;
        }

        public CustomAdapter(Context context,List<MyUser> list,boolean isAdmin){
            this.context = context;
            this.customers = list;
            this.isAdmin = isAdmin;
            this.findAdmin = isAdmin;
            listContainer = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            int count;
            if((customers.size() % 2) == 0){
                count = customers.size()/2;
            }else {
                count = customers.size()/2 + 1;
            }
            return count;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ListItemView listItemView;
            if(convertView == null) {
                listItemView = new ListItemView();
                convertView = listContainer.inflate(R.layout.family_items,null);
                listItemView.image1 = (RoundImageView)convertView.findViewById(R.id.cust1_picture);
                listItemView.name1 = (TextView)convertView.findViewById(R.id.cust1_name);
                listItemView.image2 = (RoundImageView)convertView.findViewById(R.id.cust2_picture);
                listItemView.name2 = (TextView)convertView.findViewById(R.id.cust2_name);
                convertView.setTag(listItemView);
            }else {
                listItemView = (ListItemView) convertView.getTag();
            }

            final TextView name1 = listItemView.name1;
            listItemView.image1.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(isAdmin) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("\n确定要删除成员" + name1.getText().toString() + "吗？\n");
                        builder.setCancelable(false);
                        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteCust(customers.get(2*position));
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }
                    return true;
                }

            });
            final TextView name2 = listItemView.name2;
            listItemView.image2.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(isAdmin) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("\n确定要删除成员" + name2.getText().toString() + "吗？\n");
                        builder.setCancelable(false);
                        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteCust(customers.get(2*position + 1));
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }
                    return true;
                }

            });
            System.out.println("getView--" + position);
            if(isAdmin || findAdmin) {
                if (position * 2 + 2 <= customers.size()) {
                    listItemView.image1.setVisibility(View.VISIBLE);
                    setImage(context, customers.get(2 * position), listItemView.image1);
                    listItemView.name1.setVisibility(View.VISIBLE);
                    listItemView.name1.setText(customers.get(2 * position).getCust_name());
                    listItemView.image2.setVisibility(View.VISIBLE);
                    setImage(context, customers.get(2 * position + 1), listItemView.image2);
                    listItemView.name2.setVisibility(View.VISIBLE);
                    listItemView.name2.setText(customers.get(2 * position + 1).getCust_name());

                } else {
                    listItemView.image1.setVisibility(View.VISIBLE);
                    setImage(context, customers.get(2 * position), listItemView.image1);
                    listItemView.name1.setVisibility(View.VISIBLE);
                    listItemView.name1.setText(customers.get(2 * position).getCust_name());
                    listItemView.image2.setVisibility(View.INVISIBLE);
                    listItemView.name2.setVisibility(View.INVISIBLE);
                }
            }else {
                if (position * 2 + 2 <= customers.size()) {
                    listItemView.image1.setVisibility(View.VISIBLE);
                    setImage(context, customers.get(2 * position), listItemView.image1);
                    listItemView.name1.setVisibility(View.VISIBLE);
                    if(false){                      //TODO  customers.get(2 * position).getCust_id())
                        findAdmin = true;
                        listItemView.name1.setText(customers.get(2 * position).getCust_name());
                    }else {
                        listItemView.name1.setText(customers.get(2 * position).getCust_name());
                    }
                    listItemView.image2.setVisibility(View.VISIBLE);
                    setImage(context, customers.get(2 * position + 1), listItemView.image2);
                    listItemView.name2.setVisibility(View.VISIBLE);
                    if(false){                     //TODO
                        findAdmin = true;
                        listItemView.name2.setText(customers.get(2 * position + 1).getCust_name());
                    }else {
                        listItemView.name2.setText(customers.get(2 * position + 1).getCust_name());
                    }
                } else {
                    listItemView.image1.setVisibility(View.VISIBLE);
                    setImage(context, customers.get(2 * position), listItemView.image1);
                    listItemView.name1.setVisibility(View.VISIBLE);
                    if(false){                    //TODO
                        findAdmin = true;
                        listItemView.name1.setText(customers.get(2 * position).getCust_name());
                    }else {
                        listItemView.name1.setText(customers.get(2 * position).getCust_name());
                    }
                    listItemView.image2.setVisibility(View.INVISIBLE);
                    listItemView.name2.setVisibility(View.INVISIBLE);
                }
            }
            return convertView;
        }

        private void setImage(Context context, MyUser myUser, RoundImageView image) {
            String userAvatar = myUser.getAvatar_normal();
            if (userAvatar != null && userAvatar.length() != 0) {
                HttpClient.loadImage(userAvatar,image);
            }
        }

        private void deleteCust(MyUser myUser) {
            // TODO
        }
    }
}
