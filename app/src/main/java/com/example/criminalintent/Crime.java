package com.example.criminalintent;
import android.text.format.Time;

import java.util.Date;
import java.util.UUID;

public class Crime {

    //region Declarations
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;
    private String mNumber;
    private String mDetail;
    //endregion

    //region No-argument Constructor
    public Crime() {
        this(UUID.randomUUID());
    }
    //endregion

    public Crime(UUID uuid){
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

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
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
