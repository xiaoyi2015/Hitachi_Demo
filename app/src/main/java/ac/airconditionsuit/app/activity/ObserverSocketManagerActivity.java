package ac.airconditionsuit.app.activity;

import ac.airconditionsuit.app.MyApp;

import java.util.Observer;

/**
 * Created by ac on 10/10/15.
 */
public abstract class ObserverSocketManagerActivity extends BaseActivity implements Observer {
    @Override
    protected void onResume() {
        super.onResume();
        MyApp.getApp().getSocketManager().addObserver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApp.getApp().getSocketManager().deleteObserver(this);
    }
}
