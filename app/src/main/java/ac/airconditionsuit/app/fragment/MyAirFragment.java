package ac.airconditionsuit.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.activity.BaseActivity;
import ac.airconditionsuit.app.network.HttpClient;
import ac.airconditionsuit.app.view.CommonTopBar;
import ac.airconditionsuit.app.view.RoundImageView;

/**
 * Created by Administrator on 2015/10/3.
 */
public class MyAirFragment extends BaseFragment {

    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_air, container, false);
        return view;
    }

    @Override
    public void setTopBar() {
        BaseActivity baseActivity = myGetActivity();
        CommonTopBar commonTopBar = baseActivity.getCommonTopBar();
        commonTopBar.setIconView(null,null);
        commonTopBar.setRoundLeftIconView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
