package com.ebabu.event365live.homedrawer.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NotificationListModal {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private Data data;
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

    public class Sender {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("profilePic")
        @Expose
        private String profilePic;

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

        public String getProfilePic() {
            return profilePic;
        }

        public void setProfilePic(String profilePic) {
            this.profilePic = profilePic;
        }

    }

    public class NotificationList {

        @SerializedName("eventId")
        @Expose
        private Integer eventId;
        @SerializedName("msg")
        @Expose
        private String msg;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("dateTime")
        @Expose
        private String dateTime;
        @SerializedName("sender")
        @Expose
        private List<Sender> sender = null;

        public Integer getEventId() {
            return eventId;
        }

        public void setEventId(Integer eventId) {
            this.eventId = eventId;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public List<Sender> getSender() {
            return sender;
        }

        public void setSender(List<Sender> sender) {
            this.sender = sender;
        }

    }

    public class Data {

        @SerializedName("NotificationList")
        @Expose
        private List<NotificationList> notificationList = null;
        @SerializedName("page")
        @Expose
        private String page;

        public List<NotificationList> getNotificationList() {
            return notificationList;
        }

        public void setNotificationList(List<NotificationList> notificationList) {
            this.notificationList = notificationList;
        }

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }

    }
}
