package com.ebabu.event365live.homedrawer.modal.bubblecategory;

import android.os.Parcel;
import android.os.Parcelable;

import com.ebabu.event365live.home.modal.GetCategoryModal;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class EventCategoryModal implements Parcelable {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private Data data = null;
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;

    public class Data implements Parcelable {

        @SerializedName("category")
        @Expose
        private ArrayList<EventCategoryData> category = null;
        @SerializedName("maxPrice")
        @Expose
        private GetCategoryModal.MaxPrice maxPrice;


        protected Data(Parcel in) {
            category = in.createTypedArrayList(EventCategoryData.CREATOR);
        }

        public final Creator<Data> CREATOR = new Creator<Data>() {
            @Override
            public Data createFromParcel(Parcel in) {
                return new Data(in);
            }

            @Override
            public Data[] newArray(int size) {
                return new Data[size];
            }
        };

        public ArrayList<EventCategoryData> getCategory() {
            return category;
        }

        public void setCategory(ArrayList<EventCategoryData> category) {
            this.category = category;
        }

        public GetCategoryModal.MaxPrice getMaxPrice() {
            return maxPrice;
        }

        public void setMaxPrice(GetCategoryModal.MaxPrice maxPrice) {
            this.maxPrice = maxPrice;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeTypedList(category);
        }

        public class GetCategoryData {
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
    }

    public class MaxPrice {

        @SerializedName("max")
        @Expose
        private Integer max;

        public Integer getMax() {
            return max;
        }

        public void setMax(Integer max) {
            this.max = max;
        }

    }

    protected EventCategoryModal(Parcel in) {
        byte tmpSuccess = in.readByte();
        success = tmpSuccess == 0 ? null : tmpSuccess == 1;
        if (in.readByte() == 0) {
            code = null;
        } else {
            code = in.readInt();
        }
        message = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (success == null ? 0 : success ? 1 : 2));
        if (code == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(code);
        }
        dest.writeString(message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EventCategoryModal> CREATOR = new Creator<EventCategoryModal>() {
        @Override
        public EventCategoryModal createFromParcel(Parcel in) {
            return new EventCategoryModal(in);
        }

        @Override
        public EventCategoryModal[] newArray(int size) {
            return new EventCategoryModal[size];
        }
    };

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

}

