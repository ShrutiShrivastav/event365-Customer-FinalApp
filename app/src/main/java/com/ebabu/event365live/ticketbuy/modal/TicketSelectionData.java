package com.ebabu.event365live.ticketbuy.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TicketSelectionData {

    @SerializedName("freeTicket")
    @Expose
    private List<FreeTicket> freeTicket = null;
    @SerializedName("vipTicket")
    @Expose
    private VipTableSeating vipTableSeating;
    @SerializedName("regularTicket")
    @Expose
    private RegularTableSeating regularTableSeating;

    public List<FreeTicket> getFreeTicket() {
        return freeTicket;
    }

    public void setFreeTicket(List<FreeTicket> freeTicket) {
        this.freeTicket = freeTicket;
    }


    public VipTableSeating getVipTableSeating() {
        return vipTableSeating;
    }

    public void setVipTableSeating(VipTableSeating vipTableSeating) {
        this.vipTableSeating = vipTableSeating;
    }


    public RegularTableSeating getRegularTableSeating() {
        return regularTableSeating;
    }

    public void setRegularTableSeating(RegularTableSeating regularTableSeating) {
        this.regularTableSeating = regularTableSeating;
    }
}
