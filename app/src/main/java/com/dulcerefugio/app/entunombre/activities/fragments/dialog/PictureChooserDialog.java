package com.dulcerefugio.app.entunombre.activities.fragments.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.dulcerefugio.app.entunombre.R;
import com.dulcerefugio.app.entunombre.ui.adapters.PictureChooserOptionsAdapter;

/**
 * Created by euriperez16 on 9/2/2015.
 */
public class PictureChooserDialog extends DialogFragment {

    //======================================================
    //                      FIELDS
    //======================================================
    private OnPictureChooserListeners mListeners;
    private LayoutInflater mLayoutInflater;

    //======================================================
    //                    CONSTRUCTORS
    //======================================================

    //======================================================
    //                  OVERRIDDEN METHODS
    //======================================================

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialize();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListeners = (OnPictureChooserListeners) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPicturePostChooserListeners");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        View view = mLayoutInflater.inflate(R.layout.df_picture_chooser, null);

        dialog.setContentView(view);
        dialog.setTitle(getActivity().getString(R.string.f_picture_chooser_title));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });

        String[] options = getActivity().getResources().getStringArray(R.array.picture_chooser_items);
        ListView lvOptions = (ListView)view.findViewById(R.id.df_lv_picture_chooser);
        PictureChooserOptionsAdapter pictureChooserOptionsAdapter =
                new PictureChooserOptionsAdapter(getActivity(), R.layout.item_picture_chooser,options);
        lvOptions.setAdapter(pictureChooserOptionsAdapter);
        lvOptions.setClickable(true);
        lvOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0: //CAMERA
                        PictureChooserDialog.this.dismiss();
                        mListeners.onTakePicture(PictureChooserDialog.this);
                        break;
                    case 1:
                        PictureChooserDialog.this.dismiss();
                        mListeners.onChooseFromGallery(PictureChooserDialog.this);
                        break;
                }
            }
        });
        Button btnCancel = (Button) view.findViewById(R.id.df_btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PictureChooserDialog.this.dismiss();
                mListeners.onPicturePostCancel();
            }
        });

        return dialog;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    //======================================================
    //                      METHODS
    //======================================================

    private void initialize() {

        mLayoutInflater = getActivity().getLayoutInflater();
    }

    //======================================================
    //              INNER CLASSES/INTERFACES
    //======================================================

    public interface OnPictureChooserListeners {
        void onTakePicture(Fragment fragment);
        void onChooseFromGallery(Fragment fragment);
        void onPicturePostCancel();
    }

    //======================================================
    //                  GETTERS/SETTERS
    //======================================================
}