package com.dulcerefugio.app.entunombre.ui.adapters;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dulcerefugio.app.entunombre.EnTuNombre;
import com.dulcerefugio.app.entunombre.R;
import com.dulcerefugio.app.entunombre.data.dao.GeneratedImages;
import com.dulcerefugio.app.entunombre.logic.AsyncTaskEx;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by euriperez16 on 8/19/2015.
 */
public class PictureListAdapter extends RecyclerView.Adapter<PictureListAdapter.ViewHolder> {

    //======================================================
    //                      FIELDS
    //======================================================
    private List<GeneratedImages> mImages;
    private ImageLoader mImageLoader;

    //======================================================
    //                    CONSTRUCTORS
    //======================================================
    public PictureListAdapter(List<GeneratedImages> mPictures) {
        mImages = mPictures;
        this.mImageLoader = ImageLoader.getInstance();
    }

    //======================================================
    //                  OVERRIDEN METHODS
    //======================================================
    @Override
    public PictureListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_picture, parent, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.f_picture_list_iv_picture);
        TextView tvTitle = (TextView) itemView.findViewById(R.id.f_picture_list_tv_name);

        return new ViewHolder(itemView, imageView, tvTitle);
    }

    @Override
    public void onBindViewHolder(final PictureListAdapter.ViewHolder holder, int position) {
        final GeneratedImages generatedImage = mImages.get(position);
        if(generatedImage != null){
            holder.tvTitle.setText(generatedImage.getPath());
            mImageLoader.displayImage(Uri.decode(Uri.fromFile(new File(generatedImage.getPath())).toString()), holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }
    //======================================================
    //                      METHODS
    //======================================================

    //======================================================
    //              INNER CLASSES/INTERFACES
    //======================================================
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView tvTitle;

        public ViewHolder(View itemView, ImageView imageView, TextView tvTitle) {
            super(itemView);
            this.imageView = imageView;
            this.tvTitle = tvTitle;
        }
    }

    //======================================================
    //                  GETTERS/SETTERS
    //======================================================
}