package com.dulcerefugio.app.entunombre.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.dulcerefugio.app.entunombre.EnTuNombre;
import com.dulcerefugio.app.entunombre.logic.BitmapProcessor;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;

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
}
