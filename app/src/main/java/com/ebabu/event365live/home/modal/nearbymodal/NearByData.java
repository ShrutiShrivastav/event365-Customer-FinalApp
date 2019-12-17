package com.ebabu.event365live.home.modal.nearbymodal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class NearByData implements Parcelable {


    @SerializedName("eventList")
    @Expose
    private ArrayList<EventList> eventList = null;

    protected NearByData(Parcel in) {
        eventList = in.createTypedArrayList(EventList.CREATOR);
    }

    public static final Creator<NearByData> CREATOR = new Creator<NearByData>() {
        @Override
        public NearByData createFromParcel(Parcel in) {
            return new NearByData(in);
        }

        @Override
        public NearByData[] newArray(int size) {
            return new NearByData[size];
        }
    };

    public ArrayList<EventList> getEventList() {
        return eventList;
    }

    public void setEventList(ArrayList<EventList> eventList) {
        this.eventList = eventList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(eventList);
    }
}
