package com.ebabu.event365live.home.modal.nearbymodal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Host {


    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("name")
    @Expose
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
