package com.bry.petfood.Models;

import android.graphics.Bitmap;

public class FoodItem {
    private String mName;
    private String mPushId;
    private String mDescription;

    private Double mQuantityAvailable;
    private String mImage;
    private Double mPrice;
    private String mCategory;

    private Bitmap imageBitmap;

    public FoodItem(){}

    public FoodItem(String name, String pushId, String desc){
        this.mName = name;
        this.mPushId = pushId;
        this.mDescription = desc;
    }

    public FoodItem(String name, String pushId, String desc, Double quantity, Double price, String category){
        this.mName = name;
        this.mPushId = pushId;
        this.mDescription = desc;
        this.mQuantityAvailable = quantity;
        this.mPrice = price;
        this.mCategory = category;
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



    public Double getQuantityAvailable() {
        return mQuantityAvailable;
    }

    public void setQuantityAvailable(Double mQuantityAvailable) {
        this.mQuantityAvailable = mQuantityAvailable;
    }

    public String getImage() {
        return mImage;
    }



    public void setImage(String mImage) {
        this.mImage = mImage;
    }

    public Double getPrice() {
        return mPrice;
    }

    public void setPrice(Double mPrice) {
        this.mPrice = mPrice;
    }



    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }
}
