package com.dulcerefugio.app.entunombre.activities.fragments.listeners;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by eperez on 7/15/15.
 */
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

    //======================================================
    //                      FIELDS
    //======================================================
    private ArrayList<OnItemClickListener> mListeners = new ArrayList<OnItemClickListener>();
    private GestureDetector mGestureDetector;

    //======================================================
    //                    CONSTRUCTORS
    //======================================================
    public RecyclerItemClickListener(Context context) {
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }
    //======================================================
    //                  OVERRIDDEN METHODS
    //======================================================

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View childView = rv.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListeners != null && mGestureDetector.onTouchEvent(e)) {
            for (OnItemClickListener mListener : mListeners) {
                mListener.onRecyclerItemClick(rv, rv.getChildPosition(childView));
            }
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    //======================================================
    //                      METHODS
    //======================================================

    //======================================================
    //              INNER CLASSES/INTERFACES
    //======================================================
    public interface OnItemClickListener {
        void onRecyclerItemClick(View view, int position);
    }
    //======================================================
    //                  GETTERS/SETTERS
    //======================================================

    public void addOnItemClickListener(OnItemClickListener listener) {
        mListeners.add(listener);
    }
}