package com.ebabu.event365live.home.modal.nearbymodal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GuestList implements Parcelable{
    @SerializedName("guestUser")
    @Expose
    private GuestUser guestUser;


    protected GuestList(Parcel in) {
        guestUser = in.readParcelable(GuestUser.class.getClassLoader());
    }

    public static final Creator<GuestList> CREATOR = new Creator<GuestList>() {
        @Override
        public GuestList createFromParcel(Parcel in) {
            return new GuestList(in);
        }

        @Override
        public GuestList[] newArray(int size) {
            return new GuestList[size];
        }
    };

    public GuestUser getGuestUser() {
        return guestUser;
    }

    public void setGuestUser(GuestUser guestUser) {
        this.guestUser = guestUser;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(guestUser, flags);
    }
}
