package com.example.mjbpi.proxilend;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class Entry implements Parcelable {

    private String mName;
    private Long mDate;
    private String mId;
    private String mType;
    private String mKey = "not assigned yet";

    public Entry() {

    }

    public Entry(Parcel in) {

        // the order needs to be the same as in writeToParcel() method
        mId = in.readString();
        mName = in.readString();
        mDate = in.readLong();
        mType = in.readString();
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

    public void setmId() {
        mId = FirebaseAuth.getInstance().getCurrentUser().getUid();
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

    public void doStuff(){
        doStuff();
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
    }


    public void setCreationDate(Long date) {
        mDate = date;
    }

    public void setKey(String key){
        mKey = key;
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
