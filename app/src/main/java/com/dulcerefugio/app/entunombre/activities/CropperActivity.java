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
        EditPicture.onEditPictureListener {

    public static final String PICTURE_PATH_EXTRA = "PICTURE_PATH_EXTRA";
    private static final String CROP_PICTURE_FRAGMENT = "CropPictureTag";
    private static final String EDIT_PICTURE_FRAGMENT = "EditPictureTag";
    private static final String FRAME_WAIT_DIALOG = "FRAME_WAIT_DIALOG";
    private static final String MUST_SELECT_FRAME_DIALOG="mAppMessageMustSelectFrame";
    public static final String GENERATED_IMAGE_ID = "GENERATED_IMAGE_ID";

    //fields
    @Extra(PICTURE_PATH_EXTRA)
    public String mPicturePath;
    private BitmapProcessor mBitmapProcessor;

    @FragmentByTag(CROP_PICTURE_FRAGMENT)
    CropPicture mCropPicture;

    @FragmentByTag(EDIT_PICTURE_FRAGMENT)
    EditPicture mEditPicture;
    private Bitmap mLastResult;
    private DialogFragment mAppMessageWait;
    private boolean mSelectingFrame;
    private boolean mIsFrameSelected;
    private DialogFragment mAppMessageMustSelectFrame;
    private String mCroppedPicturePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialize();
        showUI();
    }

    @Override
    protected boolean needToolbar() {
        return true;
    }

    private void showUI() {
        showCropFragment();
    }

    private void showCropFragment() {
        mCropPicture = CropPicture_.builder().mPicturePath(mPicturePath).build();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.a_cropper_fl_container, mCropPicture, CROP_PICTURE_FRAGMENT);
        fragmentTransaction.commit();
    }

    @UiThread
    private void showEditFragment() {
        if(mAppMessageWait!=null)
            mAppMessageWait.dismiss();
        mEditPicture = EditPicture_.builder().mPicturePath(mCroppedPicturePath).build();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.a_cropper_fl_container, mEditPicture, EDIT_PICTURE_FRAGMENT);
        fragmentTransaction.commit();
    }

    private void initialize() {
        // Initialize components of the app
        mBitmapProcessor = BitmapProcessor.getInstance(this);
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
        finish();
    }

    @UiThread
    @Override
    public void onShowWaitDialog() {
        if(mAppMessageWait == null)
            mAppMessageWait = Util.getAppMessageDialog(AppMessageDialog.MessageType.PLEASE_WAIT, null, false);

        mAppMessageWait.show(mFragmentManager, FRAME_WAIT_DIALOG);
    }

    @Override
    public void onFrameSelected(final String picturePath, final int _frame) {
        if(!mSelectingFrame) {
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
                    if(mAppMessageWait != null)
                        mAppMessageWait.dismiss();
                }
            });
        }
    }

    @Background
    @Override
    public void onFinishEditing() {
        if(mIsFrameSelected) {
            File takenPicture = new File(mPicturePath);
            onShowWaitDialog();
            File finalImage = mBitmapProcessor.storeImage(mLastResult);
            Logger.d(finalImage.getAbsolutePath());

            //persisting picture path
            GeneratedImages generatedImage = new GeneratedImages();
            generatedImage.setPath(finalImage.getAbsolutePath());
            generatedImage.setDate(Util.parseDateString(new Date()));
            EnTuNombre.getInstance()
                    .getDaoSession()
                    .getGeneratedImagesDao()
                    .insertOrReplaceInTx(generatedImage);
            Logger.d(takenPicture.getPath() + " : " + takenPicture.exists());
            Logger.d(takenPicture.delete() + "");
            mBitmapProcessor.deleteLastPhotoTaken();
            finishActivity(generatedImage.getId());
        }else{
            if(mAppMessageMustSelectFrame == null)
                mAppMessageMustSelectFrame = Util.getAppMessageDialog(AppMessageDialog.MessageType.MUST_SELECT_FRAME, null, false);

            mAppMessageMustSelectFrame.show(mFragmentManager, MUST_SELECT_FRAME_DIALOG);
        }
    }

    @UiThread
    public void finishActivity(Long id) {
        Intent i = new Intent();
        i.putExtra(GENERATED_IMAGE_ID, id);
        setResult(RESULT_OK, i);
        finish();
    }
}
