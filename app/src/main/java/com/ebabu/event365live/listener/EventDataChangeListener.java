package com.ebabu.event365live.listener;

import com.ebabu.event365live.home.modal.nearbymodal.EventList;

public interface EventDataChangeListener {
    void eventDataListener(EventList eventList);
}
