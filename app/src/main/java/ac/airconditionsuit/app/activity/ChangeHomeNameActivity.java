package ac.airconditionsuit.app.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/9/19.
 */
public class ChangeHomeNameActivity extends BaseActivity {
    private MyOnClickListener myOnClickListener = new MyOnClickListener(){
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.left_icon:
                    finish();
                    break;
                case R.id.right_icon:
                //TODO for zln
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_setting_change_home_name);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.home_name));
        switch (UIManager.UITYPE){
            case 1:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_hit);
                commonTopBar.setRightIconView(R.drawable.top_bar_save_hit);
                break;
            case 2:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_dc);
                commonTopBar.setRightIconView(R.drawable.top_bar_save_dc);
                break;
            default:
                break;
        }
        commonTopBar.setIconView(myOnClickListener, myOnClickListener);
        EditText changeName = (EditText)findViewById(R.id.edit_home_name);
        changeName.setText(MyApp.getApp().getServerConfigManager().getHome().getName());
        changeName.setSelection(MyApp.getApp().getServerConfigManager().getHome().getName().length());

    }

}
