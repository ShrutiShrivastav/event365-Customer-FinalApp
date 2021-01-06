package com.ebabu.event365live.utils;

import org.greenrobot.eventbus.EventBus;

public class GlobalBus {
    private static EventBus sBus;

    public static EventBus getBusInstance()
    {
        if(sBus == null)
        {
            sBus = EventBus.getDefault();
        }
        return sBus;
    }
}
