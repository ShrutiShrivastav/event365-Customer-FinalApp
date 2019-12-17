package com.ebabu.event365live.homedrawer.modal.bubblecategory;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventSubCategoryData implements Parcelable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("categoryId")
    @Expose
    private Integer categoryId;
    @SerializedName("subCategoryName")
    @Expose
    private String subCategoryName;
    @SerializedName("subCategoryImage")
    @Expose
    private Object subCategoryImage;

    protected EventSubCategoryData(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            categoryId = null;
        } else {
            categoryId = in.readInt();
        }
        subCategoryName = in.readString();
    }

    public static final Creator<EventSubCategoryData> CREATOR = new Creator<EventSubCategoryData>() {
        @Override
        public EventSubCategoryData createFromParcel(Parcel in) {
            return new EventSubCategoryData(in);
        }

        @Override
        public EventSubCategoryData[] newArray(int size) {
            return new EventSubCategoryData[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public Object getSubCategoryImage() {
        return subCategoryImage;
    }

    public void setSubCategoryImage(Object subCategoryImage) {
        this.subCategoryImage = subCategoryImage;
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
        if (categoryId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(categoryId);
        }
        dest.writeString(subCategoryName);
    }
}
