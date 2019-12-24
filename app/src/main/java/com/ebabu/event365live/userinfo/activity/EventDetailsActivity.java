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
import com.ebabu.event365live.userinfo.adapter.RelatedEventAdapter;
import com.ebabu.event365live.ticketbuy.SelectTicketActivity;
import com.ebabu.event365live.userinfo.adapter.GalleryAdapter;
import com.ebabu.event365live.userinfo.adapter.ReviewsAdapter;
import com.ebabu.event365live.userinfo.fragment.RatingDialogFragment;
import com.ebabu.event365live.userinfo.modal.GetAllGalleryImgModal;
import com.ebabu.event365live.userinfo.modal.eventdetailsmodal.EventImage;
import com.ebabu.event365live.userinfo.modal.eventdetailsmodal.RelatedEvent;
import com.ebabu.event365live.userinfo.modal.eventdetailsmodal.Review;
import com.ebabu.event365live.userinfo.modal.eventdetailsmodal.UserEventDetailsModal;
import com.ebabu.event365live.userinfo.modal.seemore.SeeMoreData;
import com.ebabu.event365live.userinfo.utils.GalleryListItemDecoration;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.SessionValidation;
import com.ebabu.event365live.utils.ShowToast;
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

public class EventDetailsActivity extends AppCompatActivity implements OnMapReadyCallback, GetResponseData, SwipeRefreshLayout.OnRefreshListener {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailsBinding = DataBindingUtil.setContentView(this,R.layout.activity_event_details);
        detailsBinding.eventDetailsSwipeLayout.setOnRefreshListener(this);
        myLoader = new MyLoader(this);


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
        detailsBinding.recyclerGallery.setLayoutManager(manager);
        detailsBinding.recyclerGallery.addItemDecoration(galleryListItemDecoration);
        detailsBinding.recyclerGallery.setAdapter(galleryAdapter);
    }

    private void setupShowEventRelatedList(List<RelatedEvent> relatedEventsList) {
        relatedEventAdapter = new RelatedEventAdapter(EventDetailsActivity.this, relatedEventsList);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        detailsBinding.recyclerRelatesEvent.setLayoutManager(manager);
        //detailsBinding.recyclerRelatesEvent.addItemDecoration(galleryListItemDecoration);
        detailsBinding.recyclerRelatesEvent.setAdapter(relatedEventAdapter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;
    }

    public void buyTicketOnClick(View view) {

        /* if isExternalTicketStatus true or not login, navigate to URL section, other wise user login and isExternalTicketStatus false, navigate to select ticket activity*/

        if(!CommonUtils.getCommonUtilsInstance().isUserLogin() || isExternalTicketStatus){
            CommonUtils.openBrowser(EventDetailsActivity.this,"https://www.google.com/");
        } else if(CommonUtils.getCommonUtilsInstance().isUserLogin() && !isExternalTicketStatus){
            Intent selectTicketIntent = new Intent(EventDetailsActivity.this, SelectTicketActivity.class);
            selectTicketIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            selectTicketIntent.putExtra(Constants.ApiKeyName.eventId, "277");
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

    public void backBtnOnClick(View view) {
        finish();
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        detailsBinding.eventDetailsRootContainer.setVisibility(View.VISIBLE);

        if (responseObj != null) {

            if(typeAPI.equalsIgnoreCase(APIs.MARK_FAVORITES_EVENT)){
                if(detailsModal.getData().getFavorite()){
                    detailsModal.getData().setFavorite(false);
                    Glide.with(EventDetailsActivity.this).load(R.drawable.unselect_heart_icon).into(detailsBinding.ivLikeDislikeImg);
                    ShowToast.successToast(EventDetailsActivity.this,"Removed favorite");
                }else {
                    detailsModal.getData().setFavorite(true);
                    Glide.with(EventDetailsActivity.this).load(R.drawable.heart).into(detailsBinding.ivLikeDislikeImg);
                    ShowToast.successToast(EventDetailsActivity.this,"Added to favorite list");
                }

                return;
            }
            detailsModal = new Gson().fromJson(responseObj.toString(), UserEventDetailsModal.class);

            if(detailsModal.getData().getAddress() != null && detailsModal.getData().getAddress().get(0) != null)
            getCurrentLocation(Double.parseDouble(detailsModal.getData().getAddress().get(0).getLatitude()),Double.parseDouble(detailsModal.getData().getAddress().get(0).getLongitude()));

            isExternalTicketStatus = detailsModal.getData().getExternalTicket();

            validateEventDetails();

            if(!CommonUtils.getCommonUtilsInstance().isUserLogin() || detailsModal.getData().getReviewed()){
                detailsBinding.tvAddReview.setVisibility(View.GONE);
            }
            else{
                detailsBinding.tvAddReview.setVisibility(View.VISIBLE);
            }

            if(!CommonUtils.getCommonUtilsInstance().isUserLogin()){
                detailsBinding.ivLikeDislikeImg.setVisibility(View.GONE);
            }
            else {
                detailsBinding.ivLikeDislikeImg.setVisibility(View.VISIBLE);
                if(detailsModal.getData().getFavorite()){
                    Glide.with(EventDetailsActivity.this).load(R.drawable.heart).into(detailsBinding.ivLikeDislikeImg);
                }
                else {
                    Glide.with(EventDetailsActivity.this).load(R.drawable.unselect_heart_icon).into(detailsBinding.ivLikeDislikeImg);
                }

            }

            if (detailsModal.getData().getAddress().get(0) != null && detailsModal.getData().getAddress().get(0).getVenueAddress() != null) {
//                double getLat = Double.parseDouble(detailsModal.getData().getAddress().get(0).getLatitude());
//                double getLng = Double.parseDouble(detailsModal.getData().getAddress().get(0).getLongitude());
//                getCurrentLocation(getLat, getLng);

                detailsBinding.tvShowMapAdd.setText(detailsModal.getData().getAddress().get(0).getVenueAddress());
            }else{
                detailsBinding.tvShowMapAdd.setText(getString(R.string.na));
            }

            eventName = detailsModal.getData().getName();
            detailsBinding.ivEventTitle.setText(eventName);
            detailsBinding.ivEventTitle.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            if (detailsModal.getData().getHost().getProfilePic() != null && detailsModal.getData().getHost().getName() != null) {
                String hostPic = detailsModal.getData().getHost().getProfilePic();
                String hostName = detailsModal.getData().getHost().getName();
                if(!TextUtils.isEmpty(hostPic)){
                    detailsBinding.hostUserImgShowName.setVisibility(View.GONE);
                    detailsBinding.ivHostedUserImg.setVisibility(View.VISIBLE);
                    Glide.with(EventDetailsActivity.this).load(hostPic).into(detailsBinding.ivHostedUserImg);
                } else{
                    detailsBinding.hostUserImgShowName.setVisibility(View.VISIBLE);
                    detailsBinding.ivHostedUserImg.setVisibility(View.GONE);
                    ((TextView)detailsBinding.hostUserImgShowName.findViewById(R.id.tvShowUserName)).setText(CommonUtils.getCommonUtilsInstance().getHostName(hostName));
                }
                detailsBinding.tvShowHostName.setText(hostName);
            }
            if (detailsModal.getData().getRating() != null)
                detailsBinding.ratingBar.setRating(detailsModal.getData().getRating());
            if (detailsModal.getData().getReviewCount() != null)
                detailsBinding.tvShowRatingCount.setText(String.valueOf(detailsModal.getData().getReviewCount()));
            if (detailsModal.getData().getStartDate() != null){
                eventDate = detailsModal.getData().getStartDate();
                detailsBinding.tvStartEventDate.setText(getDateMonthName(eventDate));
            }
            if (detailsModal.getData().getEndDate() != null)
                detailsBinding.tvEndEventDate.setText(getDateMonthName(detailsModal.getData().getStartDate()));
            Log.d("fnaslkfnsla", "onSuccess: "+detailsModal.getData().getEndDate());
            if (detailsModal.getData().getStartTime() != null){
                eventStartTime = detailsModal.getData().getStartTime();
                detailsBinding.tvStartEventTime.setText(getStartEndEventTime(eventStartTime));
            }
            if (detailsModal.getData().getEndTime() != null){
                eventEndTime = detailsModal.getData().getEndTime();
                detailsBinding.tvEndEventTime.setText(getStartEndEventTime(eventEndTime));
            }
            if (detailsModal.getData().getDescription() != null) {
                detailsBinding.descriptionContainer.setVisibility(View.VISIBLE);
                detailsBinding.tvShowDescription.setText(detailsModal.getData().getDescription());
            }
            if (detailsModal.getData().getCategoryName() != null) {
                detailsBinding.tvShowCatName.setVisibility(View.VISIBLE);
                detailsBinding.tvShowCatName.setText(detailsModal.getData().getCategoryName());
            }
            if (detailsModal.getData().getSubCategoryName() != null) {
                detailsBinding.tvShowSubCatName.setVisibility(View.VISIBLE);
                detailsBinding.tvShowSubCatName.setText(detailsModal.getData().getSubCategoryName());
            }
            if (!detailsBinding.tvShowCatName.isShown() && !detailsBinding.tvShowSubCatName.isShown())
                detailsBinding.tvTagTitle.setVisibility(View.GONE);

            if (detailsModal.getData().getReviews() != null && detailsModal.getData().getReviews().size() != 0) {

                Log.d("fnanflknaklnskl", "onSuccess: "+detailsModal.getData().getReviews().size());
                detailsBinding.tvShowReviewTitle.setVisibility(View.VISIBLE);
                detailsBinding.reviewContainer.setVisibility(View.VISIBLE);
                setupUserReview(detailsModal.getData().getReviews());
            }
            if (detailsModal.getData().getGallery() != null) {
                allGalleryImgModalList = new ArrayList<>();
                if (detailsModal.getData().getGallery().getEventImgList() != null) {
                    detailsBinding.galleryContainer.setVisibility(View.VISIBLE);
                    for (int i = 0; i < detailsModal.getData().getGallery().getEventImgList().size(); i++) {
                        allGalleryImgModalList.add(new GetAllGalleryImgModal(detailsModal.getData().getGallery().getEventImgList().get(i).getEventImage()));
                    }
                } else detailsBinding.galleryContainer.setVisibility(View.GONE);

                if (detailsModal.getData().getGallery().getVenueImgList() != null) {
                    detailsBinding.galleryContainer.setVisibility(View.VISIBLE);
                    for (int i = 0; i < detailsModal.getData().getGallery().getVenueImgList().size(); i++) {
                        allGalleryImgModalList.add(new GetAllGalleryImgModal(detailsModal.getData().getGallery().getVenueImgList().get(i).getVenueImages()));
                    }
                } else {
                    detailsBinding.galleryContainer.setVisibility(View.GONE);
                    return;
                }
                setupGalleryImgView(allGalleryImgModalList);
            }if (detailsModal.getData().getRelatedEvents() != null && detailsModal.getData().getRelatedEvents().size() != 0) {
                detailsBinding.relatedEventContainer.setVisibility(View.VISIBLE);
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

    private String getStartEndEventTime(String eventTime) {
        String formattedTime = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH);
            Date dt = sdf.parse(eventTime);

            SimpleDateFormat sdfs = new SimpleDateFormat("h a", Locale.ENGLISH);
            formattedTime = sdfs.format(dt).toLowerCase();

        } catch (ParseException e) {
            e.printStackTrace();

        }
        return formattedTime;
    }

    private void setupUserReview(List<Review> seeMoreDataList) {
        if(seeMoreDataList.size()>3)
            detailsBinding.tvSeeMore.setVisibility(View.VISIBLE);

        LinearLayoutManager manager = new LinearLayoutManager(this);
            detailsBinding.recyclerReviews.setLayoutManager(manager);
            reviewsAdapter = new ReviewsAdapter(EventDetailsActivity.this,seeMoreDataList,null,false);
            detailsBinding.recyclerReviews.setAdapter(reviewsAdapter);
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
        mMap.moveCamera(CameraUpdateFactory.zoomTo(12));
        try {
            Geocoder geocoder = new Geocoder(EventDetailsActivity.this, Locale.getDefault());
            addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null) {
                address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String pinCode = addresses.get(0).getPostalCode();
                String country = addresses.get(0).getCountryName();
                String getFullAdd = address + " " + city + " " + state + " " + pinCode + " " + country;
                detailsBinding.tvShowMapAdd.setText(address);
                Log.d("hflanflknakln", "getCurrentLocation: "+address);


            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("hflanflknakln", "getCurrentLocation: "+e.getMessage());
        }
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
        detailsBinding.eventDetailsSwipeLayout.setRefreshing(false);
    }

    public void hostProfileOnClick(View view) {
        Intent hostProfileIntent = new Intent(this, HostProfileActivity.class);
        hostProfileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        hostProfileIntent.putExtra(Constants.hostId, detailsModal.getData().getHost().getId());
        startActivity(hostProfileIntent);
    }

    private void validateEventDetails(){
        if(!CommonUtils.getCommonUtilsInstance().isUserLogin()){

            detailsBinding.tvAddReview.setVisibility(View.GONE);

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
}