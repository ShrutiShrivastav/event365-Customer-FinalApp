package com.ebabu.event365live.ticketbuy.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RegularTableSeating {
    @SerializedName("regularTicketInfo")
    @Expose
    private List<RegularTicketInfo> regularTicketInfo = null;
    @SerializedName("regularSeating")
    @Expose
    private List<RegularTicketSeatingInfo> regularSeating = null;

    public List<RegularTicketInfo> getRegularTicketInfo() {
        return regularTicketInfo;
    }

    public void setRegularTicketInfo(List<RegularTicketInfo> regularTicketInfo) {
        this.regularTicketInfo = regularTicketInfo;
    }

    public List<RegularTicketSeatingInfo> getRegularTicketSeatingInfo() {
        return regularSeating;
    }

    public void setRegularTicketSeatingInfo(List<RegularTicketSeatingInfo> regularSeating) {
        this.regularSeating = regularSeating;
    }

}
