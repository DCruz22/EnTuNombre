package com.dulcerefugio.app.entunombre.activities.fragments.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dulcerefugio.app.entunombre.R;

import java.io.File;

import fr.tvbarthel.lib.blurdialogfragment.BlurDialogEngine;
/**
 * Created by eperez on 8/20/15.
 */

/**
 * Created by cristian on 22/04/15.
 */
public class AppMessageDialog extends DialogFragment {
    //========================================================
    //CONSTANTS
    //========================================================
    public static final String cBUNDLE_ARG_MESSAGE_TYPE = "ERROR_TYPE";
    public static final String cBUNDLE_ARG_IMAGE_URI = "IMAGE_URI";

    //========================================================
    //FIELDS
    //========================================================
    private BlurDialogEngine mBlurDialogEngine;
    private MaterialDialog mDialog;
    private static OnAppMessageDialogListener mListener;
    private boolean shown;

    //========================================================
    //CONSTRUCTORS
    //========================================================

    //========================================================
    //OVERRIDDEN METHODS
    //========================================================


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBlurDialogEngine = new BlurDialogEngine(getActivity());
    }


    /*@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = ((OnAppMessageDialogListener)activity);
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnAppMessageDialogListener");
        }
    }*/

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        initialize();
        return mDialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        mBlurDialogEngine.onResume(getRetainInstance());
    }

    @Override
    public void onDestroy() {
        mBlurDialogEngine.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        shown = false;
        mBlurDialogEngine.onDismiss();
        super.onDismiss(dialog);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (shown) return;

        super.show(manager, tag);
        shown = true;
    }
    //========================================================
    //METHODS
    //========================================================

    private void initialize() {
        final MessageType messageTypeInt = getArguments().getParcelable(cBUNDLE_ARG_MESSAGE_TYPE);
        final String imageUri = getArguments().getString(cBUNDLE_ARG_IMAGE_URI);
        String title = getString(R.string.app_name);
        String message = "";
        if(messageTypeInt.getResource() != 0)
            message = getString(messageTypeInt.getResource());

        MaterialDialog.Builder builder = getBuilder(messageTypeInt, title, message);
        mDialog = builder.build();

        if(messageTypeInt == MessageType.IMAGE_PREVIEW){
            ImageView imageView = (ImageView)mDialog.getCustomView().findViewById(R.id.dialog_preview_iv_image);
            imageView.setImageURI(Uri.fromFile(new File(imageUri)));

        }
    }

    private MaterialDialog.Builder getBuilder(MessageType messageTypeInt, String title, String message) {
        MaterialDialog.Builder builder = new MaterialDialog
                .Builder(getActivity())
                .title(title)
                .titleColorRes(android.R.color.darker_gray)
                .backgroundColorRes(android.R.color.white);

        if(message.length() > 0)
            builder.content(message);

        if (messageTypeInt.positiveTextNeeded) {
            builder.positiveText(android.R.string.ok);
        }
        if(messageTypeInt.progressBarNeeded){
            builder.progress(true, 0);
        }
        if(messageTypeInt == MessageType.IMAGE_PREVIEW){
            builder.customView(R.layout.dialog_preview_image, true);
        }
        return builder;
    }

    //========================================================
    //INNER CLASSES
    //========================================================
    public interface OnAppMessageDialogListener {
        void getImagePreviewBitmap(String imageUri);
    }

    public enum MessageType implements Parcelable {
        PLEASE_WAIT(R.string.please_wait, true, false),
        MUST_SELECT_FRAME(R.string.must_select_frame, false, true),
        IMAGE_PREVIEW(0, false, true);

        private final boolean progressBarNeeded;
        private final boolean positiveTextNeeded;
        private int resource;

        MessageType(int resource, boolean progressBarNeeded,
                    boolean positiveTextNeeded) {
            this.resource = resource;
            this.progressBarNeeded = progressBarNeeded;
            this.positiveTextNeeded = positiveTextNeeded;
        }

        public int getResource() {
            return resource;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(ordinal());
        }

        public static final Creator<MessageType> cCREATOR = new Creator<MessageType>() {
            @Override
            public MessageType createFromParcel(Parcel source) {
                return MessageType.values()[source.readInt()];
            }

            @Override
            public MessageType[] newArray(int size) {
                return new MessageType[size];
            }
        };
    }
    //========================================================
    //GETTERS AND SETTERS
    //========================================================
}

