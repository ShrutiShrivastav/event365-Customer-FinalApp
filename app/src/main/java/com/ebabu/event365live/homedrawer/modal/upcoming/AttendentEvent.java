package com.ebabu.event365live.homedrawer.modal.upcoming;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AttendentEvent{
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
    private String end;
    @SerializedName("eventImages")
    @Expose
    private List<AttendEventImg> eventImages = null;
    @SerializedName("venueEvents")
    @Expose
    private ArrayList<AttendVenueEvent> venueEvents = null;



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



    public List<AttendEventImg> getEventImages() {
        return eventImages;
    }

    public void setEventImages(List<AttendEventImg> eventImages) {
        this.eventImages = eventImages;
    }

    public ArrayList<AttendVenueEvent> getVenueEvents() {
        return venueEvents;
    }

    public void setVenueEvents(ArrayList<AttendVenueEvent> venueEvents) {
        this.venueEvents = venueEvents;
    }


    public class AttendVenueEvent{
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
