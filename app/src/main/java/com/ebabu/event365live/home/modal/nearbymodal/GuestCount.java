package com.ebabu.event365live.home.modal.nearbymodal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GuestCount implements Parcelable {
    @SerializedName("count")
    @Expose
    private String count;

    protected GuestCount(Parcel in) {
        count = in.readString();
    }

    public static final Creator<GuestCount> CREATOR = new Creator<GuestCount>() {
        @Override
        public GuestCount createFromParcel(Parcel in) {
            return new GuestCount(in);
        }

        @Override
        public GuestCount[] newArray(int size) {
            return new GuestCount[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(count);
    }
}
