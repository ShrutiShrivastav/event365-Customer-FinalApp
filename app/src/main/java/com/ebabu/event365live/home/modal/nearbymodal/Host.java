package com.ebabu.event365live.home.modal.nearbymodal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Host {
    @SerializedName("name")
    @Expose
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
