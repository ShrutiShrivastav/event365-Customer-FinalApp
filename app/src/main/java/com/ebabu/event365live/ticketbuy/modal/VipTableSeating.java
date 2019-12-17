package com.ebabu.event365live.ticketbuy.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VipTableSeating {
    @SerializedName("vipTicketInfo")
    @Expose
    private List<VipTicketInfo> vipTicketInfo = null;
    @SerializedName("vipSeating")
    @Expose
    private List<VipTableSeatingInfo> vipSeating = null;

    public List<VipTicketInfo> getVipTicketInfo() {
        return vipTicketInfo;
    }

    public void setVipTicketInfoSeating(List<VipTicketInfo> vipTicketInfo) {
        this.vipTicketInfo = vipTicketInfo;
    }

    public List<VipTableSeatingInfo> getVipTableSeatingInfo() {
        return vipSeating;
    }

    public void setVipSeating(List<VipTableSeatingInfo> vipSeating) {
        this.vipSeating = vipSeating;
    }
}
