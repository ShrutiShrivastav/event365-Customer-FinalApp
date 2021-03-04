package com.ebabu.event365live.ticketbuy.modal.ticketmodal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FinalSelectTicketModal {

    @SerializedName("tickets")
    @Expose
    private List<Ticket> tickets = null;

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public class Ticket {

        private Boolean isSeatingTable;

        @SerializedName("id")
        @Expose
        private Integer ticketId;
        @SerializedName("ticketType")
        @Expose
        private String ticketType;
        @SerializedName("ticketName")
        @Expose
        private String ticketName;
        @SerializedName("ticketNumber")
        @Expose
        private String ticketNumber;
        @SerializedName("totalQuantity")
        @Expose
        private Integer totalQuantity;
        @SerializedName("description")
        @Expose
        private String description;

        @SerializedName("pricePerTicket")
        @Expose
        private Float pricePerTicket;


        @SerializedName("noOfTables")
        @Expose
        private Integer noOfTables;


        @SerializedName("parsonPerTable")
        @Expose
        private Integer parsonPerTable;

        @SerializedName("pricePerTable")
        @Expose
        private Integer pricePerTable;


        @SerializedName("discountedPrice")
        @Expose
        private Float discountedPrice;

        @SerializedName("disPercentage")
        @Expose
        private Integer disPercentage;


        public String getTicketName() {
            return ticketName;
        }

        public void setTicketName(String ticketName) {
            this.ticketName = ticketName;
        }

        public Integer getTotalQuantity() {
            return totalQuantity;
        }

        public void setTotalQuantity(Integer totalQuantity) {
            this.totalQuantity = totalQuantity;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTicketNumber() {
            return ticketNumber;
        }

        public void setTicketNumber(String ticketNumber) {
            this.ticketNumber = ticketNumber;
        }

        public Float getPricePerTicket() {
            return pricePerTicket;
        }

        public void setPricePerTicket(Float pricePerTicket) {
            this.pricePerTicket = pricePerTicket;
        }

        public Integer getTicketId() {
            return ticketId;
        }

        public void setTicketId(Integer ticketId) {
            this.ticketId = ticketId;
        }

        public String getTicketType() {
            return ticketType;
        }

        public void setTicketType(String ticketType) {
            this.ticketType = ticketType;
        }


        public Boolean getSeatingTable() {
            return isSeatingTable;
        }

        public void setSeatingTable(Boolean seatingTable) {
            isSeatingTable = seatingTable;
        }

        public Integer getNoOfTables() {
            return noOfTables;
        }

        public void setNoOfTables(Integer noOfTables) {
            this.noOfTables = noOfTables;
        }

        public Integer getParsonPerTable() {
            return parsonPerTable;
        }

        public void setParsonPerTable(Integer parsonPerTable) {
            this.parsonPerTable = parsonPerTable;
        }

        public Float getDiscountedPrice() {
            return discountedPrice;
        }

        public void setDiscountedPrice(Float discountedPrice) {
            this.discountedPrice = discountedPrice;
        }

        public Integer getDisPercentage() {
            return disPercentage;
        }

        public void setDisPercentage(Integer disPercentage) {
            this.disPercentage = disPercentage;
        }

        public Integer getPricePerTable() {
            return pricePerTable;
        }

        public void setPricePerTable(Integer pricePerTable) {
            this.pricePerTable = pricePerTable;
        }
    }



}
