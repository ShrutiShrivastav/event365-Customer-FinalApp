package com.ebabu.event365live.homedrawer.modal.bubblecategory;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class EventSubCategoryModal  {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private List<EventSubCategoryData> data = null;
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

    public List<EventSubCategoryData> getEventSubCatData() {
        return data;
    }

    public void setEventSubCatData(List<EventSubCategoryData> data) {
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


}
