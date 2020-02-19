package com.ebabu.event365live.home.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.NearYouCustomLayoutBinding;
import com.ebabu.event365live.home.fragment.NearYouFragment;
import com.ebabu.event365live.home.modal.nearbymodal.EventList;
import com.ebabu.event365live.home.modal.nearbymodal.UserLikes;
import com.ebabu.event365live.listener.BottomSheetOpenListener;
import com.ebabu.event365live.listener.EventDataChangeListener;
import com.ebabu.event365live.listener.EventLikeDislikeListener;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.ShowToast;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.List;

public class EventSliderAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<EventList> eventLists;
    private EventDataChangeListener eventDataChangeListener;
    private EventLikeDislikeListener eventLikeDislikeListener;
    BottomSheetOpenListener bottomSheetOpenListener;
    private boolean isFirstTimeExpanded = false;
    private NearYouFragment nearYouFragment;

    //int color[] = {android.R.color.holo_blue_bright, android.R.color.holo_green_dark , android.R.color.holo_red_light ,android.R.color.darker_gray, android.R.color.holo_orange_light, android.R.color.holo_purple};
    EventList eventList;

    public EventSliderAdapter(Context context, ArrayList<EventList> eventLists, NearYouFragment nearYouFragment) {
        this.eventLists = eventLists;
        this.context = context;
        eventDataChangeListener = nearYouFragment;
        eventLikeDislikeListener = nearYouFragment;
        bottomSheetOpenListener = nearYouFragment;
        this.nearYouFragment = nearYouFragment;
    }

    @Override
    public int getCount() {
        return eventLists.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        NearYouCustomLayoutBinding customLayoutBinding = DataBindingUtil.inflate(layoutInflater, R.layout.near_you_custom_layout, container, false);

        eventList = eventLists.get(position);
        if (eventList.getEventImages() != null && eventList.getEventImages().size() > 0)
            Glide.with(context).load(eventList.getEventImages().get(0).getEventImage()).placeholder(R.drawable.tall_loading_img).error(R.drawable.tall_error_img).into(customLayoutBinding.ivNearByBg);
        // eventDataChangeListener.eventDataListener(eventList);

        if (eventList.getGuestList() != null && eventList.getGuestList().size() > 0) {
            for (int i = 0; i < eventList.getGuestList().size(); i++) {
                switch (i) {
                    case 0:
                        customLayoutBinding.ivShowUserOne.setVisibility(View.VISIBLE);
                        Glide.with(context).load(eventList.getGuestList().get(0).getGuestUser().getProfilePic()).into(customLayoutBinding.ivShowUserOne);
                        break;
                    case 1:
                        customLayoutBinding.ivShowUserTwo.setVisibility(View.VISIBLE);
                        Glide.with(context).load(eventList.getGuestList().get(1).getGuestUser().getProfilePic()).into(customLayoutBinding.ivShowUserTwo);
                        break;
                    case 2:
                        customLayoutBinding.ivShowThreeUser.setVisibility(View.VISIBLE);
                        Glide.with(context).load(eventList.getGuestList().get(2).getGuestUser().getProfilePic()).into(customLayoutBinding.ivShowThreeUser);
                        break;
                }
            }

            if (eventList.getGuestList().size() > 4) {
                if (eventList.getGuestCount() != null) {
                    customLayoutBinding.tvShowMoreUserLikeCount.setText(eventList.getGuestCount() + " + Going");
                    customLayoutBinding.tvShowMoreUserLikeCount.setVisibility(View.VISIBLE);
                }
            }
        } else {
            customLayoutBinding.ivShowUserOne.setVisibility(View.INVISIBLE);
            customLayoutBinding.ivShowUserTwo.setVisibility(View.INVISIBLE);
            customLayoutBinding.ivShowThreeUser.setVisibility(View.INVISIBLE);
            customLayoutBinding.tvShowMoreUserLikeCount.setVisibility(View.INVISIBLE);
        }

        if (eventList.getUserLikes() != null && eventList.getUserLikes().getLike()) {
            customLayoutBinding.likeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_bg_wrapper);
            customLayoutBinding.disLikeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_border);
        } else if (eventList.getUserLikes() != null && eventList.getUserLikes().getDisLike()) {
            customLayoutBinding.likeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_border);
            customLayoutBinding.disLikeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_bg_wrapper);
        }

        /* isLike 2 shows user dislike the event or 1 means like, o means default*/
        if (eventList.getStartDate() != null) {
            String[] getDate = CommonUtils.getCommonUtilsInstance().getSplitMonthDate(eventList.getStartDate()).split(",");
            customLayoutBinding.tvShowDateInNumeric.setText(getDate[0]);
            customLayoutBinding.ivShowDateInName.setText(getDate[1]);
        }
        if (eventList.getCurrentLikeCount() != null)
            customLayoutBinding.tvEventLikeCount.setText(eventList.getCurrentLikeCount());
        if (eventList.getCurrentDisLikeCount() != null)
            customLayoutBinding.tvShowDislike.setText(eventList.getCurrentDisLikeCount());

        container.addView(customLayoutBinding.getRoot());
        clickEvent(customLayoutBinding, eventList);

        return customLayoutBinding.getRoot();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    private void clickEvent(NearYouCustomLayoutBinding customLayoutBinding, EventList eventListData) {

        /*like or dislike denotes from 1(like) or 2(dislike)*/

        customLayoutBinding.likeEventContainer.setOnClickListener(v -> {
            if (CommonUtils.getCommonUtilsInstance().isUserLogin()) {

                if(eventListData.getUserLikes() !=null){
                    if(eventListData.getUserLikes().getLike()){
                        eventLikeDislikeListener.likeDislikeEvent(customLayoutBinding, eventListData,0,true);
                    }else
                        eventLikeDislikeListener.likeDislikeEvent(customLayoutBinding, eventListData,1,true);
                    return;
                }
                eventLikeDislikeListener.likeDislikeEvent(customLayoutBinding, eventListData,1,true);
                return;
            }
            CommonUtils.getCommonUtilsInstance().loginAlert((Activity) context, false);

        });
        customLayoutBinding.disLikeEventContainer.setOnClickListener(v -> {

            if (CommonUtils.getCommonUtilsInstance().isUserLogin()) {

                if(eventListData.getUserLikes() != null){
                    if(eventListData.getUserLikes().getDisLike()){
                        eventLikeDislikeListener.likeDislikeEvent(customLayoutBinding, eventListData,0,false);
                    } else
                        eventLikeDislikeListener.likeDislikeEvent(customLayoutBinding, eventListData,2,false);
                    return;
                }
                eventLikeDislikeListener.likeDislikeEvent(customLayoutBinding, eventListData, 2,false);
                return;
            }
            CommonUtils.getCommonUtilsInstance().loginAlert((Activity) context, false);

        });

        customLayoutBinding.sliderCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("fnafnlka", "onClick: " + nearYouFragment.getBottomSheetStatus());
                if (nearYouFragment.getBottomSheetStatus() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetOpenListener.openBottomSheet(true);
                } else if (nearYouFragment.getBottomSheetStatus() == BottomSheetBehavior.STATE_EXPANDED)
                    bottomSheetOpenListener.openBottomSheet(false);

            }
        });
    }


    public void likeDisLikeEvent(NearYouCustomLayoutBinding customLayoutBinding, EventList eventListData, int isLikeOrDisLike, boolean fromLike){
        if (isLikeOrDisLike == 1) {
            forLike(customLayoutBinding,eventListData);
        } else if (isLikeOrDisLike == 2) {
            forDisLike(customLayoutBinding,eventListData);
        }else if(isLikeOrDisLike == 0){
            if(fromLike){
                forLike(customLayoutBinding,eventListData);
            }else {
                forDisLike(customLayoutBinding,eventListData);
            }
        }
        notifyDataSetChanged();
    }

    private void forLike(NearYouCustomLayoutBinding customLayoutBinding, EventList eventListData){
        int currentLikeCount = Integer.parseInt(eventListData.getCurrentLikeCount());
        ++currentLikeCount;
        eventListData.setCurrentLikeCount(String.valueOf(currentLikeCount));
        customLayoutBinding.tvEventLikeCount.setText("" + currentLikeCount);
        int currentDisLikeCount = Integer.parseInt(eventListData.getCurrentDisLikeCount());
        --currentDisLikeCount;
        eventListData.setCurrentDisLikeCount(String.valueOf(currentDisLikeCount));
        if(eventListData.getUserLikes() !=null){
            eventListData.getUserLikes().setLike(true);
            eventListData.getUserLikes().setDisLike(false);
        }

        customLayoutBinding.tvShowDislike.setText("" + currentDisLikeCount);

        customLayoutBinding.disLikeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_border);
        customLayoutBinding.likeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_bg_wrapper);
    }
    private void forDisLike(NearYouCustomLayoutBinding customLayoutBinding, EventList eventListData){
        int currentLikeCount = Integer.parseInt(eventListData.getCurrentLikeCount());
        --currentLikeCount;
        eventListData.setCurrentLikeCount(String.valueOf(currentLikeCount));
        customLayoutBinding.tvEventLikeCount.setText("" + currentLikeCount);
        int currentDisLikeCount = Integer.parseInt(eventListData.getCurrentDisLikeCount());
        ++currentDisLikeCount;
        eventListData.setCurrentDisLikeCount(String.valueOf(currentDisLikeCount));
        if(eventListData.getUserLikes() !=null){
            eventListData.getUserLikes().setLike(false);
            eventListData.getUserLikes().setDisLike(true);
        }
        customLayoutBinding.tvShowDislike.setText("" + currentDisLikeCount);
        customLayoutBinding.likeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_border);
        customLayoutBinding.disLikeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_bg_wrapper);
    }
}
