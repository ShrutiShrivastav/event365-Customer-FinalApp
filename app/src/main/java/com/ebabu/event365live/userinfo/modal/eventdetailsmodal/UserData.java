package com.ebabu.event365live.userinfo.modal.eventdetailsmodal;

import com.ebabu.event365live.homedrawer.modal.rsvpmodal.Address;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserData {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("start")
    @Expose
    private String start;
    @SerializedName("end")
    @Expose
    private String end;
    @SerializedName("reviewCount")
    @Expose
    private Object reviewCount;
    @SerializedName("rating")
    @Expose
    private Integer rating;
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
    private List<VenueImages> venueVenuImages = null;
    @SerializedName("reviews")
    @Expose
    private List<Review> reviews = null;
    @SerializedName("relatedEvents")
    @Expose
    private List<RelatedEvent> relatedEvents = null;

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

    public Object getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Object reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
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
        return venueVenuImages;
    }

    public void setVenueVenuImages(List<VenueImages> venueVenuImages) {
        this.venueVenuImages = venueVenuImages;
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
        @SerializedName("venueImage")
        @Expose
        private String venueImage = null;

        public String getVenueImage() {
            return venueImage;
        }

        public void setVenueImage(String venueImage) {
            this.venueImage = venueImage;
        }
    }





}