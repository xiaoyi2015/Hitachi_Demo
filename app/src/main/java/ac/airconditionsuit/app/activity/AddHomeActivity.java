package ac.airconditionsuit.app.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/9/30.
 */
public class AddHomeActivity extends BaseActivity{
    private MyOnClickListener myOnClickListener = new MyOnClickListener(){
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()){
                case R.id.left_icon:
                    finish();
                    break;
                case R.id.right_icon:


                    break;
            }
        }
    };
    private EditText addHomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_setting_add_home);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.add_new_home));
        commonTopBar.setIconView(myOnClickListener, myOnClickListener);
        addHomeText = (EditText)findViewById(R.id.edit_new_home_name);
        //TODO for luzheqi Ìí¼Ó¼Ò
    }
}
