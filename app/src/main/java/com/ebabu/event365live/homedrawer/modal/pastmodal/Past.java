package com.ebabu.event365live.homedrawer.modal.pastmodal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Past {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("venueEvents")
    @Expose
    private ArrayList<VenueEvent> venueEvents = null;
    @SerializedName("start")
    @Expose
    private String startDate;

    @SerializedName("end")
    @Expose
    private String endDate;
    @SerializedName("favorite")
    @Expose
    private Favorite favorite;
    @SerializedName("eventImages")
    @Expose
    private List<EventImage> eventImages = null;

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

    public ArrayList<VenueEvent> getVenueEvents() {
        return venueEvents;
    }

    public void setVenueEvents(ArrayList<VenueEvent> venueEvents) {
        this.venueEvents = venueEvents;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Favorite getFavorite() {
        return favorite;
    }

    public void setFavorite(Favorite favorite) {
        this.favorite = favorite;
    }

    public List<EventImage> getEventImages() {
        return eventImages;
    }

    public void setEventImages(List<EventImage> eventImages) {
        this.eventImages = eventImages;
    }

    public class VenueEvent{
        @SerializedName("latitude")
        @Expose
        private String latitude;
        @SerializedName("longitude")
        @Expose
        private String longitude;

        @SerializedName("venueAddress")
        @Expose
        private String venueAddress;

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
    }

}
