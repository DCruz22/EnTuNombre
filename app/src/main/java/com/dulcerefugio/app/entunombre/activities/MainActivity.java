package com.dulcerefugio.app.entunombre.activities;

import android.content.ActivityNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.dulcerefugio.app.entunombre.EnTuNombre;
import com.dulcerefugio.app.entunombre.R;
import com.dulcerefugio.app.entunombre.activities.fragments.PictureListFragment.PictureListListeners;
import com.dulcerefugio.app.entunombre.activities.fragments.PictureListFragment_;
import com.dulcerefugio.app.entunombre.activities.fragments.VideoListFragment;
import com.dulcerefugio.app.entunombre.activities.fragments.dialog.AppMessageDialog;
import com.dulcerefugio.app.entunombre.activities.fragments.dialog.PictureChooserDialog;
import com.dulcerefugio.app.entunombre.data.dao.GeneratedImages;
import com.dulcerefugio.app.entunombre.logic.BitmapProcessor;
import com.dulcerefugio.app.entunombre.ui.adapters.SectionsPagerAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.dulcerefugio.app.entunombre.util.Loggable;
import com.dulcerefugio.app.entunombre.util.Util;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionItemTarget;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
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
        VideoListFragment.VideoListListeners,
        AppMessageDialog.OnAppMessageDialogListener,
        PictureChooserDialog.OnPictureChooserListeners,
        Loggable {

    //=============================CONSTANTS======================================
    private static final int PLAY_YOUTUBE_VIDEO = 1123;
    private final int cCHOOSE_IMAGE_REQUEST_CODE = 21;
    private static final int CROPPER_ACTIVITY_RESULT_CODE = 153;
    protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 820;
    private final int cSELECT_FILE_RQ = 1934;
    public static final java.lang.String VIDEO_URL_PLAY = "URL_VIDEO_PLAY";
    private static final String TAG = "MAIN_ACTIVITY";
    private static final String PICTURE_PREVIEW_DIALOG = "PICTURE_PREVIEW_DIALOG";
    private static final String APP_ABOUT_DIALOG = "APP_ABOUT_DIALOG";
    private static final long SHOWCASE_ID = 1234L;
    private String cPICTURE_POST_CHOOSER_FRAGMENT_TAG = "cPICTURE_POST_CHOOSER_FRAGMENT_TAG";
    private String cCAMERA_PICTURE_TEMP_NAME = "temp.jpg";

    //=============================FIELDS======================================
    @ViewById(R.id.drawer_layout)
    public DrawerLayout mDrawerLayout;

    @ViewById(R.id.materialViewPager)
    public MaterialViewPager mViewPager;

    @ViewById(R.id.drawer_lv_items)
    public ListView mDrawerList;

    @ViewById(R.id.left_drawer)
    public View mDrawer;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private static File imageFile;
    private static String randomNumber = "";
    public static String saveFolderName;
    static String cameraPhotoImagePath = "";
    private DialogFragment mAppMessageImagePreview;
    private String[] mDrawerOptions;
    private DialogFragment mAppMessageAbout;
    private long mGeneratedImageID;
    private boolean mIsPreviewShown;
    private boolean mIsSharedShown;
    private boolean mIsCameraInit;
    private Uri mOutputFileUri;

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
        switch (resultCode) {

            case RESULT_OK:
            mOutputFileUri = Uri.fromFile(new File(Environment
                    .getExternalStorageDirectory(), cCAMERA_PICTURE_TEMP_NAME));
            boolean picFromCam = data == null;

            switch (requestCode) {
                case cCHOOSE_IMAGE_REQUEST_CODE:
                case cSELECT_FILE_RQ:
                    mIsCameraInit = false;
                    Uri selectedImageUri = Util.getSelectedImage(data, picFromCam, mOutputFileUri);
                    if(selectedImageUri != null) {
                        CropperActivity_.intent(this)
                                .pictureUri(selectedImageUri)
                                .startForResult(CROPPER_ACTIVITY_RESULT_CODE);
                    }else{
                        log.d("uri null");
                    }
                    break;
                case CROPPER_ACTIVITY_RESULT_CODE:
                    mIsCameraInit = false;
                    mGeneratedImageID = data.getLongExtra(CropperActivity.GENERATED_IMAGE_ID, 0);
                    break;
            }
            break;

            case RESULT_CANCELED:
                mIsCameraInit = false;
                break;
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
        if (!mIsCameraInit) {
            mIsCameraInit = true;
            mOutputFileUri = openImageChooserIntent(cCHOOSE_IMAGE_REQUEST_CODE,
                    cCAMERA_PICTURE_TEMP_NAME);
        }
    }

    @Override
    public void onPictureShare(String imageUri) {
        if (!mIsSharedShown) {
            mIsSharedShown = true;
            openShareIntent(imageUri);
        }
    }

    @Override
    public void onCardSelected(GeneratedImages generatedImages) {
        if (!mIsPreviewShown) {
            mIsPreviewShown = true;
            mAppMessageImagePreview = Util.getAppMessageDialog(AppMessageDialog.MessageType.IMAGE_PREVIEW,
                    generatedImages.getPath(), true);

            mAppMessageImagePreview.show(mFragmentManager, PICTURE_PREVIEW_DIALOG);

        }
    }

    @Override
    public void onPictureListLoaded(View view) {
        Button btnShow = new Button(this);
        btnShow.setVisibility(View.GONE);

        new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(findViewById(R.id.materialButton)))
                .hideOnTouchOutside()
                .replaceEndButton(btnShow)
                .setContentText(getString(R.string.create_image_showcase))
                .singleShot(SHOWCASE_ID)
                .build();
    }

    @Override
    public void onVideoPlayback(String videoId) {
        checkInternetConnection();
        if (Util.isNetworkAvailable(this)) {
            final Intent i = new Intent(this, VideoPlayerActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putString(VIDEO_URL_PLAY, videoId);
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
                        return HeaderDesign.fromColorResAndDrawable(
                                R.color.light_gold,
                                getResources().getDrawable(R.drawable.ic_etn_wide_logo));
                    case 1:
                        return HeaderDesign.fromColorResAndDrawable(
                                R.color.dark_gold,
                                getResources().getDrawable(R.drawable.ic_etn_wide_logo));
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
        share.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imageUri)));
        startActivity(Intent.createChooser(share, "Compartir Imagen"));
        mIsSharedShown = false;
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
    public void onDismiss() {
        mIsPreviewShown = false;
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
        mIsCameraInit = false;

    }

    @Override
    public void onPictureChooserDismiss() {
        mIsCameraInit = false;
    }

    @Override
    public void onPictureListFragmentReady() {
        if (mGeneratedImageID != 0) {
            final GeneratedImages generatedImage = EnTuNombre
                    .getInstance()
                    .getDaoSession()
                    .getGeneratedImagesDao()
                    .load(mGeneratedImageID);
            if (generatedImage != null) {
                try {
                    final PictureListFragment_ fragment = (PictureListFragment_) mSectionsPagerAdapter.getItem(0);
                    fragment.addItem(generatedImage, 0);
                    openShareIntent(generatedImage.getPath());
                    Snackbar.make(findViewById(android.R.id.content),
                            R.string.picture_added_snackbar,
                            Snackbar.LENGTH_LONG)
                            .show();

                    mGeneratedImageID = 0;

                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                    Snackbar.make(findViewById(android.R.id.content),
                            getString(R.string.picture_error_displayn_snackbar),
                            Snackbar.LENGTH_LONG)
                            .show();
                }
            } else {
                Snackbar.make(findViewById(android.R.id.content),
                        getString(R.string.picture_error_snackbar),
                        Snackbar.LENGTH_LONG)
                        .show();
            }
        }
    }
}
