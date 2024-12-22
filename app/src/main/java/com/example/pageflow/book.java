package com.example.pageflow;

public class book {
    private String title;
    private String category;
    private String uuid;
    private long timestamp;
    private String url;

    public book() {

    }

    public book(String id, String category, String uid, long timestamp, String url) {
        this.title = id;
        this.category = category;
        this.uuid = uid;
        this.timestamp = timestamp;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
