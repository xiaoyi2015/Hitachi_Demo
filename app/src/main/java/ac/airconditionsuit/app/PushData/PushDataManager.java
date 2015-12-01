package ac.airconditionsuit.app.PushData;

import ac.airconditionsuit.app.Config.ServerConfigManager;
import ac.airconditionsuit.app.entity.Timer;
import ac.airconditionsuit.app.listener.CommonNetworkListener;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.network.HttpClient;
import ac.airconditionsuit.app.network.response.PushDataListResponse;

/**
 * Created by ac on 10/11/15.
 * 用来管理推送得到的消息
 */
public class PushDataManager {
    public static final String TABLE_NAME = "pushdata";
    public static final String TS = "ts";
    public static final String ID = "pushdataid";
    public static final String CHATID = "chatid";
    public static final String TYPE = "type";
    public static final String CONTENT = "content";
    private static final String TAG = "PushDataManager";

    public class PushData {

        private Long tableId = 0l;

        private long ts;
        private long id;
        private long chatid;

        public long getTid() {
            return tid;
        }

        private long tid;
        private long chat_id;
        private int type;
        private String content;

        public long getTs() {
            return ts;
        }

        public long getId() {
            return id;
        }

        public long getChatid() {
            if (chatid != 0) {
                return chatid;
            } else if (chat_id != 0) {
                return chat_id;
            } else {
                Log.e(TAG, "error!!! for push data chat id");
                return 0;
            }
        }

        public int getType() {
            return type;
        }

        public String getContent() {
            return content;
        }

        public void setTs(long ts) {
            this.ts = ts;
        }

        public void setId(long id) {
            this.id = id;
        }

        public void setChatid(long chatid) {
            this.chatid = chatid;
            this.chat_id = chatid;
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Long getTableId() {
            return tableId;
        }

        public void setTableId(long tableId) {
            this.tableId = tableId;
        }

        public void fixTime() {
            if (ts == 0) {
                ts = System.currentTimeMillis() / 1000;
            }
        }
    }

    public class PushDataDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 2;
        public static final String DATABASE_NAME = "pushData.db";
        private final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + TS + " INTEGER, " + ID + " INTEGER, " +
                        CHATID + " Text, " + TYPE + " INTEGER, " + CONTENT + " Text)";
        private final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public PushDataDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

    public long add(String data) {
        try {
            PushData pushData = new Gson().fromJson(data, PushData.class);
            if (pushData.getType() == 26) {
                return 0;
            }
            pushData.fixTime();
            saveToSQLite(pushData);
            if (pushData.getType() == 103
                    || pushData.getType() == 101
                    || pushData.getType() == 102
                    || pushData.getType() == 20) {
                if (pushData.getType() == 103) {
                    for (Timer t : MyApp.getApp().getServerConfigManager().getTimer()) {
                        if (pushData.getTid() == (long) t.getTimerid()) {
                            MyApp.getApp().showToast(pushData.getContent().substring(0, 4) + "\"" + t.getName() + "\"" + pushData.getContent().substring(4));
                            break;
                        }
                    }
                } else {
                    MyApp.getApp().showToast(pushData.getContent());
                }
            }
            if (pushData.getType() == 20) {
                if (MyApp.getApp().getServerConfigManager().hasDevice()) {
                    Log.v(TAG, "delete device local!!!!");
                    MyApp.getApp().getServerConfigManager().deleteDeviceLocal();
                } else {
                    for (String configFileName : MyApp.getApp().getLocalConfigManager().getCurrentUserConfig().getHomeConfigFileNames()) {
                        ServerConfigManager serverConfigManager = new ServerConfigManager();
                        serverConfigManager.readFromFile(configFileName);
                        if (serverConfigManager.getRootJavaObj() == null) {
                            continue;
                        }
                        if (serverConfigManager.hasDevice()) {
                            if (serverConfigManager.getConnections().get(0).getChat_id() == pushData.getChatid()) {
                                serverConfigManager.deleteDeviceLocal();
                                Log.v(TAG, "delete device local!!!!");
                                break;
                            }
                        }
                    }
                }
            }
            return pushData.getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * @param pushDatas
     * @return return message ids, for ack
     */
    private void saveToSQLite(List<PushDataListResponse> pushDatas) {
        try (SQLiteDatabase db = new PushDataDbHelper(MyApp.getApp()).getWritableDatabase()) {
            for (PushDataListResponse pushData : pushDatas) {
                ContentValues values = getContentValues(pushData.getContent());
                if (db.insert(TABLE_NAME, null, values) == -1) {
                    System.out.println("insert push data failed!!!");
                }
            }
            db.close();
        }
    }

    private void saveToSQLite(PushData pushData) {
        SQLiteDatabase db = new PushDataDbHelper(MyApp.getApp()).getWritableDatabase();
        ContentValues values = getContentValues(pushData);
        if (db.insert(TABLE_NAME, null, values) == -1) {
            System.out.println("insert push data failed!!!");
        } else {
            System.out.println("insert push data success!!!");
        }
        db.close();
    }

    public List<PushData> readPushDataFromDatabase() {
        // Select All Query
        // String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        String currentHomeDeviceId = MyApp.getApp().getLocalConfigManager().getCurrentHomeDeviceId();
        if (currentHomeDeviceId == null) {
            return new ArrayList<>();
        }
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " where " + CHATID + " = \"" + currentHomeDeviceId + "\"";

        SQLiteDatabase db = new PushDataDbHelper(MyApp.getApp()).getReadableDatabase();
        List<PushData> result = new ArrayList<>();

        try {

            Cursor cursor = db.rawQuery(selectQuery, null);
            try {

                // looping through all rows and adding to list
                if (cursor.moveToFirst()) {
                    do {
                        PushData obj = tableRowToPushData(cursor);
                        //only one column

                        result.add(obj);
                    } while (cursor.moveToNext());
                }

            } finally {
                try {
                    cursor.close();
                } catch (Exception ignore) {
                }
            }

        } finally {
            try {
                db.close();
            } catch (Exception ignore) {
            }
        }
        return result;
    }

    public void deletePushData(PushData pushData) {
        SQLiteDatabase db = new PushDataDbHelper(MyApp.getApp()).getWritableDatabase();
        db.delete(TABLE_NAME, "_id = ?", new String[]{pushData.getTableId().toString()});
    }

    private PushData tableRowToPushData(Cursor cursor) {
        PushData pushData = new PushData();
        pushData.setTableId(cursor.getLong(0));
        pushData.setTs(cursor.getLong(1));
        pushData.setId(cursor.getLong(2));
        pushData.setChatid(cursor.getLong(3));
        pushData.setType(cursor.getInt(4));
        pushData.setContent(cursor.getString(5));
        return pushData;
    }

    private ContentValues getContentValues(PushData pushData) {
        ContentValues cv = new ContentValues();
        cv.put(TS, pushData.getTs());
        cv.put(ID, pushData.getId());
        cv.put(CHATID, String.valueOf(pushData.getChatid()));
        cv.put(TYPE, pushData.getType());
        cv.put(CONTENT, pushData.getContent());
        return cv;
    }

    public void checkPushDataFromService() {
        final RequestParams requestParams = new RequestParams();
        requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_CHAT);
        requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_TYPE_GET_PUSHDATA);
        requestParams.put("auth", MyApp.getApp().getUser().getAuth());

        HttpClient.get(requestParams, new TypeToken<List<PushDataListResponse>>() {
                }.getType(),
                new HttpClient.JsonResponseHandler<List<PushDataListResponse>>() {
                    @Override
                    public void onSuccess(List<PushDataListResponse> pushDatas) {
                        if (pushDatas == null) {
                            return;
                        }
                        saveToSQLite(pushDatas);
                        ack(pushDatas);
                        if (pushDatas.size() != 0) {
                            checkPushDataFromService();
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        MyApp.getApp().showToast(R.string.check_push_data_failure);
                    }
                });
    }

    private void ack(List<PushDataListResponse> pushDatas) {
        String ids = "";
        for (PushDataListResponse pd : pushDatas) {
            ids += "," + pd.getContent().getId();
        }
        if (ids.length() != 0) {
            ids = ids.substring(1);

            final RequestParams requestParams = new RequestParams();
            requestParams.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_CHAT);
            requestParams.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_TYPE_ACK_MSG_ID);
            requestParams.put(Constant.REQUEST_PARAMS_KEY_PN_MSG_IDS, ids);

            HttpClient.get(requestParams, String.class, new HttpClient.JsonResponseHandler<String>() {
                @Override
                public void onSuccess(String string) {
                    MyApp.getApp().showToast(R.string.success);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    MyApp.getApp().showToast(R.string.failure);
                }
            });
        }
    }
}
