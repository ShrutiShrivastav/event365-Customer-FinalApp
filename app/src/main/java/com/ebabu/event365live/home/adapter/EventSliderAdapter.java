package com.ebabu.event365live.home.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.NearBySliderLayoutBinding;
import com.ebabu.event365live.databinding.NearYouCustomLayoutBinding;
import com.ebabu.event365live.home.fragment.NearYouFragment;
import com.ebabu.event365live.home.modal.nearbymodal.EventList;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.listener.BottomSheetOpenListener;
import com.ebabu.event365live.listener.EventDataChangeListener;
import com.ebabu.event365live.listener.EventLikeDislikeListener;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.google.gson.JsonObject;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class EventSliderAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<EventList> eventListArrayList;
    private EventDataChangeListener eventDataChangeListener;
    private EventLikeDislikeListener eventLikeDislikeListener;
    BottomSheetOpenListener bottomSheetOpenListener;
    private boolean isFirstTimeExpanded = false;
    private NearYouFragment nearYouFragment;
    EventList eventList;
    private MyLoader myLoader;
    private CompositeDisposable compositeDisposable;
    private String deviceToken;

    public EventSliderAdapter(Context context, ArrayList<EventList> eventListArrayList, NearYouFragment nearYouFragment, MyLoader myLoader) {
        this.eventListArrayList = eventListArrayList;
        this.context = context;
        eventDataChangeListener = nearYouFragment;
        eventLikeDislikeListener = nearYouFragment;
        bottomSheetOpenListener = nearYouFragment;
        this.nearYouFragment = nearYouFragment;
        this.myLoader = myLoader;
        compositeDisposable = new CompositeDisposable();
        deviceToken = CommonUtils.getCommonUtilsInstance().getDeviceAuth();
    }

    @Override
    public int getCount() {
        return eventListArrayList.size();
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
        eventList = eventListArrayList.get(position);

        if (eventList.getEventImages() != null && eventList.getEventImages().size() > 0)
            Glide.with(context).load(eventList.getEventImages().get(0).getEventImage()).placeholder(R.drawable.tall_loading_img).error(R.drawable.tall_error_img).into(customLayoutBinding.ivNearByBg);

        if (eventList.getGuestList() != null && eventList.getGuestList().size() > 0) {
            for (int i = 0; i < eventList.getGuestList().size(); i++) {
                switch (i) {
                    case 0:
                        customLayoutBinding.ivShowThreeUser.setVisibility(View.VISIBLE);
                        Glide.with(context).load(eventList.getGuestList().get(0).getGuestUser().getProfilePic()).into(customLayoutBinding.ivShowThreeUser);
                        break;
                    case 1:
                        customLayoutBinding.ivShowUserTwo.setVisibility(View.VISIBLE);
                        Glide.with(context).load(eventList.getGuestList().get(1).getGuestUser().getProfilePic()).into(customLayoutBinding.ivShowUserTwo);
                        break;
                    case 2:
                        customLayoutBinding.ivShowUserOne.setVisibility(View.VISIBLE);
                        Glide.with(context).load(eventList.getGuestList().get(2).getGuestUser().getProfilePic()).into(customLayoutBinding.ivShowUserOne);
                        break;
                }
            }
            if (eventList.getGuestList().size() > 3) {
                if (eventList.getGuestCount() != null) {
                    customLayoutBinding.tvShowMoreUserLikeCount.setText(eventList.getGuestCount() + " + Going");
                    customLayoutBinding.tvShowMoreUserLikeCount.setVisibility(View.VISIBLE);
                }
            }else  customLayoutBinding.tvShowMoreUserLikeCount.setVisibility(View.GONE);

        } else {
            customLayoutBinding.ivShowUserOne.setVisibility(View.INVISIBLE);
            customLayoutBinding.ivShowUserTwo.setVisibility(View.INVISIBLE);
            customLayoutBinding.ivShowThreeUser.setVisibility(View.INVISIBLE);
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
            Log.d("nflasknkla", "instantiateItem: "+eventList.getStartDate());
            String[] getDate = CommonUtils.getCommonUtilsInstance().getDateMonthYearName(eventList.getStartDate(),false).split(" ");
            customLayoutBinding.tvShowDateInNumeric.setText(getDate[0]);
            customLayoutBinding.ivShowDateInName.setText(getDate[1]);
        }
        if (eventList.getCurrentLikeCount() != null)
            customLayoutBinding.tvEventLikeCount.setText(eventList.getCurrentLikeCount());
        if (eventList.getCurrentDisLikeCount() != null)
            customLayoutBinding.tvShowDislike.setText(eventList.getCurrentDisLikeCount());

        container.addView(customLayoutBinding.getRoot());
        //  clickEvent(customLayoutBinding, eventList);


        customLayoutBinding.getRoot().setOnClickListener(v-> {
                int bottomSheetStatus = nearYouFragment.getBottomSheetStatus();
                if (bottomSheetStatus == 4) {
                    bottomSheetOpenListener.openBottomSheet(true);
                } else if (bottomSheetStatus == 3) {
                    bottomSheetOpenListener.openBottomSheet(false);
                }
        });

        customLayoutBinding.likeEventContainer.setOnClickListener(v -> {
            if (!CommonUtils.getCommonUtilsInstance().isUserLogin()) {
                CommonUtils.getCommonUtilsInstance().loginAlert((Activity) context, false, "");
                return;
            }

            if (eventListArrayList.get(position).getIsLike() == 1) {
                likeOrDislike(eventListArrayList.get(position).getId(), 0, position, customLayoutBinding, 1);
                return;
            }
            likeOrDislike(eventListArrayList.get(position).getId(), 1, position, customLayoutBinding, -1);

        });

        customLayoutBinding.disLikeEventContainer.setOnClickListener(v -> {
            if (!CommonUtils.getCommonUtilsInstance().isUserLogin()) {
                CommonUtils.getCommonUtilsInstance().loginAlert((Activity) context, false, "");
                return;
            }
            if (eventListArrayList.get(position).getIsLike() == 2) {
                likeOrDislike(eventListArrayList.get(position).getId(), 0, position, customLayoutBinding, 2);
                return;
            }
            likeOrDislike(eventListArrayList.get(position).getId(), 2, position, customLayoutBinding, -2);

        });

        return customLayoutBinding.getRoot();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    private void likeOrDislike(int eventId, int type, int itemPosition, NearYouCustomLayoutBinding sliderBinding, int likeType) {
        myLoader.show("");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Constants.ApiKeyName.eventId, eventId);
        jsonObject.addProperty(Constants.type, type);
        AtomicInteger currentLikeCount = new AtomicInteger(Integer.parseInt(eventListArrayList.get(itemPosition).getCurrentLikeCount()));
        AtomicInteger currentDislikeCount = new AtomicInteger(Integer.parseInt(eventListArrayList.get(itemPosition).getCurrentDisLikeCount()));
        Log.d("dasfasfsaf", currentLikeCount + " before: " + currentDislikeCount);
        compositeDisposable.add(APICall.getApiInterface().eventLikeDislike(deviceToken, jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    myLoader.dismiss();
                    try {
                        String rawData = responseBody.string();
                        JSONObject obj = new JSONObject(rawData);
                        boolean success = obj.getBoolean("success");
                        if (success) {
                            if (type == 1) {
                                currentLikeCount.getAndIncrement();
                                eventListArrayList.get(itemPosition).setCurrentLikeCount(currentLikeCount.toString());
                                sliderBinding.likeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_bg_wrapper);
                                sliderBinding.disLikeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_border);
                                sliderBinding.tvEventLikeCount.setText(eventListArrayList.get(itemPosition).getCurrentLikeCount());
                                if (eventListArrayList.get(itemPosition).getIsLike() == 2) {
                                    currentDislikeCount.getAndDecrement();
                                    eventListArrayList.get(itemPosition).setCurrentDisLikeCount(currentDislikeCount.toString());
                                    sliderBinding.tvShowDislike.setText(eventListArrayList.get(itemPosition).getCurrentDisLikeCount());
                                }
                                eventListArrayList.get(itemPosition).setIsLike(1);
                                ShowToast.infoToast(context, "Liked");
                                notifyDataSetChanged();
                                return;
                            } else if (type == 2) {
                                currentDislikeCount.getAndIncrement();
                                eventListArrayList.get(itemPosition).setCurrentDisLikeCount(currentDislikeCount.toString());
                                sliderBinding.likeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_border);
                                sliderBinding.disLikeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_bg_wrapper);
                                sliderBinding.tvShowDislike.setText(eventListArrayList.get(itemPosition).getCurrentDisLikeCount());
                                if (eventListArrayList.get(itemPosition).getIsLike() == 1) {
                                    currentLikeCount.getAndDecrement();
                                    eventListArrayList.get(itemPosition).setCurrentLikeCount(currentLikeCount.toString());
                                    sliderBinding.tvEventLikeCount.setText(eventListArrayList.get(itemPosition).getCurrentLikeCount());
                                }
                                eventListArrayList.get(itemPosition).setIsLike(2);
                                ShowToast.infoToast(context, "Disliked");
                                notifyDataSetChanged();
                                return;
                            } else if (type == 0) {
                                if (likeType == 1) {
                                    currentLikeCount.getAndDecrement();
                                    eventListArrayList.get(itemPosition).setCurrentLikeCount(currentLikeCount.toString());
                                    sliderBinding.likeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_border);
                                    eventListArrayList.get(itemPosition).setIsLike(0);
                                    sliderBinding.tvEventLikeCount.setText(eventListArrayList.get(itemPosition).getCurrentLikeCount());
                                    notifyDataSetChanged();
                                    return;

                                } else if (likeType == 2) {
                                    currentDislikeCount.getAndDecrement();
                                    eventListArrayList.get(itemPosition).setCurrentDisLikeCount(currentDislikeCount.toString());
                                    sliderBinding.disLikeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_border);
                                    eventListArrayList.get(itemPosition).setIsLike(0);
                                    sliderBinding.tvShowDislike.setText(eventListArrayList.get(itemPosition).getCurrentDisLikeCount());
                                    notifyDataSetChanged();
                                    return;
                                }
                            }
                            return;
                        }
                        ShowToast.errorToast(context, context.getString(R.string.something_wrong));
                    } catch (Exception e) {
                        Log.d("bjbnl", "likeOrDislike: " + e.getMessage());
                        ShowToast.errorToast(context, context.getString(R.string.something_wrong));
                    }
                }, throwable -> ShowToast.infoToast(context,context.getString(R.string.something_wrong)))
        );
    }
}
