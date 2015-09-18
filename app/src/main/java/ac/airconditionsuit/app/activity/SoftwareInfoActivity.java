package ac.airconditionsuit.app.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import ac.airconditionsuit.app.R;

/**
 * Created by Administrator on 2015/9/18.
 */
public class SoftwareInfoActivity extends FragmentActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_setting_software_info);
    }

    @Override
    public void onClick(View v) {

    }
}
