package com.dulcerefugio.app.entunombre.activities;

import android.content.ActivityNotFoundException;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.dulcerefugio.app.entunombre.R;
import com.dulcerefugio.app.entunombre.activities.fragments.PictureListFragment.PictureListListeners;
import com.dulcerefugio.app.entunombre.activities.fragments.VideoListFragment;
import com.dulcerefugio.app.entunombre.activities.fragments.dialog.WelcomeDialogFragment;
import com.dulcerefugio.app.entunombre.logic.BitmapProcessor;
import com.dulcerefugio.app.entunombre.logic.Preferences;
import com.dulcerefugio.app.entunombre.ui.adapters.SectionsPagerAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.dulcerefugio.app.entunombre.util.Util;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.orhanobut.logger.Logger;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;

@EActivity(R.layout.a_main)
public class MainActivity extends Base implements
        PictureListListeners,
        VideoListFragment.VideoListListeners {

    //=============================CONSTANTS======================================
    public static final java.lang.String VIDEO_URL_PLAY = "URL_VIDEO_PLAY";
    private static final int PLAY_YOUTUBE_VIDEO = 1123;
    private static final String TAG = "MAIN_ACTIVITY";
    protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 820;

    //=============================FIELDS======================================
    private SectionsPagerAdapter mSectionsPagerAdapter;
    @ViewById(R.id.materialViewPager)
    public MaterialViewPager mViewPager;
    private ActionBarDrawerToggle mDrawerToggle;
    private static DialogFragment welcomeDialog;
    private static File imageFile;
    private static String randomNumber = "";
    public static String saveFolderName;
    static String cameraPhotoImagePath = "";
    @ViewById(R.id.drawer_layout)
    public DrawerLayout mDrawer;

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
                Logger.d(imageFile.getPath());
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
            Logger.d("is vp toolbar");
            setSupportActionBar(toolbar);

            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("");
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, 0, 0);
        mDrawer.setDrawerListener(mDrawerToggle);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager(), this);

        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                switch (page) {
                    case 0:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.blue,
                                "http://cdn1.tnwcdn.com/wp-content/blogs.dir/1/files/2014/06/wallpaper_51.jpg");
                    case 1:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.green,
                                "https://fs01.androidpit.info/a/63/0e/android-l-wallpapers-630ea6-h900.jpg");
                    case 2:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.cyan,
                                "http://www.droid-life.com/wp-content/uploads/2014/10/lollipop-wallpapers10.jpg");
                    case 3:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.red,
                                "http://www.tothemobile.com/wp-content/uploads/2014/07/original.jpg");
                }

                //execute others actions if needed (ex : modify your header logo)

                return null;
            }
        });
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

        Logger.d("f  " + imageFile);
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
    }
}
