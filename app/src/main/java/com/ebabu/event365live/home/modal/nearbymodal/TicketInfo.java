package com.ebabu.event365live.home.modal.nearbymodal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TicketInfo implements Parcelable {
    @SerializedName("pricePerTicket")
    @Expose
    private String pricePerTicket;

    protected TicketInfo(Parcel in) {
    }

    public static final Creator<TicketInfo> CREATOR = new Creator<TicketInfo>() {
        @Override
        public TicketInfo createFromParcel(Parcel in) {
            return new TicketInfo(in);
        }

        @Override
        public TicketInfo[] newArray(int size) {
            return new TicketInfo[size];
        }
    };

    public String getPricePerTicket() {
        return pricePerTicket;
    }

    public void setPricePerTicket(String pricePerTicket) {
        this.pricePerTicket = pricePerTicket;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
