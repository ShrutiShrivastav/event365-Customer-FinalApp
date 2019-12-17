package com.ebabu.event365live.homedrawer.modal.rsvpmodal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RsvpBookedTicketData {

    @SerializedName("paymentUser")
    @Expose
    private List<PaymentUser> paymentUser = null;

    public List<PaymentUser> getPaymentUser() {
        return paymentUser;
    }

    public void setPaymentUser(List<PaymentUser> paymentUser) {
        this.paymentUser = paymentUser;
    }

}
