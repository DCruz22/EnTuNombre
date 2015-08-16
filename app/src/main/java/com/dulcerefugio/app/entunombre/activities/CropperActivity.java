package com.dulcerefugio.app.entunombre.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.dulcerefugio.app.entunombre.EnTuNombre;
import com.dulcerefugio.app.entunombre.R;
import com.dulcerefugio.app.entunombre.activities.fragments.CropPicture;
import com.dulcerefugio.app.entunombre.activities.fragments.CropPicture_;
import com.dulcerefugio.app.entunombre.activities.fragments.EditPicture;
import com.dulcerefugio.app.entunombre.activities.fragments.EditPicture_;
import com.dulcerefugio.app.entunombre.data.dao.GeneratedImages;
import com.dulcerefugio.app.entunombre.logic.BitmapProcessor;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentByTag;

import java.io.File;
import java.util.Date;

/**
 * Created by eperez on 6/20/15.
 */
@EActivity(R.layout.a_cropper)
public class CropperActivity extends FragmentActivity
        implements CropPicture.onCropPictureListener,
EditPicture.onEditPictureListener{

    public static final String PICTURE_PATH_EXTRA = "PICTURE_PATH_EXTRA";
    private static final String TAG = "CROPPER_ACTIVITY";
    private static final String CROP_PICTURE_FRAGMENT = "CropPictureTag";
    private static final String EDIT_PICTURE_FRAGMENT = "EditPictureTag";

    //fields
    @Extra(PICTURE_PATH_EXTRA)
    public String mPicturePath;
    private BitmapProcessor mBitmapProcessor;

    @FragmentByTag(CROP_PICTURE_FRAGMENT)
    CropPicture mCropPicture;

    @FragmentByTag(EDIT_PICTURE_FRAGMENT)
    EditPicture mEditPicture;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialize();
        showUI();
    }

    private void showUI() {
        showCropFragment();
    }

    private void showCropFragment(){
        mCropPicture = new CropPicture_().builder().mPicturePath(mPicturePath).build();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.a_cropper_fl_container, mCropPicture, CROP_PICTURE_FRAGMENT);
        fragmentTransaction.commit();
    }

    private void showEditFragment(){
        mEditPicture = new EditPicture_().builder().mPicturePath(mPicturePath).build();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.a_cropper_fl_container, mEditPicture, CROP_PICTURE_FRAGMENT);
        fragmentTransaction.commit();
    }

    private void initialize() {
        // Initialize components of the app
        mBitmapProcessor = BitmapProcessor.getInstance(this);
    }

    @Override
    public void onCropImage(final Bitmap croppedImage, final Bitmap frame) {
        showEditFragment();
    }

    @Override
    public void onCropCancel() {
        finish();
    }

    @Override
    public void onFrameSelected(final Bitmap croppedImage, final int _frame) {
        final Bitmap frame = BitmapFactory.decodeResource(getResources(),
                _frame);
        mBitmapProcessor.processImage(new BitmapProcessor.OnImageProcess() {
            @Override
            public Bitmap onBackgroundProcess() {
                Log.d(TAG, croppedImage.getWidth() + " pic WIDTH");
                Log.d(TAG, croppedImage.getHeight() + " pic HEIGHT");
                Log.d(TAG, frame.getWidth() + " fr WIDTH");
                Log.d(TAG, frame.getHeight() + " fr HEIGHT");
                Bitmap result = mBitmapProcessor.mergeImages(croppedImage, frame);
                File finalImage = mBitmapProcessor.storeImage(result);
                Log.d(TAG, finalImage.getAbsolutePath());

                //TODO: persist picture path
                GeneratedImages generatedImage = new GeneratedImages();
                generatedImage.setPath(finalImage.getAbsolutePath());
                generatedImage.setDate(new Date().toString());
                EnTuNombre.getInstance()
                        .getDaoSession()
                        .getGeneratedImagesDao()
                        .insertOrReplaceInTx(generatedImage);

                return result;
            }

            @Override
            public void onPostExecute(Bitmap bitmap) {
                Toast.makeText(CropperActivity.this, "", Toast.LENGTH_LONG).show();
                //croppedImageView.setImageBitmap(bitmap);
                //croppedImageView.setVisibility(View.VISIBLE);
                //TODO: show image
                if(mEditPicture!=null)
                    mEditPicture.showFramedImage(bitmap);
                System.gc();
            }
        });
    }
}
