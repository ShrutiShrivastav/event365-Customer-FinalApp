package com.ebabu.event365live.ticketbuy.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegularTicketSeatingInfo {
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
    @SerializedName("noOfTables")
    @Expose
    private Object noOfTables;
    @SerializedName("pricePerTable")
    @Expose
    private String pricePerTable;
    @SerializedName("parsonPerTable")
    @Expose
    private Object parsonPerTable;

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

    public Object getParsonPerTable() {
        return parsonPerTable;
    }

    public void setParsonPerTable(Object parsonPerTable) {
        this.parsonPerTable = parsonPerTable;
    }

}
