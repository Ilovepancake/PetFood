package com.bry.petfood.Services;

import android.content.Context;
import android.content.SharedPreferences;

import com.bry.petfood.Constants;

public class SharedPreferenceManager {
    private Context mContext;
    private final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public SharedPreferenceManager(Context context){
        this.mContext = context;
    }

    public void setFirstTimeLaunch(boolean isFirstTime){
        SharedPreferences.Editor myEditor = mContext.getSharedPreferences(IS_FIRST_TIME_LAUNCH,Context.MODE_PRIVATE).edit();
        myEditor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        myEditor.apply();
    }


    public boolean isFirstTimeLaunch(){
        return mContext.getSharedPreferences(IS_FIRST_TIME_LAUNCH,Context.MODE_PRIVATE).getBoolean(IS_FIRST_TIME_LAUNCH,true);
    }

    public void setPasswordInSharedPrefs(String password){
        SharedPreferences.Editor myEditor = mContext.getSharedPreferences(Constants.PASSWORD_THING,Context.MODE_PRIVATE).edit();
        myEditor.putString(IS_FIRST_TIME_LAUNCH, password);
        myEditor.apply();
    }

    public String getUsersPasswordFromSharedPrefs(){
        String pass =  mContext.getSharedPreferences(Constants.PASSWORD_THING,Context.MODE_PRIVATE).getString(Constants.PASSWORD_THING,"null");
        SharedPreferences.Editor myEditorX = mContext.getSharedPreferences(Constants.PASSWORD_THING,Context.MODE_PRIVATE).edit();
        myEditorX.clear();
        myEditorX.apply();
        return pass;
    }


}
