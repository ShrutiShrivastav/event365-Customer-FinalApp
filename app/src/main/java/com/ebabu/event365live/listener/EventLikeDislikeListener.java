package com.ebabu.event365live.listener;

import com.ebabu.event365live.databinding.NearBySliderLayoutBinding;
import com.ebabu.event365live.databinding.NearYouCustomLayoutBinding;
import com.ebabu.event365live.home.modal.nearbymodal.EventList;

public interface EventLikeDislikeListener {
    void likeDislikeEvent(NearBySliderLayoutBinding customLayoutBinding, EventList eventListData, int type, boolean fromLike);
}
