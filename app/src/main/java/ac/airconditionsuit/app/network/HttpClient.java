package ac.airconditionsuit.app.network;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.entity.MyUser;
import ac.airconditionsuit.app.network.response.CommonResponse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;

import java.io.File;
import java.lang.reflect.Type;

/**
 * Created by ac on 9/19/15.
 */
public class HttpClient {
    public static final String TAG = "HttpClient";
    public static final String BASE_URL = "http://114.215.83.189/eliteall/3187/hosts/openapi/api.php"; //日立
    public static final String FILE_BASE_URL = "http://114.215.83.189/deviceconfig/";
    private static AsyncHttpClient asyncHttpClient;

    public interface JsonResponseHandler<T> {
        void onSuccess(T response);

        void onFailure(Throwable throwable);
    }

    static class CommonError extends Throwable {
        CommonResponse.Message myMessage;

        public CommonError(CommonResponse.Message myMessage) {
            super();
            this.myMessage = myMessage;
        }

        public CommonResponse.Message getMyMessage() {
            return myMessage;
        }

        public void setMyMessage(CommonResponse.Message message) {
            this.myMessage = message;
        }
    }


    @SuppressWarnings("unchecked")
    public static <T> void post(RequestParams params, final Type type, final JsonResponseHandler<T> handler) {
        AsyncHttpClient asyncHttpClient = getAsyncHttpClient();
        asyncHttpClient.post(BASE_URL, wrapParams(params), new BaseJsonHttpResponseHandler<CommonResponse>() {
            @Override
            protected CommonResponse parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                Log.v(TAG, "response rawJsonData:\n" + rawJsonData);
                try {
                    return new Gson().fromJson(rawJsonData, CommonResponse.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, CommonResponse errorResponse) {
                switch (statusCode) {
                    //can not access to internet
                    case 0:
                        MyApp.getApp().showToast(R.string.toast_inf_no_net);
                        break;
                    case 200:
                        if (throwable != null) {
                            if (throwable instanceof JsonSyntaxException) {
                                MyApp.getApp().showToast(R.string.toast_inf_net_data_error);
                            } else if (throwable instanceof CommonError) {
                                MyApp.getApp().showToast(((CommonError) throwable).getMyMessage().getDialog());
                            } else {
                                MyApp.getApp().showToast(throwable.getMessage());
                            }
                        } else {
                            MyApp.getApp().showToast(R.string.toast_inf_unknown_net_error);
                        }
                        break;
                    default:
                        MyApp.getApp().showToast(R.string.toast_inf_unknown_net_error);
                }

                if (handler != null) {
                    handler.onFailure(throwable);
                }
            }

            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, String rawJsonResponse, CommonResponse response) {
                //handle result
                if (response.getCode() != 2000) {
                    onFailure(statusCode, headers, rawJsonResponse, new CommonError(response.getMsg()));
                } else {
                    if (handler != null) {
                        try {
                            handler.onSuccess((T) new Gson().fromJson(response.getData(), type));
                        } catch (Exception e) {
                            //可能 response.getData() 是空字符而type 是数组进行如下处理：
                            if (response.getData().getAsString().equals("")) {
                                try {
                                    Log.v(TAG, "get empty string as \"[]\"");
                                    handler.onSuccess((T) new Gson().fromJson("[]", type));
                                } catch (Exception e2) {
                                    onFailure(statusCode, headers, e2, rawJsonResponse, response);
                                }
                            } else {
                                onFailure(statusCode, headers, e, rawJsonResponse, response);
                            }
                        }
                    }
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> void get(RequestParams params, final Type type, final JsonResponseHandler<T> handler) {
        AsyncHttpClient asyncHttpClient = getAsyncHttpClient();

        asyncHttpClient.get(BASE_URL, wrapParams(params), new BaseJsonHttpResponseHandler<CommonResponse>() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, String rawJsonResponse, CommonResponse response) {
                //handle result
                if (response.getCode() != 2000) {
                    onFailure(statusCode, headers, rawJsonResponse, new CommonError(response.getMsg()));
                } else {
                    if (handler != null) {
                        try {
                            handler.onSuccess((T) new Gson().fromJson(response.getData(), type));
                        } catch (Exception e) {
//                            e.printStackTrace();
                            //可能 response.getData() 是空字符而type 是数组进行如下处理：
                            if (response.getData().getAsString().equals("")) {
                                try {
                                    Log.v(TAG, "get empty string as \"[]\"");
                                    handler.onSuccess((T) new Gson().fromJson("[]", type));
                                } catch (Exception e2) {
                                    onFailure(statusCode, headers, e2, rawJsonResponse, response);
                                }
                            } else {
                                onFailure(statusCode, headers, e, rawJsonResponse, response);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, String rawJsonData, CommonResponse errorResponse) {
                switch (statusCode) {
                    //can not access to internet
                    case 0:
                        MyApp.getApp().showToast(R.string.toast_inf_no_net);
                        break;
                    case 200:
                        if (throwable != null) {
                            throwable.printStackTrace();
                            if (throwable instanceof JsonSyntaxException) {
                                MyApp.getApp().showToast(R.string.toast_inf_net_data_error);
                            } else if (throwable instanceof CommonError) {
                                CommonError commonError = (CommonError) throwable;
                                MyApp.getApp().showToast(commonError.getMyMessage().getDialog());
                            } else {
                                MyApp.getApp().showToast(throwable.getMessage());
                            }
                        } else {
                            MyApp.getApp().showToast(R.string.toast_inf_unknown_net_error);
                        }
                        break;
                    default:
                        MyApp.getApp().showToast(R.string.toast_inf_unknown_net_error);
                }

                if (handler != null) {
                    handler.onFailure(throwable);
                }
            }

            @Override
            protected CommonResponse parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                Log.v(TAG, "response rawJsonData:\n" + rawJsonData);
                if (rawJsonData.equals("MOBILE_VALIDATE_CODE_ERROR")) {
                    CommonResponse cr = new CommonResponse();
                    cr.setCode(2002);
                    CommonResponse.Message m = new CommonResponse.Message();
                    m.setDialog("验证码错误");
                    cr.setMsg(m);
                    return cr;
                }
                try {
                    return new Gson().fromJson(rawJsonData, CommonResponse.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        });
    }

    private static AsyncHttpClient getAsyncHttpClient() {
        if (asyncHttpClient == null) {
            asyncHttpClient = new AsyncHttpClient();
            asyncHttpClient.setTimeout(5000);
        }
        return asyncHttpClient;
    }

    public interface DownloadFileHandler {
        void onFailure(Throwable throwable);

        void onSuccess(File file);
    }

    public static void loadCurrentUserAvatar(final String url, final ImageView imageView) {
        if (imageView == null) {
            Log.e(TAG, "load image fail");
            return;
        }

        Bitmap oldUserAvatar = MyApp.getApp().getOldUserAvatar();
        if (oldUserAvatar != null) {
            imageView.setImageBitmap(oldUserAvatar);
        } else {
            imageView.setImageResource(R.drawable.user_dc);
        }

        if (url == null) {
            return;
        }

        getAsyncHttpClient().get(url, new FileAsyncHttpResponseHandler(MyApp.getApp()) {
            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, File file) {
                Log.e(TAG, "download from " + url + " failed");
            }

            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, File file) {
                Log.v(TAG, "download from " + url + " success");
                imageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                MyApp.getApp().setOldUserAvatar(file);
            }
        });
    }

    public static void loadImage(final String url, final ImageView imageView) {
        if (imageView == null) {
            Log.e(TAG, "load image fail");
            return;
        }

        if (url == null) {
            return;
        }
        imageView.setImageResource(R.drawable.user_dc);

        getAsyncHttpClient().get(url, new FileAsyncHttpResponseHandler(MyApp.getApp()) {
            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, File file) {
                Log.e(TAG, "download from " + url + " failed");
            }

            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, File file) {
                Log.v(TAG, "download from " + url + " success");
                imageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
            }
        });
    }

    public static void downloadFile(final String url, final File outputFile, final DownloadFileHandler handler) {
        File tempOutputFile = MyApp.getApp().getPrivateFile(outputFile.getName(), "temp");
        getAsyncHttpClient().get(url, new FileAsyncHttpResponseHandler(tempOutputFile) {
            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, File file) {
                throwable.printStackTrace();
                Log.e(TAG, "download file from " + url + "failed");
                if (file.exists()) {
                    file.delete();
                }
                if (handler != null) {
                    handler.onFailure(throwable);
                }
            }

            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, File file) {
                Log.v(TAG, "download file " + file.getPath() + " success");
                if (handler != null) {
                    if (outputFile.exists()) {
                        outputFile.delete();
                    }
                    file.renameTo(outputFile);
                    handler.onSuccess(outputFile);
                }
            }
        });
    }


    private static RequestParams wrapParams(RequestParams params) {
        params.put(Constant.REQUEST_PARAMS_KEY_LANGUAGE, "zh-Hans");
        params.put(Constant.REQUEST_PARAMS_KEY_VERSION, "1.0");

        MyUser user = MyApp.getApp().getUser();
        if (user != null) {
            params.put("token", user.getToken());
            params.put("cust_id", user.getCust_id());
            params.put("display_id", user.getDisplay_id());
        }
        Log.v(TAG, "output params\n" + params.toString());
        return params;
    }

    public static String getDownloadConfigUrl(Long deviceId, Long userId) {
        return FILE_BASE_URL + deviceId + "/" + userId.toString() + ".xml";
    }

    public static String getDownloadConfigUrl(Long deviceId) {
        return getDownloadConfigUrl(deviceId, MyApp.getApp().getUser().getCust_id());
    }
}
