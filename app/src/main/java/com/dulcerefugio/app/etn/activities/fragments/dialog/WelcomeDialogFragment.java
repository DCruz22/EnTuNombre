package com.dulcerefugio.app.etn.activities.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import com.dulcerefugio.app.etn.R;

/**
 * Created by Eury on 31/08/2014.
 */
public class WelcomeDialogFragment extends DialogFragment {

    private static final String TAG ="WelcomeDialogFragment" ;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.f_welcome_message_dialog, null);

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setView(view);
        dialog.setTitle("Bienvenido a la app de #EnTuNombre");

        return dialog.create();
    }
}
