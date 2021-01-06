package com.ebabu.event365live.homedrawer.modal.rsvpmodal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Events {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("start")
    @Expose
    private String startDate;

    @SerializedName("end")
    @Expose
    private String endDate;


    @SerializedName("address")
    @Expose
    private List<Address> address;
    @SerializedName("ticketBooked")
    @Expose
    private List<TicketBooked> ticketBooked = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public List<Address> getAddress() {
        return address;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }

    public List<TicketBooked> getTicketBooked() {
        return ticketBooked;
    }

    public void setTicketBooked(List<TicketBooked> ticketBooked) {
        this.ticketBooked = ticketBooked;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
