package ac.airconditionsuit.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;

import ac.airconditionsuit.app.R;

/**
 * Created by ac on 9/17/15.
 */
public class SettingFragment extends BaseFragment implements View.OnClickListener{

    private static final int REQUEST_ADD_DEVICE = 10086;
    private View view;
    private ImageButton userIcon;
    private ImageButton softwareInfo;
    private TextView connectStatusTextView;
    private Timer refreshTimer;
    private RelativeLayout deviceView;
    private TextView addController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting,container,false);
        softwareInfo = (ImageButton)view.findViewById(R.id.software_information);
        return view;
    }

    @Override
    public void onClick(View v) {

    }
}
