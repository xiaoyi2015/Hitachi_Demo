package ac.airconditionsuit.app.fragment;

import ac.airconditionsuit.app.network.socket.SocketManager;
import ac.airconditionsuit.app.util.NetworkConnectionStatusUtil;
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

    private static final int REQUEST_CHANGE_ICON = 240;
    private View view;
    public final static int REQUEST_HOME_SETTING = 160;
    private TextView home_name;
    private CommonButtonWithArrow connectionStatusView;
    private RoundImageView roundImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        roundImageView = (RoundImageView) view.findViewById(R.id.user_icon);
        HttpClient.loadImage(MyApp.getApp().getUser().getAvatar(), roundImageView);
        home_name = (TextView) view.findViewById(R.id.setting_home_name);
        home_name.setText(MyApp.getApp().getServerConfigManager().getHome().getName());

        view.findViewById(R.id.software_information).setOnClickListener(this);
        view.findViewById(R.id.user_icon).setOnClickListener(this);
        view.findViewById(R.id.setting_home_setting).setOnClickListener(this);
        view.findViewById(R.id.software_page).setOnClickListener(this);

        if (MyApp.getApp().getServerConfigManager().hasDevice()) {
            view.findViewById(R.id.add_device).setVisibility(View.GONE);
            CommonButtonWithArrow hostDevice = (CommonButtonWithArrow) view.findViewById(R.id.host_device);
            hostDevice.getLabelTextView().setText(getString(R.string.host_device));
            hostDevice.setOnlineTextView(MyApp.getApp().getServerConfigManager().getConnections().get(0).getName());
            hostDevice.setOnClickListener(this);
        } else {
            view.findViewById(R.id.host_device).setVisibility(View.GONE);
            view.findViewById(R.id.add_device).setOnClickListener(this);
        }

        connectionStatusView = (CommonButtonWithArrow) view.findViewById(R.id.connect_status);
        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.user_icon:
                myGetActivity().shortStartActivityForResult(UserInfoActivity.class,REQUEST_CHANGE_ICON);
                break;
            case R.id.software_information:
                startActivity(new Intent(getActivity(), SoftwareInfoActivity.class));
                break;
            case R.id.setting_home_setting:
                startActivityForResult(new Intent(getActivity(), HomeSettingActivity.class), REQUEST_HOME_SETTING);
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
        commonTopBar.getTitleView().setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1)
            switch (requestCode) {
                case REQUEST_HOME_SETTING:
                    home_name.setText(data.getStringExtra("name"));
                    break;
                case REQUEST_CHANGE_ICON:
                    HttpClient.loadImage(MyApp.getApp().getUser().getAvatar(), roundImageView);
                    break;
            }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            refreshNetworkStatus();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshNetworkStatus();
    }

    public void refreshNetworkStatus() {
        MyApp.getApp().getHandler().post(new Runnable() {
            @Override
            public void run() {
                int connectivityStatus = NetworkConnectionStatusUtil.getConnectivityStatus(getActivity());

                if (MyApp.getApp().getServerConfigManager().hasDevice()) {

                    switch (MyApp.getApp().getSocketManager().getStatus()) {
                        case SocketManager.TCP_DEVICE_CONNECT:
                            if (connectivityStatus == NetworkConnectionStatusUtil.TYPE_WIFI_CONNECT) {
                                connectionStatusView.setOnlineTextView(R.string.settingFragmentWifiConnectDevice);
                            } else if (connectivityStatus == NetworkConnectionStatusUtil.TYPE_MOBILE_CONNECT) {
                                connectionStatusView.setOnlineTextView(R.string.settingFragmentMobileConnectDevice);
                            } else {
                                connectionStatusView.setOnlineTextView(R.string.settingFragmentUnConnect);
                            }
                            break;
                        case SocketManager.UDP_DEVICE_CONNECT:
                            if (connectivityStatus == NetworkConnectionStatusUtil.TYPE_WIFI_CONNECT) {
                                connectionStatusView.setOnlineTextView(R.string.settingFragmentWifiUdpConnect);
                            } else {
                                connectionStatusView.setOnlineTextView(R.string.settingFragmentUnConnect);
                            }
                            break;
                        case SocketManager.TCP_HOST_CONNECT:
                            if (connectivityStatus == NetworkConnectionStatusUtil.TYPE_WIFI_CONNECT ||
                                    connectivityStatus == NetworkConnectionStatusUtil.TYPE_MOBILE_CONNECT) {
                                connectionStatusView.setOnlineTextView(R.string.settingFragmentConnectServer);
                            } else {
                                connectionStatusView.setOnlineTextView(R.string.settingFragmentUnConnect);
                            }

                            break;
                        case SocketManager.TCP_UDP_ALL_UNCONNECT:
                            if (connectivityStatus == NetworkConnectionStatusUtil.TYPE_WIFI_CONNECT) {
                                connectionStatusView.setOnlineTextView(R.string.settingFragmentWifiNoServer);
                            } else if (connectivityStatus == NetworkConnectionStatusUtil.TYPE_MOBILE_CONNECT) {
                                connectionStatusView.setOnlineTextView(R.string.settingFragmentMobileNoServer);
                            } else {
                                connectionStatusView.setOnlineTextView(R.string.settingFragmentUnConnect);
                            }
                            break;
                        default:
                    }
                } else {
                    switch (MyApp.getApp().getSocketManager().getStatus()) {
                        case SocketManager.TCP_HOST_CONNECT:
                            connectionStatusView.setOnlineTextView(R.string.settingFragmentHasHostNoDevice);
                            break;
                        default:
                            connectionStatusView.setOnlineTextView(R.string.settingFragmentNoHostNoDevice);
                            break;
                    }
                }

            }
        });
    }

}
