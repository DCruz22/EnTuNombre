package com.dulcerefugio.app.etn.ui.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dulcerefugio.app.etn.R;
import com.dulcerefugio.app.etn.data.callbacks.ThumbnailCallback;
import com.dulcerefugio.app.etn.data.pojos.ThumbnailItem;
import com.nineoldandroids.view.ViewHelper;

import java.util.List;

/**
 * @author Varun on 01/07/15.
 */
public class ThumbnailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "THUMBNAILS_ADAPTER";
    private static int lastPosition = -1;
    private ThumbnailCallback mListener;
    private List<ThumbnailItem> dataSet;


    public ThumbnailsAdapter(List<ThumbnailItem> dataSet, ThumbnailCallback mListener) {
        Log.v(TAG, "Thumbnails Adapter has " + dataSet.size() + " items");
        this.dataSet = dataSet;
        this.mListener = mListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.v(TAG, "On Create View Holder Called");
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.row_filter, viewGroup, false);
        return new ThumbnailsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int i) {
        final ThumbnailItem thumbnailItem = dataSet.get(i);
        Log.v(TAG, "On Bind View Called");
        ThumbnailsViewHolder thumbnailsViewHolder = (ThumbnailsViewHolder) holder;
        thumbnailsViewHolder.name.setText(thumbnailItem.name);
        thumbnailsViewHolder.thumbnail.setImageBitmap(thumbnailItem.image);
        thumbnailsViewHolder.thumbnail.setScaleType(ImageView.ScaleType.FIT_START);
        setAnimation(thumbnailsViewHolder.thumbnail, i);
        thumbnailsViewHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastPosition != i) {
                    mListener.onThumbnailClick(thumbnailItem.filter);
                    lastPosition = i;
                }
            }

        });
    }

    private void setAnimation(View viewToAnimate, int position) {
        {
            ViewHelper.setAlpha(viewToAnimate, .0f);
            com.nineoldandroids.view.ViewPropertyAnimator.animate(viewToAnimate).alpha(1).setDuration(250).start();
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ThumbnailsViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView name;

        ThumbnailsViewHolder(View v) {
            super(v);
            this.thumbnail = v.findViewById(R.id.row_filter_iv_picture);
            this.name = v.findViewById(R.id.row_filter_tv_name);
        }
    }
}