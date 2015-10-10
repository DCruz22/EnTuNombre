package com.dulcerefugio.app.entunombre.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.dulcerefugio.app.entunombre.EnTuNombre;
import com.dulcerefugio.app.entunombre.activities.fragments.dialog.AppMessageDialog;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by eperez on 10/13/14.
 */
public class Util {
    private static final String TAG = "Util";

    public static boolean isNetworkAvailable(Context mContext) {

        if(mContext ==null)
            Log.e("Util", "mContext is null");

        ConnectivityManager cm = (ConnectivityManager) EnTuNombre.getInstance()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.e("Util", "connected");
            return true;
        }
        Log.e("Util", "not Connected");
        return false;
    }

    public static String nextSessionId() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    public static DialogFragment getAppMessageDialog(AppMessageDialog.MessageType messageType, String imageUri,
                                                     boolean cancelable){
        Bundle args = new Bundle();
        args.putParcelable(AppMessageDialog.cBUNDLE_ARG_MESSAGE_TYPE, messageType);
        if(imageUri != null)
            args.putString(AppMessageDialog.cBUNDLE_ARG_IMAGE_URI, imageUri);
        AppMessageDialog dialog = new AppMessageDialog();
        dialog.setArguments(args);
        dialog.setCancelable(cancelable);

        return dialog;
    }

    public static String parseDateString(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    public static Date parseStringToDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Intent getRateUsIntent(){
        final String appPackageName = EnTuNombre.context.getPackageName(); // getPackageName() from Context or Activity object
        try {
            return (new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        }
        catch (android.content.ActivityNotFoundException anfe) {
            return (new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }



    public static String getPath(Uri uri) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = EnTuNombre.context.getContentResolver().query(uri,
                filePathColumn, null, null, null);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
        }else{
            return "";
        }

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        return picturePath;
    }
}
