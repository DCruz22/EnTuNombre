package com.dulcerefugio.app.etn.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dulcerefugio.app.etn.R;

/**
 * Created by euriperez16 on 9/2/2015.
 */public class PictureChooserOptionsAdapter extends ArrayAdapter<String> {


    //======================================================
    //                      FIELDS
    //======================================================
    private final int mResource;
    private String[] mData;

    //======================================================
    //                    CONSTRUCTORS
    //======================================================
    public PictureChooserOptionsAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
        mResource = resource;
        mData = objects;
    }


    //======================================================
    //                  OVERRIDEN METHODS
    //======================================================

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String text = mData[position];

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(mResource,parent, false);
        }

        TextView tv = (TextView)convertView.findViewById(R.id.item_tv_option_name);
        tv.setText(text);

        return convertView;
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