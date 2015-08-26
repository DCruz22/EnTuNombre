package com.dulcerefugio.app.entunombre.activities.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dulcerefugio.app.entunombre.EnTuNombre;
import com.dulcerefugio.app.entunombre.R;
import com.dulcerefugio.app.entunombre.activities.fragments.listeners.RecyclerItemClickListener;
import com.dulcerefugio.app.entunombre.data.dao.GeneratedImages;
import com.dulcerefugio.app.entunombre.data.dao.GeneratedImagesDao;
import com.dulcerefugio.app.entunombre.ui.adapters.PictureListAdapter;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;
import com.orhanobut.logger.Logger;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EFragment(R.layout.f_picture_list)
public class PictureListFragment extends Fragment
        implements RecyclerItemClickListener.OnItemClickListener {

    public static final String ARG_SECTION_NUMBER = "1234000";
    private PictureListListeners mListener;
    private RecyclerView.Adapter mAdapter;
    @ViewById(R.id.f_picture_list_rv_pictures)
    public RecyclerView mRecyclerView;
    private List<GeneratedImages> mPictures;
    private PictureListAdapter mPictureListAdapter;

    public PictureListFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (PictureListListeners) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BuildPictureListeners");
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mPictures != null && mPictures.size() > 0) {
            mRecyclerView.smoothScrollToPosition(0); //I use this to get back to pos 0 if i have many cards
            MaterialViewPagerHelper.getAnimator(getActivity()).onMaterialScrolled(null, 0); //jump back to 0 Yoffset
        }
    }

    @Override
    public void onRecyclerItemClick(View view, int position) {
        Logger.d(view.toString());
        Logger.d(view.getId()+"");
        if(mPictures.size() > 0)
            mListener.onCardSelected(mPictures.get(position - 1));
    }

    public void addItem(GeneratedImages generatedImage, int position){
        mPictures.add(position, generatedImage);
        //mPictureListAdapter.addItem(generatedImage, position);
        mAdapter.notifyDataSetChanged();
    }

    @AfterViews
    public void initialize() {
        Logger.d("0");
        mPictures = EnTuNombre.getInstance()
                .getDaoSession()
                .getGeneratedImagesDao()
                .queryBuilder()
                .orderDesc(GeneratedImagesDao.Properties.Id)
                .list();
        mPictureListAdapter = new PictureListAdapter(mPictures, new PictureListAdapter.OnPictureListAdapter() {
            @Override
            public void onPictureShare(String imageUri) {
                mListener.onPictureShare(imageUri);
            }
        });
        mAdapter = new RecyclerViewMaterialAdapter(mPictureListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        RecyclerItemClickListener listener = new RecyclerItemClickListener(getActivity());
        listener.addOnItemClickListener(this);
        mRecyclerView.addOnItemTouchListener(listener);
        mRecyclerView.setAdapter(mAdapter);
        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);
    }

    @Click(R.id.materialButton)
    public void onPictureButton() {
        mListener.onGeneratePictureClick();
    }

    public interface PictureListListeners {
        void onGeneratePictureClick();
        void onPictureShare(String imageUri);
        void onCardSelected(GeneratedImages generatedImages);
    }
}