package ac.airconditionsuit.app.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
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
        TextView IsAdmin = (TextView)view.findViewById(R.id.admin_text);
        TextView userName = (TextView) view.findViewById(R.id.user_name);
        userName.setText(MyApp.getApp().getUser().getCust_name());
        userPicture = (RoundImageView) view.findViewById(R.id.current_user);
        if(!MyApp.getApp().getUser().isAdmin())
        {
            admin = false;
            adminText.setVisibility(View.GONE);
            IsAdmin.setText(getString(R.string.member));
        }else{
            admin = true;
            IsAdmin.setText(getString(R.string.admin));
        }
        HttpClient.loadImage(MyApp.getApp().getUser().getAvatar_normal(), userPicture);
        initDataFromInternet();

        return view;
    }

    private void initDataFromInternet() {

        final RequestParams requestParams = new RequestParams();
        requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_CHAT);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_TYPE_GET_CHAT_CUST_LIST);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_CHAT_ID, MyApp.getApp().getLocalConfigManager().getCurrentHomeDeviceId());

        HttpClient.get(requestParams, GetChatCustListResponse.class, new HttpClient.JsonResponseHandler<GetChatCustListResponse>() {
            @Override
            public void onSuccess(GetChatCustListResponse response) {
                inflaterUI(response.getCust_list());
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

        int n = customers1.size();
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
            public TextView adminText1;
            public RoundImageView image1;
            public TextView name1;
            public TextView adminText2;
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
                listItemView.adminText1 = (TextView)convertView.findViewById(R.id.admin_text1);
                listItemView.image1 = (RoundImageView)convertView.findViewById(R.id.cust1_picture);
                listItemView.name1 = (TextView)convertView.findViewById(R.id.cust1_name);
                listItemView.adminText2 = (TextView)convertView.findViewById(R.id.admin_text2);
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
                        TextView toDoDelete = new TextView(getActivity());
                        toDoDelete.setGravity(Gravity.CENTER);
                        toDoDelete.setText(getString(R.string.delete_member1) + name1.getText().toString() + getString(R.string.delete_member2));
                        toDoDelete.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setView(toDoDelete);
                        builder.setCancelable(false);
                        builder.setPositiveButton(getString(R.string.make_sure), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteCust(customers.get(2*position));
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
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
                    if (isAdmin) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(getString(R.string.delete_member1) + name2.getText().toString() + getString(R.string.delete_member2));
                        builder.setCancelable(false);
                        builder.setPositiveButton(getString(R.string.make_sure), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteCust(customers.get(2 * position + 1));
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
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
            //System.out.println("getView--" + position);
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
                    listItemView.adminText1.setVisibility(View.VISIBLE);
                    listItemView.adminText1.setText(getString(R.string.member));
                    listItemView.adminText2.setVisibility(View.VISIBLE);
                    listItemView.adminText2.setText(getString(R.string.member));

                } else {
                    listItemView.image1.setVisibility(View.VISIBLE);
                    setImage(context, customers.get(2 * position), listItemView.image1);
                    listItemView.name1.setVisibility(View.VISIBLE);
                    listItemView.name1.setText(customers.get(2 * position).getCust_name());
                    listItemView.image2.setVisibility(View.INVISIBLE);
                    listItemView.name2.setVisibility(View.INVISIBLE);
                    listItemView.adminText1.setVisibility(View.VISIBLE);
                    listItemView.adminText1.setText(getString(R.string.member));
                    listItemView.adminText2.setVisibility(View.INVISIBLE);
                    
                }
            }else {
                if (position * 2 + 2 <= customers.size()) {
                    listItemView.image1.setVisibility(View.VISIBLE);
                    setImage(context, customers.get(2 * position), listItemView.image1);
                    listItemView.name1.setVisibility(View.VISIBLE);
                    listItemView.name1.setText(customers.get(2 * position).getCust_name());
                    listItemView.adminText1.setVisibility(View.VISIBLE);
                    if(customers.get(2 * position).isAdmin()){
                        findAdmin = true;
                        listItemView.adminText1.setText(getString(R.string.admin));
                    }else {
                        listItemView.adminText1.setText(getString(R.string.member));
                    }
                    listItemView.image2.setVisibility(View.VISIBLE);
                    setImage(context, customers.get(2 * position + 1), listItemView.image2);
                    listItemView.name2.setVisibility(View.VISIBLE);
                    listItemView.name2.setText(customers.get(2 * position + 1).getCust_name());
                    listItemView.adminText2.setVisibility(View.VISIBLE);
                    if(customers.get(2 * position + 1).isAdmin()){
                        findAdmin = true;
                        listItemView.adminText2.setText(getString(R.string.admin));
                    }else {
                        listItemView.adminText2.setText(getString(R.string.member));
                    }
                } else {
                    listItemView.image1.setVisibility(View.VISIBLE);
                    setImage(context, customers.get(2 * position), listItemView.image1);
                    listItemView.name1.setVisibility(View.VISIBLE);
                    listItemView.name1.setText(customers.get(2 * position).getCust_name());
                    listItemView.adminText1.setVisibility(View.VISIBLE);
                    if(customers.get(2 * position).isAdmin()){
                        findAdmin = true;
                        listItemView.adminText1.setText(getString(R.string.admin));
                    }else {
                        listItemView.adminText1.setText(getString(R.string.member));
                    }
                    listItemView.image2.setVisibility(View.INVISIBLE);
                    listItemView.name2.setVisibility(View.INVISIBLE);
                    listItemView.adminText2.setVisibility(View.INVISIBLE);
                }
            }
            return convertView;
        }

        private void setImage(Context context, MyUser myUser, RoundImageView image) {
            String userAvatar = myUser.getAvatar();
            if (userAvatar != null && userAvatar.length() != 0) {
                HttpClient.loadImage(userAvatar,image);
            }
        }

        private void deleteCust(final MyUser myUser) {
            final RequestParams requestParams = new RequestParams();
            requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_CHAT);
            requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_TYPE_GET_CHAT_CUST_LIST);
            requestParams.put(Constant.REQUEST_PARAMS_KEY_CHAT_ID, MyApp.getApp().getLocalConfigManager().getCurrentHomeDeviceId());
            requestParams.put(Constant.REQUEST_PARAMS_KEY_DELETE_CUST_ID, myUser.getCust_id());

            HttpClient.post(requestParams, GetChatCustListResponse.class, new HttpClient.JsonResponseHandler<GetChatCustListResponse>() {
                @Override
                public void onSuccess(GetChatCustListResponse response) {
                    customers.remove(myUser);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    MyApp.getApp().showToast("111");
                }
            });
        }
    }
}
