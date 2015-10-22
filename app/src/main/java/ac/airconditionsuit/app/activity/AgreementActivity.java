package ac.airconditionsuit.app.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.UIManager;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/9/29.
 */
public class AgreementActivity extends BaseActivity{
    private MyOnClickListener myOnClickListener = new MyOnClickListener(){
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
        setContentView(R.layout.fragment_setting_agreement);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        commonTopBar.setTitle(getString(R.string.common_agree_clause));
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
        commonTopBar.setIconView(myOnClickListener, null);
        WebView webView = (WebView)findViewById(R.id.agreement_web_view);
        switch (UIManager.UITYPE){
            case 1:
                webView.loadUrl("file:///android_asset/hitachi-agreement.html");
                break;
            case 2:
                webView.loadUrl("file:///android_asset/d-controls-agreement.html");
                break;
            default:
                webView.loadUrl("file:///android_asset/hitachi-agreement.html");
                break;
        }

    }
}
