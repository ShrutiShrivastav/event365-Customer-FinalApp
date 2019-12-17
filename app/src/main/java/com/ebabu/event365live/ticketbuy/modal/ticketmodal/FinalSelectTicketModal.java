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

        @SerializedName("regularTicketFlag")
        @Expose
        private Integer regularTicketFlag;

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("ticketType")
        @Expose
        private String ticketType;

        @SerializedName("ticketName")
        @Expose

        private String ticketName;
        @SerializedName("totalQuantity")
        @Expose
        private Integer totalQuantity;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("noOfTables")
        @Expose
        private Integer noOfTables;
        @SerializedName("pricePerTable")
        @Expose
        private String pricePerTable;
        @SerializedName("parsonPerTable")
        @Expose
        private Integer parsonPerTable;
        @SerializedName("pricePerTicket")
        @Expose
        private Integer pricePerTicket;

        public Integer getRegularTicketFlag() {
            return regularTicketFlag;
        }

        public void setRegularTicketFlag(Integer regularTicketFlag) {
            this.regularTicketFlag = regularTicketFlag;
        }

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

        public Integer getNoOfTables() {
            return noOfTables;
        }

        public void setNoOfTables(Integer noOfTables) {
            this.noOfTables = noOfTables;
        }

        public String getPricePerTable() {
            return pricePerTable;
        }

        public void setPricePerTable(String pricePerTable) {
            this.pricePerTable = pricePerTable;
        }

        public Integer getParsonPerTable() {
            return parsonPerTable;
        }

        public void setParsonPerTable(Integer parsonPerTable) {
            this.parsonPerTable = parsonPerTable;
        }

        public Integer getPricePerTicket() {
            return pricePerTicket;
        }

        public void setPricePerTicket(Integer pricePerTicket) {
            this.pricePerTicket = pricePerTicket;
        }

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
    }



}
