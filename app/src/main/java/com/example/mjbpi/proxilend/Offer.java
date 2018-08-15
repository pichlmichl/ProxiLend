package com.example.mjbpi.proxilend;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Offer implements Parcelable {

    private String mUsername = "Test Username";
    private String mName;
    private Long mDate;
    private int mId = 0;


    public Offer(String name) {
        this.mName = name;
    }

    public Offer(Parcel in) {

        // the order needs to be the same as in writeToParcel() method
        mId = in.readInt();
        mName = in.readString();
        mUsername = in.readString();
        mDate = in.readLong();

    }

    public String getName() {
        return mName;
    }

    public String getUsername() {
        return mUsername;
    }

    public Long getCreationDate() {
        return mDate;
    }

    public void doStuff(){
        doStuff();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mName);
        dest.writeString(mUsername);
        dest.writeLong(mDate);


    }

    //so wie der deutsche typ auf youtube
    public static final Creator<Offer> CREATOR = new Creator<Offer>() {

        public Offer createFromParcel(Parcel in) {
            return new Offer(in);
        }

        public Offer[] newArray(int size) {
            return new Offer[size];
        }
    };


    public void setCreationDate(Long mDate) {
        this.mDate = mDate;
    }
}
