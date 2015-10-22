package ac.airconditionsuit.app.activity;

import ac.airconditionsuit.app.Constant;
import ac.airconditionsuit.app.MyApp;
import ac.airconditionsuit.app.entity.Device;
import ac.airconditionsuit.app.network.HttpClient;
import ac.airconditionsuit.app.network.response.GetChatTokenResponse;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.view.CommonTopBar;
import android.widget.ImageView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.loopj.android.http.RequestParams;

/**
 * Created by Administrator on 2015/10/21.
 */
public class QRCodeActivity extends BaseActivity{

    private MyOnClickListener myOnClickListener = new MyOnClickListener() {
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.left_icon:
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_qr_code);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.qr_code));
        switch (UIManager.UITYPE){
            case 1:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_hit);
                break;
            case 2:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_dc);
                break;
            default:
                commonTopBar.setLeftIconView(R.drawable.top_bar_back_dc);
                break;
        }
        commonTopBar.setIconView(myOnClickListener,null);
        showQRCode();

    }

    void showQRCode(){
        RequestParams params = new RequestParams();
        params.put(Constant.REQUEST_PARAMS_KEY_METHOD, Constant.REQUEST_PARAMS_VALUE_METHOD_CHAT);
        params.put(Constant.REQUEST_PARAMS_KEY_TYPE, Constant.REQUEST_PARAMS_VALUE_TYPE_GET_CHAT_TOKEN);
        params.put(Constant.REQUEST_PARAMS_KEY_DEVICE_ID, MyApp.getApp().getServerConfigManager().getCurrentChatId());
        HttpClient.get(params, GetChatTokenResponse.class, new HttpClient.JsonResponseHandler<GetChatTokenResponse>() {
            @Override
            public void onSuccess(GetChatTokenResponse response) {
                Device.QRCode qrCode = new Device.QRCode(MyApp.getApp().getServerConfigManager().getCurrentChatId());
                qrCode.setT(response.getT());

                QRCodeWriter writer = new QRCodeWriter();
                try {
                    BitMatrix bitMatrix =
                            writer.encode(qrCode.toJsonString(),
                                    BarcodeFormat.QR_CODE, 1024, 1024);

                    int width = bitMatrix.getWidth();
                    int height = bitMatrix.getHeight();
                    Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                        }
                    }
                    ((ImageView)findViewById(R.id.QRCode)).setImageBitmap(bmp);
                } catch (WriterException e) {
                    Log.e(TAG, "gen qrcode failed");
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }

}
