package com.ebabu.event365live.userinfo.modal.eventdetailsmodal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RelatedEvent {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("is_availability")
    @Expose
    private boolean is_availability;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("start")
    @Expose
    private String startDate;
    @SerializedName("end")
    @Expose
    private String end;
    @SerializedName("eventImages")
    @Expose
    private List<EventImage> eventImages = null;

    @SerializedName("venueEvents")
    @Expose
    private List<EventAddress> venueEvents;

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

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public List<EventImage> getEventImages() {
        return eventImages;
    }

    public void setEventImages(List<EventImage> eventImages) {
        this.eventImages = eventImages;
    }



    public boolean isIs_availability() {
        return is_availability;
    }

    public void setIs_availability(boolean is_availability) {
        this.is_availability = is_availability;
    }

    public List<EventAddress> getVenueEvents() {
        return venueEvents;
    }

    public void setVenueEvents(List<EventAddress> venueEvents) {
        this.venueEvents = venueEvents;
    }

    public class EventAddress{
        @SerializedName("latitude")
        @Expose
        private String latitude;
        @SerializedName("longitude")
        @Expose
        private String longitude;

        @SerializedName("venueAddress")
        @Expose
        private String venueAddress;
        @SerializedName("venueName")
        @Expose
        private String venueName;

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

        public String getVenueAddress() {
            return venueAddress;
        }

        public void setVenueAddress(String venueAddress) {
            this.venueAddress = venueAddress;
        }

        public String getVenueName() {
            return venueName;
        }

        public void setVenueName(String venueName) {
            this.venueName = venueName;
        }
    }
}
