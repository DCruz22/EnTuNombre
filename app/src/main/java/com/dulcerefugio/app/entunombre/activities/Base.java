package com.dulcerefugio.app.entunombre.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.UiThread;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.dulcerefugio.app.entunombre.R;
import com.dulcerefugio.app.entunombre.activities.fragments.dialog.AppMessageDialog;
import com.dulcerefugio.app.entunombre.util.Util;
import com.orhanobut.logger.Logger;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;

/**
 * Created by eperez on 7/23/15.
 */
@EActivity
public abstract class Base extends AppCompatActivity {

    //======================================================
    //                      FIELDS
    //======================================================
    @ViewById(R.id.toolbar)
    protected Toolbar mToolBar;
    protected FragmentManager mFragmentManager;
    private DialogFragment mAppMessageWait;
    private static final String FRAME_WAIT_DIALOG = "FRAME_WAIT_DIALOG";
    //======================================================
    //                    CONSTRUCTORS
    //======================================================

    //======================================================
    //                  OVERRIDEN METHODS
    //======================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(needToolbar()+"");

        if (mFragmentManager == null){
            mFragmentManager = getSupportFragmentManager();
        }
    }

    @AfterViews
    public void init(){
        if(needToolbar()){
            mToolBar = (Toolbar) findViewById(R.id.toolbar);
            Logger.d((mToolBar == null)+"");
            if( mToolBar != null) {
                Logger.d("0");
                setSupportActionBar(mToolBar);

                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(isDisplayHomeAsUpEnabled());
                }
            }
        }
    }

    @UiThread
    protected void showWaitDialog() {
        if (mAppMessageWait == null)
            mAppMessageWait = Util.getAppMessageDialog(AppMessageDialog.MessageType.PLEASE_WAIT, null, false);

        Log.d("CropperActivity", "showing");
        mAppMessageWait.show(mFragmentManager, FRAME_WAIT_DIALOG);
    }

    @UiThread
    protected void dismissWaitDialog(){
        Log.d("Base", "dismissing0");
        if (mAppMessageWait != null) {
            mAppMessageWait.dismiss();

            Log.d("Base", "dismissing2");
            return;
        }

        Log.d("Base","dismissing3");
    }

    protected abstract boolean isDisplayHomeAsUpEnabled();

    protected void checkInternetConnection() {
        if(!Util.isNetworkAvailable(this)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false)
                    .setMessage("No esta conectado a la internet")
                    .setPositiveButton("Reintentar",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            checkInternetConnection();
                        }
                    }).show();
        }
    }

    protected Uri openImageChooserIntent(int requestCode, String imageName) {
        final File outputFile = new File(Environment
                .getExternalStorageDirectory(), imageName);
        Uri outputFileUri = Uri.fromFile(outputFile);
        Intent intent = Util.getImageChooserIntent(this, outputFile);
        startActivityForResult(intent, requestCode);
        return outputFileUri;
    }

    protected abstract boolean needToolbar();
}
