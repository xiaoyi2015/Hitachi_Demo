package ac.airconditionsuit.app.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/10/6.
 */
public class EditSceneActivity extends BaseActivity{

    private MyOnClickListener myOnClickListener = new MyOnClickListener() {
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_edit_scene);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        String scene_name  = getIntent().getStringExtra("title");
        commonTopBar.setTitle(scene_name);
        commonTopBar.setIconView(myOnClickListener, myOnClickListener);
        EditText sceneName = (EditText)findViewById(R.id.scene_name_text);
        sceneName.setText(scene_name);
        sceneName.setSelection(scene_name.length());
        setOnclickListenerOnTextViewDrawable(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof EditText) {
                    ((EditText) v).setText("");
                }
            }
        }, sceneName);
    }
}
