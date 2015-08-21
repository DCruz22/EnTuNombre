package com.dulcerefugio.app.entunombre.activities.fragments.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dulcerefugio.app.entunombre.R;

import fr.tvbarthel.lib.blurdialogfragment.BlurDialogEngine;
/**
 * Created by eperez on 8/20/15.
 */

/**
 * Created by cristian on 22/04/15.
 */
public class AppMessage extends DialogFragment {
    //========================================================
    //CONSTANTS
    //========================================================
    public static final String cBUNDLE_ARG_MESSAGE_TYPE = "ERROR_TYPE";
    //========================================================
    //FIELDS
    //========================================================
    private BlurDialogEngine mBlurDialogEngine;
    private MaterialDialog mDialog;
    private static AppMessage sAppMessage;
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
        String title = getString(R.string.app_name);
        String message = getString(messageTypeInt.getResource());

        MaterialDialog.Builder builder = new MaterialDialog
                .Builder(getActivity())
                .title(title)
                .content(message);

        if (messageTypeInt != MessageType.PLEASE_WAIT) {
            builder.positiveText(android.R.string.ok);
        }else {
            builder.progress(messageTypeInt == MessageType.PLEASE_WAIT, 0);
        }
        mDialog = builder.build();
    }

    public static AppMessage newInstance() {
        sAppMessage = sAppMessage != null ? sAppMessage : new AppMessage();
        return sAppMessage;
    }

    //========================================================
    //INNER CLASSES
    //========================================================
    public interface OnAppErrorListener {
        void OnLoadingPlacesErrorOk();
    }

    public enum MessageType implements Parcelable {
        PLEASE_WAIT(R.string.please_wait),
        MUST_SELECT_FRAME(R.string.must_select_frame);

        private int resource;

        MessageType(int resource) {
            this.resource = resource;
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

