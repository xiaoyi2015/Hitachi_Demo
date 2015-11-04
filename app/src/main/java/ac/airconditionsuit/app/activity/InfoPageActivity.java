package ac.airconditionsuit.app.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.fragment.FamilyFragment;
import ac.airconditionsuit.app.fragment.InfoFragment;
import ac.airconditionsuit.app.view.SegmentControlView;

/**
 * Created by Administrator on 2015/10/3.
 */
public class InfoPageActivity extends BaseActivity{

    private SegmentControlView segmentControlView;
    private InfoFragment infoFragment;
    private FamilyFragment familyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_info_page);
        super.onCreate(savedInstanceState);
        segmentControlView = (SegmentControlView)findViewById(R.id.SegmentControlView);
        setDefaultFragment();
        Listener();
        ImageView exit = (ImageView)findViewById(R.id.exit_button);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void Listener() {
        segmentControlView.setOnSegmentControlViewClickListener(new SegmentControlView.onSegmentControlViewClickListener() {
            @Override
            public void onSegmentControlViewClick(View v, int position) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch (position) {
                    case 0:
                        if (infoFragment == null) {
                            infoFragment = new InfoFragment();
                        }
                        transaction.replace(R.id.fragment_layout, infoFragment);
                        break;
                    case 1:
                        if (familyFragment == null) {
                            familyFragment = new FamilyFragment();
                        }
                        transaction.replace(R.id.fragment_layout, familyFragment);
                        break;
                    default:
                        break;

                }
                transaction.commit();
            }
        });
    }

    private void setDefaultFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        infoFragment = new InfoFragment();
        transaction.replace(R.id.fragment_layout, infoFragment);
        transaction.commit();
    }
}
