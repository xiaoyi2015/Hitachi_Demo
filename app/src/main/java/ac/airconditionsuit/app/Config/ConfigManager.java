package ac.airconditionsuit.app.Config;

import com.dd.plist.NSDictionary;

/**
 * Created by ac on 9/19/15.
 */
public class ConfigManager {
    //整个xml配置文件的根节点
    private NSDictionary root;

    private void readFromFile(){

    }

    private void writeToFile(){

        //call when write success
        asyncWithServer();
    }

    /**
     * 与服务器同步配置文件，每次修改{@link ConfigManager#root}以后都要调用
     */
    private void asyncWithServer(){

    }


}
