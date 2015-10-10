package com.dulcerefugio.app.entunombre.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dulcerefugio.app.entunombre.EnTuNombre;
import com.dulcerefugio.app.entunombre.R;
import com.dulcerefugio.app.entunombre.data.pojos.PictureFrame;
import com.squareup.picasso.Picasso;


import java.util.List;

/**
 * Created by eperez on 8/15/15.
 */
public class PictureFramesAdapter extends RecyclerView.Adapter<PictureFramesAdapter.ViewHolder> {

    private List<PictureFrame> pictureFrames;

    public PictureFramesAdapter(List<PictureFrame> drawables) {
        this.pictureFrames = drawables;
    }

    @Override
    public PictureFramesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_frame, parent, false);
        ImageView ivFrame = ((ImageView) itemView.findViewById(R.id.row_frame_iv_frame));
        TextView tvName = ((TextView)itemView.findViewById(R.id.row_frame_tv_name));
        return new ViewHolder(itemView, ivFrame, tvName);
    }

    @Override
    public void onBindViewHolder(PictureFramesAdapter.ViewHolder holder, int position) {
        Integer drawable = pictureFrames.get(position).resToShow;
        Picasso.with(EnTuNombre.context).load(drawable).resize(300,300).into(holder.ivPicture);
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
        public TextView tvName;

        public ViewHolder(View itemView, ImageView ivPicture, TextView tvName) {
            super(itemView);
            this.ivPicture = ivPicture;
            this.tvName = tvName;
        }
    }
}
