package com.ebabu.event365live.homedrawer.modal.bubblecategory;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventCategoryData implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("categoryName")
    @Expose
    private String categoryName;
    @SerializedName("categoryImage")
    @Expose
    private Object categoryImage;
    @SerializedName("isActive")
    @Expose
    private Boolean isActive;

    public EventCategoryData(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        categoryName = in.readString();
        byte tmpIsActive = in.readByte();
        isActive = tmpIsActive == 0 ? null : tmpIsActive == 1;
    }

    public static final Creator<EventCategoryData> CREATOR = new Creator<EventCategoryData>() {
        @Override
        public EventCategoryData createFromParcel(Parcel in) {
            return new EventCategoryData(in);
        }

        @Override
        public EventCategoryData[] newArray(int size) {
            return new EventCategoryData[size];
        }
    };

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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
        dest.writeString(categoryName);
        dest.writeByte((byte) (isActive == null ? 0 : isActive ? 1 : 2));
    }
}
