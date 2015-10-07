package ac.airconditionsuit.app.activity;

import android.os.Bundle;
import android.view.View;

import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/10/7.
 */
public class EditClockActivity extends BaseActivity{

    private MyOnClickListener myOnClickListener = new MyOnClickListener() {
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.left_icon:
                    finish();
                    break;
                case R.id.right_icon:
                    //TODO save clock setting
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_edit_clock);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        String scene_name  = getIntent().getStringExtra("title");
        commonTopBar.setTitle(getString(R.string.tab_label_set_time));
        commonTopBar.setIconView(myOnClickListener, myOnClickListener);
    }
}
