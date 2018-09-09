package com.example.mjbpi.proxilend;

public class ChatMessage {

    private String mName;
    private String mId;
    private String mMessage;
    private Long mDate;
    private String mChatroomKey;

    public ChatMessage() {
        // empty constructor
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getId() {
        return mId;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public void setId(String id) {
        mId = id;
    }

    public Long getCreationDate() {
        return mDate;
    }

    public void setCreationDate(Long mDate) {
        this.mDate = mDate;
    }

    public String getChatroomKey() {
        return mChatroomKey;
    }

    public void setChatroomKey(String mChatroomKey) {
        this.mChatroomKey = mChatroomKey;
    }
}