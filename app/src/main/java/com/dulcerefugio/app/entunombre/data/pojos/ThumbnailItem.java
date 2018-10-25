package com.dulcerefugio.app.entunombre.data.pojos;


import android.graphics.Bitmap;

import com.zomato.photofilters.imageprocessors.Filter;

public class ThumbnailItem {
    public Bitmap image;
    public Filter filter;

    public ThumbnailItem() {
        image = null;
        filter = new Filter();
    }
}