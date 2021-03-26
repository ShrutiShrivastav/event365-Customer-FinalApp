package com.ebabu.event365live.homedrawer.modal.rsvpmodal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroupTicketInfo {

    @SerializedName("ticketType")
    @Expose
    private String ticketType;
    @SerializedName("pricePerTicket")
    @Expose
    private String pricePerTicket;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("ticketBookId")
    @Expose
    private Integer ticketBookId;
    @SerializedName("ticketNumber")
    @Expose
    private String ticketNumber;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("QRCode")
    @Expose
    private String QRCode;

    public GroupTicketInfo(String ticketType, String pricePerTicket, Integer id,Integer ticketBookId, String ticketNumber, String status, String QRCode) {
        this.ticketType = ticketType;
        this.pricePerTicket = pricePerTicket;
        this.id = id;
        this.ticketBookId = ticketBookId;
        this.ticketNumber = ticketNumber;
        this.status = status;
        this.QRCode = QRCode;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getPricePerTicket() {
        return pricePerTicket;
    }

    public void setPricePerTicket(String pricePerTicket) {
        this.pricePerTicket = pricePerTicket;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQRCode() {
        return QRCode;
    }

    public void setQRCode(String QRCode) {
        this.QRCode = QRCode;
    }

    public Integer getTicketBookId() {
        return ticketBookId;
    }

    public void setTicketBookId(Integer ticketBookId) {
        this.ticketBookId = ticketBookId;
    }
}