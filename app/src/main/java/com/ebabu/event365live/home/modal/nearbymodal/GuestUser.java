package com.ebabu.event365live.home.modal.nearbymodal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GuestUser implements Parcelable {

    @SerializedName("profilePic")
    @Expose
    private String profilePic;

    protected GuestUser(Parcel in) {
        profilePic = in.readString();
    }

    public static final Creator<GuestUser> CREATOR = new Creator<GuestUser>() {
        @Override
        public GuestUser createFromParcel(Parcel in) {
            return new GuestUser(in);
        }

        @Override
        public GuestUser[] newArray(int size) {
            return new GuestUser[size];
        }
    };

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(profilePic);
    }
}
