package com.bry.petfood.Models;

public class FoodItem {
    private String mName;
    private String mPushId;
    private String mDescription;

    public FoodItem(){}

    public FoodItem(String name, String pushId, String desc){
        this.mName = name;
        this.mPushId = pushId;
        this.mDescription = desc;
    }


    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getPushId() {
        return mPushId;
    }



    public void setPushId(String mPushId) {
        this.mPushId = mPushId;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }
}
