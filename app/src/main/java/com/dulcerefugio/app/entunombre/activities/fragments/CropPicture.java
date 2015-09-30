package com.dulcerefugio.app.entunombre.activities.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;

import com.dulcerefugio.app.entunombre.R;
import com.dulcerefugio.app.entunombre.logic.BitmapProcessor;
import com.edmodo.cropper.CropImageView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;

/**
 * Created by eperez on 8/15/15.
 */

@EFragment(R.layout.f_cropper)
public class CropPicture extends Fragment {

    //======================================================
    //                      CONSTANTS
    //======================================================
    private static final int DEFAULT_ASPECT_RATIO_VALUES = 20;
    public static final String PICTURE_PATH_EXTRA = "PICTURE_PATH_EXTRA";

    //======================================================
    //                      FIELDS
    //======================================================
    private Context mContext;
    private onCropPictureListener mListener;
    @InstanceState
    public int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;
    @InstanceState
    public int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;
    @FragmentArg(PICTURE_PATH_EXTRA)
    public String mPicturePath;
    @ViewById(R.id.f_cropper_civ_crop)
    public CropImageView mCropImageView;
    @ViewById(R.id.f_cropper_iv_cancel)
    public ImageView mIvCancel;
    @ViewById(R.id.f_cropper_iv_crop)
    public ImageView mIvCrop;
    private boolean mIsImageCrop;

    //======================================================
    //                    CONSTRUCTORS
    //======================================================

    //======================================================
    //                  OVERRIDEN METHODS
    //======================================================
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mContext = getActivity();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (onCropPictureListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onCropPictureListener");
        }
    }

    //======================================================
    //                      METHODS
    //======================================================

    @AfterViews
    public void initialize() {
        mCropImageView.setFixedAspectRatio(true);

        mCropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES);

        mIvCrop.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mIsImageCrop) {
                            mIsImageCrop = true;
                            mListener.onCropImage(mCropImageView.getCroppedImage());
                        }
                    }
                });
        mIvCancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onCropCancel();
                    }
                });

        if (mPicturePath != null) {
            try {
                Uri uri = Uri.fromFile(new File(mPicturePath));
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
                mCropImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface onCropPictureListener {
        void onCropImage(Bitmap croppedImage);

        void onCropCancel();
    }
}
