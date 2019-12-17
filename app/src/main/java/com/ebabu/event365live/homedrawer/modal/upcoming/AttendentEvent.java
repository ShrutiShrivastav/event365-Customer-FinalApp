package com.ebabu.event365live.homedrawer.modal.upcoming;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AttendentEvent implements Parcelable {
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
    private List<AttendEventImg> eventImages = null;
    @SerializedName("venueEvents")
    @Expose
    private ArrayList<AttendVenueEvent> venueEvents = null;

    protected AttendentEvent(Parcel in) {
        if (in.readByte() == 0){
            id = null;
        } else {
            id = in.readInt();
        }
        name = in.readString();
        startDate = in.readString();
        startTime = in.readString();
    }

    public static final Creator<AttendentEvent> CREATOR = new Creator<AttendentEvent>() {
        @Override
        public AttendentEvent createFromParcel(Parcel in) {
            return new AttendentEvent(in);
        }

        @Override
        public AttendentEvent[] newArray(int size) {
            return new AttendentEvent[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(name);
        dest.writeString(startDate);
        dest.writeString(startTime);
    }

    public class AttendVenueEvent{
        @SerializedName("latitude")
        @Expose
        private String latitude;
        @SerializedName("longitude")
        @Expose
        private String longitude;

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
