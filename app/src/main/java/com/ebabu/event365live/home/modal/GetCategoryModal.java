package com.ebabu.event365live.home.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetCategoryModal {
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


    public class Data {

        @SerializedName("category")
        @Expose
        private List<GetCategoryData> category = null;
        @SerializedName("maxPrice")
        @Expose
        private MaxPrice maxPrice;

        public List<GetCategoryData> getCategory() {
            return category;
        }

        public void setCategory(List<GetCategoryData> category) {
            this.category = category;
        }

        public MaxPrice getMaxPrice() {
            return maxPrice;
        }

        public void setMaxPrice(MaxPrice maxPrice) {
            this.maxPrice = maxPrice;
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
}
