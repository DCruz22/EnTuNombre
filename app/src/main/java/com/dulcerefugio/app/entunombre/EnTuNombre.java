package com.dulcerefugio.app.entunombre;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dulcerefugio.app.entunombre.data.dao.DaoMaster;
import com.dulcerefugio.app.entunombre.data.dao.DaoSession;
import com.dulcerefugio.app.entunombre.data.dao.openhelpers.EnTuNombreOpenHelper;
import com.dulcerefugio.app.entunombre.logic.Preferences;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.logging.Handler;

public class EnTuNombre extends Application{

    //======================================================================================
    //PROPERTIES
    //======================================================================================
    private static EnTuNombre instance;
    private static DaoSession daoSession;
    public static volatile Context context;
    private Preferences mPreferences;

    //======================================================================================
    //OVERRIDEN METHODS
    //======================================================================================
    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        mPreferences = Preferences.getInstance();
        setupDatabase();


        //ImageLoader Configuration

        File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());

        //Default Display Options
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.no_image)
                //.showImageForEmptyUri(R.drawable.locked_category)
                .showImageOnFail(R.drawable.no_image)
                .delayBeforeLoading(500)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .build();

        //Image Loader Configurations
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getApplicationContext())
                .threadPoolSize(8)
                .threadPriority(Thread.NORM_PRIORITY - 1)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .memoryCacheSizePercentage(20)
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .imageDownloader(new BaseImageDownloader(getApplicationContext()))
                .defaultDisplayImageOptions(options)
                .build();

        ImageLoader.getInstance().init(config);
    }

    //======================================================================================
    //METHODS
    //======================================================================================

    private void setupDatabase(){
        SQLiteOpenHelper helper = new EnTuNombreOpenHelper(context, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    //======================================================================================
    //Getters and Setters
    //======================================================================================

    public DaoSession getDaoSession(){
        return daoSession;
    }

    public Preferences getmPreferences() {
        return mPreferences;
    }

    //======================================================================================
    //CONSTRUCTORS
    //======================================================================================
    public EnTuNombre() {
        super();
        instance = this;
    }



    public static EnTuNombre getInstance() {
        return (instance == null) ? instance = new EnTuNombre() : instance;
    }
}
