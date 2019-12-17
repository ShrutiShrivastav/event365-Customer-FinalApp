package com.ebabu.event365live.home.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetAllCategoryModal {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private List<GetAllCategoryData> data = null;
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

    public List<GetAllCategoryData> getData() {
        return data;
    }

    public void setData(List<GetAllCategoryData> data) {
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

    public class GetAllCategoryData{
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
