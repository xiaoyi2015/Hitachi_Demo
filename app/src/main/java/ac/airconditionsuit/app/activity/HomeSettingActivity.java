package ac.airconditionsuit.app.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.view.CommonButtonWithArrow;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/9/18.
 */
public class HomeSettingActivity extends BaseActivity {

    public final static int REQUEST_CHANGE_HOME_NAME = 150;
    private String home_name;

    private MyOnClickListener myOnClickListener = new MyOnClickListener(){
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.home_name:
                    shortStartActivityForResult(ChangeHomeNameActivity.class, REQUEST_CHANGE_HOME_NAME);
                    break;
                case R.id.left_icon:
                    Intent intent = new Intent();
                    intent.putExtra("name",home_name);
                    setResult(RESULT_OK,intent);
                    finish();
                    break;
                case R.id.delete_home:
                    new AlertDialog.Builder(HomeSettingActivity.this).setTitle(R.string.tip).setMessage(R.string.is_to_delete_home).
                            setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (MyApp.getApp().getLocalConfigManager().deleteCurrentHome()) {
                                        finish();
                                    } else {
                                        MyApp.getApp().showToast(R.string.toast_inf_cannot_delete_last_home);
                                    }
                                    dialog.dismiss();
                                }
                            }).setNegativeButton(R.string.cancel, null).setCancelable(false).show();

                    break;
            }
        }
    };
    private CommonButtonWithArrow homeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_setting_home_setting);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.home));
        switch (UIManager.UITYPE){
            case 1:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_hit);
                break;
            case 2:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_dc);
                break;
            default:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_dc);
                break;
        }
        commonTopBar.setIconView(myOnClickListener, null);
        homeName = (CommonButtonWithArrow) findViewById(R.id.home_name);
        home_name = MyApp.getApp().getServerConfigManager().getHome().getName();
        homeName.setOnlineTextView(home_name);
        homeName.setOnClickListener(myOnClickListener);
        TextView deleteHome = (TextView)findViewById(R.id.delete_home);
        deleteHome.setOnClickListener(myOnClickListener);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case REQUEST_CHANGE_HOME_NAME:
                    home_name = data.getStringExtra("name");
                    homeName.setOnlineTextView(home_name);
                    break;
            }
    }

}
