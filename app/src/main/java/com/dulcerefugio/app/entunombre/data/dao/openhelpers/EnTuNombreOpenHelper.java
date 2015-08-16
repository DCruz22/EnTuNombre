package com.dulcerefugio.app.entunombre.data.dao.openhelpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.dulcerefugio.app.entunombre.data.dao.DaoMaster;

/**
 * Created by euriperez16 on 2/17/2015.
 */

public class EnTuNombreOpenHelper extends DaoMaster.OpenHelper {

    //======================================================
    //                      FIELDS
    //======================================================
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "EnTuNombre";
    private SQLiteDatabase db;

    //======================================================
    //                    CONSTRUCTORS
    //======================================================
    public EnTuNombreOpenHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory);
        db = getWritableDatabase();
    }

    //======================================================
    //                  OVERRIDEN METHODS
    //======================================================


    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        upgradeDatabase(db, oldVersion, newVersion);
    }

    //======================================================
    //                      METHODS
    //======================================================

    private void upgradeDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {

        switch (oldVersion) {
            //SWITCH BETWEEN VERSION CHANGES
            default:
                return;
        }
    }

    //======================================================
    //              INNER CLASSES/INTERFACES
    //======================================================

    //======================================================
    //                  GETTERS/SETTERS
    //======================================================
}