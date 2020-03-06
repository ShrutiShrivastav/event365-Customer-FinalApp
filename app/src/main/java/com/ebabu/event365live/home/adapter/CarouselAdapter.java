package com.ebabu.event365live.home.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.NearBySliderLayoutBinding;
import com.ebabu.event365live.databinding.NearYouCustomLayoutBinding;
import com.ebabu.event365live.home.fragment.NearYouFragment;
import com.ebabu.event365live.home.modal.nearbymodal.EventList;
import com.ebabu.event365live.listener.BottomSheetOpenListener;
import com.ebabu.event365live.listener.EventLikeDislikeListener;
import com.ebabu.event365live.utils.CommonUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.CarouselHolder> {

    private ArrayList<EventList> eventListArrayList;
    private Context context;
    private EventLikeDislikeListener eventLikeDislikeListener;
    private NearYouFragment nearYouFragment;
    BottomSheetOpenListener bottomSheetOpenListener;

    public CarouselAdapter(ArrayList<EventList> eventListArrayList,NearYouFragment nearYouFragment) {
        this.eventListArrayList = eventListArrayList;
        this.nearYouFragment = nearYouFragment;
        eventLikeDislikeListener = nearYouFragment;
        bottomSheetOpenListener = nearYouFragment;
    }

    @NonNull
    @Override
    public CarouselAdapter.CarouselHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        NearBySliderLayoutBinding sliderLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.near_by_slider_layout, parent, false);

        return new CarouselAdapter.CarouselHolder(sliderLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselAdapter.CarouselHolder holder, int position) {
        EventList eventList = eventListArrayList.get(position);

        if (eventList.getEventImages() != null && eventList.getEventImages().size() > 0)
            Glide.with(context).load(eventList.getEventImages().get(0).getEventImage()).placeholder(R.drawable.tall_loading_img).error(R.drawable.tall_error_img).into(holder.sliderBinding.ivSliderEventImg);
        // eventDataChangeListener.eventDataListener(eventList);

        if (eventList.getGuestList() != null && eventList.getGuestList().size() > 0) {
            for (int i = 0; i < eventList.getGuestList().size(); i++) {
                switch (i) {
                    case 0:
                        holder.sliderBinding.ivShowUserOne.setVisibility(View.VISIBLE);
                        Glide.with(context).load(eventList.getGuestList().get(0).getGuestUser().getProfilePic()).into(holder.sliderBinding.ivShowUserOne);
                        break;
                    case 1:
                        holder.sliderBinding.ivShowUserTwo.setVisibility(View.VISIBLE);
                        Glide.with(context).load(eventList.getGuestList().get(1).getGuestUser().getProfilePic()).into(holder.sliderBinding.ivShowUserTwo);
                        break;
                    case 2:
                        holder.sliderBinding.ivShowThreeUser.setVisibility(View.VISIBLE);
                        Glide.with(context).load(eventList.getGuestList().get(2).getGuestUser().getProfilePic()).into(holder.sliderBinding.ivShowThreeUser);
                        break;
                }
            }

            if (eventList.getGuestList().size() > 4) {
                if (eventList.getGuestCount() != null) {
                    holder.sliderBinding.tvShowMoreUserLikeCount.setText(eventList.getGuestCount() + " + Going");
                    holder.sliderBinding.tvShowMoreUserLikeCount.setVisibility(View.VISIBLE);
                }
            }
        } else {
            holder.sliderBinding.ivShowUserOne.setVisibility(View.INVISIBLE);
            holder.sliderBinding.ivShowUserTwo.setVisibility(View.INVISIBLE);
            holder.sliderBinding.ivShowThreeUser.setVisibility(View.INVISIBLE);
            holder.sliderBinding.tvShowMoreUserLikeCount.setVisibility(View.INVISIBLE);
        }

        if (eventList.getUserLikes() != null && eventList.getUserLikes().getLike()) {
            holder.sliderBinding.likeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_bg_wrapper);
            holder.sliderBinding.disLikeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_border);
        } else if (eventList.getUserLikes() != null && eventList.getUserLikes().getDisLike()) {
            holder.sliderBinding.likeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_border);
            holder.sliderBinding.disLikeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_bg_wrapper);
        }

        /* isLike 2 shows user dislike the event or 1 means like, o means default*/
        if (eventList.getStartDate() != null) {
            String[] getDate = CommonUtils.getCommonUtilsInstance().getSplitMonthDate(eventList.getStartDate()).split(",");
            holder.sliderBinding.tvShowDateInNumeric.setText(getDate[0]);
            holder.sliderBinding.ivShowDateInName.setText(getDate[1]);
        }
        if (eventList.getCurrentLikeCount() != null)
            holder.sliderBinding.tvEventLikeCount.setText(eventList.getCurrentLikeCount());
        if (eventList.getCurrentDisLikeCount() != null)
            holder.sliderBinding.tvShowDislike.setText(eventList.getCurrentDisLikeCount());

       // clickEvent(holder.sliderBinding,eventList);
    }

    @Override
    public int getItemCount() {
        return eventListArrayList.size();
    }

    class CarouselHolder extends RecyclerView.ViewHolder {
        private NearBySliderLayoutBinding sliderBinding;
        CarouselHolder(@NonNull NearBySliderLayoutBinding sliderBinding) {
            super(sliderBinding.getRoot());
            this.sliderBinding = sliderBinding;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


//    private void clickEvent(NearBySliderLayoutBinding sliderBinding, EventList eventListData) {
//
//        /*like or dislike denotes from 1(like) or 2(dislike)*/
//
//        sliderBinding.likeEventContainer.setOnClickListener(v -> {
//            if (CommonUtils.getCommonUtilsInstance().isUserLogin()) {
//
//                if(eventListData.getUserLikes() !=null){
//                    if(eventListData.getUserLikes().getLike()){
//                        eventLikeDislikeListener.likeDislikeEvent(sliderBinding, eventListData,0,true);
//                    }else
//                        eventLikeDislikeListener.likeDislikeEvent(sliderBinding, eventListData,1,true);
//                    return;
//                }
//                eventLikeDislikeListener.likeDislikeEvent(sliderBinding, eventListData,1,true);
//                return;
//            }
//            CommonUtils.getCommonUtilsInstance().loginAlert((Activity) context, false);
//
//        });
//        sliderBinding.disLikeEventContainer.setOnClickListener(v -> {
//
//            if (CommonUtils.getCommonUtilsInstance().isUserLogin()) {
//
//                if(eventListData.getUserLikes() != null){
//                    if(eventListData.getUserLikes().getDisLike()){
//                        eventLikeDislikeListener.likeDislikeEvent(sliderBinding, eventListData,0,false);
//                    } else
//                        eventLikeDislikeListener.likeDislikeEvent(sliderBinding, eventListData,2,false);
//                    return;
//                }
//                eventLikeDislikeListener.likeDislikeEvent(sliderBinding, eventListData, 2,false);
//                return;
//            }
//            CommonUtils.getCommonUtilsInstance().loginAlert((Activity) context, false);
//
//        });
//
//        sliderBinding.sliderCardView.setOnClickListener(v -> {
//            if (nearYouFragment.getBottomSheetStatus() == BottomSheetBehavior.STATE_COLLAPSED) {
//                bottomSheetOpenListener.openBottomSheet(true);
//            } else if (nearYouFragment.getBottomSheetStatus() == BottomSheetBehavior.STATE_EXPANDED)
//                bottomSheetOpenListener.openBottomSheet(false);
//
//        });
//    }

}