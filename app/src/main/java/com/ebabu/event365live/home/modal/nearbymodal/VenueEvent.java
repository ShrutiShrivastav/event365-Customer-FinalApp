package com.ebabu.event365live.home.modal.nearbymodal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VenueEvent implements Parcelable{
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;

    @SerializedName("venueAddress")
    @Expose
    private String venueAddress;

    protected VenueEvent(Parcel in) {
        latitude = in.readString();
        longitude = in.readString();
        venueAddress = in.readString();
    }


    public static final Creator<VenueEvent> CREATOR = new Creator<VenueEvent>() {
        @Override
        public VenueEvent createFromParcel(Parcel in) {
            return new VenueEvent(in);
        }

        @Override
        public VenueEvent[] newArray(int size) {
            return new VenueEvent[size];
        }
    };

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(venueAddress);
    }
}
