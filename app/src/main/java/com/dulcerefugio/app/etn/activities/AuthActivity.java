package com.dulcerefugio.app.etn.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;

import com.dulcerefugio.app.etn.EnTuNombre;
import com.dulcerefugio.app.etn.data.dao.DaoSession;
import com.dulcerefugio.app.etn.data.dao.YoutubeVideo;
import com.dulcerefugio.app.etn.activities.fragments.SplashScreenFragment;
import com.dulcerefugio.app.etn.logic.YouTubeManager;
import com.dulcerefugio.app.etn.util.Util;

import java.util.List;

/**
 * Created by Eury on 16/08/2014.
 */
public class AuthActivity extends FragmentActivity implements SplashScreenFragment.OnSplashActions {

    private static final String TAG = "AuthActivity";
    private SplashScreenFragment mSplashFragment;
    private DaoSession mDaoSession;

    //============================================================
    // ACTIVITY LYFECYCLE
    //============================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        mDaoSession = EnTuNombre.getInstance().getDaoSession();
        presentSplashFragment();
        insertVideos();
    }
    //============================================================
    // METHODS
    //============================================================

    /*private void presentAuthenticationFragment(){
        log.d("presentAuthenticationFragment");
        mAuthenticationFragment = new AuthenticationFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(android.R.id.content,mAuthenticationFragment,AUTHENTICATION_FRAGMENT_TAG);
        transaction.commit();

    }*/

    private void presentSplashFragment() {
        Log.d(TAG, "presentSplashFragment");
        mSplashFragment = new SplashScreenFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(android.R.id.content, mSplashFragment);

        transaction.commit();
    }

    private void insertVideos() {
        final YouTubeManager ym = new YouTubeManager();

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (Util.isNetworkAvailable(AuthActivity.this)) {
                    try {
                        List<YoutubeVideo> videoList = ym.getVideosFromYoutubeChannel("", YouTubeManager.CHANNEL_ID);
                        Log.d("", (mDaoSession == null) + "");
                        Log.d("", (mDaoSession.getYoutubeVideoDao() == null) + "");
                        if (videoList != null && videoList.size() > 0)
                            mDaoSession.getYoutubeVideoDao().insertOrReplaceInTx(videoList);
                    }catch (NullPointerException ex){
                        ex.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                callStartApplication();
            }
        }.execute();
    }

    private void callStartApplication() {
        Log.d(TAG, "callStartApplication");
        Intent intent = new Intent(this, MainActivity_.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void OnSplashListener() {

    }
}