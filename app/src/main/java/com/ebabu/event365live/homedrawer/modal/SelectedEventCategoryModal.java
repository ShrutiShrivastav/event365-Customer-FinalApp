package com.ebabu.event365live.homedrawer.modal;

import java.io.Serializable;

public class SelectedEventCategoryModal implements Serializable {
    private String id;
    private String eventName;

    public SelectedEventCategoryModal(String id, String eventName) {
        this.id = id;
        this.eventName = eventName;
    }

    public String getId() {
        return id;
    }

    public String getEventName() {
        return eventName;
    }
}
