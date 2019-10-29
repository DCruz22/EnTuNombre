package com.dulcerefugio.app.etn.ui.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dulcerefugio.app.etn.EnTuNombre;
import com.dulcerefugio.app.etn.R;
import com.dulcerefugio.app.etn.data.pojos.PictureFrame;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.util.List;

/**
 * Created by eperez on 8/15/15.
 */
public class PictureFramesAdapter extends RecyclerView.Adapter<PictureFramesAdapter.ViewHolder> {

    private List<PictureFrame> pictureFrames;
    private String mPicturePath;

    public PictureFramesAdapter(List<PictureFrame> drawables, String picturePath) {
        this.pictureFrames = drawables;
        this.mPicturePath = picturePath;
    }

    @Override
    public PictureFramesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_frame, parent, false);
        ImageView ivFrame = ((ImageView) itemView.findViewById(R.id.row_frame_iv_frame));
        ImageView ivPicture = ((ImageView) itemView.findViewById(R.id.row_frame_iv_picture));
        TextView tvName = ((TextView)itemView.findViewById(R.id.row_frame_tv_name));
        return new ViewHolder(itemView, ivFrame, ivPicture, tvName);
    }

    @Override
    public void onBindViewHolder(PictureFramesAdapter.ViewHolder holder, int position) {
        Integer drawable = pictureFrames.get(position).resToUse;
        Picasso.with(EnTuNombre.context).load(drawable).resize(300,300).into(holder.ivPicture);
        Picasso.with(EnTuNombre.context).load(new File(mPicturePath)).into(holder.ivFrame);
        //holder.ivPicture.setImageDrawable(EnTuNombre.context.getResources().getDrawable(drawable));
        holder.tvName.setText(pictureFrames.get(position).name);
    }

    @Override
    public int getItemCount() {
        return pictureFrames.size();
    }

    public int getItem(int position) {
        return pictureFrames.get(position).resToUse;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivPicture;
        public ImageView ivFrame;
        public TextView tvName;

        public ViewHolder(View itemView, ImageView ivPicture, ImageView ivFrame, TextView tvName) {
            super(itemView);
            this.ivPicture = ivPicture;
            this.tvName = tvName;
            this.ivFrame = ivFrame;
        }
    }
}
