package ac.airconditionsuit.app.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import ac.airconditionsuit.app.R;
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
        commonTopBar.setIconView(myOnClickListener, null);
        WebView webView = (WebView)findViewById(R.id.agreement_web_view);
        webView.loadUrl("file:///android_asset/d-controls-agreement.html");
    }
}
