package ac.airconditionsuit.app.aircondition;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.entity.*;
import ac.airconditionsuit.app.network.socket.socketpackage.*;
import ac.airconditionsuit.app.util.ByteUtil;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ac on 10/15/15.
 */
public class AirConditionManager {

    private static final String TAG = "AirConditionManager";
    public static final int QUERY_ALL_TIMER = 0xffff;
    public static final int QUERY_TIMER_NO = 0xfffe;
    List<AirCondition> airConditions = new ArrayList<>();
//    List<Timer> timers = new ArrayList<>();//usused

    public void queryAirConditionStatus() {
        try {
            if (MyApp.getApp().getSocketManager().shouldSendPacketsToQuery())
            MyApp.getApp().getSocketManager().getAllAirConditionStatusFromHostDevice(
                    MyApp.getApp().getServerConfigManager().getDevices()
            );
        } catch (Exception e) {
            Log.e(TAG, "init air condition status fail");
            e.printStackTrace();
        }
    }

    public void updateAirConditionStatueLocal(byte[] status) {
        try {
            AirConditionStatusResponse airConditionStatusResponse =
                    AirConditionStatusResponse.decodeFromByteArray(status);
            AirCondition airCondition = getAirConditionByAddress(airConditionStatusResponse.getAddress());
            if (airCondition == null) {
                airCondition = new AirCondition(airConditionStatusResponse);
                airConditions.add(airCondition);
            }
            airCondition.changeStatus(airConditionStatusResponse);
            MyApp.getApp().getSocketManager().notifyActivity(new ObserveData(ObserveData.AIR_CONDITION_STATUS_RESPONSE, airCondition));
        } catch (Exception e) {
            Log.i(TAG, "decode air condition status failed");
            e.printStackTrace();
        }
    }

    public void updateTimerStatueLocal(byte[] contentData) {
        try {
            Timer timer = Timer.decodeFromByteArray(contentData);
            MyApp.getApp().getServerConfigManager().updateTimer(timer);
            MyApp.getApp().getSocketManager().notifyActivity(new ObserveData(ObserveData.TIMER_STATUS_RESPONSE, timer));
        } catch (Exception e) {
            Log.i(TAG, "decode timer status failed");
            e.printStackTrace();
        }
    }

    /**
     * @param timerId 定时器id
     */
    public void timerRun(int timerId) {
        Log.v("liutao", "定时器执行");
        updateAcsByTimerRunned(timerId);
    }

    public void controlScene(Scene scene) throws Exception {
        MyApp.getApp().getSocketManager().sendMessage(scene.toSocketControlPackage());
        //发送场景时，不主动查询空调状态，因为空调控制成功需要时间，此时查询到的状态，有可能是控制之前空调的状态。进而造成本地显示与实际空调状态不一致
        //目前，控制空调成功后，主机不一定会反馈空调状态给app
        //在我的空调界面，允许用户手动下拉刷新，主动发包读取所有空调状态
        //MyApp.getApp().getAirConditionManager().queryAirConditionStatus();

        //发送场景控制命令时，先set到本地缓存，使界面得到更新。
        updateACsBySceneControl(scene);
    }

    public void controlRoom(Room room, AirConditionControl airConditionControl) throws Exception {
        MyApp.getApp().getSocketManager().sendMessage(new ControlPackage(room, airConditionControl));
        updateAirconditions(room, airConditionControl);
    }

    private void  updateAcsByTimerRunned(int timer_id) {
        List<Timer> timers = MyApp.getApp().getServerConfigManager().getTimer();
        for (Timer tm : timers) {
            if (tm.getTimerid() == timer_id) {
                List<Integer> indexes = tm.getIndexes();
                for (Integer idxInTimer : indexes) {
                    Integer idx = idxInTimer - 1;
                    if (idx >= 0 && idx < airConditions.size()) {
                        AirCondition ac = airConditions.get(idx);
                        ac.setAirconditionMode(tm.getMode());
                        ac.setAirconditionFan(tm.getFan());
                        ac.setTemperature(tm.getTemperature());
                        ac.setOnoff(tm.isOnoff() ? 1 : 0);
                        MyApp.getApp().getSocketManager().notifyActivity(new ObserveData(ObserveData.AIR_CONDITION_STATUS_RESPONSE, ac));
                    }
                }
            }
        }
    }

    private void updateACsBySceneControl(Scene scene) {
        List<Command> commands = scene.getCommands();
        if (commands == null) return;
        for (Command command : commands) {
            int address = command.getAddress();
            for (AirCondition airCondition : airConditions) {
                if (airCondition.getAddress() == address) {
                    airCondition.setAirconditionMode(command.getMode());
                    airCondition.setAirconditionFan(command.getFan());
                    airCondition.setTemperature(command.getTemperature());
                    airCondition.setOnoff(command.getOnoff());
                }
            }
        }
    }
    private void updateAirconditions(Room room, AirConditionControl airConditionControl) throws Exception {

        for (int index : room.getElements()) {
            List<DeviceFromServerConfig> devices = MyApp.getApp().getServerConfigManager().getDevices();
            if (devices.size() <= index) {
                throw new Exception("air condition index is to large");
            }
            DeviceFromServerConfig deviceFromServerConfig = devices.get(index);
            int address = deviceFromServerConfig.getAddress();
            if (address > 255 || address < 0) {
                throw new Exception("air condition address error");
            }
            for (AirCondition airCondition : airConditions) {
                if (airCondition.getAddress() == address) {
                    airCondition.setAirconditionMode(airConditionControl.getMode());
                    airCondition.setAirconditionFan(airConditionControl.getWindVelocity());
                    airCondition.setTemperature(airConditionControl.getTemperature());
                    airCondition.setOnoff(airConditionControl.getOnoff());
                }
            }
        }
    }

    /**
     * @param index 待查找的空调的地址
     * @return 可能为空
     */
    public AirCondition getAirConditionByIndex(int index) {
        List<DeviceFromServerConfig> devices = MyApp.getApp().getServerConfigManager().getDevices();
        if (index < 0 || index >= devices.size()) {
            return null;
        }
        int address = devices.get(index).getAddress();
        for (AirCondition airCondition : airConditions) {
            if (airCondition.getAddress() == address) {
                return airCondition;
            }
        }
        return null;
    }

    public AirCondition getAirConditionByAddress(int address) {
        for (AirCondition airCondition : airConditions) {
            if (airCondition.getAddress() == address) {
                return airCondition;
            }
        }
        return null;
    }

    public AirCondition getAirConditions(Room room) {
        List<Integer> elements = room.getElements();
        if (elements == null || elements.size() == 0) {
            return null;
        }
        AirCondition airCondition = null;
        for (int i = 0; i < elements.size(); ++i) {
            airCondition = getAirConditionByIndex(elements.get(0));
            if (airCondition == null) {
                break;
            }
        }
        if (airCondition == null) {
            airCondition = new AirCondition();
            airCondition.setMode(AirConditionControl.UNKNOW);
            airCondition.setOnoff(AirConditionControl.UNKNOW);
            airCondition.setFan(AirConditionControl.UNKNOW);
            airCondition.setTemperature(AirConditionControl.UNKNOW);
            airCondition.setRealTemperature(AirConditionControl.UNKNOW);
            return airCondition;
        }
        for (int i = 1; i < elements.size(); ++i) {
            AirCondition temp = getAirConditionByIndex(elements.get(i));
            if (temp == null) {
                continue;
            }
            if (temp.getMode() != airCondition.getMode()) {
                airCondition.setMode(AirConditionControl.UNKNOW);
            }
            if (temp.getOnoff() != airCondition.getOnoff()) {
                airCondition.setOnoff(AirConditionControl.UNKNOW);
            }
            if (temp.getFan() != airCondition.getFan()) {
                airCondition.setFan(AirConditionControl.UNKNOW);
            }
            if (temp.getTemperature() != airCondition.getTemperature()) {
                airCondition.setTemperature(AirConditionControl.UNKNOW);
            }
            if (temp.getRealTemperature() != airCondition.getRealTemperature()) {
                airCondition.setRealTemperature(AirConditionControl.UNKNOW);
            }
        }
        return airCondition;
    }

    public void queryTimerAll() {
        try {
            Log.v("liutao", "主动发包读取所有定时器状态");
            queryTimerAllWithException();
        }
        catch (Exception e) {

        }
    }

    private void queryTimerAllWithException() throws Exception {
        if (MyApp.getApp().getSocketManager().shouldSendPacketsToQuery())
            MyApp.getApp().getSocketManager().sendMessage(new QueryTimerPackage(0xffff));
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

    public void addTimerServer(Timer timer) {
        timer.setTimerid(0xffffffff);
        SocketPackage p = new TimerPackage(timer);
        MyApp.getApp().getSocketManager().sendMessage(p);
    }

    public void modifyTimerServer(Timer timer) {
        SocketPackage p = new TimerPackage(timer);
        MyApp.getApp().getSocketManager().sendMessage(p);
    }

    public void deleteTimerServer(int id) {
        SocketPackage p = new DeleteTimerPackage(id);
        MyApp.getApp().getSocketManager().sendMessage(p);
    }

    public void deleteTimerLocal(byte[] id) {
        int idInt = ByteUtil.byteArrayToShortAsBigEndian(id);
        MyApp.getApp().getServerConfigManager().deleteTimerById(idInt);
    }

}
