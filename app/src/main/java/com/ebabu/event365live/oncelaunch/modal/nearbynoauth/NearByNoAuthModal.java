package com.ebabu.event365live.oncelaunch.modal.nearbynoauth;

import android.util.Log;

import com.ebabu.event365live.home.modal.nearbymodal.GuestCount;
import com.ebabu.event365live.utils.CommonUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    public class EventList  implements Comparable<EventList> {

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
        @SerializedName("host")
        @Expose
        private Host host;
        @SerializedName("eventImages")
        @Expose
        private List<EventImage> eventImages = null;
        @SerializedName("address")
        @Expose
        private List<Address> address = null;
        @SerializedName("distance")
        @Expose
        private String distance;
        @SerializedName("guestList")
        @Expose
        private List<Object> guestList;
        @SerializedName("guestCount")
        @Expose
        private ArrayList<GuestCount> guestCount = null;

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

        public List<Address> getAddress() {
            return address;
        }

        public void setAddress(List<Address> address) {
            this.address = address;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public List<Object> getGuestList() {
            return guestList;
        }

        public void setGuestList(List<Object> guestList) {
            this.guestList = guestList;
        }

        public ArrayList<GuestCount> getGuestCount() {
            return guestCount;
        }

        public void setGuestCount(ArrayList<GuestCount> guestCount) {
            this.guestCount = guestCount;
        }

        @Override
        public int compareTo(EventList o) {
            int value = 0;
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();

            Log.d("fasfafas", "compareTo: "+today);



            if(!CommonUtils.getCommonUtilsInstance().getDateMonthYearName(startDate,false).equalsIgnoreCase(CommonUtils.getCommonUtilsInstance().getDateMonthYearName(o.getStartDate(),false))){
                value = -1;
            }

            Log.d("fasfsafaf", "compareTo: "+value);
            return -1;
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

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;

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

    }

    public class Address {

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