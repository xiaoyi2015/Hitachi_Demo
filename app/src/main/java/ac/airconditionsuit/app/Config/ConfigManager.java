package ac.airconditionsuit.app.Config;

import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.activity.UserInfoActivity;
import ac.airconditionsuit.app.util.MyBase64Util;
import android.content.Context;
import android.provider.MediaStore;
import android.util.Log;
import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListFormatException;
import com.dd.plist.PropertyListParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.text.ParseException;

/**
 * Created by ac on 9/19/15.
 *
 */
public class ConfigManager {

    public static final String Tag = "ConfigManager";

    //整个xml配置文件的根节点
    private NSDictionary root;

    private void readFromFile() {
        if (!MyApp.getApp().isUserLogin()) {
            return;
        }

        FileInputStream fis = null;
        try {
            fis = MyApp.getApp().openFileInput(MyApp.getApp().getConfigFileName());
            byte[] bytes = new byte[fis.available()];
            if (fis.read(bytes) != bytes.length) {
                Log.e(Tag, "read config file error");
                return;
            }
            root = (NSDictionary) PropertyListParser.parse(MyBase64Util.decodeToByte(bytes));
        } catch (ParserConfigurationException | SAXException | ParseException | IOException | PropertyListFormatException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void writeToFile() {
        if (!MyApp.getApp().isUserLogin()) {
            return;
        }

        FileOutputStream fos = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PropertyListParser.saveAsXML(root, byteArrayOutputStream);
            byte[] bytes = MyBase64Util.encodeToByte(byteArrayOutputStream.toByteArray(), true);

            fos = MyApp.getApp().openFileOutput(MyApp.getApp().getConfigFileName(), Context.MODE_PRIVATE);
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //call when write success
        asyncWithServer();
    }

    /**
     * 与服务器同步配置文件，每次修改{@link ConfigManager#root}以后都要调用
     */
    private void asyncWithServer() {

    }


}
