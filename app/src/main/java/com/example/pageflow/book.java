package com.example.pageflow;

public class book {
    private String id;
    private String title;
    private String uid;
    long timestamp;

    public book() {

    }

    public book(String id, String title,  String uid, long timestamp) {
        this.id = id;
        this.title = title;
        this.uid = uid;
        this.timestamp = timestamp;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;


    }

}