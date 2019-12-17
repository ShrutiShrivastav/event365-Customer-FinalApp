package com.ebabu.event365live.home.modal.nearbymodal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GuestUser {

    @SerializedName("profilePic")
    @Expose
    private String profilePic;

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
