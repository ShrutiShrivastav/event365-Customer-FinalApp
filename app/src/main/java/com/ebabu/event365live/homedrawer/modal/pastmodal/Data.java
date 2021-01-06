package com.ebabu.event365live.homedrawer.modal.pastmodal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {

    @SerializedName("comingSoon")
    @Expose
    private List<ComingSoon> comingSoon = null;
    @SerializedName("past")
    @Expose
    private List<Past> past = null;

    public List<ComingSoon> getComingSoon() {
        return comingSoon;
    }

    public void setComingSoon(List<ComingSoon> comingSoon) {
        this.comingSoon = comingSoon;
    }

    public List<Past> getPast() {
        return past;
    }

    public void setPast(List<Past> past) {
        this.past = past;
    }
}
