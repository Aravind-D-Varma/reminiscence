package my.project.nostalgia.models;
import androidx.annotation.Nullable;

import java.util.Date;
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

    @Override
    public boolean equals(@Nullable Object obj) {

        final Memory other = (Memory) obj;

        if(!(this.mTitle).equals(other.getTitle()))
            return false;
        if(!(this.mDetail).equals(other.getDetail()))
            return false;
        if(!(this.mEvent).equals(other.getEvent()))
            return false;
        if(!(this.mMediaPaths).equals(other.getMediaPaths()))
            return false;

        return true;
    }
}
