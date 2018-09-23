package com.bry.petfood.Services;

import android.content.Context;
import android.content.SharedPreferences;

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


}
