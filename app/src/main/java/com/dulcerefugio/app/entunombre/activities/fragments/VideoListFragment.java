package com.dulcerefugio.app.entunombre.activities.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dulcerefugio.app.entunombre.EnTuNombre;
import com.dulcerefugio.app.entunombre.R;
import com.dulcerefugio.app.entunombre.activities.MainActivity;
import com.dulcerefugio.app.entunombre.activities.fragments.listeners.RecyclerItemClickListener;
import com.dulcerefugio.app.entunombre.data.dao.YoutubeVideo;
import com.dulcerefugio.app.entunombre.ui.adapters.RecyclerViewDividerItemDecorator;
import com.dulcerefugio.app.entunombre.ui.adapters.VideosAdapter;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;

import java.util.List;

public class VideoListFragment extends Base
implements RecyclerItemClickListener.OnItemClickListener{


    //==================================================================================
    //CONSTANTS
    //==================================================================================

    public static final String ARG_SECTION_NUMBER = "1254000";


    //==================================================================================
    //PROPERTIES
    //==================================================================================
    private Context mContext;
    private VideoListListeners mCallbacks;
    private RecyclerView.Adapter mAdapter;
    private ViewGroup mContainer;
    private LayoutInflater mLayoutInflater;
    private RecyclerView mRecyclerView;
    private List<YoutubeVideo> mYoutubeVideos;


    //==================================================================================
    //CONSTRUCTORS
    //==================================================================================

    public VideoListFragment() {
    }


    //==================================================================================
    //OVERRIDEN METHODS
    //==================================================================================

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);

        mCallbacks = (MainActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        loadParameters();
        initialize(container, inflater);

        return createLayout();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView.setAdapter(mAdapter);
        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);
    }

    //4th
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onRecyclerItemClick(View view, int position) {
        YoutubeVideo youtubeVideo = mYoutubeVideos.get(position);
        mCallbacks.onVideoPlayback(youtubeVideo.getVideo_id());
    }


    //==================================================================================
    //OVERRIDEN LOADER STUFF
    //==================================================================================

    private void loadParameters() {
        this.mContext = getActivity();
    }

    //==================================================================================
    //METHODS
    //==================================================================================

    public void initialize(ViewGroup container, LayoutInflater inflater) {
        this.mContainer = container;
        this.mLayoutInflater = inflater;
        mYoutubeVideos = EnTuNombre.getInstance().getDaoSession().getYoutubeVideoDao().loadAll();
        mAdapter = new RecyclerViewMaterialAdapter(new VideosAdapter(mYoutubeVideos));
        Log.d("VLF", EnTuNombre.getInstance().getDaoSession().getYoutubeVideoDao().loadAll().size() + "");
    }

    public View createLayout() {
        View view = mLayoutInflater.inflate(R.layout.f_video_list, mContainer, false);
        assert view != null;
        mRecyclerView = (RecyclerView)view.findViewById(R.id.f_video_list_rv_video_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        RecyclerItemClickListener listener = new RecyclerItemClickListener(getActivity());
        listener.addOnItemClickListener(this);
        mRecyclerView.addOnItemTouchListener(listener);

        return view;
    }

    //==================================================================================
    //Callbacks
    //==================================================================================

    public interface VideoListListeners {
        void onVideoPlayback(String videoId);
    }

}