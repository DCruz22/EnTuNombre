package com.dulcerefugio.app.entunombre.activities.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.dulcerefugio.app.entunombre.R;
import com.dulcerefugio.app.entunombre.activities.fragments.listeners.RecyclerItemClickListener;
import com.dulcerefugio.app.entunombre.ui.adapters.PictureFramesAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eperez on 8/15/15.
 */
@EFragment(R.layout.f_edit_picture)
public class EditPicture extends Fragment
        implements RecyclerItemClickListener.OnItemClickListener{

    public static final String PICTURE_PATH_EXTRA = "PICTURE_PATH_EXTRA";

    @FragmentArg(PICTURE_PATH_EXTRA)
    public String mPicturePath;

    @ViewById(R.id.f_edit_picture_iv_picture)
    public ImageView mIvPicture;
    @ViewById(R.id.f_edit_picture_rv_frames)
    RecyclerView mRecyclerFrames;
    private PictureFramesAdapter mAdapter;
    private onEditPictureListener mListener;
    private Bitmap mPictureBitmap;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (onEditPictureListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onCropPictureListener");
        }
    }

    @AfterViews
    public void init() {
        if (mPicturePath != null) {
            try {
                Uri uri = Uri.fromFile(new File(mPicturePath));
                mPictureBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                mIvPicture.setImageBitmap(mPictureBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<Integer> drawables = new ArrayList<>();
            drawables.add(R.drawable.frame);
            drawables.add(R.drawable.frame);
            drawables.add(R.drawable.frame);
            drawables.add(R.drawable.frame);
            drawables.add(R.drawable.frame);
            mAdapter = new PictureFramesAdapter(drawables);
            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            mRecyclerFrames.setLayoutManager(layoutManager);
            mRecyclerFrames.setAdapter(mAdapter);
            RecyclerItemClickListener listener = new RecyclerItemClickListener(getActivity());
            listener.addOnItemClickListener(this);
            mRecyclerFrames.addOnItemTouchListener(listener);
        }
    }

    @Override
    public void onRecyclerItemClick(View view, int position) {
        Uri uri = Uri.fromFile(new File(mPicturePath));
        try {
            mPictureBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mListener.onFrameSelected(mPictureBitmap, mAdapter.getItem(position));
    }

    public void showFramedImage(Bitmap bitmap){
        mIvPicture.setImageBitmap(bitmap);
    }

    public interface onEditPictureListener{
        void onFrameSelected(Bitmap croppedImage, final int frame);
    }
}