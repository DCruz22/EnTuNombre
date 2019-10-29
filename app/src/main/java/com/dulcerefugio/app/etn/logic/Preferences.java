package com.dulcerefugio.app.etn.logic;

import com.dulcerefugio.app.etn.EnTuNombre;
import vee.android.lib.SimpleSharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eefret on 6/24/2014.
 */
public class Preferences {
    //======================================================================================
    //CONSTANTS
    //======================================================================================
    private static final String USER_EMAIL = "USER_EMAIL";
    private static final String USER_NAME = "USER_NAME";
    private static final String IS_APP_SHARED = "IS_APP_SHARED";
    private static final String WELCOME_DIALOG_SHOWN = "WELCOME_DIALOG_SHOWN";
    private static final String NEW_MOVIES_QUANTITY = "NEW_MOVIES_QUANTITY";
    private static final String RATE_APP_DIALOG_LIMIT = "RATE_APP_DIALOG_LIMIT";
    private static final String RATE_APP_DIALOG_LIMIT_COUNT = "RATE_APP_DIALOG_LIMIT_COUNT";
    private static final String PLAY_COUNT = "PLAY_COUNT";
    //======================================================================================
    //FIELDS
    //======================================================================================
    private SimpleSharedPreferences mPreferences;
    private String userEmail;
    private String userName;
    private boolean shared = false;
    private boolean welcomeDialogShown = false;
    private int newMoviesQuantity;
    private int rateAppDialogLimit;
    private int rateAppDialogLimitCount;
    private int playCount;
    //======================================================================================
    //CONSTRUCTORS
    //======================================================================================
    private Preferences() {
        this.mPreferences = new SimpleSharedPreferences(EnTuNombre.getInstance());
        if (PreferencesLoader.INSTANCE != null) {
            throw new IllegalStateException("Already Instantiated");
        }
    }

    //======================================================================================
    //INNER CLASSES
    //======================================================================================
    private static class PreferencesLoader {
        private static final Preferences INSTANCE = new Preferences();
    }

    //======================================================================================
    //METHODS
    //======================================================================================

    public static Preferences getInstance() {
        return PreferencesLoader.INSTANCE;
    }

    public String getUserEmail() {
        if(userEmail == null || userEmail.isEmpty()){
            userEmail = mPreferences.getString(USER_EMAIL,"");
        }
        return userEmail;
    }

    public List<String> getUserEmailAsList() {
        if(userEmail == null || userEmail.isEmpty()){
            userEmail = mPreferences.getString(USER_EMAIL,"");
        }
        List<String> userEmailAsList = new ArrayList<String>();
        userEmailAsList.add(userEmail);

        return userEmailAsList;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        mPreferences.putString(USER_EMAIL,userEmail);
    }

    public String getUserName() {
        if (userName == null || userName.isEmpty()){
            userName = mPreferences.getString(USER_NAME,"");
        }
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        mPreferences.putString(USER_NAME,userName);
    }

    public boolean isShared() {
        shared = mPreferences.getBoolean(IS_APP_SHARED,false);
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
        mPreferences.putBoolean(IS_APP_SHARED,shared);
    }

    public boolean isWelcomeDialogShown(){
        welcomeDialogShown = mPreferences.getBoolean(WELCOME_DIALOG_SHOWN,false);
        return welcomeDialogShown;
    }

    public void setWelcomeDialogShown(boolean welcomeDialogShown){
        this.welcomeDialogShown = welcomeDialogShown;
        mPreferences.putBoolean(WELCOME_DIALOG_SHOWN, welcomeDialogShown);
    }

    public int getNewMoviesQuantity(){
        this.newMoviesQuantity = mPreferences.getInt(NEW_MOVIES_QUANTITY, 0);
        return this.newMoviesQuantity;
    }

    public void setNewMoviesQuantity(int moviesQuantity){
        this.newMoviesQuantity = moviesQuantity;
        mPreferences.putInt(NEW_MOVIES_QUANTITY,moviesQuantity);
    }

    public int getRateAppDialogLimit(){
        this.rateAppDialogLimit = mPreferences.getInt(RATE_APP_DIALOG_LIMIT, 0);
        return this.rateAppDialogLimit;
    }

    public void setRateAppDialogLimit(int rateAppDialogLimit){
        this.rateAppDialogLimit = rateAppDialogLimit;
        mPreferences.putInt(RATE_APP_DIALOG_LIMIT, rateAppDialogLimit);
    }

    public int getRateAppDialogLimitCount(){
        this.rateAppDialogLimitCount = mPreferences.getInt(RATE_APP_DIALOG_LIMIT_COUNT, 0);
        return this.rateAppDialogLimitCount;
    }

    public void setRateAppDialogLimitCount(int rateAppDialogLimitCount){
        this.rateAppDialogLimitCount = rateAppDialogLimitCount;
        mPreferences.putInt(RATE_APP_DIALOG_LIMIT_COUNT, rateAppDialogLimitCount);
    }

    public int getPlayCount() {
        this.playCount = mPreferences.getInt(PLAY_COUNT,0);
        return playCount;
    }
    public void addPlayCount(){
        int playCount = getPlayCount();
        playCount++;
        mPreferences.putInt(PLAY_COUNT,playCount);
    }
}
