package com.ebabu.event365live.userinfo.modal.eventdetailsmodal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventImage {
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
