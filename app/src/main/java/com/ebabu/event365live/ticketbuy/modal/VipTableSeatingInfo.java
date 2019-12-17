package com.ebabu.event365live.ticketbuy.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class    VipTableSeatingInfo {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("ticketType")
    @Expose
    private String ticketType;
    @SerializedName("ticketName")
    @Expose
    private Object ticketName;
    @SerializedName("noOfTables")
    @Expose
    private Object noOfTables;
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

    public Object getTicketName() {
        return ticketName;
    }

    public void setTicketName(Object ticketName) {
        this.ticketName = ticketName;
    }

    public Object getNoOfTables() {
        return noOfTables;
    }

    public void setNoOfTables(Object noOfTables) {
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
}
