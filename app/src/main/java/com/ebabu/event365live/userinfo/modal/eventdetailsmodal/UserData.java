package com.ebabu.event365live.userinfo.modal.eventdetailsmodal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UserData {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("eventType")
    @Expose
    private Integer eventType;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("startDate")
    @Expose
    private String startDate;
    @SerializedName("endDate")
    @Expose
    private String endDate;
    @SerializedName("startTime")
    @Expose
    private String startTime;
    @SerializedName("endTime")
    @Expose
    private String endTime;
    @SerializedName("paidType")
    @Expose
    private String paidType;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("categoryId")
    @Expose
    private Integer categoryId;
    @SerializedName("subCategoryId")
    @Expose
    private Integer subCategoryId;
    @SerializedName("guestList")
    @Expose
    private Boolean guestList;
    @SerializedName("reviewCount")
    @Expose
    private Integer reviewCount;
    @SerializedName("rating")
    @Expose
    private Integer rating;
    @SerializedName("userLikeCount")
    @Expose
    private Integer userLikeCount;
    @SerializedName("userDisLikeCount")
    @Expose
    private Integer userDisLikeCount;
    @SerializedName("venueId")
    @Expose
    private Integer venueId;
    @SerializedName("sellingStartDate")
    @Expose
    private String sellingStartDate;
    @SerializedName("sellingEndDate")
    @Expose
    private String sellingEndDate;
    @SerializedName("sellingStartTime")
    @Expose
    private String sellingStartTime;
    @SerializedName("sellingEndTime")
    @Expose
    private String sellingEndTime;
    @SerializedName("notRegVenueId")
    @Expose
    private Object notRegVenueId;
    @SerializedName("host")
    @Expose
    private Host host;
    @SerializedName("eventImages")
    @Expose
    private List<EventImage> eventImages = null;

    @SerializedName("isReviewed")
    @Expose
    private Boolean isReviewed;

    @SerializedName("isFavorite")
    @Expose
    private Boolean isFavorite;

    @SerializedName("gallery")
    @Expose
    private     Gallery gallery;

    @SerializedName("address")
    @Expose
    private ArrayList<Address> address;
    @SerializedName("venue")
    @Expose
    private Venue venue;
    @SerializedName("categoryName")
    @Expose
    private String categoryName;

    @SerializedName("ticketInfoURL")
    @Expose
    private String ticketInfoURL;

    @SerializedName("subCategoryName")
    @Expose
    private String subCategoryName;
    @SerializedName("reviews")
    @Expose
    private List<Review> reviews = null;
    @SerializedName("relatedEvents")
    @Expose
    private List<RelatedEvent> relatedEvents = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getEventType() {
        return eventType;
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getPaidType() {
        return paidType;
    }

    public void setPaidType(String paidType) {
        this.paidType = paidType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(Integer subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public Boolean getGuestList() {
        return guestList;
    }

    public void setGuestList(Boolean guestList) {
        this.guestList = guestList;
    }

    public Object getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
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

    public Integer getVenueId() {
        return venueId;
    }

    public void setVenueId(Integer venueId) {
        this.venueId = venueId;
    }

    public String getSellingStartDate() {
        return sellingStartDate;
    }

    public void setSellingStartDate(String sellingStartDate) {
        this.sellingStartDate = sellingStartDate;
    }

    public String getSellingEndDate() {
        return sellingEndDate;
    }

    public void setSellingEndDate(String sellingEndDate) {
        this.sellingEndDate = sellingEndDate;
    }

    public String getSellingStartTime() {
        return sellingStartTime;
    }

    public void setSellingStartTime(String sellingStartTime) {
        this.sellingStartTime = sellingStartTime;
    }

    public String getSellingEndTime() {
        return sellingEndTime;
    }

    public void setSellingEndTime(String sellingEndTime) {
        this.sellingEndTime = sellingEndTime;
    }

    public Object getNotRegVenueId() {
        return notRegVenueId;
    }

    public void setNotRegVenueId(Object notRegVenueId) {
        this.notRegVenueId = notRegVenueId;
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

    public ArrayList<Address> getAddress() {
        return address;
    }

    public void setAddress(ArrayList<Address> address) {
        this.address = address;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

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

    public class Address {
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

    public class Venue {
        @SerializedName("venueName")
        @Expose
        private String venueName;
        @SerializedName("venueAddress")
        @Expose
        private String venueAddress;
        @SerializedName("latitude")
        @Expose
        private String latitude;
        @SerializedName("longitude")
        @Expose
        private String longitude;

        public String getVenueName() {
            return venueName;
        }

        public void setVenueName(String venueName) {
            this.venueName = venueName;
        }

        public String getVenueAddress() {
            return venueAddress;
        }

        public void setVenueAddress(String venueAddress) {
            this.venueAddress = venueAddress;
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

    public String getTicketInfoURL() {
        return ticketInfoURL;
    }

    public void setTicketInfoURL(String ticketInfoURL) {
        this.ticketInfoURL = ticketInfoURL;
    }

    public class Gallery {
        @SerializedName("galleryEventData")
        @Expose
        private List<GalleryEventImg> eventImgList = null;

        @SerializedName("galleryVenueData")
        @Expose
        private List<GalleryVenueImg> venueImgList = null;

        public List<GalleryEventImg> getEventImgList() {
            return eventImgList;
        }

        public void setEventImgList(List<GalleryEventImg> eventImgList) {
            this.eventImgList = eventImgList;
        }

        public List<GalleryVenueImg> getVenueImgList() {
            return venueImgList;
        }

        public void setVenueImgList(List<GalleryVenueImg> venueImgList) {
            this.venueImgList = venueImgList;
        }
    }

    public class GalleryEventImg {
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

    public class GalleryVenueImg {
        @SerializedName("venueImages")
        @Expose
        private String venueImages;

        public String getVenueImages() {
            return venueImages;
        }

        public void setVenueImages(String venueImages) {
            this.venueImages = venueImages;
        }
    }

    public Gallery getGallery() {
        return gallery;
    }

    public void setGallery(Gallery gallery) {
        this.gallery = gallery;
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
}