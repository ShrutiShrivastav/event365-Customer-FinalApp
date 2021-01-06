package com.ebabu.event365live.listener;

import com.ebabu.event365live.databinding.NearBySliderLayoutBinding;
import com.ebabu.event365live.databinding.NearYouCustomLayoutBinding;
import com.ebabu.event365live.home.modal.nearbymodal.EventList;

import java.util.ArrayList;

public interface EventLikeDislikeListener {
    void likeDislikeEvent(ArrayList<EventList> eventListData, int type, int position, boolean isFromLike);
}
