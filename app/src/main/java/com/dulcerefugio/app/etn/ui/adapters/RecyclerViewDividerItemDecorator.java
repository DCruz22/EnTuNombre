package com.dulcerefugio.app.etn.ui.adapters;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.dulcerefugio.app.etn.R;

/**
 * Created by eperez on 7/15/15.
 */

public class RecyclerViewDividerItemDecorator extends RecyclerView.ItemDecoration {

    //======================================================
    //                      FIELDS
    //======================================================
    private Drawable mDivider;

    //======================================================
    //                    CONSTRUCTORS
    //======================================================
    public RecyclerViewDividerItemDecorator(Context context) {
        mDivider = context.getResources().getDrawable(R.drawable.rv_horizontal_divider);
    }
    //======================================================
    //                  OVERRIDDEN METHODS
    //======================================================

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
        super.onDrawOver(c, parent, state);
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
