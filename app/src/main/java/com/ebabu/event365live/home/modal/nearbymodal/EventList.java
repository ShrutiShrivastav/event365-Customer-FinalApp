package com.ebabu.event365live.home.modal.nearbymodal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class EventList implements Parcelable {

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
    @SerializedName("userLikeCount")
    @Expose
    private Integer userLikeCount;
    @SerializedName("userDisLikeCount")
    @Expose
    private Integer userDisLikeCount;
    @SerializedName("host")
    @Expose
    private Host host;
    @SerializedName("ticket_info")
    @Expose
    private ArrayList<TicketInfo> ticketInfo = null;
    @SerializedName("eventImages")
    @Expose
    private ArrayList<EventImage> eventImages = null;
    @SerializedName("address")
    @Expose
    private ArrayList<VenueEvent> venueEvents = null;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("guestList")
    @Expose
    private ArrayList<GuestList> guestList = null;
    @SerializedName("guestCount")
    @Expose
    private String guestCount;
    @SerializedName("currentLikeCount")
    @Expose
    private String currentLikeCount;
    @SerializedName("currentDisLikeCount")
    @Expose
    private String currentDisLikeCount;
    @SerializedName("isLike")
    @Expose
    private Integer isLike;

    protected EventList(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        name = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        if (in.readByte() == 0) {
            userLikeCount = null;
        } else {
            userLikeCount = in.readInt();
        }
        if (in.readByte() == 0) {
            userDisLikeCount = null;
        } else {
            userDisLikeCount = in.readInt();
        }
        ticketInfo = in.createTypedArrayList(TicketInfo.CREATOR);
        eventImages = in.createTypedArrayList(EventImage.CREATOR);
        distance = in.readString();
        guestList = in.createTypedArrayList(GuestList.CREATOR);
        guestCount = in.readString();
        currentLikeCount = in.readString();
        currentDisLikeCount = in.readString();
        if (in.readByte() == 0) {
            isLike = null;
        } else {
            isLike = in.readInt();
        }
    }

    public static final Creator<EventList> CREATOR = new Creator<EventList>() {
        @Override
        public EventList createFromParcel(Parcel in) {
            return new EventList(in);
        }

        @Override
        public EventList[] newArray(int size) {
            return new EventList[size];
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


    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getUserLikeCount() {
        return userLikeCount;
    }

    public void setUserLikeCount(Integer userLikeCount) {
        this.userLikeCount = userLikeCount;
    }

    public Integer getUserDisLikeCount() {
        return userDisLikeCount;
    }

    public void setUserDisLikeCount(Integer userDisLikeCount) {
        this.userDisLikeCount = userDisLikeCount;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public List<TicketInfo> getTicketInfo() {
        return ticketInfo;
    }

    public void setTicketInfo(ArrayList<TicketInfo> ticketInfo) {
        this.ticketInfo = ticketInfo;
    }

    public List<EventImage> getEventImages() {
        return eventImages;
    }

    public void setEventImages(ArrayList<EventImage> eventImages) {
        this.eventImages = eventImages;
    }

    public ArrayList<VenueEvent> getVenueEvents() {
        return venueEvents;
    }

    public void setVenueEvents(ArrayList<VenueEvent> venueEvents) {
        this.venueEvents = venueEvents;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public ArrayList<GuestList> getGuestList() {
        return guestList;
    }

    public void setGuestList(ArrayList<GuestList> guestList) {
        this.guestList = guestList;
    }

    public String getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(String guestCount) {
        this.guestCount = guestCount;
    }

    public String getCurrentLikeCount() {
        return currentLikeCount;
    }

    public void setCurrentLikeCount(String currentLikeCount) {
        this.currentLikeCount = currentLikeCount;
    }

    public String getCurrentDisLikeCount() {
        return currentDisLikeCount;
    }

    public void setCurrentDisLikeCount(String currentDisLikeCount) {
        this.currentDisLikeCount = currentDisLikeCount;
    }

    public Integer getIsLike() {
        return isLike;
    }

    public void setIsLike(Integer isLike) {
        this.isLike = isLike;
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
        dest.writeString(endDate);
        if (userLikeCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userLikeCount);
        }
        if (userDisLikeCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userDisLikeCount);
        }
        dest.writeTypedList(ticketInfo);
        dest.writeTypedList(eventImages);
        dest.writeString(distance);
        dest.writeTypedList(guestList);
        dest.writeString(guestCount);
        dest.writeString(currentLikeCount);
        dest.writeString(currentDisLikeCount);
        if (isLike == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(isLike);
        }
    }
}