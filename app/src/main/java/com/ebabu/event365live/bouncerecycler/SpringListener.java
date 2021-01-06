package com.ebabu.event365live.bouncerecycler;

public interface SpringListener {
    void onSpringUpdate(Spring var1);

    void onSpringAtRest(Spring var1);

    void onSpringActivate(Spring var1);

    void onSpringEndStateChange(Spring var1);
}
