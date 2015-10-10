package ac.airconditionsuit.app.fragment;

import ac.airconditionsuit.app.Config.ServerConfigManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.activity.AddDeviceActivity;
import ac.airconditionsuit.app.activity.BaseActivity;
import ac.airconditionsuit.app.activity.HomeSettingActivity;
import ac.airconditionsuit.app.activity.SoftwareInfoActivity;
import ac.airconditionsuit.app.activity.SoftwarePageActivity;
import ac.airconditionsuit.app.activity.UserInfoActivity;
import ac.airconditionsuit.app.network.HttpClient;
import ac.airconditionsuit.app.view.CommonTopBar;
import ac.airconditionsuit.app.view.RoundImageView;

/**
 * Created by ac on 9/17/15.
 */
public class SettingFragment extends BaseFragment implements View.OnClickListener {

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        RoundImageView roundImageView =(RoundImageView)view.findViewById(R.id.user_icon);
        HttpClient.loadImage(MyApp.getApp().getUser().getAvatar_normal(), roundImageView);
        TextView home_name = (TextView)view.findViewById(R.id.setting_home_name);
        /**TODO for zhulinan
         * 如果用户没有关联任何主机的话,这边getHome(),会产生控制指针。先通过这个函数判断一下是否关联了设备
         * {@link ServerConfigManager#hasDevice()}这个函数
         */
        if (MyApp.getApp().getServerConfigManager().hasDevice()) {
            home_name.setText(MyApp.getApp().getServerConfigManager().getHome().getName());
        }else{
            //TODO for zhulinan, 如果没有家，做相应处理。
        }
        view.findViewById(R.id.software_information).setOnClickListener(this);
        view.findViewById(R.id.user_icon).setOnClickListener(this);
        view.findViewById(R.id.setting_home_setting).setOnClickListener(this);
        view.findViewById(R.id.software_page).setOnClickListener(this);
        view.findViewById(R.id.add_device).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.user_icon:
                startActivity(new Intent(getActivity(), UserInfoActivity.class));
                break;
            case R.id.software_information:
                startActivity(new Intent(getActivity(), SoftwareInfoActivity.class));
                break;
            case R.id.setting_home_setting:
                startActivity(new Intent(getActivity(), HomeSettingActivity.class));
                break;
            case R.id.software_page:
                startActivity(new Intent(getActivity(), SoftwarePageActivity.class));
                break;
            case R.id.add_device:
                startActivity(new Intent(getActivity(), AddDeviceActivity.class));
                break;
        }
    }

    @Override
    public void setTopBar() {
        BaseActivity baseActivity = myGetActivity();
        CommonTopBar commonTopBar = baseActivity.getCommonTopBar();
        commonTopBar.setTitle(baseActivity.getString(R.string.tab_label_setting));
        commonTopBar.setIconView(null,null);
        commonTopBar.setRoundLeftIconView(null);
    }
}
