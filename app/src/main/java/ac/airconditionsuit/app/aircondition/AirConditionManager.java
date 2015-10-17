package ac.airconditionsuit.app.aircondition;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.entity.*;
import ac.airconditionsuit.app.network.socket.socketpackage.ControlPackage;
import ac.airconditionsuit.app.network.socket.socketpackage.QueryTimerPackage;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ac on 10/15/15.
 */
public class AirConditionManager {

    private static final String TAG = "AirConditionManager";
    public static final int QUERY_ALL_TIMER = 0xffff;
    public static final int QUERY_TIMER_NO = 0xfffe;
    List<AirCondition> airConditions = new ArrayList<>();
    List<Timer> timers = new ArrayList<>();

    public void queryAirConditionStatus() {
        try {
            MyApp.getApp().getSocketManager().getAllAirConditionStatusFromHostDevice(
                    MyApp.getApp().getServerConfigManager().getDevices()
            );
        } catch (Exception e) {
            Log.e(TAG, "init air condition status fail");
            e.printStackTrace();
        }
    }

    public void updateAirconditionStatue(byte[] status) {
        try {
            AirConditionStatusResponse airConditionStatusResponse =
                    AirConditionStatusResponse.decodeFromByteArray(status);

            AirCondition airCondition = getAirCondition(airConditionStatusResponse.getAddress());
            if (airCondition == null) {
                airCondition = new AirCondition();
            }
            airCondition.changeStatus(airConditionStatusResponse);

            MyApp.getApp().getSocketManager().notifyActivity(new ObserveData(ObserveData.AIR_CONDITION_STATUS_RESPONSE, airCondition));
        } catch (Exception e) {
            Log.i(TAG, "decode air condition status failed");
            e.printStackTrace();
        }
    }

    public void updateTimerStatue(byte[] contentData) {
        try {
            Timer timer = Timer.decodeFromByteArray(contentData);
            MyApp.getApp().getServerConfigManager().updateTimer(timer);
            //todo for luzheqi
        } catch (Exception e) {
            Log.i(TAG, "decode timer status failed");
            e.printStackTrace();
        }
    }

    /**
     *
     * @param timerId 定时器id
     */
    public void timerRun(int timerId) {
        //todo for luzheqi
    }

    public void controlScene(Scene scene) throws Exception {
        MyApp.getApp().getSocketManager().sendMessage(scene.toSocketControlPackage());
    }

    public void controlRoom(Room room, AirConditionControl airConditionControl) throws Exception {
        MyApp.getApp().getSocketManager().sendMessage(new ControlPackage(room, airConditionControl));
    }

    /**
     * @param address 待查找的空调的地址
     * @return 可能为空
     */
    public AirCondition getAirCondition(int address) {
        for (AirCondition airCondition : airConditions) {
            if (airCondition.getAddress() == address) {
                return airCondition;
            }
        }
        return null;
    }



    public void queryTimer(int id) {
        MyApp.getApp().getSocketManager().sendMessage(new QueryTimerPackage(id));
    }

    public void controlAirCondition(Command command) {
        try {
            MyApp.getApp().getSocketManager().sendMessage(new ControlPackage(command));
        } catch (Exception e) {
            Log.e(TAG, "invalid command");
            e.printStackTrace();
        }
    }
}
