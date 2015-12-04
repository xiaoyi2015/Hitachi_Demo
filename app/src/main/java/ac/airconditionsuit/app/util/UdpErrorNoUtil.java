package ac.airconditionsuit.app.util;

/**
 * Created by ac on 10/15/15.
 */
public class UdpErrorNoUtil {
    public static String getMessage(int errorNo) {
        switch (errorNo) {
            case 1001:
                return "终端未登陆";
            case 1002:
                return "终端数量超限制";
            case 1003:
                return "认证码错误";
            case 1004:
                return "校验码错误";
            case 1005:
                return "功能代码错误";
            case 1006:
                return "获取i-EZ控制器序列号失败";
            case 1007:
                return "网络故障";
            case 1008:
                return "空调系统初始化未完成，请稍等";
            case 1009:
                return "系统繁忙";
            case 1010:
                return "定时器数量超限";
            case 1099:
                return "系统故障";
            case 2000:
                return "空调控制成功";
            case 2001:
                return "控制失败";
            default:
                return "未知错误";
        }
    }
}
