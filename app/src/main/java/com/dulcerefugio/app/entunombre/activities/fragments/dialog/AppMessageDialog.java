package com.dulcerefugio.app.entunombre.activities.fragments.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dulcerefugio.app.entunombre.EnTuNombre;
import com.dulcerefugio.app.entunombre.R;
import com.dulcerefugio.app.entunombre.ui.widgets.CustomShareButton;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import java.io.File;

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
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        initialize();
        return mDialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        shown = false;
        try {
            super.onDismiss(dialog);
            try {
                ((OnAppMessageDialogListener) getActivity()).onDismiss();
            } catch (ClassCastException cce) {
                cce.printStackTrace();
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
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
        if (messageTypeInt.getResource() != 0)
            message = getString(messageTypeInt.getResource());

        MaterialDialog.Builder builder = getBuilder(messageTypeInt, title, message);
        mDialog = builder.build();

        if (messageTypeInt == MessageType.IMAGE_PREVIEW) {
            if (mDialog != null && mDialog.getCustomView() != null) {
                ImageView imageView = (ImageView) mDialog.getCustomView().findViewById(R.id.dialog_preview_iv_image);
                Picasso.with(EnTuNombre.context).load(new File(imageUri)).into(imageView);

                CustomShareButton csb = (CustomShareButton) mDialog.getCustomView().findViewById(R.id.dialog_preview_csb_picture_share);
                csb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((OnAppMessageDialogListener) getActivity()).onPreviewDialogShare(imageUri);
                        AppMessageDialog.this.dismiss();
                    }
                });
            }
        }
    }

    private MaterialDialog.Builder getBuilder(final MessageType messageTypeInt, String title, String message) {
        final MaterialDialog.Builder builder = new MaterialDialog
                .Builder(getActivity())
                .title(title)
                .titleColorRes(android.R.color.darker_gray)
                .contentColorRes(android.R.color.black)
                .backgroundColorRes(android.R.color.white);

        if (message.length() > 0)
            builder.content(message);

        if (messageTypeInt.progressBarNeeded)
            builder.progress(true, 0);

        if (messageTypeInt.negativeTextNeeded)
            builder.negativeText(android.R.string.no);

        if (messageTypeInt.positiveTextNeeded) {
            if (messageTypeInt == MessageType.ASK_TO_EXIT) {
                builder.positiveText(android.R.string.yes);
            } else {
                builder.positiveText(android.R.string.ok);
            }

            builder.callback(new MaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(MaterialDialog dialog) {
                    super.onPositive(dialog);
                    if (messageTypeInt == MessageType.ASK_TO_EXIT) {
                        ((OnAppMessageDialogListener) getActivity()).onPositiveButton();
                        AppMessageDialog.this.dismiss();
                    }
                }

                @Override
                public void onNegative(MaterialDialog dialog) {
                    super.onNegative(dialog);
                    AppMessageDialog.this.dismiss();
                }
            });
        }

        if (messageTypeInt == MessageType.IMAGE_PREVIEW) {
            builder.customView(R.layout.dialog_preview_image, true);
        }
        return builder;
    }

    //========================================================
    //INNER CLASSES
    //========================================================
    public interface OnAppMessageDialogListener {
        void onPreviewDialogShare(String ImageUri);

        void onPositiveButton();

        void onDismiss();
    }

    public enum MessageType implements Parcelable {
        PLEASE_WAIT(R.string.please_wait, true, false, false),
        MUST_SELECT_FRAME(R.string.must_select_frame, false, true, false),
        IMAGE_PREVIEW(0, false, true, false),
        ABOUT(R.string.about, false, true, false),
        ASK_TO_EXIT(R.string.ask_to_exit_msg, false, true, true);

        private final boolean progressBarNeeded;
        private final boolean positiveTextNeeded;
        private final boolean negativeTextNeeded;
        private int resource;

        MessageType(int resource, boolean progressBarNeeded,
                    boolean positiveTextNeeded, boolean negativeTextNeeded) {
            this.resource = resource;
            this.progressBarNeeded = progressBarNeeded;
            this.positiveTextNeeded = positiveTextNeeded;
            this.negativeTextNeeded = negativeTextNeeded;
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

