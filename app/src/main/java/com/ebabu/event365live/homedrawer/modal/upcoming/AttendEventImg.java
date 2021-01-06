package com.ebabu.event365live.homedrawer.modal.upcoming;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AttendEventImg {

    @SerializedName("eventImage")
    @Expose
    private String eventImage;

    public String getEventImage() {
        return eventImage;
    }

    public void setEventImage(String eventImage) {
        this.eventImage = eventImage;
    }
}
