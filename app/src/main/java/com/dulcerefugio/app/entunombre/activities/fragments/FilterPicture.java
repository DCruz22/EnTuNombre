package com.dulcerefugio.app.entunombre.activities.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dulcerefugio.app.entunombre.R;
import com.dulcerefugio.app.entunombre.activities.CropperActivity;
import com.dulcerefugio.app.entunombre.activities.fragments.dialog.AppMessageDialog;
import com.dulcerefugio.app.entunombre.activities.fragments.listeners.RecyclerItemClickListener;
import com.dulcerefugio.app.entunombre.data.callbacks.ThumbnailCallback;
import com.dulcerefugio.app.entunombre.data.pojos.PictureFrame;
import com.dulcerefugio.app.entunombre.data.pojos.ThumbnailItem;
import com.dulcerefugio.app.entunombre.ui.adapters.PictureFramesAdapter;
import com.dulcerefugio.app.entunombre.ui.adapters.ThumbnailsAdapter;
import com.dulcerefugio.app.entunombre.util.ThumbnailsManager;
import com.dulcerefugio.app.entunombre.util.Util;
import com.orhanobut.logger.Logger;
import com.zomato.photofilters.SampleFilters;
import com.zomato.photofilters.imageprocessors.Filter;

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
@EFragment(R.layout.f_filter_picture)
public class FilterPicture extends Fragment
        implements ThumbnailCallback {

    private Fragment fragment;

    public static final String PICTURE_PATH_EXTRA = "PICTURE_PATH_EXTRA";
    private static final String MUST_SELECT_FRAME_DIALOG = "mAppMessageMustSelectFrame";

    @FragmentArg(PICTURE_PATH_EXTRA)
    public String mPicturePath;

    @ViewById(R.id.f_filter_picture_iv_picture)
    public ImageView mIvPicture;

    @ViewById(R.id.f_filter_picture_rv_frames)
    RecyclerView mRecyclerFilters;

    @ViewById(R.id.f_filter_picture_vg_picture)
    RelativeLayout mRelativeFinalPicture;
    private onFilterPictureListener mListener;
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
            mListener = (onFilterPictureListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onCropPictureListener");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mIvPicture != null) {
            mIvPicture.setImageBitmap(null);
            if (!mPictureBitmap.isRecycled()) {
                Log.d("--Edit--", "recycled");
                mPictureBitmap.recycle();
            }
            mPictureBitmap = null;
            System.gc();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_a_filter, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logger.d("0");
        switch (item.getItemId()) {
            case R.id.action_send:
                mListener.onFilterImage(mPictureBitmap);
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
        }
            fragment = this;
        bindDataToAdapter();
    }

    private void bindDataToAdapter() {
        final Context context = getActivity().getApplication();
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                Bitmap thumbImage = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(mPicturePath), 640, 640, false);
                ThumbnailItem t1 = new ThumbnailItem();
                ThumbnailItem t2 = new ThumbnailItem();
                ThumbnailItem t3 = new ThumbnailItem();
                ThumbnailItem t4 = new ThumbnailItem();
                ThumbnailItem t5 = new ThumbnailItem();
                ThumbnailItem t6 = new ThumbnailItem();

                t1.image = thumbImage;
                t2.image = thumbImage;
                t3.image = thumbImage;
                t4.image = thumbImage;
                t5.image = thumbImage;
                t6.image = thumbImage;
                ThumbnailsManager.clearThumbs();
                ThumbnailsManager.addThumb(t1); // Original Image

                t2.filter = SampleFilters.getStarLitFilter();
                ThumbnailsManager.addThumb(t2);

                t3.filter = SampleFilters.getBlueMessFilter();
                ThumbnailsManager.addThumb(t3);

                t4.filter = SampleFilters.getAweStruckVibeFilter();
                ThumbnailsManager.addThumb(t4);

                t5.filter = SampleFilters.getLimeStutterFilter();
                ThumbnailsManager.addThumb(t5);

                t6.filter = SampleFilters.getNightWhisperFilter();
                ThumbnailsManager.addThumb(t6);

                List<ThumbnailItem> thumbs = ThumbnailsManager.processThumbs(context);

                ThumbnailsAdapter adapter = new ThumbnailsAdapter(thumbs, (ThumbnailCallback) fragment);
                LinearLayoutManager layoutManager
                        = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                mRecyclerFilters.setLayoutManager(layoutManager);
                mRecyclerFilters.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        };
        handler.post(r);
    }

    @Override
    public void onThumbnailClick(Filter filter) {
        final Bitmap filteredImage = filter.processFilter(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(mPicturePath), 640, 640, false));
        mIvPicture.setImageBitmap(filteredImage);
        mPictureBitmap = filteredImage;
    }

    public interface onFilterPictureListener {
        void onFilterImage(Bitmap croppedImage);
        void onFilterCancel();
    }
}
