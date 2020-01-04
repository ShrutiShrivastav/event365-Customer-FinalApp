package com.ebabu.event365live.listener;

public interface RsvpAcceptListener {
    void acceptRejectListener(int rsvpId,int eventId, String statusMsg);
}
