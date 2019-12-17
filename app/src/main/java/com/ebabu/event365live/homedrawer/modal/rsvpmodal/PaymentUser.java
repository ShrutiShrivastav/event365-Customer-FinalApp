package com.ebabu.event365live.homedrawer.modal.rsvpmodal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentUser {


    @SerializedName("QRkey")
    @Expose
    private String qRkey;
    @SerializedName("events")
    @Expose
    private Events events;

    public String getQRkey() {
        return qRkey;
    }

    public void setQRkey(String qRkey) {
        this.qRkey = qRkey;
    }

    public Events getEvents() {
        return events;
    }

    public void setEvents(Events events) {
        this.events = events;
    }

}
