package com.ebabu.event365live.home.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.FragmentNearYouBinding;
import com.ebabu.event365live.home.adapter.EventListAdapter;
import com.ebabu.event365live.home.adapter.EventSliderAdapter;
import com.ebabu.event365live.home.adapter.NearByEventListAdapter;
import com.ebabu.event365live.home.modal.nearbymodal.EventList;
import com.ebabu.event365live.home.modal.nearbymodal.NearByEventModal;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.listener.BottomSheetOpenListener;
import com.ebabu.event365live.listener.EventDataChangeListener;
import com.ebabu.event365live.listener.EventLikeDislikeListener;
import com.ebabu.event365live.oncelaunch.modal.nearbynoauth.NearByNoAuthModal;
import com.ebabu.event365live.userinfo.activity.EventDetailsActivity;
import com.ebabu.event365live.utils.CarouselEffectTransformer;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.DemoPageTransform;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.SessionValidation;
import com.ebabu.event365live.utils.ShowToast;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class NearYouFragment extends Fragment implements GetResponseData, View.OnClickListener, EventDataChangeListener, EventLikeDislikeListener, BottomSheetOpenListener {

    private MyLoader myLoader;
    private Activity activity;
    private NearByEventListAdapter nearByEventListAdapter;
    private Context context;
    private FragmentNearYouBinding nearYouBinding;
    private EventSliderAdapter eventSliderAdapter;
    private BottomSheetBehavior homeBottomSheet;

    private Integer currentShowingEventId;
    private float fromRotation;
    private float clockRotation;
    private float antiClockRotation;
    private RotateAnimation clockWiseRotation, antiClockWiseRotation;
    TranslateAnimation mAnimation;
    private String eventImg;
    private ArrayList<EventList> eventListArrayList;



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myLoader = new MyLoader(context);
        activity = (Activity) context;

    }

    public NearYouFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        nearYouBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_near_you, container, false);
        eventListArrayList = new ArrayList<>();
        nearYouBinding.bottomSheet.ivShowEventDetails.setOnClickListener(this);
        setRetainInstance(true);
        setupBottomSheet();

        if (getArguments() != null) {
            ArrayList<EventList> nearByNoAuthModal = getArguments().getParcelableArrayList(Constants.nearByData);

            Log.d("fnlkanfla", "onCreateView: "+nearByNoAuthModal.size());

            if (nearByNoAuthModal.size() > 0) {
                nearYouBinding.noDataFoundContainer.setVisibility(View.GONE);
                if (!CommonUtils.getCommonUtilsInstance().isSwipeMode()) {
                    nearYouBinding.bottomSheet.homeButtonSheetContainer.setVisibility(View.GONE);
                    nearYouBinding.shadow.setVisibility(View.GONE);
                    nearYouBinding.nearByRecycler.setVisibility(View.VISIBLE);
                    nearYouBinding.nearYouViewpager.setVisibility(View.GONE);
                    setupVerticalEventList(nearByNoAuthModal);
                } else {
                    nearYouBinding.nearYouViewpager.setVisibility(View.VISIBLE);
                    nearYouBinding.nearByRecycler.setVisibility(View.GONE);
                    nearYouBinding.bottomSheet.homeButtonSheetContainer.setVisibility(View.VISIBLE);
                    nearYouBinding.shadow.setVisibility(View.VISIBLE);
                    eventListArrayList.addAll(nearByNoAuthModal);
                    setupHomeViewPager();
                }

            } else {
                nearYouBinding.noDataFoundContainer.setVisibility(View.VISIBLE);
                nearYouBinding.bottomSheet.homeButtonSheetContainer.setVisibility(View.GONE);
                ((TextView) nearYouBinding.noDataFoundContainer.findViewById(R.id.tvShowNoDataFound)).setText(getString(R.string.event_not_available));
            }
        }

        return nearYouBinding.getRoot();
    }

    private void setupHomeViewPager() {
        Log.d("fkafnkla", "setupHomeViewPager: "+eventListArrayList.size());
        eventSliderAdapter = new EventSliderAdapter(getContext(),eventListArrayList,NearYouFragment.this);
        nearYouBinding.nearYouViewpager.setAdapter(eventSliderAdapter);
       // nearYouBinding.nearYouViewpager.setPageMargin(30);
        nearYouBinding.nearYouViewpager.setClipToPadding(false);
       // nearYouBinding.nearYouViewpager.setPadding(100, 0, 100, 0);
        //nearYouBinding.nearYouViewpager.setPageTransformer(false, new DemoPageTransform());
        setEventDetailsDataToBottomSheet(eventListArrayList.get(0));
        nearYouBinding.nearYouViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                setEventDetailsDataToBottomSheet(eventListArrayList.get(position));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        Log.d("bfkjabsfkjbsjkafba", "onSuccess: " + responseObj);
        myLoader.dismiss();
        if (responseObj != null) {

            if(typeAPI.equalsIgnoreCase(APIs.EventLikeOrDislike)){

                return;
            }
            NearByEventModal eventModal = new Gson().fromJson(responseObj.toString(), NearByEventModal.class);
            eventListArrayList.addAll(eventModal.getData().getEventList());
            setupHomeViewPager();
        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        Log.d("bfkjabsfkjbsjkafba", "onFailed: " + errorBody);
        myLoader.dismiss();
        ShowToast.errorToast(activity, message);
        if (errorBody != null) {
        }

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.ivShowEventDetails){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent eventIntent = new Intent(activity, EventDetailsActivity.class);
                    eventIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    eventIntent.putExtra(Constants.ApiKeyName.eventId,currentShowingEventId);
                    eventIntent.putExtra(Constants.ApiKeyName.eventImg,eventImg);
                    getContext().startActivity(eventIntent);
                }
            },400);
            homeBottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);

        }
    }
    @Override
    public void eventDataListener(EventList eventList) {
        if(eventList != null){
            eventImg = eventList.getEventImages().get(0).getEventImage();
            if (eventList.getName() != null) {
                nearYouBinding.bottomSheet.tvEventName.setText(eventList.getName());
            } else {
                nearYouBinding.bottomSheet.tvEventName.setText(context.getString(R.string.na));
            }
            if (eventList.getStartDate() != null) {
                nearYouBinding.bottomSheet.tvEventTime.setText(CommonUtils.getCommonUtilsInstance().getStartEndEventTime(eventList.getStartDate()));
            } else {
                nearYouBinding.bottomSheet.tvEventTime.setText(getString(R.string.na));
            }
            if (eventList.getVenueEvents() != null && eventList.getVenueEvents().get(0)!= null) {
                nearYouBinding.bottomSheet.tvEventAdd.setText(eventList.getVenueEvents().get(0).getVenueAddress());
            } else {
                nearYouBinding.bottomSheet.tvEventAdd.setText(activity.getString(R.string.na));
            }
            if (eventList.getHost() != null && eventList.getHost().getName() != null) {
                nearYouBinding.bottomSheet.tvEventHostName.setText(eventList.getHost().getName());
            } else {
                nearYouBinding.bottomSheet.tvEventHostName.setText(activity.getString(R.string.na));
            }
            if (eventList.getDistance() != null)
                nearYouBinding.bottomSheet.tvShowMiles.setText(eventList.getDistance());
            else
                nearYouBinding.bottomSheet.tvShowMiles.setText(activity.getString(R.string.na));

            if(eventList.getId() != null)
                currentShowingEventId = eventList.getId();
        }
    }

    @Override
    public int likeDislikeEvent(int eventType, int type) {
        /*type show 1 or zero means 1- like or 0 - dislike*/
        int getType = type;
        eventLikeDislike(eventType, type);
        return getType;
    }




    private void setupBottomSheet(){
        nearYouBinding.bottomSheet.ivHomeIndicatorIcon.clearAnimation();
        mAnimation = new TranslateAnimation(
                TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.00f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.02f);
        mAnimation.setDuration(400);
        mAnimation.setRepeatCount(-1);

        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setInterpolator(new LinearInterpolator());
        nearYouBinding.bottomSheet.ivHomeIndicatorIcon.setAnimation(mAnimation);
        homeBottomSheet = BottomSheetBehavior.from(nearYouBinding.bottomSheet.homeButtonSheetContainer);


        nearYouBinding.bottomSheet.ivHomeIndicatorIcon.setOnClickListener(view ->{
            if(homeBottomSheet.getState() == BottomSheetBehavior.STATE_EXPANDED)
            homeBottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
            else if(homeBottomSheet.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                homeBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        homeBottomSheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {

                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING: {
                        break;
                    }
                    case BottomSheetBehavior.STATE_SETTLING: {

                        break;
                    }
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        nearYouBinding.bottomSheet.ivHomeIndicatorIcon.setRotation(-180);
                        break;
                    }
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        nearYouBinding.bottomSheet.ivHomeIndicatorIcon.setRotation(0);

                        break;
                    }
                    case BottomSheetBehavior.STATE_HIDDEN: {

                        break;
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

    }



    private void setupVerticalEventList(ArrayList<EventList> eventLists){
        nearByEventListAdapter = new NearByEventListAdapter(eventLists);
        nearYouBinding.nearByRecycler.setAdapter(nearByEventListAdapter);
    }
    private void eventLikeDislike(int eventId, int type){
        /* type shows the 1-like or 0-dislike */
        myLoader.show("");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Constants.ApiKeyName.eventId,eventId);
        jsonObject.addProperty(Constants.type,type);
        Call<JsonElement> likeDislikeCall = APICall.getApiInterface().eventLikeDislike(CommonUtils.getCommonUtilsInstance().getDeviceAuth(),jsonObject);
        new APICall(getActivity()).apiCalling(likeDislikeCall,this,APIs.EventLikeOrDislike);

    }


    @Override
    public void openBottomSheet(Boolean isOpen) {
        if(isOpen != null){
            homeBottomSheet.setState(isOpen? BottomSheetBehavior.STATE_EXPANDED : BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public int getBottomSheetStatus(){
        return homeBottomSheet.getState();
    }

    private void setEventDetailsDataToBottomSheet(EventList eventList){
        if(eventList != null){
            eventImg = eventList.getEventImages().get(0).getEventImage();
            if (eventList.getName() != null) {
                nearYouBinding.bottomSheet.tvEventName.setText(eventList.getName());
            } else {
                nearYouBinding.bottomSheet.tvEventName.setText(context.getString(R.string.na));
            }
            if (eventList.getStartDate() != null) {
                nearYouBinding.bottomSheet.tvEventTime.setText(CommonUtils.getCommonUtilsInstance().getStartEndEventTime(eventList.getStartDate()));
            } else {
                nearYouBinding.bottomSheet.tvEventTime.setText(getString(R.string.na));
            }
            if (eventList.getVenueEvents() != null && eventList.getVenueEvents().get(0)!= null) {
                nearYouBinding.bottomSheet.tvEventAdd.setText(eventList.getVenueEvents().get(0).getVenueAddress());
            } else {
                nearYouBinding.bottomSheet.tvEventAdd.setText(activity.getString(R.string.na));
            }
            if (eventList.getHost() != null && eventList.getHost().getName() != null) {
                nearYouBinding.bottomSheet.tvEventHostName.setText(eventList.getHost().getName());
            } else {
                nearYouBinding.bottomSheet.tvEventHostName.setText(activity.getString(R.string.na));
            }
            if (eventList.getDistance() != null)
                nearYouBinding.bottomSheet.tvShowMiles.setText(eventList.getDistance());
            else
                nearYouBinding.bottomSheet.tvShowMiles.setText(activity.getString(R.string.na));

            if(eventList.getId() != null)
                currentShowingEventId = eventList.getId();
        }
    }
}