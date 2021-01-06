package com.ebabu.event365live.userinfo.modal.eventdetailsmodal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Review {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("reviewStar")
    @Expose
    private String reviewStar;
    @SerializedName("reviewText")
    @Expose
    private String reviewText;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("reviewer")
    @Expose
    private Reviewer reviewer;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReviewStar() {
        return reviewStar;
    }

    public void setReviewStar(String reviewStar) {
        this.reviewStar = reviewStar;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Reviewer getReviewer() {
        return reviewer;
    }

    public void setReviewer(Reviewer reviewer) {
        this.reviewer = reviewer;
    }

}
