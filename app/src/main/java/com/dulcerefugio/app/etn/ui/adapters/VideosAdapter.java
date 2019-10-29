package com.dulcerefugio.app.etn.ui.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dulcerefugio.app.etn.EnTuNombre;
import com.dulcerefugio.app.etn.R;
import com.dulcerefugio.app.etn.data.dao.YoutubeVideo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> {

    //=======================================================================
    //FIELDS
    //=======================================================================
    private List<YoutubeVideo> youtubeVideoList;

    //=======================================================================
    //CONSTRUCTORS
    //=======================================================================

    public VideosAdapter(List<YoutubeVideo> youtubeVideoList) {
        this.youtubeVideoList = youtubeVideoList;
    }

    //=======================================================================
    //OVERRIDEN METHODS
    //=======================================================================

    @Override
    public VideosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_video, parent, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.ivVideoThumbnail);
        TextView tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        TextView tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);

        return new ViewHolder(itemView, imageView, tvTitle, tvDescription);
    }

    @Override
    public void onBindViewHolder(VideosAdapter.ViewHolder viewHolder, int position) {
        YoutubeVideo youtubeVideo = youtubeVideoList.get(position);
        if (youtubeVideo != null) {
            viewHolder.tvTitle.setText(youtubeVideo.getTitle());
            viewHolder.tvDescription.setText(getShortDescription(youtubeVideo.getDescription()));
            Picasso.with(EnTuNombre.context).load(youtubeVideo.getThumbnail_url()).into(viewHolder.imageView);
            Log.d("", youtubeVideo.getTitle());
            Log.d("", youtubeVideo.getCreated_at()+"");
        }
    }

    @Override
    public int getItemCount() {
        return youtubeVideoList.size();
    }

    //=======================================================================
    //METHODS
    //=======================================================================
    public YoutubeVideo getItem(int position) {
        return youtubeVideoList.get(position);
    }

    private String getShortDescription(String description) {
        if (description == null)
            return "No overview available";

        if (description.length() <= 111)
            return description;

        return description.trim().substring(0, 111) + "...";
    }

    //=======================================================================
    //INNER CLASSES
    //=======================================================================
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView tvTitle;
        public TextView tvDescription;

        public ViewHolder(View itemView, ImageView imageView, TextView tvTitle, TextView tvDescription) {
            super(itemView);
            this.imageView = imageView;
            this.tvTitle = tvTitle;
            this.tvDescription = tvDescription;
        }
    }
}
