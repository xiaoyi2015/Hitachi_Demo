package ac.airconditionsuit.app.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ac.airconditionsuit.app.R;

/**
 * Created by Administrator on 2015/10/3.
 */
public class FamilyFragment extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_family,container,false);
        return view;
    }
}
