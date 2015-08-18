package com.dulcerefugio.app.entunombre.activities;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.dulcerefugio.app.entunombre.R;
import com.dulcerefugio.app.entunombre.activities.fragments.BuildPictureFragment.BuildPictureListeners;
import com.dulcerefugio.app.entunombre.activities.fragments.VideoListFragment;
import com.dulcerefugio.app.entunombre.activities.fragments.dialog.WelcomeDialogFragment;
import com.dulcerefugio.app.entunombre.logic.BitmapProcessor;
import com.dulcerefugio.app.entunombre.logic.Preferences;
import com.dulcerefugio.app.entunombre.ui.adapters.SectionsPagerAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import com.dulcerefugio.app.entunombre.util.Util;
import com.github.florent37.materialviewpager.MaterialViewPager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;

@EActivity(R.layout.a_main)
public class MainActivity extends Base implements
        BuildPictureListeners,
        VideoListFragment.VideoListListeners {

    //=============================CONSTANTS======================================
    public static final java.lang.String VIDEO_URL_PLAY = "URL_VIDEO_PLAY";
    private static final int PLAY_YOUTUBE_VIDEO = 1123;
    private static final String TAG = "MAIN_ACTIVITY";
    protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 820;

    //=============================FIELDS======================================
    private SectionsPagerAdapter mSectionsPagerAdapter;
    @ViewById(R.id.pager)
    public MaterialViewPager mViewPager;
    @ViewById(R.id.drawer_layout)
    public DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private static DialogFragment welcomeDialog;
    private static File imageFile;
    private static String randomNumber = "";
    public static String saveFolderName;
    static String cameraPhotoImagePath = "";

    //=============================OVERRIDEN METHODS======================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected boolean needToolbar() {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        welcomeDialog = welcomeDialog == null ? welcomeDialog = new WelcomeDialogFragment() : welcomeDialog;

        if (!welcomeDialog.isAdded() && !Preferences.getInstance().isWelcomeDialogShown()) {
            Preferences.getInstance().setWelcomeDialogShown(true);
            welcomeDialog.show(getSupportFragmentManager(), "welcome_message");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                //Opening Cropper Activity
                CropperActivity_.intent(this).mPicturePath(imageFile.getPath()).start();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public void OnGeneratePictureClick() {
        initCamera();
    }

    @Override
    public void onVideoPlayback(String videoId) {
        checkInternetConnection();
        if (Util.isNetworkAvailable(this)) {
            final Intent i = new Intent(this, VideoPlayerActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putString(VIDEO_URL_PLAY, videoId);
            Log.d("Main act", videoId);
            i.putExtras(mBundle);
            startActivityForResult(i, PLAY_YOUTUBE_VIDEO);
        }
    }

    //=============================METHODS======================================
    @AfterViews
    public void initialize() {
        Toolbar toolbar = mViewPager.getToolbar();
        if (toolbar != null) {
            setSupportActionBar(toolbar);

            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(true);
        }

        mToolBar.setTitle(R.string.app_name);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, 0, 0);
        mDrawer.setDrawerListener(mDrawerToggle);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        mViewPager.getViewPager().setAdapter(mSectionsPagerAdapter);
        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());
        //Camera configuration
        saveFolderName = BitmapProcessor.getSavePath();
        Log.d(TAG, saveFolderName);

        randomNumber = String.valueOf(Util.nextSessionId());
        cameraPhotoImagePath = saveFolderName + "/" + randomNumber + ".jpg";
        Log.d(TAG, cameraPhotoImagePath);
        imageFile = new File(cameraPhotoImagePath);
    }

    private void initCamera() {
        try {
            startActivityForResult(new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                            .putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile)),
                    CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        } catch (ActivityNotFoundException anfe) {
            //display an error message
            String errorMessage = "Whoops - No es posible acceder a la camara!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
        Log.d(TAG, "f  " + imageFile);
    }
}
