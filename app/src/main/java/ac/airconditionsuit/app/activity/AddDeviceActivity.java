package ac.airconditionsuit.app.activity;

import android.os.Bundle;
import android.view.View;

import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/9/18.
 */
public class AddDeviceActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_add_device);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.add_device));
    }

    @Override
    public void onClick(View v) {

    }
}
