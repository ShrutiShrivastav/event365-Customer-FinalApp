package com.ebabu.event365live.home.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SubCategoryModal {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private SubCategoryData data;
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public SubCategoryData getSubCategoryData() {
        return data;
    }

    public void setSubCategoryData(SubCategoryData data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class EventImage{
        @SerializedName("eventImage")
        @Expose
        private String eventImage;

        public String getEventImage() {
            return eventImage;
        }

        public void setEventImage(String eventImage) {
            this.eventImage = eventImage;
        }
    }

    public class Event{
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("startDate")
        @Expose
        private String startDate;
        @SerializedName("startTime")
        @Expose
        private String startTime;
        @SerializedName("eventImages")
        @Expose
        private List<EventImage> eventImages = null;
        @SerializedName("address")
        @Expose
        private List<Address> address;

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

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public List<EventImage> getEventImages() {
            return eventImages;
        }

        public void setEventImages(List<EventImage> eventImages) {
            this.eventImages = eventImages;
        }

        public List<Address> getAddress() {
            return address;
        }

        public void setAddress(List<Address> address) {
            this.address = address;
        }
    }

    public class SubCategoryData{
        @SerializedName("events")
        @Expose
        private List<Event> events = null;
        @SerializedName("page")
        @Expose
        private Integer page;

        public List<Event> getEvents() {
            return events;
        }

        public void setEvents(List<Event> events) {
            this.events = events;
        }

        public Integer getPage() {
            return page;
        }

        public void setPage(Integer page) {
            this.page = page;
        }
    }

    public class Address{
        @SerializedName("latitude")
        @Expose
        private String latitude;
        @SerializedName("longitude")
        @Expose
        private String longitude;

        @SerializedName("venueAddress")
        @Expose
        private String venueAddress;


        public String getVenueAddress() {
            return venueAddress;
        }

        public void setVenueAddress(String venueAddress) {
            this.venueAddress = venueAddress;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

    }
}

