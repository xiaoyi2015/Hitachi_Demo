package ac.airconditionsuit.app.network;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.entity.MyUser;
import ac.airconditionsuit.app.network.response.CommonResponse;

import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.*;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpStatus;

import java.io.File;
import java.lang.reflect.Type;

/**
 * Created by ac on 9/19/15.
 */
public class HttpClient {
    public static final String TAG = "HttpClient";
    public static final String BASE_URL = "http://114.215.83.189/eliteall/3187/hosts/openapi/api.php"; //日立
    public static final String FILE_BASE_URL = "http://114.215.83.189/deviceconfig/";

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
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(BASE_URL, wrapParams(params) , new BaseJsonHttpResponseHandler<CommonResponse>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, CommonResponse response) {
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
                                    Log.i(TAG, "get empty string as \"[]\"");
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
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, CommonResponse errorResponse) {
                switch (statusCode) {
                    //can not access to internet
                    case 0:
                        MyApp.getApp().showToast(R.string.toast_inf_no_net);
                        break;
                    case HttpStatus.SC_OK:
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
            protected CommonResponse parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
//                Log.i(TAG, "response rawJsonData:\n" + rawJsonData);
                return new Gson().fromJson(rawJsonData, CommonResponse.class);
            }
        });
    }


    @SuppressWarnings("unchecked")
    public static <T> void get(RequestParams params, final Type type, final JsonResponseHandler<T> handler) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(BASE_URL, wrapParams(params), new BaseJsonHttpResponseHandler<CommonResponse>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, CommonResponse response) {
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
                                    Log.i(TAG, "get empty string as \"[]\"");
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
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, CommonResponse errorResponse) {
                switch (statusCode) {
                    //can not access to internet
                    case 0:
                        MyApp.getApp().showToast(R.string.toast_inf_no_net);
                        break;
                    case HttpStatus.SC_OK:
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
            protected CommonResponse parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
//                Log.i(TAG, "response rawJsonData:\n" + rawJsonData);
                return new Gson().fromJson(rawJsonData, CommonResponse.class);
            }
        });
    }

    public interface DownloadFileHandler {
        void onFailure(Throwable throwable);
        void onSuccess(File file);
    }

    public static void loadImage(final String url, final ImageView imageView){
        new AsyncHttpClient().get(url, new FileAsyncHttpResponseHandler(MyApp.getApp()) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                Log.e(TAG, "download from " + url + "failed");
                //TODO for zhulinan 给imageview 设置一张默认的表示下载失败的图片
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                Log.i(TAG, "download from " + url + "success");
                imageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
            }
        });
    }

    public static void downloadFile(final String url, File file, final DownloadFileHandler handler) {
        new AsyncHttpClient().get(url, new FileAsyncHttpResponseHandler(file) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                throwable.printStackTrace();
                Log.e(TAG, "download file from " + url + "failed");
                if (handler != null) {
                    handler.onFailure(throwable);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                Log.i(TAG, "download file " + file.getPath() + " success");
                if (handler != null) {
                    handler.onSuccess(file);
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
//        Log.i(TAG, "output params\n" + params.toString());
        return params;
    }

    public static String getDownloadConfigUrl(Long deviceId) {
        return FILE_BASE_URL + deviceId + "/" + MyApp.getApp().getUser().getCust_id() + ".xml";
    }

}
