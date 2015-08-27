package com.dulcerefugio.app.entunombre.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.dulcerefugio.app.entunombre.R;
import com.dulcerefugio.app.entunombre.util.Util;
import com.orhanobut.logger.Logger;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

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
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
            }
        }
    }

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

    protected abstract boolean needToolbar();
}
