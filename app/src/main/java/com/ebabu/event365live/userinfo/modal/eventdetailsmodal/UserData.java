package com.ebabu.event365live.userinfo.modal.eventdetailsmodal;

import com.ebabu.event365live.homedrawer.modal.rsvpmodal.Address;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserData {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("isPhoneVerified")
    @Expose
    private Integer isPhoneVerified;
    @SerializedName("countryCode")
    @Expose
    private String countryCode;
    @SerializedName("phoneNo")
    @Expose
    private String phoneNo;
    @SerializedName("userAddress")
    @Expose
    private String userAddress;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("zip")
    @Expose
    private String zip;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("start")
    @Expose
    private String start;
    @SerializedName("end")
    @Expose
    private String end;

    @SerializedName(value = "ratingCount", alternate = "reviewCount")
    @Expose
    private Integer reviewCount;
    @SerializedName("rating")
    @Expose
    private Float rating;
    @SerializedName("sellingStart")
    @Expose
    private String sellingStart;
    @SerializedName("sellingEnd")
    @Expose
    private String sellingEnd;
    @SerializedName("is_availability")
    @Expose
    private Boolean isAvailability;
    @SerializedName("additional_info")
    @Expose
    private String additionalInfo;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("ticketInfoURL")
    @Expose
    private String ticketInfoURL;

    @SerializedName("eventHelpLine")
    @Expose
    private String eventHelpLine;
    @SerializedName("host")
    @Expose
    private Host host;
    @SerializedName("eventImages")
    @Expose
    private List<EventImage> eventImages = null;
    @SerializedName("address")
    @Expose
    private List<Address> address = null;

    @SerializedName("ticket_info")
    @Expose
    private TicketInfo ticket_info = null;

    @SerializedName("isReviewed")
    @Expose
    private Boolean isReviewed;
    @SerializedName("isFavorite")
    @Expose
    private Boolean isFavorite;
    @SerializedName("isExternalTicket")
    @Expose
    private Boolean isExternalTicket;
    @SerializedName("categoryName")
    @Expose
    private String categoryName;
    @SerializedName("subCategories")
    @Expose
    private List<SubCategory> subCategories = null;
    @SerializedName("venueGallery")
    @Expose
    private List<VenueImages> venueGallery = null;
    @SerializedName("reviews")
    @Expose
    private List<Review> reviews = null;
    @SerializedName("relatedEvents")
    @Expose
    private List<RelatedEvent> relatedEvents = null;

    @SerializedName("hostMobile")
    @Expose
    private String hostMobile;
    @SerializedName("hostAddress")
    @Expose
    private String hostAddress;
    @SerializedName("websiteUrl")
    @Expose
    private String websiteUrl;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public Integer getIsPhoneVerified() {
        return isPhoneVerified;
    }

    public void setIsPhoneVerified(Integer isPhoneVerified) {
        this.isPhoneVerified = isPhoneVerified;
    }

    public String getEventHelpLine() {
        return eventHelpLine;
    }

    public void setEventHelpLine(String eventHelpLine) {
        this.eventHelpLine = eventHelpLine;
    }

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

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getSellingStart() {
        return sellingStart;
    }

    public void setSellingStart(String sellingStart) {
        this.sellingStart = sellingStart;
    }

    public String getSellingEnd() {
        return sellingEnd;
    }

    public void setSellingEnd(String sellingEnd) {
        this.sellingEnd = sellingEnd;
    }

    public Boolean getAvailability() {
        return isAvailability;
    }

    public void setAvailability(Boolean availability) {
        isAvailability = availability;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTicketInfoURL() {
        return ticketInfoURL;
    }

    public void setTicketInfoURL(String ticketInfoURL) {
        this.ticketInfoURL = ticketInfoURL;
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

    public Boolean getReviewed() {
        return isReviewed;
    }

    public void setReviewed(Boolean reviewed) {
        isReviewed = reviewed;
    }

    public Boolean getFavorite() {
        return isFavorite;
    }

    public void setFavorite(Boolean favorite) {
        isFavorite = favorite;
    }

    public Boolean getExternalTicket() {
        return isExternalTicket;
    }

    public void setExternalTicket(Boolean externalTicket) {
        isExternalTicket = externalTicket;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<SubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<SubCategory> subCategories) {
        this.subCategories = subCategories;
    }

    public List<VenueImages> getVenueVenuImages() {
        return venueGallery;
    }

    public void setVenueVenuImages(List<VenueImages> venueVenuImages) {
        this.venueGallery = venueVenuImages;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<RelatedEvent> getRelatedEvents() {
        return relatedEvents;
    }

    public void setRelatedEvents(List<RelatedEvent> relatedEvents) {
        this.relatedEvents = relatedEvents;
    }


    public TicketInfo getTicket_info() {
        return ticket_info;
    }

    public void setTicket_info(TicketInfo ticket_info) {
        this.ticket_info = ticket_info;
    }

    public String getHostMobile() {
        return hostMobile;
    }

    public void setHostMobile(String hostMobile) {
        this.hostMobile = hostMobile;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public class SubCategory {

        @SerializedName("categoryName")
        @Expose
        private String categoryName;
        @SerializedName("subCategoryName")
        @Expose
        private String subCategoryName;

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getSubCategoryName() {
            return subCategoryName;
        }

        public void setSubCategoryName(String subCategoryName) {
            this.subCategoryName = subCategoryName;
        }

    }

    public class VenueImages {
        @SerializedName("venueImages")
        @Expose
        private String venueImage = null;

        public String getVenueImage() {
            return venueImage;
        }

        public void setVenueImage(String venueImage) {
            this.venueImage = venueImage;
        }
    }


    public class TicketInfo {
        @SerializedName("maxValue")
        @Expose
        private Integer maxValue = null;

        @SerializedName("MinValue")
        @Expose
        private Integer minValue = null;

        @SerializedName("type")
        @Expose
        private String type = null;


        public Integer getMaxValue() {
            return maxValue;
        }

        public void setMaxValue(Integer maxValue) {
            this.maxValue = maxValue;
        }

        public Integer getMinValue() {
            return minValue;
        }

        public void setMinValue(Integer minValue) {
            this.minValue = minValue;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }


}