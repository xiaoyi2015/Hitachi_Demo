package ac.airconditionsuit.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.util.CheckUtil;
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
                    String home_name = CheckUtil.checkLength(changeName,
                            20, R.string.home_name_empty_info, R.string.home_name_too_long_info);
                    if(home_name == null){
                        return;
                    }
                    MyApp.getApp().getServerConfigManager().getHome().setName(home_name);
                    MyApp.getApp().getServerConfigManager().writeToFile(true);
                    Intent intent = new Intent();
                    intent.putExtra("name",home_name);
                    setResult(RESULT_OK,intent);
                    finish();
                    break;
            }
        }
    };
    private EditText changeName;

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
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_dc);
                commonTopBar.setRightIconView(R.drawable.top_bar_save_dc);
                break;
        }
        commonTopBar.setIconView(myOnClickListener, myOnClickListener);
        changeName = (EditText)findViewById(R.id.edit_home_name);
        if(MyApp.getApp().getServerConfigManager().getHome().getName() == null){
            changeName.setText("");
        }else {
            changeName.setText(MyApp.getApp().getServerConfigManager().getHome().getName());
            changeName.setSelection(MyApp.getApp().getServerConfigManager().getHome().getName().length());
        }

    }

}
