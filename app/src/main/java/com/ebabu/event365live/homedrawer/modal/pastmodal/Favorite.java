package com.ebabu.event365live.homedrawer.modal.pastmodal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Favorite {
    @SerializedName("isFavorite")
    @Expose
    private Boolean isFavorite;

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

}
