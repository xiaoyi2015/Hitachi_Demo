package ac.airconditionsuit.app.fragment;

import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.activity.BaseActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ac on 9/17/15.
 */
public class BaseFragment extends Fragment {
    protected static String TAG;


    private BaseActivity activity;

    {
        TAG = getClass().getName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.test_base_fragment, container, false);
    }

    public void setTopBar(){}

    public void setActivity(BaseActivity activity){
        this.activity = activity;
    }

    public BaseActivity myGetActivity() {
        return activity;
    }
}
