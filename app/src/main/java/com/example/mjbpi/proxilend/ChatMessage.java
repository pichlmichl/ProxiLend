package com.example.mjbpi.proxilend;

public class ChatMessage {

    private String mName;
    private String mId;
    private String mMessage;

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
}