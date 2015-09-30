package com.dulcerefugio.app.entunombre.activities;

import android.content.ActivityNotFoundException;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.dulcerefugio.app.entunombre.EnTuNombre;
import com.dulcerefugio.app.entunombre.R;
import com.dulcerefugio.app.entunombre.activities.fragments.PictureListFragment;
import com.dulcerefugio.app.entunombre.activities.fragments.PictureListFragment.PictureListListeners;
import com.dulcerefugio.app.entunombre.activities.fragments.PictureListFragment_;
import com.dulcerefugio.app.entunombre.activities.fragments.VideoListFragment;
import com.dulcerefugio.app.entunombre.activities.fragments.dialog.AppMessageDialog;
import com.dulcerefugio.app.entunombre.activities.fragments.dialog.PictureChooserDialog;
import com.dulcerefugio.app.entunombre.activities.fragments.dialog.WelcomeDialogFragment;
import com.dulcerefugio.app.entunombre.data.dao.GeneratedImages;
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

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

@EActivity(R.layout.a_main)
public class MainActivity extends Base implements
        PictureListListeners,
        VideoListFragment.VideoListListeners,
        AppMessageDialog.OnAppMessageDialogListener,
        PictureChooserDialog.OnPictureChooserListeners{

    //=============================CONSTANTS======================================
    public static final java.lang.String VIDEO_URL_PLAY = "URL_VIDEO_PLAY";
    private static final int PLAY_YOUTUBE_VIDEO = 1123;
    private static final String TAG = "MAIN_ACTIVITY";
    protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 820;
    private static final int CROPPER_ACTIVITY_RESULT_CODE = 153;
    private static final String PICTURE_PREVIEW_DIALOG = "PICTURE_PREVIEW_DIALOG";
    private static final String APP_ABOUT_DIALOG = "APP_ABOUT_DIALOG";
    private static final String SHOWCASE_ID = "1234cx.";
    private final int cSELECT_FILE_RQ = 1934;
    private String cPICTURE_POST_CHOOSER_FRAGMENT_TAG="cPICTURE_POST_CHOOSER_FRAGMENT_TAG";

    //=============================FIELDS======================================
    private SectionsPagerAdapter mSectionsPagerAdapter;
    @ViewById(R.id.materialViewPager)
    public MaterialViewPager mViewPager;
    private ActionBarDrawerToggle mDrawerToggle;
    private static File imageFile;
    private static String randomNumber = "";
    public static String saveFolderName;
    static String cameraPhotoImagePath = "";
    @ViewById(R.id.drawer_layout)
    public DrawerLayout mDrawerLayout;
    private DialogFragment mAppMessageImagePreview;
    private String[] mDrawerOptions;
    @ViewById(R.id.drawer_lv_items)
    public ListView mDrawerList;
    @ViewById(R.id.left_drawer)
    public View mDrawer;
    private DialogFragment mAppMessageAbout;

    //=============================OVERRIDEN METHODS======================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected boolean isDisplayHomeAsUpEnabled() {
        return true;
    }

    @Override
    protected boolean needToolbar() {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                    Logger.d(imageFile.getPath());
                    //Opening Cropper Activity
                    CropperActivity_.intent(this).mPicturePath(imageFile.getPath()).startForResult(CROPPER_ACTIVITY_RESULT_CODE);
                    break;
                case cSELECT_FILE_RQ:
                    Uri selectedImageUri = data.getData();
                    CropperActivity_.intent(this).mPicturePath(Util.getPath(selectedImageUri)).startForResult(CROPPER_ACTIVITY_RESULT_CODE);
                    break;
                case CROPPER_ACTIVITY_RESULT_CODE:
                    long generatedImageID = data.getLongExtra(CropperActivity.GENERATED_IMAGE_ID, 0);
                    GeneratedImages generatedImage = EnTuNombre
                            .getInstance()
                            .getDaoSession()
                            .getGeneratedImagesDao()
                            .load(generatedImageID);
                    PictureListFragment_ fragment = (PictureListFragment_) mSectionsPagerAdapter.getItem(0);
                    fragment.addItem(generatedImage, 0);
                    openShareIntent(generatedImage.getPath());
                    Snackbar.make(findViewById(android.R.id.content),
                            R.string.picture_added_snackbar,
                            Snackbar.LENGTH_LONG)
                            .show();
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(mDrawer);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGeneratePictureClick() {
        PictureChooserDialog chooserDialog = new PictureChooserDialog();
        chooserDialog.show(getSupportFragmentManager(), cPICTURE_POST_CHOOSER_FRAGMENT_TAG);
    }

    @Override
    public void onPictureShare(String imageUri) {
        openShareIntent(imageUri);
    }

    @Override
    public void onCardSelected(GeneratedImages generatedImages) {
        mAppMessageImagePreview = Util.getAppMessageDialog(AppMessageDialog.MessageType.IMAGE_PREVIEW,
                generatedImages.getPath(), false);

        mAppMessageImagePreview.show(mFragmentManager, PICTURE_PREVIEW_DIALOG);
    }

    @Override
    public void onPictureListLoaded(View view) {
        new MaterialShowcaseView.Builder(this)
                .setTarget(view.findViewById(R.id.materialButton))
                .setDismissText(R.string.entendido)
                .setContentText(R.string.create_image_showcase)
                .singleUse(SHOWCASE_ID)
                .show();
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
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 0, 0);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerOptions = getResources().getStringArray(R.array.main_drawer_options);
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, mDrawerOptions));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                switch (position) {
                    case 0: //make an image
                        onGeneratePictureClick();
                        break;
                    case 1: // About
                        if (mAppMessageAbout == null)
                            mAppMessageAbout = Util.getAppMessageDialog(AppMessageDialog.MessageType.ABOUT,
                                    null,
                                    true);

                        mAppMessageAbout.show(mFragmentManager, APP_ABOUT_DIALOG);
                        break;
                    case 2: //Rate us
                        startActivity(Util.getRateUsIntent());
                        break;
                    case 3: //Exit
                        finish();
                        break;
                }
            }
        });
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
                                "http://www.droid-life.com/wp-content/uploads/2014/10/lollipop-wallpapers10.jpg");
                    case 1:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.green,
                                "https://fs01.androidpit.info/a/63/0e/android-l-wallpapers-630ea6-h900.jpg");
                }

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

    private void openShareIntent(String imageUri) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imageUri)));
        startActivity(Intent.createChooser(share, "Compartir Imagen"));
    }

    @Override
    public void onPreviewDialogShare(String imageUri) {
        openShareIntent(imageUri);
    }

    @Override
    public void onPositiveButton() {
        finish();
    }

    @Override
    public void onTakePicture(Fragment fragment) {
        initCamera();
    }

    @Override
    public void onChooseFromGallery(Fragment fragment) {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, getString(R.string.chooser_title)),
                cSELECT_FILE_RQ);
    }

    @Override
    public void onPicturePostCancel() {

    }
}
