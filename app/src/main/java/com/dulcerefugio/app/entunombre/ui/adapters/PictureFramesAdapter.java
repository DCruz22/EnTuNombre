package com.dulcerefugio.app.entunombre.ui.adapters;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dulcerefugio.app.entunombre.EnTuNombre;
import com.dulcerefugio.app.entunombre.R;

import java.util.List;

/**
 * Created by eperez on 8/15/15.
 */
public class PictureFramesAdapter extends RecyclerView.Adapter<PictureFramesAdapter.ViewHolder> {

    private List<Integer> drawables;

    public PictureFramesAdapter(List<Integer> drawables) {
        this.drawables = drawables;
    }

    @Override
    public PictureFramesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_frame, parent, false);
        ImageView ivFrame = ((ImageView)itemView.findViewById(R.id.row_frame_iv_frame));
        return new ViewHolder(itemView, ivFrame);
    }

    @Override
    public void onBindViewHolder(PictureFramesAdapter.ViewHolder holder, int position) {
        Integer drawable = drawables.get(position);

        if(drawable != null){
            holder.ivPicture.setImageDrawable(EnTuNombre.context.getResources().getDrawable(drawable));
        }
    }

    @Override
    public int getItemCount() {
        return drawables.size();
    }

    public int getItem(int position){
        return drawables.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivPicture;
        public ViewHolder(View itemView, ImageView imageView) {
            super(itemView);
            ivPicture =imageView;
        }
    }
}
