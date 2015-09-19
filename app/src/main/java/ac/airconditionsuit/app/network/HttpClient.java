package ac.airconditionsuit.app.network;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.entity.MyUser;
import ac.airconditionsuit.app.network.response.CommonResponse;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.gson.Gson;
import com.loopj.android.http.*;
import cz.msebera.android.httpclient.Header;

import javax.xml.transform.Templates;
import java.io.File;
import java.lang.reflect.Type;

/**
 * Created by ac on 9/19/15.
 */
public class HttpClient {
    public static final String TAG = "HttpClient";
    public static final String BASE_URL = "http://114.215.83.189/eliteall/3187/hosts/openapi/api.php"; //日立
    public static final String FILE_BASE_URL = "http://114.215.83.189/deviceconfig/";


    public static <T> void post(Context context, @Nullable RequestParams params, @Nullable AsyncHttpResponseHandler handler) {
        new AsyncHttpClient().post(BASE_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

        });

    }


    public static <T> void get(Context context, RequestParams params, final Type type, final BaseJsonHttpResponseHandler<T> handler) {
        new AsyncHttpClient().post(BASE_URL, params, new BaseJsonHttpResponseHandler<CommonResponse>() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, CommonResponse response) {
                //handle result

                if (handler != null) {
                    handler.onSuccess(statusCode, headers, rawJsonResponse,
                            (T) new Gson().fromJson(response.getData(), type));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, CommonResponse errorResponse) {

            }

            @Override
            protected CommonResponse parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                Log.i(TAG, "response rawJsonData:\n" + rawJsonData);
                return new Gson().fromJson(rawJsonData, CommonResponse.class);
            }
        });
    }

    public static void downloadFile(Context context, File file, RequestParams params, final FileAsyncHttpResponseHandler handler) {
        new AsyncHttpClient().get(BASE_URL, params, new FileAsyncHttpResponseHandler(file) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                throwable.printStackTrace();
                if (handler != null) {
                    handler.onFailure(statusCode, headers, throwable, file);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                if (handler != null) {
                    handler.onSuccess(statusCode, headers, file);
                }
            }
        });
    }

    private void wrapParams(RequestParams params, boolean needAuth) {
        params.put(Constant.REQUEST_PARAMS_KEY_LANGUAGE, "zh-Hans");
        params.put(Constant.REQUEST_PARAMS_KEY_VERSION, "1.0");

        if (needAuth) {
            MyUser user = MyApp.getApp().getUser();
            params.put("token", user.getToken());
            params.put("cust_id", user.getCust_id());
            params.put("display_id", user.getDisplay_id());
        }

        Log.i(TAG, "output params\n" + params.toString());
    }


}
