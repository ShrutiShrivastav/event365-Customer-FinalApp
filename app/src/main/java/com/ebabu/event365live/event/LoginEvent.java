package com.ebabu.event365live.event;

public class LoginEvent {
    private boolean isUserLogin;
    private boolean isHomeSliderSwipeView;

    public void setUserLogin(boolean userLogin) {
        isUserLogin = userLogin;
    }

    public void setHomeSliderSwipeView(boolean homeSliderSwipeView) {
        isHomeSliderSwipeView = homeSliderSwipeView;
    }

    public boolean isUserLogin() {
        return isUserLogin;
    }

    public boolean isHomeSliderSwipeView() {
        return isHomeSliderSwipeView;
    }
}
