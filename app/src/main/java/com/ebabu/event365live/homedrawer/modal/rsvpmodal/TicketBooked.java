package com.ebabu.event365live.homedrawer.modal.rsvpmodal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TicketBooked {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("ticketType")
    @Expose
    private String ticketType;
    @SerializedName("pricePerTicket")
    @Expose
    private String pricePerTicket;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("totalQuantity")
    @Expose
    private Integer totalQuantity;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("ticket_number_booked_rel")
    @Expose
    private List<TicketInfo> ticket_number_booked_rel = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<TicketInfo> getTicket_number_booked_rel() {
        return ticket_number_booked_rel;
    }

    public void setTicket_number_booked_rel(List<TicketInfo> ticket_number_booked_rel) {
        this.ticket_number_booked_rel = ticket_number_booked_rel;
    }

    public class TicketInfo {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("ticketNumber")
        @Expose
        private String ticketNumber;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("QRCode")
        @Expose
        private String QRCode;
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
    }
}
