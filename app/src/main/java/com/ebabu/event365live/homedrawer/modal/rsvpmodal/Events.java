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
    @SerializedName("description2")
    @Expose
    private String description2;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("eventCode")
    @Expose
    private String eventCode;
    @SerializedName("address")
    @Expose
    private List<Address> address = null;
    @SerializedName("eventImages")
    @Expose
    private List<EventImage> eventImages = null;
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

    public void setStartDate(String start) {
        this.startDate = start;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String end) {
        this.endDate = end;
    }

    public String getDescription2() {
        return description2;
    }

    public void setDescription2(String description2) {
        this.description2 = description2;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public List<Address> getAddress() {
        return address;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }

    public List<EventImage> getEventImages() {
        return eventImages;
    }

    public void setEventImages(List<EventImage> eventImages) {
        this.eventImages = eventImages;
    }

    public List<TicketBooked> getTicketBooked() {
        return ticketBooked;
    }

    public void setTicketBooked(List<TicketBooked> ticketBooked) {
        this.ticketBooked = ticketBooked;
    }


    public class EventImage {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("eventImage")
        @Expose
        private String eventImage;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getEventImage() {
            return eventImage;
        }

        public void setEventImage(String eventImage) {
            this.eventImage = eventImage;
        }

    }
}
