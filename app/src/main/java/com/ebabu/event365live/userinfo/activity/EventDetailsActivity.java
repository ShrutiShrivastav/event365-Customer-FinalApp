package com.ebabu.event365live.userinfo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityEventDetailsBinding;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.userinfo.adapter.EventDetailsTagAdapter;
import com.ebabu.event365live.userinfo.adapter.RelatedEventAdapter;
import com.ebabu.event365live.ticketbuy.SelectTicketActivity;
import com.ebabu.event365live.userinfo.adapter.GalleryAdapter;
import com.ebabu.event365live.userinfo.adapter.ReviewsAdapter;
import com.ebabu.event365live.userinfo.fragment.RatingDialogFragment;
import com.ebabu.event365live.userinfo.modal.GetAllGalleryImgModal;
import com.ebabu.event365live.userinfo.modal.eventdetailsmodal.RelatedEvent;
import com.ebabu.event365live.userinfo.modal.eventdetailsmodal.Review;
import com.ebabu.event365live.userinfo.modal.eventdetailsmodal.UserEventDetailsModal;
import com.ebabu.event365live.userinfo.utils.GalleryListItemDecoration;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.SessionValidation;
import com.ebabu.event365live.utils.ShowToast;
import com.facebook.appevents.suggestedevents.ViewOnClickListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

public class EventDetailsActivity extends AppCompatActivity implements OnMapReadyCallback, GetResponseData, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private RatingDialogFragment ratingDialogFragment;
    private MyLoader myLoader;
    private GalleryAdapter galleryAdapter;
    private ActivityEventDetailsBinding detailsBinding;
    private RelatedEventAdapter relatedEventAdapter;
    private GalleryListItemDecoration galleryListItemDecoration;
    private GoogleMap mMap;

    private ReviewsAdapter reviewsAdapter;
    private List<Address> addresses;
    private Location currentLocation;
    String eventName, eventStartTime,eventEndTime, eventDate, address;
    private UserEventDetailsModal detailsModal;
    private int getEventId;
    private List<GetAllGalleryImgModal> allGalleryImgModalList;
    private Boolean isExternalTicketStatus;
    private String eventImg;
    private List<String> tagList;
    private int hostId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailsBinding = DataBindingUtil.setContentView(this,R.layout.activity_event_details);
        detailsBinding.content.eventDetailsSwipeLayout.setOnRefreshListener(this);

//        detailsBinding.arcLayout.findViewById(R.id.ivBackBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

      //  detailsBinding.ivBackBtn.setOnClickListener(this);
      //  detailsBinding.ivBackBtn.setFocusable(false);
        myLoader = new MyLoader(this);
        tagList = new ArrayList<>();
        allGalleryImgModalList = new ArrayList<>();
        setBundleData();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Log.d("bfkljabfjkbasjkfbsjka", "onCreate: "+ CommonUtils.getCommonUtilsInstance().getDeviceAuth());
    }

    private void setBundleData() {
        if (getIntent().getExtras() != null) {
            getEventId = getIntent().getExtras().getInt(Constants.ApiKeyName.eventId);
            eventImg = getIntent().getExtras().getString(Constants.ApiKeyName.eventImg);
            if (!CommonUtils.getCommonUtilsInstance().isUserLogin()) {
                eventDetailsNoAuthRequest(getEventId);
            } else {
                eventDetailsAuthRequest(getEventId);
            }
            galleryListItemDecoration = new GalleryListItemDecoration(this);

            Log.d("anfklnaslfa", "onCreate: "+getEventId);
        }

        Glide.with(EventDetailsActivity.this).load(eventImg != null ? eventImg : R.drawable.couple_img).into(detailsBinding.ivEventImg);
    }

    private void setupGalleryImgView(List<GetAllGalleryImgModal> eventImageList) {
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        galleryAdapter = new GalleryAdapter( eventImageList);
        detailsBinding.content.recyclerGallery.setLayoutManager(manager);
        detailsBinding.content.recyclerGallery.addItemDecoration(galleryListItemDecoration);
        detailsBinding.content.recyclerGallery.setAdapter(galleryAdapter);
    }

    private void setupShowEventRelatedList(List<RelatedEvent> relatedEventsList) {
        relatedEventAdapter = new RelatedEventAdapter(EventDetailsActivity.this, relatedEventsList);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        detailsBinding.content.recyclerRelatesEvent.setLayoutManager(manager);
        //detailsBinding.recyclerRelatesEvent.addItemDecoration(galleryListItemDecoration);
        detailsBinding.content.recyclerRelatesEvent.setAdapter(relatedEventAdapter);

    }

    public void buyTicketOnClick(View view) {

        /* if isExternalTicketStatus true or not login, navigate to URL section, other wise user login and isExternalTicketStatus false, navigate to select ticket activity*/

        if(!CommonUtils.getCommonUtilsInstance().isUserLogin() || isExternalTicketStatus){
            CommonUtils.openBrowser(EventDetailsActivity.this,"https://www.google.com/");
        } else if(CommonUtils.getCommonUtilsInstance().isUserLogin() && !isExternalTicketStatus){

            Intent selectTicketIntent = new Intent(EventDetailsActivity.this, SelectTicketActivity.class);
            selectTicketIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            selectTicketIntent.putExtra(Constants.ApiKeyName.eventId, getEventId);
            selectTicketIntent.putExtra(Constants.hostId, hostId);
            selectTicketIntent.putExtra(Constants.eventName, eventName);
            selectTicketIntent.putExtra(Constants.eventStartTime, eventStartTime);
            selectTicketIntent.putExtra(Constants.eventEndTime, eventEndTime);
            selectTicketIntent.putExtra(Constants.eventDate, eventDate);
            selectTicketIntent.putExtra(Constants.eventAdd, address);
            startActivity(selectTicketIntent);
        }
    }

    public void seeMoreOnClick(View view) {
        Intent seeMoreIntent = new Intent(this, SeeMoreReviewActivity.class);
        seeMoreIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        seeMoreIntent.putExtra(Constants.ApiKeyName.eventId, "170");
        startActivity(seeMoreIntent);
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        detailsBinding.eventDetailsRootContainer.setVisibility(View.VISIBLE);

        if (responseObj != null) {

            if(typeAPI.equalsIgnoreCase(APIs.MARK_FAVORITES_EVENT)){
                if(detailsModal.getData().getFavorite()){
                    detailsModal.getData().setFavorite(false);
                    Glide.with(EventDetailsActivity.this).load(R.drawable.unselect_heart_icon).into(detailsBinding.content.ivLikeDislikeImg);
                    ShowToast.successToast(EventDetailsActivity.this,"Removed favorite");
                }else {
                    detailsModal.getData().setFavorite(true);
                    Glide.with(EventDetailsActivity.this).load(R.drawable.heart).into(detailsBinding.content.ivLikeDislikeImg);
                    ShowToast.successToast(EventDetailsActivity.this,"Added to favorite list");
                }

                return;
            }
            detailsModal = new Gson().fromJson(responseObj.toString(), UserEventDetailsModal.class);

            hostId = detailsModal.getData().getHost().getId();
            isExternalTicketStatus = detailsModal.getData().getExternalTicket();
            validateEventDetails();

            if(!CommonUtils.getCommonUtilsInstance().isUserLogin() || detailsModal.getData().getReviewed() != null && detailsModal.getData().getReviewed()){
                detailsBinding.content.tvAddReview.setVisibility(View.GONE);
            }
            else{
                detailsBinding.content.tvAddReview.setVisibility(View.VISIBLE);
            }
            if(!CommonUtils.getCommonUtilsInstance().isUserLogin()){
                detailsBinding.content.ivLikeDislikeImg.setVisibility(View.GONE);
                detailsBinding.content.markFavoriteContainer.setVisibility(View.GONE);
            }
            else{
                detailsBinding.content.markFavoriteContainer.setVisibility(View.VISIBLE);
                detailsBinding.content.ivLikeDislikeImg.setVisibility(View.VISIBLE);
                if(detailsModal.getData().getFavorite()){
                    Glide.with(EventDetailsActivity.this).load(R.drawable.heart).into(detailsBinding.content.ivLikeDislikeImg);
                }
                else {
                    Glide.with(EventDetailsActivity.this).load(R.drawable.unselect_heart_icon).into(detailsBinding.content.ivLikeDislikeImg);
                }
            }

            if (detailsModal.getData().getAddress() != null && detailsModal.getData().getAddress().get(0).getVenueAddress() != null) {
                address = detailsModal.getData().getAddress().get(0).getVenueAddress();
                detailsBinding.content.tvShowMapAdd.setText(address);

            }else{
                detailsBinding.content.tvShowMapAdd.setText(getString(R.string.na));
            }
            eventName = detailsModal.getData().getName();
            detailsBinding.content.ivEventTitle.setText(CommonUtils.getCommonUtilsInstance().makeFirstLatterCapital(eventName));

            String hostPic = detailsModal.getData().getHost().getProfilePic();
            String hostName = detailsModal.getData().getHost().getName();
            if (!TextUtils.isEmpty(hostPic)) {
                detailsBinding.content.hostUserImgShowName.setVisibility(View.GONE);
                detailsBinding.content.ivHostedUserImg.setVisibility(View.VISIBLE);
                Glide.with(EventDetailsActivity.this).load(hostPic).into(detailsBinding.content.ivHostedUserImg);
            } else {
                detailsBinding.content.hostUserImgShowName.setVisibility(View.VISIBLE);
                detailsBinding.content.ivHostedUserImg.setVisibility(View.GONE);
                ((TextView) detailsBinding.content.hostUserImgShowName.findViewById(R.id.tvShowUserName)).setText(CommonUtils.getCommonUtilsInstance().getHostName(hostName));
            }
                detailsBinding.content.tvShowHostName.setText(hostName);
                detailsBinding.content.ratingBar.setRating(detailsModal.getData().getRating()!=null ? detailsModal.getData().getRating() : 0);
                detailsBinding.content.tvShowRatingCount.setText(detailsModal.getData().getReviewCount() !=null ? String.valueOf(detailsModal.getData().getReviewCount()): "(0)");
                eventDate = detailsModal.getData().getStart();
                detailsBinding.content.tvStartEventDate.setText(eventDate != null ? CommonUtils.getCommonUtilsInstance().getDateMonthName(eventDate) : getString(R.string.na));

                detailsBinding.content.tvEndEventDate.setText(detailsModal.getData().getEnd() != null ? CommonUtils.getCommonUtilsInstance().getDateMonthName(detailsModal.getData().getEnd()) : getString(R.string.na));
                eventStartTime = detailsModal.getData().getStart();

            if (detailsModal.getData().getStart() != null && detailsModal.getData().getEnd() != null){
                eventStartTime = detailsModal.getData().getStart();
                eventEndTime = detailsModal.getData().getEnd();
                detailsBinding.content.tvStartEventTime.setText(CommonUtils.getCommonUtilsInstance().getStartEndEventTime(eventStartTime)+
                " - "+CommonUtils.getCommonUtilsInstance().getStartEndEventTime(eventEndTime));

                detailsBinding.content.tvEndEventTime.setText(CommonUtils.getCommonUtilsInstance().getStartEndEventTime(eventStartTime)+
                        " - "+CommonUtils.getCommonUtilsInstance().getStartEndEventTime(eventEndTime));
            }
            else{
                detailsBinding.content.tvStartEventTime.setText(getString(R.string.na));
                detailsBinding.content.tvEndEventTime.setText(getString(R.string.na));
            }
            if (detailsModal.getData().getDescription() != null) {
                detailsBinding.content.descriptionContainer.setVisibility(View.VISIBLE);
                detailsBinding.content.tvShowDescription.setText(detailsModal.getData().getDescription());
            }

            if (detailsModal.getData().getCategoryName() != null) {
                tagList.add(detailsModal.getData().getCategoryName());
                detailsBinding.content.tagContainer.setVisibility(View.VISIBLE);
            }
            else {
                detailsBinding.content.tagContainer.setVisibility(View.GONE);
            }
            if (detailsModal.getData().getSubCategories() != null) {
                detailsBinding.content.tagContainer.setVisibility(View.VISIBLE);
                for(int i=0;i<detailsModal.getData().getSubCategories().size();i++){
                    tagList.add(detailsModal.getData().getSubCategories().get(i).getSubCategoryName());
                }
            }else {
                detailsBinding.content.tagContainer.setVisibility(View.GONE);
            }
            showEventDetailsTag();
            getCurrentLocation(Double.parseDouble(detailsModal.getData().getAddress().get(0).getLatitude()),Double.parseDouble(detailsModal.getData().getAddress().get(0).getLongitude()));

            if (detailsModal.getData().getReviews() != null && detailsModal.getData().getReviews().size() != 0) {
                Log.d("fnanflknaklnskl", "onSuccess: "+detailsModal.getData().getReviews().size());
                detailsBinding.content.tvShowReviewTitle.setVisibility(View.VISIBLE);
                detailsBinding.content.reviewContainer.setVisibility(View.VISIBLE);
                setupUserReview(detailsModal.getData().getReviews());
            }
            if (detailsModal.getData().getEventImages() != null || detailsModal.getData().getVenueVenuImages() != null) {
                if(allGalleryImgModalList.size()>0)
                    allGalleryImgModalList.clear();


                if (detailsModal.getData().getEventImages().size() >0) {
                    for (int i = 0; i < detailsModal.getData().getEventImages().size(); i++) {
                        allGalleryImgModalList.add(new GetAllGalleryImgModal(detailsModal.getData().getEventImages().get(i).getEventImage()));
                    }
                }
                if (detailsModal.getData().getVenueVenuImages().size() >0) {
                    for (int i = 0; i < detailsModal.getData().getVenueVenuImages().size(); i++) {
                        allGalleryImgModalList.add(new GetAllGalleryImgModal(detailsModal.getData().getVenueVenuImages().get(i).getVenueImage()));
                    }
                }
            }

            if(allGalleryImgModalList.size()>0){
                detailsBinding.content.galleryContainer.setVisibility(View.VISIBLE);
                setupGalleryImgView(allGalleryImgModalList);
            }else {
                detailsBinding.content.galleryContainer.setVisibility(View.GONE);
            }


            if (detailsModal.getData().getRelatedEvents() != null && detailsModal.getData().getRelatedEvents().size() != 0) {
                detailsBinding.content.relatedEventContainer.setVisibility(View.VISIBLE);
                setupShowEventRelatedList(detailsModal.getData().getRelatedEvents());
            }


        }
    }
    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        detailsBinding.eventDetailsRootContainer.setVisibility(View.INVISIBLE);
        ShowToast.errorToast(EventDetailsActivity.this, message);
        finish();
        if (errorBody != null) {
        }
    }
    private void eventDetailsNoAuthRequest(int getEventId) {
        myLoader.show(" ");
        Call<JsonElement> userEventDetailsObj = APICall.getApiInterface().getEventDetailsNoAuth(getEventId);
        new APICall(EventDetailsActivity.this).apiCalling(userEventDetailsObj, this, APIs.USER_EVENT_DETAILS_AUTH);
    }
    private String getDateMonthName(String dateFormat) {
        int getDate = 0;
        String getMonth = "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
            Date date = inputFormat.parse(dateFormat);
            Calendar calendar = outputFormat.getCalendar();
            calendar.setTime(date);
            getDate = calendar.get(Calendar.DATE);
            getMonth = (String) DateFormat.format("MMM", date);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("sfsafavfdhdhdss", "ParseException: " + e.getMessage());
        }
        return getDate + " " + getMonth;
    }





    private void setupUserReview(List<Review> seeMoreDataList) {
        if(seeMoreDataList.size()>3)
            detailsBinding.content.tvSeeMore.setVisibility(View.VISIBLE);

        LinearLayoutManager manager = new LinearLayoutManager(this);
            detailsBinding.content.recyclerReviews.setLayoutManager(manager);
            reviewsAdapter = new ReviewsAdapter(EventDetailsActivity.this,seeMoreDataList,null,false);
            detailsBinding.content.recyclerReviews.setAdapter(reviewsAdapter);
    }

    public void addReviewDialogLaunch() {
        if (ratingDialogFragment == null) {
            ratingDialogFragment = new RatingDialogFragment();
        }
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.ApiKeyName.eventId,getEventId);
        ratingDialogFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        ratingDialogFragment.show(fragmentTransaction, RatingDialogFragment.TAG);
    }

    public void addReviewOnClick(View view) {
        addReviewDialogLaunch();
    }

    private void getCurrentLocation(double lat, double lng) {
        LatLng currentLatLng = new LatLng(lat, lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        MarkerOptions markerOptions = new MarkerOptions().position(currentLatLng);
        mMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        //mMap.moveCamera(CameraUpdateFactory.zoomTo(12));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    @Override
    public void onRefresh() {
        if (!CommonUtils.getCommonUtilsInstance().isUserLogin()) {
            eventDetailsNoAuthRequest(getEventId);
        } else {
            eventDetailsAuthRequest(getEventId);
        }
        detailsBinding.content.eventDetailsSwipeLayout.setRefreshing(false);
    }

    public void hostProfileOnClick(View view) {
        Intent hostProfileIntent = new Intent(this, HostProfileActivity.class);
        hostProfileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        hostProfileIntent.putExtra(Constants.hostId, detailsModal.getData().getHost().getId());
        startActivity(hostProfileIntent);
    }

    private void validateEventDetails(){
        if(!CommonUtils.getCommonUtilsInstance().isUserLogin()){
            detailsBinding.content.tvAddReview.setVisibility(View.GONE);
        }
    }
    private void eventDetailsAuthRequest(int getEventId) {
        myLoader.show(" ");
        Call<JsonElement> userEventDetailsObj = APICall.getApiInterface().getEventDetailsAuth(CommonUtils.getCommonUtilsInstance().getDeviceAuth(),getEventId);
        new APICall(EventDetailsActivity.this).apiCalling(userEventDetailsObj, this, APIs.USER_EVENT_DETAILS_NO_AUTH);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(SessionValidation.getPrefsHelper().getPref("userlogin") != null){
            SessionValidation.getPrefsHelper().delete("userlogin");
            Toast.makeText(this,"deleted",Toast.LENGTH_SHORT).show();
        }
    }

    private void likeEventRequest(){
        myLoader.show("");
        JsonObject obj = new JsonObject();
        obj.addProperty(Constants.ApiKeyName.eventId,getEventId);
        obj.addProperty(Constants.ApiKeyName.isFavorite,detailsModal.getData().getFavorite() ? true : detailsModal.getData().getFavorite());
        Call<JsonElement> likeEventCall = APICall.getApiInterface().likeEvent(CommonUtils.getCommonUtilsInstance().getDeviceAuth(),obj);
        new APICall(EventDetailsActivity.this).apiCalling(likeEventCall,this,APIs.MARK_FAVORITES_EVENT);
    }



    public void markFavoriteEventOnClick(View view) {
        likeEventRequest();
    }

    private void showEventDetailsTag(){
        Log.d("fmnafnkla", "showEventDetailsTag: "+tagList);
        EventDetailsTagAdapter eventDetailsTagAdapter = new EventDetailsTagAdapter(tagList);
        detailsBinding.content.showTagRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        detailsBinding.content.showTagRecycler.setAdapter(eventDetailsTagAdapter);
    }

    public void shareOnClick(View view) {
        CommonUtils.getCommonUtilsInstance().shareIntent(EventDetailsActivity.this);
    }

    public void addEventToCalenderOnClick(View view) {
        ShowToast.infoToast(EventDetailsActivity.this,"Comming soon");
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        if (mOfferDetail.getLongitude() != null && mOfferDetail.getLatitude() != null){
//            LatLng latLng = new LatLng(Double.parseDouble(mOfferDetail.getLatitude()), Double.parseDouble(mOfferDetail.getLongitude()));
//            googleMap.addMarker(new MarkerOptions().position(latLng));
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ivBackBtn)
            finish();
    }

    public void raj(View view) {
        ShowToast.successToast(EventDetailsActivity.this,"raj");
    }
}