package com.ebabu.event365live.homedrawer.modal.upcoming;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UpcomingAttendData implements Parcelable {
    @SerializedName("upcomingEvent")
    @Expose
    private List<UpcomingEvent> upcomingEvent = null;
    @SerializedName("attendentEvent")
    @Expose
    private List<AttendentEvent> attendentEvent = null;

    protected UpcomingAttendData(Parcel in) {
    }

    public static final Creator<UpcomingAttendData> CREATOR = new Creator<UpcomingAttendData>() {
        @Override
        public UpcomingAttendData createFromParcel(Parcel in) {
            return new UpcomingAttendData(in);
        }

        @Override
        public UpcomingAttendData[] newArray(int size) {
            return new UpcomingAttendData[size];
        }
    };

    public List<UpcomingEvent> getUpcomingEvent() {
        return upcomingEvent;
    }

    public void setUpcomingEvent(List<UpcomingEvent> upcomingEvent) {
        this.upcomingEvent = upcomingEvent;
    }

    public List<AttendentEvent> getAttendentEvent() {
        return attendentEvent;
    }

    public void setAttendentEvent(List<AttendentEvent> attendentEvent) {
        this.attendentEvent = attendentEvent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
