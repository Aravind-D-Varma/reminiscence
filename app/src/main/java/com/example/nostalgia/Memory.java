package com.example.nostalgia;

import java.util.Date;
import java.util.UUID;

public class Memory {

    //region Declarations
    private UUID mId;
    private String mTitle;
    private String mEvent;

    public String getEvent() {
        return mEvent;
    }

    public void setEvent(String event) {
        mEvent = event;
    }

    private Date mDate;
    private boolean mSolved;
    private String mSuspect;
    private String mNumber;
    private String mDetail;
    //endregion

    //region No-argument Constructor
    public Memory() {
        this(UUID.randomUUID());
    }
    //endregion

    public Memory(UUID uuid){
        mId = uuid;
        mDate = new Date();
    }

    // region ID and Photo getter
    public UUID getId() {
        return mId;
    }

    public String getPhotoFileName(){
        return "IMG_" + this.getId().toString() + ".jpg";
    }
    //endregion

    // region Getters and Setters for Title, Date, Time, isSolved


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

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String number) {
        mNumber = number;
    }

    //endregion,
}
