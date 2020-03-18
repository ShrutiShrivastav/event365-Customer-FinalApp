package com.ebabu.event365live.home.modal.rsvp;

import com.ebabu.event365live.utils.CommonUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Comparator;
import java.util.List;

public class GetRsvpUserModal{

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

    public class Data{
        @SerializedName("RSPVList")
        @Expose
        private List<RSPVList> data = null;

        public List<RSPVList> getData() {
            return data;
        }

        public void setData(List<RSPVList> data) {
            this.data = data;
        }
    }

    public static class RSPVList{

        public boolean isHead() {
            return isHead;
        }

        public void setHead(boolean head) {
            isHead = head;
        }

        private boolean isHead;
        private String getEventDate;

        private String dateString;

        @SerializedName("id")
        @Expose
        private Integer id;

        @SerializedName("eventId")
        @Expose
        private Integer eventId;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("msg")
        @Expose
        private String msg;

        @SerializedName("dateTime")
        @Expose
        private String dateTime;

        private String dateOnly;
        @SerializedName("sender")
        @Expose
        private List<Sender> sender = null;

        public Integer getEventId() {
            return eventId;
        }

        public void setEventId(Integer eventId) {
            this.eventId = eventId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public List<Sender> getSender() {
            return sender;
        }

        public void setSender(List<Sender> sender) {
            this.sender = sender;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }


        public String getDateOnly() {
            return CommonUtils.getCommonUtilsInstance().getDateMonthName(dateTime,false);
        }

        public void setDateOnly(String dateOnly) {
            this.dateOnly = dateOnly;
        }

        public void setDateString(String dateString) {
            this.dateString = dateString;
        }

        public String getDateString() {
            if (dateTime != null) {
                dateString = dateTime.split("T")[0];
                return dateString;
            }
            return "";
        }

        public String getHeadTitle() {
            return dateString;
        }
        public String getGetEventDate() {
            return getEventDate;
        }

        public void setGetEventDate(String getEventDate) {
            this.getEventDate = getEventDate;
        }
    }

}
