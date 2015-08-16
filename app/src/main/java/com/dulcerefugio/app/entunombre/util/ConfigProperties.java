package com.dulcerefugio.app.entunombre.util;

import android.content.Context;

import com.dulcerefugio.app.entunombre.EnTuNombre;
import com.github.fernandodev.androidproperties.lib.AssetsProperties;
import com.github.fernandodev.androidproperties.lib.Property;

/**
 * Created by eperez on 8/15/15.
 */
public class ConfigProperties extends AssetsProperties{

    private static ConfigProperties mInstance;
    @Property public String YOUTUBE_API_KEY;
    @Property public String YOUTUBE_CHANNEL_ID;

    private ConfigProperties(Context context) {
        super(context);
    }

    public static ConfigProperties getInstance(){
        return mInstance != null ? mInstance : (mInstance=new ConfigProperties(EnTuNombre.context));
    }
}
