package com.ebabu.event365live.auth.modal;

import androidx.lifecycle.Observer;

import java.io.Serializable;

public class LoginModal implements Serializable, Observer<LoginModal> {
    private Boolean isUserLogin;
    private Boolean isEventListSwipe;

    public void setUserLogin(Boolean userLogin) {
        isUserLogin = userLogin;
    }

    public void setEventListSwipe(Boolean eventListSwipe) {
        isEventListSwipe = eventListSwipe;
    }

    public Boolean getUserLogin() {
        return isUserLogin;
    }

    public Boolean getEventListSwipe() {
        return isEventListSwipe;
    }

    @Override
    public void onChanged(LoginModal loginModal) {

    }
}
