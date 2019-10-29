package com.dulcerefugio.app.etn.activities.fragments;

/**
 * Created by eperez on 7/15/15.
 */

import android.app.ActionBar;
import android.app.AlertDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.View;

public abstract class Base extends Fragment {

    //======================================================
    //                      FIELDS
    //======================================================
    private AlertDialog visibleDialog = null;

    protected View fragmentView;
    protected ActionBar actionBar;
    //======================================================
    //                    CONSTRUCTORS
    //======================================================

    //======================================================
    //                  OVERRIDEN METHODS
    //======================================================


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (visibleDialog != null && visibleDialog.isShowing()) {
                visibleDialog.dismiss();
                visibleDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Base Fragment", e.getMessage());
        }
    }

    protected void showAlertDialog(AlertDialog.Builder builder) {

    }

    //======================================================
    //                      METHODS
    //======================================================

    //======================================================
    //              INNER CLASSES/INTERFACES
    //======================================================

    //======================================================
    //                  GETTERS/SETTERS
    //======================================================
}