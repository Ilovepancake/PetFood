package com.bry.petfood.Models;

public class CurrentUser {
    private String mUserName;
    private String mUserId;
    private String mUserEmail;
    private String mPassword;

    public CurrentUser(){}

    public CurrentUser(String name, String uid, String email, String password){
        this.mUserName = name;
        this.mUserId = uid;
        this.mUserEmail = email;
        this.mPassword = password;
    }

    public CurrentUser(String name, String email, String password){
        this.mUserName = name;
        this.mUserEmail = email;
        this.mPassword = password;
    }



    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String mUserName) {
        this.mUserName = mUserName;
    }



    public String getmUserId() {
        return mUserId;
    }

    public void setUserId(String mUserId) {
        this.mUserId = mUserId;
    }



    public String getUserEmail() {
        return mUserEmail;
    }

    public void setUserEmail(String mUserEmail) {
        this.mUserEmail = mUserEmail;
    }



    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }
}
