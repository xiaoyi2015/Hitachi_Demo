package ac.airconditionsuit.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;

import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.listener.MyOnClickListener;

/**
 * Created by Administrator on 2015/9/26.
 */
public class ChangeRoomNameActivity extends BaseActivity{
    private MyOnClickListener myOnClickListener = new MyOnClickListener(){
        @Override
        public void onClick(View v) {
            super.onClick(v);
            Intent intent = new Intent();
            switch (v.getId()){
                case R.id.add_room_page:
                    intent.putExtra("name",roomName.getText().toString());
                    intent.putExtra("room_index",index);
                    setResult(RESULT_OK,intent);
                    finish();
                    break;
            }
        }
    };
    private EditText roomName;
    private String index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_setting_change_room_name);
        super.onCreate(savedInstanceState);

        index = getIntent().getStringExtra("index");
        RelativeLayout ChangeRoomNamePage = (RelativeLayout) findViewById(R.id.add_room_page);
        roomName = (EditText) findViewById(R.id.room_name);
        roomName.setText(getString(R.string.new_room));
        roomName.setSelection(getString(R.string.new_room).length());
        ChangeRoomNamePage.setOnClickListener(myOnClickListener);
        roomName.setOnClickListener(myOnClickListener);
        //TODO add air_device to room,change the icon and so on
    }
}
