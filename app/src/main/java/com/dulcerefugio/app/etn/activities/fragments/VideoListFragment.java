package com.dulcerefugio.app.etn.activities.fragments;

import android.app.Activity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.dulcerefugio.app.etn.EnTuNombre;
import com.dulcerefugio.app.etn.R;
import com.dulcerefugio.app.etn.activities.MainActivity;
import com.dulcerefugio.app.etn.activities.fragments.listeners.RecyclerItemClickListener;
import com.dulcerefugio.app.etn.data.dao.YoutubeVideo;
import com.dulcerefugio.app.etn.ui.adapters.VideosAdapter;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EFragment(R.layout.f_video_list)
public class VideoListFragment extends Base
        implements RecyclerItemClickListener.OnItemClickListener {


    //==================================================================================
    //CONSTANTS
    //==================================================================================

    public static final String ARG_SECTION_NUMBER = "1254000";

    //==================================================================================
    //PROPERTIES
    //==================================================================================
    private VideoListListeners mCallbacks;
    private RecyclerView.Adapter mAdapter;
    @ViewById(R.id.f_video_list_rv_video_list)
    public RecyclerView mRecyclerView;
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
    public void onRecyclerItemClick(View view, int position) {
        try {
            YoutubeVideo youtubeVideo = mYoutubeVideos.get(position - 1); 
            mCallbacks.onVideoPlayback(youtubeVideo.getVideo_id());
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(getActivity(), "No se puede reproducir este video, intente luego", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mYoutubeVideos != null && mYoutubeVideos.size() > 0) {
            mRecyclerView.smoothScrollToPosition(0); //I use this to get back to pos 0 if i have many cards
            MaterialViewPagerHelper.getAnimator(getActivity()).onMaterialScrolled(null, 0); //jump back to 0 Yoffset
        }
    }
    //==================================================================================
    //METHODS
    //==================================================================================

    @AfterViews
    public void initialize() {
        mYoutubeVideos = EnTuNombre.getInstance()
                .getDaoSession()
                .getYoutubeVideoDao()
                .queryBuilder()
                .list();

        mAdapter = new RecyclerViewMaterialAdapter(new VideosAdapter(mYoutubeVideos));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        RecyclerItemClickListener listener = new RecyclerItemClickListener(getActivity());
        listener.addOnItemClickListener(this);
        mRecyclerView.addOnItemTouchListener(listener);
        mRecyclerView.setAdapter(mAdapter);
        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);
    }
    //==================================================================================
    //Callbacks
    //==================================================================================

    public interface VideoListListeners {
        void onVideoPlayback(String videoId);
    }
}