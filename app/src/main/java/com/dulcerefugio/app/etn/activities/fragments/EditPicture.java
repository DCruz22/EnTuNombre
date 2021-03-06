package com.dulcerefugio.app.etn.activities.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dulcerefugio.app.etn.R;
import com.dulcerefugio.app.etn.activities.fragments.dialog.AppMessageDialog;
import com.dulcerefugio.app.etn.activities.fragments.listeners.RecyclerItemClickListener;
import com.dulcerefugio.app.etn.data.pojos.PictureFrame;
import com.dulcerefugio.app.etn.ui.adapters.PictureFramesAdapter;
import com.dulcerefugio.app.etn.util.Util;
import com.orhanobut.logger.Logger;

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
    private static final String MUST_SELECT_FRAME_DIALOG = "mAppMessageMustSelectFrame";

    @FragmentArg(PICTURE_PATH_EXTRA)
    public String mPicturePath;

    @ViewById(R.id.f_edit_picture_iv_picture)
    public ImageView mIvPicture;
    @ViewById(R.id.f_edit_picture_iv_frame)
    public ImageView mIvFrame;
    @ViewById(R.id.f_edit_picture_rv_frames)
    RecyclerView mRecyclerFrames;
    @ViewById(R.id.f_edit_picture_vg_picture)
    RelativeLayout mRelativeFinalPicture;
    private PictureFramesAdapter mAdapter;
    private onEditPictureListener mListener;
    private Bitmap mPictureBitmap;
    private boolean mIsFrameSelected;

    private DialogFragment mAppMessageMustSelectFrame;

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
        inflater.inflate(R.menu.menu_a_cropper, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logger.d("0");
        switch (item.getItemId()) {
            case R.id.action_send:
                Logger.d("0");
                if(!mIsFrameSelected) {
                    if (mAppMessageMustSelectFrame == null)
                        mAppMessageMustSelectFrame = Util.getAppMessageDialog(AppMessageDialog.MessageType.MUST_SELECT_FRAME,
                                null, false);

                    mAppMessageMustSelectFrame.show(getChildFragmentManager(), MUST_SELECT_FRAME_DIALOG);
                    return true;
                }
                mListener.onFinishEditing(mRelativeFinalPicture);
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
            pictureFrames.add(new PictureFrame(R.drawable.frame1, "Circular"));
            pictureFrames.add(new PictureFrame(R.drawable.frame2, "Rectangular"));
            pictureFrames.add(new PictureFrame(R.drawable.frame3, "Classic"));
            pictureFrames.add(new PictureFrame(R.drawable.frame4, "Modern"));
            pictureFrames.add(new PictureFrame(R.drawable.frame5, "Waves"));
            pictureFrames.add(new PictureFrame(R.drawable.frame6, "Square"));

//            Calendar calendar = Calendar.getInstance();
//            int currentYear = calendar.get(Calendar.YEAR);
//            int currentMonth = calendar.get(Calendar.MONTH) + 1;
//            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
//
//            Calendar eventDayCal = new GregorianCalendar(2017, Calendar.NOVEMBER, 5);
//            int eventYear = eventDayCal.get(Calendar.YEAR);
//            int eventMonth = eventDayCal.get(Calendar.MONTH) + 1;
//            int eventDay = eventDayCal.get(Calendar.DAY_OF_MONTH);
//
//            if (currentYear == eventYear && currentMonth == eventMonth && currentDay <= eventDay) {
//                int diff = eventDay - currentDay;
//
//                switch (diff) {
//                    case 4:
//                        pictureFrames.add(new PictureFrame(R.drawable.frame_cd4_show, R.drawable.frame_cd4, "Countdown"));
//                        break;
//                    case 3:
//                        pictureFrames.add(new PictureFrame(R.drawable.frame_cd3_show, R.drawable.frame_cd3, "Countdown"));
//                        break;
//                    case 2:
//                        pictureFrames.add(new PictureFrame(R.drawable.frame_cd2_show, R.drawable.frame_cd2, "Countdown"));
//                        break;
//                    case 1:
//                        pictureFrames.add(new PictureFrame(R.drawable.frame_cd1_show, R.drawable.frame_cd1, "Countdown"));
//                        break;
//                    case 0:
//                        pictureFrames.add(new PictureFrame(R.drawable.frame_cd0_show, R.drawable.frame_cd0, "Countdown"));
//                        break;
//                }
//            }

            mAdapter = new PictureFramesAdapter(pictureFrames, mPicturePath);
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
        //mListener.onShowWaitDialog();
        Log.d("EditPicture", "showed");
        mIsFrameSelected = true;
        mIvFrame.setImageResource(mAdapter.getItem(position));
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

        void onFinishEditing(ViewGroup vgFinalPicture);
    }
}
