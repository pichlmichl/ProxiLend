package com.example.mjbpi.proxilend;

public class User {


    String id;
    String mail;

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getmUserName() {
        return mUserName;
    }

    String mUserName;

    public User(){}


    public void setId(String id) {
        this.id = id;
    }

    public void setMail(String mail){
        this.mail = mail;
    }
    public String getId() {
        return id;
    }


    public String getMail() {
        return mail;
    }
}
