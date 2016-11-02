package com.dulcerefugio.app.entunombre.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.UiThread;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.androidannotations.annotations.AfterExtras;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentByTag;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
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
    public Uri pictureUri;

    @FragmentByTag(CROP_PICTURE_FRAGMENT)
    CropPicture mCropPicture;

    @FragmentByTag(EDIT_PICTURE_FRAGMENT)
    EditPicture mEditPicture;
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

    @Override
    @Background
    public void onCropImage(final Bitmap croppedImage) {
        onShowWaitDialog();
        File file = new BitmapProcessor().storeImage(croppedImage);
        if (!croppedImage.isRecycled()) {
            croppedImage.recycle();
        }
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
            try {
                mSelectingFrame = true;
                mTargetFrame = new Target() {
                    @Override
                    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                        if (bitmap == null || bitmap.isRecycled())
                            return;
                        new AsyncTask<Void, Void, Bitmap>() {
                            @Override
                            protected Bitmap doInBackground(Void... params) {
                                try {
                                    mLastResult = new BitmapProcessor().mergeImages(mCroppedImage, bitmap);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    Toast.makeText(CropperActivity.this, "Ha ocurrido un error, por favor intente luego", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                System.gc();
                                return mLastResult;
                            }

                            @Override
                            protected void onPostExecute(Bitmap bitmap) {
                                super.onPostExecute(bitmap);
                                if (bitmap != null) {
                                    if (mEditPicture != null) {
                                        mEditPicture.setImageBitmap(null);
                                        System.gc();
                                        mEditPicture.showFramedImage(bitmap);

                                    }
                                    mSelectingFrame = false;
                                    mIsFrameSelected = true;
                                } else {
                                    finish();
                                    Toast.makeText(CropperActivity.this,
                                            "Ha ocurrido un error, por favor intente luego", Toast.LENGTH_LONG).show();
                                }
                                dismissWaitDialog();
                            }
                        }.execute();

                        if (mTargetFrame != null) {
                            mTargetFrame.onBitmapFailed(null);
                            Picasso.with(CropperActivity.this).cancelRequest(mTargetFrame);
                        }
                        if (mTargetCroppedImage != null) {
                            mTargetCroppedImage.onBitmapFailed(null);
                            Picasso.with(CropperActivity.this).cancelRequest(mTargetCroppedImage);
                        }
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                mTargetCroppedImage = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        if (bitmap == null || bitmap.isRecycled())
                            return;

                        mCroppedImage = bitmap;
                        Picasso.with(EnTuNombre.context).load(_frame).memoryPolicy(MemoryPolicy.NO_CACHE).into(mTargetFrame);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }

                };
                Picasso.with(EnTuNombre.context).load(new File(picturePath))
                        .memoryPolicy(MemoryPolicy.NO_CACHE).into(mTargetCroppedImage);
            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(this, "Ha ocurrido un error, por favor intente luego", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Background
    @Override
    public void onFinishEditing() {
        if (mIsFrameSelected) {
            onShowWaitDialog();
            File finalImage = mBitmapProcessor.storeImage(mLastResult);
            try {
                mBitmapProcessor.saveImageToExternal("etn"+System.currentTimeMillis(), mLastResult);
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
        } else {
            if (mAppMessageMustSelectFrame == null)
                mAppMessageMustSelectFrame = Util.getAppMessageDialog(AppMessageDialog.MessageType.MUST_SELECT_FRAME,
                        null, false);

            mAppMessageMustSelectFrame.show(mFragmentManager, MUST_SELECT_FRAME_DIALOG);
        }
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

    public File compressImage(File imageFile){
        File cachedImage = new File(getFilesDir(), imageFile.getName());
        try {

            float beforeSize = imageFile.length();

            Util.copyFile(imageFile, cachedImage);
            Util.compressImage(Uri.fromFile(cachedImage));

            float afterSize = cachedImage.length();

            if(afterSize > beforeSize){
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
