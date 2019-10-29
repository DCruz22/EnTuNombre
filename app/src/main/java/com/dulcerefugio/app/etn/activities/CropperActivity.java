package com.dulcerefugio.app.etn.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.UiThread;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.dulcerefugio.app.etn.EnTuNombre;
import com.dulcerefugio.app.etn.R;
import com.dulcerefugio.app.etn.activities.fragments.CropPicture;
import com.dulcerefugio.app.etn.activities.fragments.CropPicture_;
import com.dulcerefugio.app.etn.activities.fragments.EditPicture;
import com.dulcerefugio.app.etn.activities.fragments.EditPicture_;
import com.dulcerefugio.app.etn.activities.fragments.FilterPicture;
import com.dulcerefugio.app.etn.activities.fragments.FilterPicture_;
import com.dulcerefugio.app.etn.activities.fragments.dialog.AppMessageDialog;
import com.dulcerefugio.app.etn.data.dao.GeneratedImages;
import com.dulcerefugio.app.etn.logic.BitmapProcessor;
import com.dulcerefugio.app.etn.util.Util;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentByTag;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by eperez on 6/20/15.
 */
@EActivity(R.layout.a_cropper)
public class CropperActivity extends Base
        implements CropPicture.onCropPictureListener,
        EditPicture.onEditPictureListener,
        FilterPicture.onFilterPictureListener,
        AppMessageDialog.OnAppMessageDialogListener {

    public static final String PICTURE_PATH_EXTRA = "PICTURE_PATH_EXTRA";
    private static final String CROP_PICTURE_FRAGMENT = "CropPictureTag";
    private static final String EDIT_PICTURE_FRAGMENT = "EditPictureTag";
    private static final String FILTER_PICTURE_FRAGMENT = "FilterPictureTag";
    private static final String MUST_SELECT_FRAME_DIALOG = "mAppMessageMustSelectFrame";
    public static final String GENERATED_IMAGE_ID = "GENERATED_IMAGE_ID";
    private static final String ASK_EXIT_DIALOG = "ASK_EXIT_DIALOG";

    //fields
    @Extra(PICTURE_PATH_EXTRA)
    public Uri pictureUri;

    @FragmentByTag(CROP_PICTURE_FRAGMENT)
    CropPicture mCropPicture;

    @FragmentByTag(EDIT_PICTURE_FRAGMENT)
    EditPicture mEditPicture;

    @FragmentByTag(FILTER_PICTURE_FRAGMENT)
    FilterPicture mFilterPicture;

    private Bitmap mLastResult;
    private boolean mSelectingFrame;
    private boolean mIsFrameSelected;
    private DialogFragment mAppMessageMustSelectFrame;
    private String mCroppedPicturePath;
    private Bitmap mCroppedImage;
    private Target mTargetCroppedImage;
    private Target mTargetFrame;
    private BitmapProcessor mBitmapProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBitmapProcessor = new BitmapProcessor();
        showCropFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLastResult != null)
            mLastResult.recycle();
        mLastResult = null;
        //mCroppedImage.recycle();
        mCroppedImage = null;
        mBitmapProcessor = null;

        Picasso.with(this).cancelRequest(mTargetFrame);
        Picasso.with(this).cancelRequest(mTargetCroppedImage);
        System.gc();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Util.getAppMessageDialog(AppMessageDialog.MessageType.ASK_TO_EXIT, "", true)
                        .show(mFragmentManager, ASK_EXIT_DIALOG);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean isDisplayHomeAsUpEnabled() {
        return true;
    }

    @Override
    protected boolean needToolbar() {
        return true;
    }

    @Override
    public void onShowWaitDialog() {
        showWaitDialog();
    }

    private void showCropFragment() {
        mCropPicture = CropPicture_.builder().mPictureUri(pictureUri).build();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.a_cropper_fl_container, mCropPicture, CROP_PICTURE_FRAGMENT);
        fragmentTransaction.commit();
    }

    @UiThread
    private void showEditFragment() {
        dismissWaitDialog();
        mEditPicture = EditPicture_.builder().mPicturePath(mCroppedPicturePath).build();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.a_cropper_fl_container, mEditPicture, EDIT_PICTURE_FRAGMENT);
        fragmentTransaction.commit();
    }

    @UiThread
    private void showFilterFragment() {
        dismissWaitDialog();
        mFilterPicture = FilterPicture_.builder().mPicturePath(mCroppedPicturePath).build();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.a_cropper_fl_container, mFilterPicture, FILTER_PICTURE_FRAGMENT);
        fragmentTransaction.commit();
    }

    @Override
    @Background
    public void onCropImage(final Bitmap croppedImage) {
        onShowWaitDialog();
        File file = new BitmapProcessor().storeImage(croppedImage);
        if (!croppedImage.isRecycled()) {
            croppedImage.recycle();
        }
        mCroppedPicturePath = file.getPath();
        showFilterFragment();
    }

    @Override
    @Background
    public void onFilterImage(final Bitmap filteredImage) {
        onShowWaitDialog();
        File file = new BitmapProcessor().storeImage(filteredImage);
        if (!filteredImage.isRecycled()) {
            filteredImage.recycle();
        }
        mCroppedPicturePath = file.getPath();
        showEditFragment();
    }

    @Override
    public void onFilterCancel() {
        finishActivity(0L, RESULT_CANCELED);
    }

    @Override
    public void onCropCancel() {
        finishActivity(0L, RESULT_CANCELED);
    }

    @Background
    @Override
    public void onFinishEditing(ViewGroup vgFinalPicture) {
        onShowWaitDialog();
        Bitmap bitmap = Bitmap.createBitmap(vgFinalPicture.getWidth(), vgFinalPicture.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vgFinalPicture.draw(canvas);
        mLastResult = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        File finalImage = mBitmapProcessor.storeImage(bitmap);
        try {
            mBitmapProcessor.saveImageToExternal("etn" + System.currentTimeMillis(), mLastResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //persisting picture path
        GeneratedImages generatedImage = new GeneratedImages();
        generatedImage.setPath(finalImage.getAbsolutePath());
        generatedImage.setDate(Util.parseDateString(new Date()));
        EnTuNombre.getInstance()
                .getDaoSession()
                .getGeneratedImagesDao()
                .insertOrReplaceInTx(generatedImage);
        finishActivity(generatedImage.getId(), RESULT_OK);
    }

    @UiThread
    public void finishActivity(Long id, int result) {
        deleteLastPicture();
        switch (result) {
            case RESULT_OK:
                Intent i = new Intent();
                i.putExtra(GENERATED_IMAGE_ID, id);
                setResult(RESULT_OK, i);
                break;
            case RESULT_CANCELED:
                setResult(result, new Intent());
        }
        finish();
    }

    public File compressImage(File imageFile) {
        File cachedImage = new File(getFilesDir(), imageFile.getName());
        try {

            float beforeSize = imageFile.length();

            Util.copyFile(imageFile, cachedImage);
            Util.compressImage(Uri.fromFile(cachedImage));

            float afterSize = cachedImage.length();

            if (afterSize > beforeSize) {
                Util.copyFile(imageFile, cachedImage);
            }
        } catch (IOException e) {
            cachedImage = imageFile;
        }
        return cachedImage;
    }

    @Override
    public void onPreviewDialogShare(String ImageUri) {

    }

    @Override
    public void onPositiveButton() {
        Logger.d("0");
        finishActivity(0L, RESULT_CANCELED);
    }

    @Override
    public void onDismiss() {

    }

    @Background
    public void deleteLastPicture() {
        //BitmapProcessor.deleteLastPhotoTaken();
    }
}
