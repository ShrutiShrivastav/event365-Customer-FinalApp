package com.ebabu.event365live.ticketbuy.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VipTicketInfo {
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("ticketType")
    @Expose
    private String ticketType;
    @SerializedName("ticketName")
    @Expose
    private String ticketName;
    @SerializedName("totalQuantity")
    @Expose
    private Integer totalQuantity;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("pricePerTicket")
    @Expose
    private String pricePerTicket;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getTicketName() {
        return ticketName;
    }

    public void setTicketName(String ticketName) {
        this.ticketName = ticketName;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPricePerTicket() {
        return pricePerTicket;
    }

    public void setPricePerTicket(String pricePerTicket) {
        this.pricePerTicket = pricePerTicket;
    }


}
