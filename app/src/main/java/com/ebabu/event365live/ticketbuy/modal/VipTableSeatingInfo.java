package com.ebabu.event365live.ticketbuy.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VipTableSeatingInfo {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("ticketType")
    @Expose
    private String ticketType;
    @SerializedName("ticketName")
    @Expose
    private String ticketName;
    @SerializedName("noOfTables")
    @Expose
    private Integer noOfTables;
    @SerializedName("pricePerTable")
    @Expose
    private String pricePerTable;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("totalQuantity")
    @Expose
    private Integer totalQuantity;
    @SerializedName("parsonPerTable")
    @Expose
    private Integer parsonPerTable;

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

    public Object getNoOfTables() {
        return noOfTables;
    }

    public void setNoOfTables(Integer noOfTables) {
        this.noOfTables = noOfTables;
    }

    public String getPricePerTable() {
        return pricePerTable;
    }

    public void setPricePerTable(String pricePerTable) {
        this.pricePerTable = pricePerTable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Integer getParsonPerTable() {
        return parsonPerTable;
    }

    public void setParsonPerTable(Integer parsonPerTable) {
        this.parsonPerTable = parsonPerTable;
    }

    public String getPricePerTicket() {
        return pricePerTicket;
    }

    public void setPricePerTicket(String pricePerTicket) {
        this.pricePerTicket = pricePerTicket;
    }
}
