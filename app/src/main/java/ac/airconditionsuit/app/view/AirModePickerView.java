package ac.airconditionsuit.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.ArrayList;

import ac.airconditionsuit.app.R;

/**
 * Created by Administrator on 2015/10/13.
 */
public class AirModePickerView extends LinearLayout {

    private CommonWheelView onOffView;
    private CommonWheelView modeView;
    private CommonWheelView fanView;
    private CommonWheelView tempView;
    private ArrayList<String> on_off_list = new ArrayList<>();
    private ArrayList<String> mode_list = new ArrayList<>();
    private ArrayList<String> fan_list = new ArrayList<>();
    private ArrayList<String> temp_list = new ArrayList<>();

    public AirModePickerView(Context context) {
        super(context);
        init(context);
    }

    public AirModePickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AirModePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.custom_air_mode_picker_view,this);
        onOffView = (CommonWheelView)findViewById(R.id.set_on_off);
        modeView = (CommonWheelView)findViewById(R.id.set_mode);
        fanView = (CommonWheelView)findViewById(R.id.set_fan);
        tempView = (CommonWheelView)findViewById(R.id.set_temp);
        on_off_list.add(getResources().getString(R.string.on));
        on_off_list.add(getResources().getString(R.string.off));
        mode_list.add(getResources().getString(R.string.cool));
        mode_list.add(getResources().getString(R.string.heat));
        mode_list.add(getResources().getString(R.string.dry));
        mode_list.add(getResources().getString(R.string.wind));
        fan_list.add(getResources().getString(R.string.low_wind));
        fan_list.add(getResources().getString(R.string.medium_wind));
        fan_list.add(getResources().getString(R.string.high_wind));
        for(int i = 18; i < 31 ; i++){
            temp_list.add(String.valueOf(i) + getResources().getString(R.string.temp_symbol));
        }
        onOffView.setData(on_off_list);
        modeView.setData(mode_list);
        fanView.setData(fan_list);
        tempView.setData(temp_list);
        onOffView.setDefault(1);
        modeView.setDefault(0);
        fanView.setDefault(0);
        tempView.setDefault(7);

    }

}
