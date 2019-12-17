package com.ebabu.event365live.userinfo.modal.userdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetUserDetailsModal {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private List<UserDetailsData> data = null;
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

    public List<UserDetailsData> getData() {
        return data;
    }

    public void setData(List<UserDetailsData> data) {
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

    public class UserDetailsData{
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("password")
        @Expose
        private String password;
        @SerializedName("userType")
        @Expose
        private String userType;
        @SerializedName("isPhoneVerified")
        @Expose
        private Integer isPhoneVerified;
        @SerializedName("isEmailVerified")
        @Expose
        private Integer isEmailVerified;
        @SerializedName("isAdminVerified")
        @Expose
        private Object isAdminVerified;
        @SerializedName("deviceToken")
        @Expose
        private String deviceToken;
        @SerializedName("deviceType")
        @Expose
        private String deviceType;
        @SerializedName("countryCode")
        @Expose
        private Integer countryCode;
        @SerializedName("phoneNo")
        @Expose
        private String phoneNo;
        @SerializedName("address")
        @Expose
        private String address;
        @SerializedName("city")
        @Expose
        private String city;
        @SerializedName("state")
        @Expose
        private String state;
        @SerializedName("zip")
        @Expose
        private String zip;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("emailOTP")
        @Expose
        private Integer emailOTP;
        @SerializedName("token")
        @Expose
        private String token;
        @SerializedName("profilePic")
        @Expose
        private String profilePic;
        @SerializedName("shortInfo")
        @Expose
        private String shortInfo;
        @SerializedName("URL")
        @Expose
        private String uRL;
        @SerializedName("isRemind")
        @Expose
        private Boolean isRemind;
        @SerializedName("isNotify")
        @Expose
        private Boolean isNotify;
        @SerializedName("roles")
        @Expose
        private Object roles;
        @SerializedName("createdBy")
        @Expose
        private Integer createdBy;
        @SerializedName("latitude")
        @Expose
        private String latitude;
        @SerializedName("longitude")
        @Expose
        private String longitude;
        @SerializedName("deviceId")
        @Expose
        private Object deviceId;
        @SerializedName("isContactVia")
        @Expose
        private Boolean isContactVia;

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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public Integer getIsPhoneVerified() {
            return isPhoneVerified;
        }

        public void setIsPhoneVerified(Integer isPhoneVerified) {
            this.isPhoneVerified = isPhoneVerified;
        }

        public Integer getIsEmailVerified() {
            return isEmailVerified;
        }

        public void setIsEmailVerified(Integer isEmailVerified) {
            this.isEmailVerified = isEmailVerified;
        }

        public Object getIsAdminVerified() {
            return isAdminVerified;
        }

        public void setIsAdminVerified(Object isAdminVerified) {
            this.isAdminVerified = isAdminVerified;
        }

        public String getDeviceToken() {
            return deviceToken;
        }

        public void setDeviceToken(String deviceToken) {
            this.deviceToken = deviceToken;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public Integer getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(Integer countryCode) {
            this.countryCode = countryCode;
        }

        public String getPhoneNo() {
            return phoneNo;
        }

        public void setPhoneNo(String phoneNo) {
            this.phoneNo = phoneNo;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
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

        public Integer getEmailOTP() {
            return emailOTP;
        }

        public void setEmailOTP(Integer emailOTP) {
            this.emailOTP = emailOTP;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public void setProfilePic(String profilePic) {
            this.profilePic = profilePic;
        }

        public String getShortInfo() {
            return shortInfo;
        }

        public void setShortInfo(String shortInfo) {
            this.shortInfo = shortInfo;
        }

        public String getURL() {
            return uRL;
        }

        public void setURL(String uRL) {
            this.uRL = uRL;
        }

        public Boolean getIsRemind() {
            return isRemind;
        }

        public void setIsRemind(Boolean isRemind) {
            this.isRemind = isRemind;
        }

        public Boolean getIsNotify() {
            return isNotify;
        }

        public void setIsNotify(Boolean isNotify) {
            this.isNotify = isNotify;
        }

        public Object getRoles() {
            return roles;
        }

        public void setRoles(Object roles) {
            this.roles = roles;
        }

        public Integer getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(Integer createdBy) {
            this.createdBy = createdBy;
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

        public Object getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(Object deviceId) {
            this.deviceId = deviceId;
        }

        public Boolean getIsContactVia() {
            return isContactVia;
        }

        public void setIsContactVia(Boolean isContactVia) {
            this.isContactVia = isContactVia;
        }
    }
}
