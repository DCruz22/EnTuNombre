package com.dulcerefugio.app.etn.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.dulcerefugio.app.etn.R;

/**
 * Created by eperez on 8/25/15.
 */
public class CustomShareButton extends LinearLayout {
    public CustomShareButton(Context context) {
        super(context);
    }

    public CustomShareButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(
                R.layout.custom_share_button, this);
        setOrientation(HORIZONTAL);
    }
}
