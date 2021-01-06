package com.ebabu.event365live.home.modal;

import com.ebabu.event365live.home.modal.rsvp.GetRsvpUserModal;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RsvpHeaderModal {

    private int  viewType;
    private Integer id;
    private Integer eventId;
    private String status;
    private String msg;
    private String dateTime;
    private String dateOnly;
    private List<GetRsvpUserModal.Sender> sender = null;

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDateOnly() {
        return dateOnly;
    }

    public void setDateOnly(String dateOnly) {
        this.dateOnly = dateOnly;
    }

    public List<GetRsvpUserModal.Sender> getSender() {
        return sender;
    }

    public void setSender(List<GetRsvpUserModal.Sender> sender) {
        this.sender = sender;
    }
}
