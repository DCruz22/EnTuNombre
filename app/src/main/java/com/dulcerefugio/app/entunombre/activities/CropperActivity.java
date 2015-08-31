package com.dulcerefugio.app.entunombre.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.UiThread;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.dulcerefugio.app.entunombre.EnTuNombre;
import com.dulcerefugio.app.entunombre.R;
import com.dulcerefugio.app.entunombre.activities.fragments.CropPicture;
import com.dulcerefugio.app.entunombre.activities.fragments.CropPicture_;
import com.dulcerefugio.app.entunombre.activities.fragments.EditPicture;
import com.dulcerefugio.app.entunombre.activities.fragments.EditPicture_;
import com.dulcerefugio.app.entunombre.activities.fragments.dialog.AppMessageDialog;
import com.dulcerefugio.app.entunombre.data.dao.GeneratedImages;
import com.dulcerefugio.app.entunombre.logic.BitmapProcessor;
import com.dulcerefugio.app.entunombre.util.Util;
import com.orhanobut.logger.Logger;

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
        AppMessageDialog.OnAppMessageDialogListener {

    public static final String PICTURE_PATH_EXTRA = "PICTURE_PATH_EXTRA";
    private static final String CROP_PICTURE_FRAGMENT = "CropPictureTag";
    private static final String EDIT_PICTURE_FRAGMENT = "EditPictureTag";
    private static final String MUST_SELECT_FRAME_DIALOG = "mAppMessageMustSelectFrame";
    public static final String GENERATED_IMAGE_ID = "GENERATED_IMAGE_ID";
    private static final String ASK_EXIT_DIALOG = "ASK_EXIT_DIALOG";

    //fields
    @Extra(PICTURE_PATH_EXTRA)
    public String mPicturePath;
    private BitmapProcessor mBitmapProcessor;

    @FragmentByTag(CROP_PICTURE_FRAGMENT)
    CropPicture mCropPicture;

    @FragmentByTag(EDIT_PICTURE_FRAGMENT)
    EditPicture mEditPicture;
    private Bitmap mLastResult;
    private boolean mSelectingFrame;
    private boolean mIsFrameSelected;
    private DialogFragment mAppMessageMustSelectFrame;
    private String mCroppedPicturePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBitmapProcessor = BitmapProcessor.getInstance(this);
        showCropFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Logger.d("0");
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
        mCropPicture = CropPicture_.builder().mPicturePath(mPicturePath).build();
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

    @Override
    @Background
    public void onCropImage(final Bitmap croppedImage, final Bitmap frame) {
        onShowWaitDialog();
        File file = mBitmapProcessor.storeImage(croppedImage);
        mCroppedPicturePath = file.getPath();
        showEditFragment();
    }

    @Override
    public void onCropCancel() {
        finishActivity(0L, RESULT_CANCELED);
    }

    @Override
    public void onFrameSelected(final String picturePath, final int _frame) {
        if (!mSelectingFrame) {
            mSelectingFrame = true;
            mBitmapProcessor.processImage(new BitmapProcessor.OnImageProcess() {
                @Override
                public Bitmap onBackgroundProcess() {
                    Uri uri = Uri.fromFile(new File(picturePath));
                    Bitmap croppedImage = null;
                    try {
                        croppedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    final Bitmap frame = BitmapFactory.decodeResource(getResources(), _frame);

                    return mLastResult = mBitmapProcessor.mergeImages(croppedImage, frame);
                }

                @Override
                public void onPostExecute(Bitmap bitmap) {
                    if (mEditPicture != null)
                        mEditPicture.showFramedImage(bitmap);
                    System.gc();
                    mSelectingFrame = false;
                    mIsFrameSelected = true;
                    dismissWaitDialog();
                }
            });
        }
    }

    @Background
    @Override
    public void onFinishEditing() {
        Logger.d("0");
        if (mIsFrameSelected) {
            onShowWaitDialog();
            File finalImage = mBitmapProcessor.storeImage(mLastResult);

            //persisting picture path
            GeneratedImages generatedImage = new GeneratedImages();
            generatedImage.setPath(finalImage.getAbsolutePath());
            generatedImage.setDate(Util.parseDateString(new Date()));
            EnTuNombre.getInstance()
                    .getDaoSession()
                    .getGeneratedImagesDao()
                    .insertOrReplaceInTx(generatedImage);
            finishActivity(generatedImage.getId(), RESULT_OK);
        } else {
            if (mAppMessageMustSelectFrame == null)
                mAppMessageMustSelectFrame = Util.getAppMessageDialog(AppMessageDialog.MessageType.MUST_SELECT_FRAME, null, false);

            mAppMessageMustSelectFrame.show(mFragmentManager, MUST_SELECT_FRAME_DIALOG);
        }
    }

    @UiThread
    public void finishActivity(Long id, int result) {
        Logger.d("0");
        deleteLastPicture();
        switch(result){
            case RESULT_OK:
                Intent i = new Intent();
                i.putExtra(GENERATED_IMAGE_ID, id);
                setResult(RESULT_OK, i);
                break;
            case RESULT_CANCELED:
                setResult(result,new Intent());
        }

        finish();
    }

    @Override
    public void onPreviewDialogShare(String ImageUri) {

    }

    @Override
    public void onPositiveButton() {
        Logger.d("0");
        finishActivity(0L, RESULT_CANCELED);
    }

    @Background
    public void deleteLastPicture() {
        mBitmapProcessor.deleteLastPhotoTaken();
    }
}
