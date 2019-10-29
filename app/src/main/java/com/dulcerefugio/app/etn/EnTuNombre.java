package com.dulcerefugio.app.etn;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.crashlytics.android.Crashlytics;
import com.dulcerefugio.app.etn.data.dao.DaoMaster;
import com.dulcerefugio.app.etn.data.dao.DaoSession;
import com.dulcerefugio.app.etn.data.dao.openhelpers.EnTuNombreOpenHelper;
import com.dulcerefugio.app.etn.logic.Preferences;

import io.fabric.sdk.android.Fabric;

public class EnTuNombre extends Application{

    //======================================================================================
    //PROPERTIES
    //======================================================================================
    private static EnTuNombre instance;
    private static DaoSession daoSession;
    public static volatile Context context;
    private Preferences mPreferences;

    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    //======================================================================================
    //OVERRIDEN METHODS
    //======================================================================================
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        context = getApplicationContext();
        mPreferences = Preferences.getInstance();
        setupDatabase();
    }

    //======================================================================================
    //METHODS
    //======================================================================================

    private void setupDatabase(){
        SQLiteOpenHelper helper = new EnTuNombreOpenHelper(context, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    //======================================================================================
    //Getters and Setters
    //======================================================================================

    public DaoSession getDaoSession(){
        return daoSession;
    }

    public Preferences getmPreferences() {
        return mPreferences;
    }

    //======================================================================================
    //CONSTRUCTORS
    //======================================================================================
    public EnTuNombre() {
        super();
        instance = this;
    }



    public static EnTuNombre getInstance() {
        return (instance == null) ? instance = new EnTuNombre() : instance;
    }
}
