package com.ebabu.event365live.home.modal.nearbymodal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventImage implements Parcelable{

    @SerializedName("eventImage")
    @Expose
    private String eventImage;


    protected EventImage(Parcel in) {
        eventImage = in.readString();
    }

    public static final Creator<EventImage> CREATOR = new Creator<EventImage>() {
        @Override
        public EventImage createFromParcel(Parcel in) {
            return new EventImage(in);
        }

        @Override
        public EventImage[] newArray(int size) {
            return new EventImage[size];
        }
    };

    public String getEventImage() {
        return eventImage;
    }

    public void setEventImage(String eventImage) {
        this.eventImage = eventImage;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eventImage);
    }
}
