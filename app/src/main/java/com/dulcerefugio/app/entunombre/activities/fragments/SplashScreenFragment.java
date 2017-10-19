package com.dulcerefugio.app.entunombre.activities.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.dulcerefugio.app.entunombre.R;

/**
 * Created by Eury on 16/08/2014.
 */
public class SplashScreenFragment extends Fragment {
    //============================================================
    //FIELDS
    //============================================================
    private OnSplashActions mSplashCallback;
    private ProgressBar mProgressBar;
    private final String TAG = "SplashScreenFragment";

    //============================================================
    //OVERRIDEN METHODS
    //============================================================

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mSplashCallback = (OnSplashActions) activity;
        }catch (ClassCastException e){
            Log.wtf(TAG, "Please implement OnSplash Actions on Activity");
            Log.e(TAG,e.getMessage());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.f_splash,container,false);
        mSplashCallback.OnSplashListener();
        initialize(v);
        return v;
    }

    private void initialize(View v){

        mProgressBar = (ProgressBar)v.findViewById(R.id.pbSplash);
        mProgressBar.getIndeterminateDrawable().setColorFilter(
                getResources().getColor(android.R.color.white),
                android.graphics.PorterDuff.Mode.SRC_IN);

        /*mProgressBar.setIndeterminateDrawable(new FoldingCirclesDrawable.Builder(getActivity())
                .build());*/

    }

    public interface OnSplashActions{
        public void OnSplashListener();
    }
}