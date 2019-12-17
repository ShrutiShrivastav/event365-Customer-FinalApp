package com.ebabu.event365live.oncelaunch.modal.nearbynoauth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class NearByNoAuthModal {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private NearByNoAuthData data;
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

    public NearByNoAuthData getData() {
        return data;
    }

    public void setData(NearByNoAuthData data) {
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

    public class NearByNoAuthData {
        @SerializedName("category")
        @Expose
        private List<Category> category = null;
        @SerializedName("eventList")
        @Expose
        private ArrayList<EventList> eventList = null;

        public List<Category> getCategory() {
            return category;
        }

        public void setCategory(List<Category> category) {
            this.category = category;
        }

        public ArrayList<EventList> getEventList() {
            return eventList;
        }

        public void setEventList(ArrayList<EventList> eventList) {
            this.eventList = eventList;
        }
    }

    public class Category {
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("categoryName")
        @Expose
        private String categoryName;
        @SerializedName("categoryImage")
        @Expose
        private Object categoryImage;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public Object getCategoryImage() {
            return categoryImage;
        }

        public void setCategoryImage(Object categoryImage) {
            this.categoryImage = categoryImage;
        }

    }

    public class EventImage {

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

    public class EventList {
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
        @SerializedName("endDate")
        @Expose
        private String endDate;
        @SerializedName("endTime")
        @Expose
        private String endTime;
        @SerializedName("userLikeCount")
        @Expose
        private Integer userLikeCount;
        @SerializedName("userDisLikeCount")
        @Expose
        private Integer userDisLikeCount;
        @SerializedName("host")
        @Expose
        private Host host;
        @SerializedName("eventImages")
        @Expose
        private List<EventImage> eventImages = null;
        @SerializedName("ticket_info")
        @Expose
        private List<TicketInfo> ticketInfo = null;
        @SerializedName("venueEvents")
        @Expose
        private List<VenueEvent> venueEvents;
        @SerializedName("distance")
        @Expose
        private String distance;
        @SerializedName("guestList")
        @Expose
        private List<GuestList> guestList = null;
        @SerializedName("guestCount")
        @Expose
        private String guestCount;

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

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
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

        public List<EventImage> getEventImages() {
            return eventImages;
        }

        public void setEventImages(List<EventImage> eventImages) {
            this.eventImages = eventImages;
        }

        public List<TicketInfo> getTicketInfo() {
            return ticketInfo;
        }

        public void setTicketInfo(List<TicketInfo> ticketInfo) {
            this.ticketInfo = ticketInfo;
        }

        public List<VenueEvent>getVenueEvents() {
            return venueEvents;
        }

        public void setVenueEvents(List<VenueEvent> venueEvents) {
            this.venueEvents = venueEvents;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public List<GuestList> getGuestList() {
            return guestList;
        }

        public void setGuestList(List<GuestList> guestList) {
            this.guestList = guestList;
        }

        public String getGuestCount() {
            return guestCount;
        }

        public void setGuestCount(String guestCount) {
            this.guestCount = guestCount;
        }
    }

    public class GuestList {
        @SerializedName("guestUser")
        @Expose
        private GuestUser guestUser;

        public GuestUser getGuestUser() {
            return guestUser;
        }

        public void setGuestUser(GuestUser guestUser) {
            this.guestUser = guestUser;
        }
    }

    public class GuestUser {

        @SerializedName("profilePic")
        @Expose
        private Object profilePic;

        public Object getProfilePic() {
            return profilePic;
        }

        public void setProfilePic(Object profilePic) {
            this.profilePic = profilePic;
        }
    }

    public class Host {

        @SerializedName("name")
        @Expose
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public class TicketInfo {

        @SerializedName("pricePerTicket")
        @Expose
        private Object pricePerTicket;

        public Object getPricePerTicket() {
            return pricePerTicket;
        }

        public void setPricePerTicket(Object pricePerTicket) {
            this.pricePerTicket = pricePerTicket;
        }

    }

    public class VenueEvent {

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