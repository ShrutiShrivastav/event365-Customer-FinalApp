package com.ebabu.event365live.homedrawer.modal.searchevent;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchEventModal {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose

    private Data data;

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("page")
    @Expose
    private String page;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }


    public Data getData() {
        return data;
    }

    public void setData(Data data) {
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

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }


    public class SearchData{
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("start")
        @Expose
        private String startDate;

        @SerializedName("eventImages")
        @Expose
        private List<EventImages> eventImage;
        @SerializedName("latitude")
        @Expose
        private String latitude;
        @SerializedName("longitude")
        @Expose
        private String longitude;


        @SerializedName("venueAddress")
        @Expose
        private String venueAddress;


        @SerializedName("city")
        @Expose
        private String city;


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

        public String getVenueAddress() {
            return venueAddress;
        }

        public void setVenueAddress(String venueAddress) {
            this.venueAddress = venueAddress;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public List<EventImages> getEventImage() {
            return eventImage;
        }

        public void setEventImage(List<EventImages> eventImage) {
            this.eventImage = eventImage;
        }

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
    public class RecentSearch {

        @SerializedName("text")
        @Expose
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

    }


    public class TopEvent {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("start")
        @Expose
        private String startDate;
        @SerializedName("eventImages")
        @Expose
        private List<EventImages> eventImage;
        @SerializedName("latitude")
        @Expose
        private String latitude;
        @SerializedName("longitude")
        @Expose
        private String longitude;

        @SerializedName("venueAddress")
        @Expose
        private String venueAddress;

        @SerializedName("city")
        @Expose
        private String city;

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

        public List<EventImages> getEventImage() {
            return eventImage;
        }

        public void setEventImage(List<EventImages> eventImage) {
            this.eventImage = eventImage;
        }

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

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }


    public class Data{
        @SerializedName("searchData")
        @Expose
        private List<SearchData> data;
        @SerializedName("recentSearch")
        @Expose
        private List<RecentSearch> recentSearch = null;

        @SerializedName("topEvents")
        @Expose
        private List<TopEvent> topEvents = null;

        public List<SearchData> getData() {
            return data;
        }

        public void setData(List<SearchData> data) {
            this.data = data;
        }

        public List<RecentSearch> getRecentSearch() {
            return recentSearch;
        }

        public void setRecentSearch(List<RecentSearch> recentSearch) {
            this.recentSearch = recentSearch;
        }

        public List<TopEvent> getTopEvents() {
            return topEvents;
        }

        public void setTopEvents(List<TopEvent> topEvents) {
            this.topEvents = topEvents;
        }
    }

    public class EventImages{
        @SerializedName("eventImage")
        @Expose
        private String eventImg;

        public String getEventImg() {
            return eventImg;
        }

        public void setEventImg(String eventImg) {
            this.eventImg = eventImg;
        }
    }

}


