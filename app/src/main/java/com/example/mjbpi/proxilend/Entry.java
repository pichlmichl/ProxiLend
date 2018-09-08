package com.example.mjbpi.proxilend;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

public class Entry implements Parcelable {

    private String mName;
    private Long mDate;
    private String mId;
    private String mType;
    private String mKey = "not assigned yet";
    private Double mLong = 0.0;
    private Double mLat = 0.0;
    private int mDist;

    public Entry() {

    }

    public Entry(Parcel in) {

        // the order needs to be the same as in writeToParcel() method
        mId = in.readString();
        mName = in.readString();
        mDate = in.readLong();
        mType = in.readString();
        mKey = in.readString();
        mLong = in.readDouble();
        mLat = in.readDouble();
    }

    public void setName(String name) {
        mName = name;
    }

    public void setType(String type){
        mType = type;
    }

    public void setId(String id) {
        mId = id;
    }

    public void setCreationDate(Long date) {
        mDate = date;
    }

    public void setKey(String key){
        mKey = key;
    }

    public void setLongitude(Double longitude){
        mLong = longitude;
    }

    public void setLatitude(Double latitude) {
        mLat = latitude;
    }

    public void setDistance(int dist){
        mDist = dist;
    }

    public int getDistance(){
        return mDist;
    }

    public Double getLongitude(){
        return mLong;
    }

    public Double getLatitude() {
        return mLat;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    public String getType(){
        return mType;
    }

    public String getKey(){
            return mKey;
    }

    public Long getCreationDate() {
        return mDate;
    }

    public String getId() {
        return mId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mName);
        dest.writeLong(mDate);
        dest.writeString(mType);
        dest.writeString(mKey);
        dest.writeDouble(mLat);
        dest.writeDouble(mLong);
    }




    //so wie der deutsche typ auf youtube
    public static final Creator<Entry> CREATOR = new Creator<Entry>() {

        public Entry createFromParcel(Parcel in) {
            return new Entry(in);
        }

        public Entry[] newArray(int size) {
            return new Entry[size];
        }
    };
}
