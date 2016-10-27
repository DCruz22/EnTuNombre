package com.dulcerefugio.app.entunombre.logic;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.dulcerefugio.app.entunombre.EnTuNombre;

public class BitmapProcessor {

    private final String TAG = "BitmapProcessor";

    public Bitmap mergeImages(Bitmap bottomImage, Bitmap topImage) {
        Log.d(TAG, "bottom image height: " + bottomImage.getHeight());
        Log.d(TAG, "bottom image width: " + bottomImage.getWidth());

        Bitmap resizedbitmap = Bitmap.createScaledBitmap(bottomImage, topImage.getWidth(), topImage.getHeight(), true);

        if (!bottomImage.isRecycled()) {
            bottomImage = null;
            System.gc();
        }

        final Bitmap output = Bitmap.createBitmap(topImage.getWidth(),
                topImage.getHeight(),
                Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        canvas.drawBitmap(resizedbitmap, 26, 10, paint);
        canvas.drawBitmap(topImage, 0, 0, paint);

        if (!resizedbitmap.isRecycled()) {
            resizedbitmap.recycle();
            resizedbitmap=null;
        }
        System.gc();

        return output;
    }

    public File storeImage(final Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d("", "Error creating media file, check storage permissions: ");
            return null;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
            image.recycle();
            return pictureFile;
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
        return null;
    }

    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        String path = getSavePath();
        Log.d("pathsave", path);
        File mediaStorageDir = new File(path);

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName = "MI_" + timeStamp + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    public static String getSavePath() {
        return Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + EnTuNombre.getInstance().getApplicationContext().getPackageName()
                + "/Files";
    }

    /**
     * Added this method for a specific workaround implementation for
     * CropImageView in some devices
     *
     * @param mCurrentPhotoPath
     * @return
     */
    public static Bitmap decodeFile(String mCurrentPhotoPath) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inPurgeable = true;
        return BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
    }

    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                  int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        Bitmap bitmap = BitmapFactory.decodeResource(res, resId, options);

        if (bitmap == null)
            Log.d(TAG, "bitmap null");

        return bitmap;
    }

    public Bitmap decodeSampledBitmapFromFile(File f, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, null);

            return Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, true);

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            System.gc();

            return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            return null;
        }
    }

    public Bitmap decodeSampledBitmapFromBitmap(Bitmap bitmap, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        try {

            return Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, true);

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            System.gc();

            return null;
        }
    }

    public void processImage(final OnImageProcess onImageProcess) {
        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                return onImageProcess.onBackgroundProcess();
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                onImageProcess.onPostExecute(bitmap);
            }
        }.execute();
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static void deleteLastPhotoTaken() {

        String[] projection = new String[] {
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE };

        final Cursor cursor = EnTuNombre.context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                null,null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

        if (cursor != null) {
            cursor.moveToFirst();

            int column_index_data =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            String image_path = cursor.getString(column_index_data);

            File file = new File(image_path);
            if (file.exists()) {
                file.delete();
            }
            cursor.close();
        }
    }
    public interface OnImageProcess {
        Bitmap onBackgroundProcess();

        void onPostExecute(Bitmap bitmap);
    }
}
