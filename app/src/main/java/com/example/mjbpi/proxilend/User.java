package com.example.mjbpi.proxilend;

public class User {

    String mUserName;
    String mId;
    String mMail;

    public void setUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getUserName() {
        return mUserName;
    }



    public User(){}


    public void setId(String id) {
        mId = id;
    }

    public void setMail(String mail){
        mMail = mail;
    }
    public String getId() {
        return mId;
    }


    public String getMail() {
        return mMail;
    }
}
