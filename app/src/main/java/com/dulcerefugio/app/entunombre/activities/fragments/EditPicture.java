package com.dulcerefugio.app.entunombre.activities.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.dulcerefugio.app.entunombre.R;
import com.dulcerefugio.app.entunombre.activities.fragments.listeners.RecyclerItemClickListener;
import com.dulcerefugio.app.entunombre.data.pojos.PictureFrame;
import com.dulcerefugio.app.entunombre.ui.adapters.PictureFramesAdapter;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
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
        implements RecyclerItemClickListener.OnItemClickListener {

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mIvPicture!= null) {
            mIvPicture.setImageBitmap(null);
            if(!mPictureBitmap.isRecycled()) {
                Log.d("--Edit--", "recycled");
                mPictureBitmap.recycle();
            }
            mPictureBitmap = null;
            System.gc();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_a_cropper, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logger.d("0");
        switch (item.getItemId()) {
            case R.id.action_send:
                Logger.d("0");
                mListener.onFinishEditing();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
            List<PictureFrame> pictureFrames = new ArrayList<>();
            pictureFrames.add(new PictureFrame(R.drawable.frame1, R.drawable.frame1, "Concierto"));
            pictureFrames.add(new PictureFrame(R.drawable.frame2, R.drawable.frame2, "England"));
            pictureFrames.add(new PictureFrame(R.drawable.frame3, R.drawable.frame3, "Everest dark"));
            pictureFrames.add(new PictureFrame(R.drawable.frame4, R.drawable.frame4, "Everest light"));
            pictureFrames.add(new PictureFrame(R.drawable.frame5, R.drawable.frame5, "Grunge"));
            pictureFrames.add(new PictureFrame(R.drawable.frame6, R.drawable.frame6, "Guitar"));
            mAdapter = new PictureFramesAdapter(pictureFrames);
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
        mListener.onShowWaitDialog();
        Log.d("EditPicture", "showed");
        mListener.onFrameSelected(mPicturePath, mAdapter.getItem(position));
    }

    public void showFramedImage(Bitmap bitmap) {
        mIvPicture.setImageBitmap(null);
        mPictureBitmap.recycle();
        mPictureBitmap = null;
        System.gc();

        mPictureBitmap = bitmap;
        mIvPicture.setImageBitmap(bitmap);
    }

    public void setImageBitmap(Bitmap bitmap) {
        if (mIvPicture != null)
            mIvPicture.setImageBitmap(bitmap);
    }

    public interface onEditPictureListener {
        void onShowWaitDialog();
        void onFrameSelected(String croppedImage, final int frame);
        void onFinishEditing();
    }
}
