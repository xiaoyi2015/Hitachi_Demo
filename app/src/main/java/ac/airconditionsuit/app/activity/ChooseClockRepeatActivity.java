package ac.airconditionsuit.app.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.view.CommonChooseView;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/10/11.
 */
public class ChooseClockRepeatActivity extends BaseActivity{

    private MyOnClickListener myOnClickListener = new MyOnClickListener() {
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.left_icon:
                    finish();
                    break;
                case R.id.right_icon:
                    int k = 0;
                    for(int i = 0; i < flag.length -1; i++){
                        if(flag[i] == 1){
                            k = 1;
                        }
                    }
                    if(flag_repeat == 1 && k == 0){
                        new AlertDialog.Builder(ChooseClockRepeatActivity.this).setTitle(R.string.tip).setMessage("\n请选择重复日期\n").
                                setPositiveButton(R.string.make_sure, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                        return;
                    }
                    Intent intent = new Intent();
                    intent.putExtra("repeat",flag_repeat);
                    intent.putExtra("week",flag);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                case R.id.repeat:
                    if(flag_repeat == 0){
                        flag_repeat = 1;
                        repeat.getIcon().setVisibility(View.VISIBLE);
                    }else{
                        flag_repeat = 0;
                        repeat.getIcon().setVisibility(View.GONE);
                    }
                    break;
                case R.id.week1:
                    if(flag[0] == 0){
                        flag[0] = 1;
                        week1.getIcon().setVisibility(View.VISIBLE);
                    }else {
                        flag[0] = 0;
                        week1.getIcon().setVisibility(View.GONE);
                    }
                    break;
                case R.id.week2:
                    if(flag[1] == 0){
                        flag[1] = 1;
                        week2.getIcon().setVisibility(View.VISIBLE);
                    }else {
                        flag[1] = 0;
                        week2.getIcon().setVisibility(View.GONE);
                    }
                    break;
                case R.id.week3:
                    if(flag[2] == 0){
                        flag[2] = 1;
                        week3.getIcon().setVisibility(View.VISIBLE);
                    }else {
                        flag[2] = 0;
                        week3.getIcon().setVisibility(View.GONE);
                    }
                    break;
                case R.id.week4:
                    if(flag[3] == 0){
                        flag[3] = 1;
                        week4.getIcon().setVisibility(View.VISIBLE);
                    }else {
                        flag[3] = 0;
                        week4.getIcon().setVisibility(View.GONE);
                    }
                    break;
                case R.id.week5:
                    if(flag[4] == 0){
                        flag[4] = 1;
                        week5.getIcon().setVisibility(View.VISIBLE);
                    }else {
                        flag[4] = 0;
                        week5.getIcon().setVisibility(View.GONE);
                    }
                    break;
                case R.id.week6:
                    if(flag[5] == 0){
                        flag[5] = 1;
                        week6.getIcon().setVisibility(View.VISIBLE);
                    }else {
                        flag[5] = 0;
                        week6.getIcon().setVisibility(View.GONE);
                    }
                    break;
                case R.id.week7:
                    if(flag[6] == 0){
                        flag[6] = 1;
                        week7.getIcon().setVisibility(View.VISIBLE);
                    }else {
                        flag[6] = 0;
                        week7.getIcon().setVisibility(View.GONE);
                    }
                    break;

            }
        }
    };
    private CommonChooseView repeat;
    private CommonChooseView week1;
    private CommonChooseView week2;
    private CommonChooseView week3;
    private CommonChooseView week4;
    private CommonChooseView week5;
    private CommonChooseView week6;
    private CommonChooseView week7;
    private int[] flag = new int[]{};
    private int flag_repeat = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_choose_clock_repeat);
        super.onCreate(savedInstanceState);
        flag_repeat = getIntent().getIntExtra("repeat",-1);
        flag = getIntent().getIntArrayExtra("week");

        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.tab_label_set_time));
        switch (UIManager.UITYPE){
            case 1:
                commonTopBar.setLeftIconView(R.drawable.top_bar_cancel_hit);
                commonTopBar.setRightIconView(R.drawable.top_bar_save_hit);
                break;
            case 2:
                commonTopBar.setLeftIconView(R.drawable.top_bar_cancel_dc);
                commonTopBar.setRightIconView(R.drawable.top_bar_save_dc);
                break;
            default:
                commonTopBar.setLeftIconView(R.drawable.top_bar_cancel_dc);
                commonTopBar.setRightIconView(R.drawable.top_bar_save_dc);
                break;
        }
        commonTopBar.setIconView(myOnClickListener, myOnClickListener);

        repeat = (CommonChooseView)findViewById(R.id.repeat);
        repeat.setText(getString(R.string.repeat));
        repeat.setOnClickListener(myOnClickListener);
        week1 = (CommonChooseView)findViewById(R.id.week1);
        week1.setText(getString(R.string.Monday));
        week1.setOnClickListener(myOnClickListener);
        week2 = (CommonChooseView)findViewById(R.id.week2);
        week2.setText(getString(R.string.Tuesday));
        week2.setOnClickListener(myOnClickListener);
        week3 = (CommonChooseView)findViewById(R.id.week3);
        week3.setText(getString(R.string.Wednesday));
        week3.setOnClickListener(myOnClickListener);
        week4 = (CommonChooseView)findViewById(R.id.week4);
        week4.setText(getString(R.string.Thursday));
        week4.setOnClickListener(myOnClickListener);
        week5 = (CommonChooseView)findViewById(R.id.week5);
        week5.setText(getString(R.string.Friday));
        week5.setOnClickListener(myOnClickListener);
        week6 = (CommonChooseView)findViewById(R.id.week6);
        week6.setText(getString(R.string.Saturday));
        week6.setOnClickListener(myOnClickListener);
        week7 = (CommonChooseView)findViewById(R.id.week7);
        week7.setText(getString(R.string.Sunday));
        week7.setOnClickListener(myOnClickListener);

        if(flag_repeat == 1) {
            repeat.getIcon().setVisibility(View.VISIBLE);
        }
        if(flag[0] == 1){
            week1.getIcon().setVisibility(View.VISIBLE);
        }
        if(flag[1] == 1){
            week2.getIcon().setVisibility(View.VISIBLE);
        }
        if(flag[2] == 1){
            week3.getIcon().setVisibility(View.VISIBLE);
        }
        if(flag[3] == 1){
            week4.getIcon().setVisibility(View.VISIBLE);
        }
        if(flag[4] == 1){
            week5.getIcon().setVisibility(View.VISIBLE);
        }
        if(flag[5] == 1){
            week6.getIcon().setVisibility(View.VISIBLE);
        }
        if(flag[6] == 1){
            week7.getIcon().setVisibility(View.VISIBLE);
        }

    }
}
