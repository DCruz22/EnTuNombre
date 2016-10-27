package com.dulcerefugio.app.entunombre.activities.fragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.dulcerefugio.app.entunombre.EnTuNombre;
import com.dulcerefugio.app.entunombre.R;
import com.dulcerefugio.app.entunombre.logic.BitmapProcessor;
import com.dulcerefugio.app.entunombre.util.Util;
import com.edmodo.cropper.CropImageView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

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
    private final String[] SHARE_APPS = {
            "content://com.google.android.apps.photos.content",
            "content://com.android.providers.media.documents"};

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
    public Uri mPictureUri;
    @ViewById(R.id.f_cropper_civ_crop)
    public CropImageView mCropImageView;
    @ViewById(R.id.f_cropper_iv_cancel)
    public ImageView mIvCancel;
    @ViewById(R.id.f_cropper_iv_crop)
    public ImageView mIvCrop;
    @ViewById(R.id.f_cropper_iv_rotate)
    public ImageView mIvRotate;
    private boolean mIsImageCrop;
    private Bitmap mBitmap;

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mCropImageView!=null){
            mCropImageView.setImageBitmap(null);
            mBitmap.recycle();
            mBitmap = null;
            System.gc();
        }
    }

    //======================================================
    //                      METHODS
    //======================================================

    @AfterViews
    public void initialize() {
        mIvCrop.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mIsImageCrop) {
                            mIsImageCrop = true;
                            Bitmap bitmap = mCropImageView.getCroppedImage();
                            mCropImageView.setImageBitmap(null);
                            mListener.onCropImage(bitmap);
                            bitmap = null;
                            System.gc();
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

        mIvRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCropImageView.rotateImage(90);
            }
        });

        /*if (mPicturePath != null) {
            Uri uri = Uri.fromFile(new File(mPicturePath));
            Util.compressImage(uri);
            mBitmap = fromGallery(uri);
            mCropImageView.setImageBitmap(mBitmap);
            mCropImageView.requestLayout();
        }else{
            Toast.makeText(getActivity(), "No se puede mostrar esta imagen", Toast.LENGTH_LONG).show();
            getActivity().finish();
        }*/
        
        if (mPictureUri.toString().startsWith("content://com.sec.android.gallery3d.provider")) {
            String[] FILE = { MediaStore.Images.Media.DATA };
            Cursor cursor = EnTuNombre.context.getContentResolver().query(mPictureUri,
                    FILE, null, null, null);

            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(FILE[0]);
            //Get link for download
            String ImageDecode = cursor.getString(columnIndex);
            cursor.close();

            DownloadFileTask downloadTask = new DownloadFileTask();
            downloadTask.execute(ImageDecode);

        } else if(mPictureUri.toString().startsWith(SHARE_APPS[0]) ||
                mPictureUri.toString().startsWith(SHARE_APPS[1])) {
            //Photos or Documents
            InputStream is = null;
            try {
                is = EnTuNombre.context.getContentResolver().openInputStream(mPictureUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            mBitmap = BitmapFactory.decodeStream(is);
        } else {
            File imageFile = new File(Util.getPath(mPictureUri));
            File cachedImage = mListener.compressImage(imageFile);
            mBitmap = BitmapProcessor.decodeFile(Util.getPath(Uri.fromFile(cachedImage)));
        }

        if(mBitmap != null){
            setupCropper(mBitmap);
        }
    }

    public void setupCropper(Bitmap bitmap) {
        mCropImageView.setImageBitmap(bitmap);
        mCropImageView.requestLayout();
        mCropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES);
        mCropImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mCropImageView.post(new Runnable() {
            @Override
            public void run() {
                mCropImageView.setFixedAspectRatio(true);
                mCropImageView.setVisibility(View.GONE);
                mCropImageView.setVisibility(View.VISIBLE);
            }
        });
    }

    private Bitmap fromGallery(final Uri selectedImageUri) {
        try {
            Bitmap bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);

            ExifInterface exif = new ExifInterface(selectedImageUri.getPath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            int angle = 0;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    angle = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    angle = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    angle = 270;
                    break;
                default:
                    angle = 0;
                    break;
            }
            Matrix mat = new Matrix();
            if (angle == 0 && bm.getWidth() > bm.getHeight())
                mat.postRotate(90);
            else
                mat.postRotate(angle);

            return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), mat, true);

        } catch (IOException e) {
            Log.e("", "-- Error in setting image");
        } catch (OutOfMemoryError oom) {
            Log.e("", "-- OOM Error in setting image");
        }
        return null;
    }

    public interface onCropPictureListener {
        void onCropImage(Bitmap croppedImage);

        void onCropCancel();

        File compressImage(File imageFile);
    }

    public class DownloadFileTask extends AsyncTask<String, Void, Bitmap> {

        public DownloadFileTask() {
            //empty
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL imageUrl = new URL(params[0]);
                return BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());

            } catch (MalformedURLException mue) {
                Log.e("SYNC getUpdate", "malformed url error", mue);
            } catch (IOException ioe) {
                Log.e("SYNC getUpdate", "io error", ioe);
            } catch (SecurityException se) {
                Log.e("SYNC getUpdate", "security error", se);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            mBitmap = result;
            if(mBitmap != null){
                setupCropper(mBitmap);
            }
        }
    }
}
