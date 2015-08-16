package com.dulcerefugio.app.entunombre.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.dulcerefugio.app.entunombre.util.Util;

/**
 * Created by eperez on 7/23/15.
 */
public class Base extends AppCompatActivity {
    protected void checkInternetConnection() {
        if(!Util.isNetworkAvailable(this)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false)
                    .setMessage("No esta conectado a la internet")
                    .setPositiveButton("Reintentar",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            checkInternetConnection();
                        }
                    }).show();
        }else{
            Toast.makeText(this, "No se pudo establecer una conexion", Toast.LENGTH_LONG).show();
        }
    }
}
