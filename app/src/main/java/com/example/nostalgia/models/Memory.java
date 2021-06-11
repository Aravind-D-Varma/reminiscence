package com.example.nostalgia.models;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Stores the title, details, event type, date & time, and concatenated filePaths of photos and videos seperated by ",".
 */
public class Memory {

    private UUID mId;
    private String mTitle;
    private String mEvent;
    private Date mDate;
    private String mDetail;
    private String mMediaPaths;

    public Memory() {
        this(UUID.randomUUID());
    }

    public Memory(UUID uuid){
        mId = uuid;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }
    public String getMediaPaths() {
        return mMediaPaths;
    }
    public void setMediaPaths(String photos) {
        mMediaPaths = photos;
    }
    public String getEvent() {
        return mEvent;
    }
    public void setEvent(String event) {
        mEvent = event;
    }
    public String getDetail() {
        return mDetail;
    }
    public void setDetail(String detail) {
        mDetail = detail;
    }
    public String getTitle() {
        return mTitle;
    }
    public void setTitle(String title) {
        mTitle = title;
    }
    public Date getDate() {
        return mDate;
    }
    public void setDate(Date date) {
        mDate = date;
    }
}
