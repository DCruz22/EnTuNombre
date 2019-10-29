package com.dulcerefugio.app.etn.ui.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dulcerefugio.app.etn.EnTuNombre;
import com.dulcerefugio.app.etn.R;
import com.dulcerefugio.app.etn.data.dao.GeneratedImages;
import com.dulcerefugio.app.etn.ui.widgets.CustomShareButton;
import com.dulcerefugio.app.etn.util.Util;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by euriperez16 on 8/19/2015.
 */
public class PictureListAdapter extends RecyclerView.Adapter<PictureListAdapter.ViewHolder> {

    //======================================================
    //                      FIELDS
    //======================================================
    private List<GeneratedImages> mImages;
    private OnPictureListAdapter mListener;

    //======================================================
    //                    CONSTRUCTORS
    //======================================================
    public PictureListAdapter(List<GeneratedImages> mPictures, OnPictureListAdapter listener) {
        mImages = mPictures;
        mListener = listener;
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
        TextView tvDate = (TextView) itemView.findViewById(R.id.f_picture_list_tv_date);
        CustomShareButton shareContainer = (CustomShareButton) itemView.findViewById(R.id.row_picture_share);

        return new ViewHolder(itemView, imageView, tvTitle, tvDate, shareContainer);
    }

    @Override
    public void onBindViewHolder(final PictureListAdapter.ViewHolder holder, final int position) {
        final GeneratedImages generatedImage = mImages.get(position);
        if (generatedImage != null) {
            String[] path = generatedImage.getPath().split("/");
            holder.tvTitle.setText(path[path.length - 1].replace(".jpg", ""));
            Date date = Util.parseStringToDate(generatedImage.getDate());
            holder.tvDate.setText(new PrettyTime().format(date == null ? new Date() : date));
            Picasso.with(EnTuNombre.context).load(new File(generatedImage.getPath())).into(holder.imageView);
            holder.mBtnShare.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Logger.d("0");
                    mListener.onPictureShare(generatedImage.getPath());
                    return false;
                }
            });
            holder.imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    mListener.onImageSelected(generatedImage);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public void addItem(GeneratedImages generatedImage, int position) {
        mImages.add(position, generatedImage);
        notifyDataSetChanged();
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
        public TextView tvDate;
        public CustomShareButton mBtnShare;

        public ViewHolder(View itemView, ImageView imageView, TextView tvTitle, TextView tvDate, CustomShareButton shareContainer) {
            super(itemView);
            this.imageView = imageView;
            this.tvTitle = tvTitle;
            this.tvDate = tvDate;
            this.mBtnShare = shareContainer;
        }
    }

    public interface OnPictureListAdapter {
        void onPictureShare(String imageUri);

        void onImageSelected(GeneratedImages generatedImages);
    }

    //======================================================
    //                  GETTERS/SETTERS
    //======================================================
}