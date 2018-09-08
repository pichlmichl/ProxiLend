package com.example.mjbpi.proxilend;

public class User {

    private String mUserName;
    private String mId;
    private String mMail;


    public User(){}

    public String getUserName() {
        return mUserName;
    }
    public String getId() {
        return mId;
    }
    public String getMail() {
        return mMail;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }
    public void setId(String id) {
        mId = id;
    }
    public void setMail(String mail){
        mMail = mail;
    }



}
