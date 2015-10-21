package ac.airconditionsuit.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.activity.AddDeviceActivity;
import ac.airconditionsuit.app.activity.BaseActivity;
import ac.airconditionsuit.app.activity.HomeSettingActivity;
import ac.airconditionsuit.app.activity.HostDeviceActivity;
import ac.airconditionsuit.app.activity.SoftwareInfoActivity;
import ac.airconditionsuit.app.activity.SoftwarePageActivity;
import ac.airconditionsuit.app.activity.UserInfoActivity;
import ac.airconditionsuit.app.network.HttpClient;
import ac.airconditionsuit.app.view.CommonButtonWithArrow;
import ac.airconditionsuit.app.view.CommonTopBar;
import ac.airconditionsuit.app.view.RoundImageView;

/**
 * Created by ac on 9/17/15.
 */
public class SettingFragment extends BaseFragment implements View.OnClickListener {

    private View view;
    public final static int REQUEST_HOME_SETTING = 160;
    private TextView home_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        RoundImageView roundImageView = (RoundImageView) view.findViewById(R.id.user_icon);
        HttpClient.loadImage(MyApp.getApp().getUser().getAvatar_normal(), roundImageView);
        home_name = (TextView) view.findViewById(R.id.setting_home_name);
        home_name.setText(MyApp.getApp().getServerConfigManager().getHome().getName());

        CommonButtonWithArrow hostDevice = (CommonButtonWithArrow)view.findViewById(R.id.host_device);
        //TODO add host device id
        hostDevice.getLabelTextView().setText(getString(R.string.host_device) + MyApp.getApp().getServerConfigManager().getCurrentHostMac());
        hostDevice.setOnClickListener(this);
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
                startActivityForResult(new Intent(getActivity(), HomeSettingActivity.class),REQUEST_HOME_SETTING);
                break;
            case R.id.software_page:
                startActivity(new Intent(getActivity(), SoftwarePageActivity.class));
                break;
            case R.id.add_device:
                startActivity(new Intent(getActivity(), AddDeviceActivity.class));
                break;
            case R.id.host_device:
                startActivity(new Intent(getActivity(), HostDeviceActivity.class));
                break;
        }
    }

    @Override
    public void setTopBar() {
        BaseActivity baseActivity = myGetActivity();
        CommonTopBar commonTopBar = baseActivity.getCommonTopBar();
        commonTopBar.setTitle(baseActivity.getString(R.string.tab_label_setting));
        commonTopBar.setIconView(null, null);
        commonTopBar.setRoundLeftIconView(null);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1)
            switch (requestCode) {
                case REQUEST_HOME_SETTING:
                    home_name.setText(data.getStringExtra("name"));
                    break;
            }
    }

}
