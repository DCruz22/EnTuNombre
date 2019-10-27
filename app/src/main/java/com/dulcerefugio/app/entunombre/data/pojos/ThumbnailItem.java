package com.dulcerefugio.app.entunombre.data.pojos;


import android.graphics.Bitmap;

import com.zomato.photofilters.imageprocessors.Filter;

public class ThumbnailItem {
    public Bitmap image;
    public Filter filter;
    public String name;

    public ThumbnailItem(String name) {
        image = null;
        filter = new Filter();
        this.name = name;
    }
}