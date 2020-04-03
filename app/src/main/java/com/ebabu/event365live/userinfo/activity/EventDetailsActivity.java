package com.ebabu.event365live.userinfo.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityEventDetailsBinding;
import com.ebabu.event365live.homedrawer.activity.ContactUsActivity;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.ticketbuy.SelectTicketActivity;
import com.ebabu.event365live.userinfo.adapter.EventDetailsTagAdapter;
import com.ebabu.event365live.userinfo.adapter.GalleryAdapter;
import com.ebabu.event365live.userinfo.adapter.RelatedEventAdapter;
import com.ebabu.event365live.userinfo.adapter.ReviewsAdapter;
import com.ebabu.event365live.userinfo.fragment.RatingDialogFragment;
import com.ebabu.event365live.userinfo.modal.GetAllGalleryImgModal;
import com.ebabu.event365live.userinfo.modal.eventdetailsmodal.RelatedEvent;
import com.ebabu.event365live.userinfo.modal.eventdetailsmodal.Review;
import com.ebabu.event365live.userinfo.modal.eventdetailsmodal.UserData;
import com.ebabu.event365live.userinfo.modal.eventdetailsmodal.UserEventDetailsModal;
import com.ebabu.event365live.userinfo.utils.GalleryListItemDecoration;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.SessionValidation;
import com.ebabu.event365live.utils.ShowToast;
import com.ebabu.event365live.utils.SnapHelperOneByOne;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    String eventName, eventStartTime, eventEndTime, eventStartDate, eventEndDate, address, eventShortDes, eventImg, hostName, eventStartDateOrTime, eventEndDateOrTime;
    private UserEventDetailsModal detailsModal;
    private static int getEventId;
    private List<GetAllGalleryImgModal> allGalleryImgModalList;
    private Boolean isExternalTicketStatus;
    SnapHelperOneByOne snapHelperOneByOne;
    SnapHelper snapHelper;
    private List<String> tagList;
    private int hostId;
    private String ticketInfoUrl, eventHelpLine;
    private static boolean isUserGaveReview;
    private boolean shouldButtonDisable;
    private StringBuilder stringBuffer;
    private Boolean isTicketAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_event_details);
        detailsBinding.content.eventDetailsSwipeLayout.setOnRefreshListener(this);
        detailsBinding.btnLogin.setClickable(true);
        snapHelperOneByOne = new SnapHelperOneByOne();
        snapHelper = new LinearSnapHelper();
        stringBuffer = new StringBuilder();

        galleryListItemDecoration = new GalleryListItemDecoration(this);
        getDynamicLinks();

//        new Handler().postDelayed(()->{
//          //  detailsBinding.content.nested.scrollTo(0,(int)detailsBinding.content.reviewContainer.getY());
//            detailsBinding.content.reviewContainer.getParent().requestChildFocus(detailsBinding.content.recyclerReviews,detailsBinding.content.recyclerReviews);
//
//        },10000);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        detailsBinding.appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {

            if (verticalOffset >= (appBarLayout.getTotalScrollRange() - 100) * -1) {
                if (verticalOffset == 0) {
                    detailsBinding.content.eventDetailsSwipeLayout.setEnabled(true);
                } else {
                    detailsBinding.content.eventDetailsSwipeLayout.setEnabled(false);
                }
                detailsBinding.toolbarTitle.setVisibility(View.GONE);
                Glide.with(EventDetailsActivity.this).load(R.drawable.back_arrow_white).into(detailsBinding.ivBackBtn);
                Log.d("fnasklfna", "onCreate: " + verticalOffset);

            } else {
                detailsBinding.content.eventDetailsSwipeLayout.setEnabled(true);
                detailsBinding.toolbarTitle.setVisibility(View.VISIBLE);
                Glide.with(EventDetailsActivity.this).load(R.drawable.back_arrow).into(detailsBinding.ivBackBtn);
                Log.d("fnasklfna", "else: ");
            }
        });

        detailsBinding.content.tvHavingTrouble.setOnClickListener(v -> {
            Intent intent = new Intent(this, ContactUsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        detailsBinding.content.tvContactHost.setOnClickListener(v -> {
            if (CommonUtils.getCommonUtilsInstance().isUserLogin()) {
                Intent intent = new Intent(this, ConversationActivity.class);
                intent.putExtra(ConversationUIService.USER_ID, String.valueOf(hostId));
                intent.putExtra(ConversationUIService.DISPLAY_NAME, hostName); //put it for displaying the title.
                intent.putExtra(ConversationUIService.TAKE_ORDER, true);
                startActivity(intent);
                return;
            }
            CommonUtils.getCommonUtilsInstance().loginAlert(EventDetailsActivity.this, false, "");
        });
    }

    private void setBundleData(int eventId) {
        if (getIntent().getExtras() != null) {
            if (!CommonUtils.getCommonUtilsInstance().isUserLogin()) {
                eventDetailsNoAuthRequest(eventId);
            } else {
                eventDetailsAuthRequest(eventId);
            }
            setupGalleryImgView();
        }
    }

    private void setupGalleryImgView() {
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        galleryAdapter = new GalleryAdapter(allGalleryImgModalList);
        detailsBinding.content.recyclerGallery.setLayoutManager(manager);
        detailsBinding.content.recyclerGallery.addItemDecoration(galleryListItemDecoration);
        detailsBinding.content.recyclerGallery.setAdapter(galleryAdapter);
    }

    private void setupShowEventRelatedList(List<RelatedEvent> relatedEventsList) {
        relatedEventAdapter = new RelatedEventAdapter(EventDetailsActivity.this, relatedEventsList);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        detailsBinding.content.recyclerRelatesEvent.setLayoutManager(manager);
        //detailsBinding.recyclerRelatesEvent.addItemDecoration(galleryListItemDecoration);
        snapHelper.attachToRecyclerView(detailsBinding.content.recyclerRelatesEvent);
        detailsBinding.content.recyclerRelatesEvent.setAdapter(relatedEventAdapter);
    }

    public void buyTicketOnClick(View view) {

        /* if isExternalTicketStatus true or not login, navigate to URL section, other wise user login and isExternalTicketStatus false, navigate to select ticket activity*/
        if (!CommonUtils.getCommonUtilsInstance().isUserLogin()) {
            CommonUtils.getCommonUtilsInstance().loginAlert(EventDetailsActivity.this, false, "");
        } else if (isTicketAvailable != null && isTicketAvailable && isExternalTicketStatus != null && !isExternalTicketStatus) {
            Intent selectTicketIntent = new Intent(EventDetailsActivity.this, SelectTicketActivity.class);
            selectTicketIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            selectTicketIntent.putExtra(Constants.ApiKeyName.eventId, getEventId);
            selectTicketIntent.putExtra(Constants.hostId, hostId);
            selectTicketIntent.putExtra(Constants.eventName, eventName);
            selectTicketIntent.putExtra(Constants.eventStartTime, eventStartTime);
            selectTicketIntent.putExtra(Constants.eventEndTime, eventEndTime);
            selectTicketIntent.putExtra(Constants.eventDate, eventStartDate);
            selectTicketIntent.putExtra(Constants.eventAdd, address);
            startActivity(selectTicketIntent);
        } else if (isExternalTicketStatus != null && isExternalTicketStatus) {
            Log.d("fasfnaskl", "buyTicketOnClick: " + stringBuffer.toString());
            if (stringBuffer.length() > 0) {
                CommonUtils.getCommonUtilsInstance().loginAlert(EventDetailsActivity.this, true, stringBuffer.toString());
            } else {
                CommonUtils.getCommonUtilsInstance().loginAlert(EventDetailsActivity.this, true, "Tickets not available");
            }

        }

    }

    public void seeMoreOnClick(View view) {
        Intent seeMoreIntent = new Intent(this, SeeMoreReviewActivity.class);
        seeMoreIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        seeMoreIntent.putExtra(Constants.ApiKeyName.eventId, getEventId);
        startActivity(seeMoreIntent);
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        detailsBinding.eventDetailsRootContainer.setVisibility(View.VISIBLE);

        if (responseObj != null) {
            if (typeAPI.equalsIgnoreCase(APIs.MARK_FAVORITES_EVENT)) {
                if (detailsModal.getData().getFavorite()) {
                    detailsModal.getData().setFavorite(false);
                    Glide.with(EventDetailsActivity.this).load(R.drawable.unselect_heart_icon).into(detailsBinding.content.ivLikeDislikeImg);
                    ShowToast.successToast(EventDetailsActivity.this, "Removed favorite");
                } else {
                    detailsModal.getData().setFavorite(true);
                    Glide.with(EventDetailsActivity.this).load(R.drawable.heart).into(detailsBinding.content.ivLikeDislikeImg);
                    ShowToast.successToast(EventDetailsActivity.this, "Added to favorite list");
                }
                return;
            }
            detailsModal = new Gson().fromJson(responseObj.toString(), UserEventDetailsModal.class);
            isTicketAvailable = detailsModal.getData().getAvailability();
            UserData.TicketInfo ticket_info = detailsModal.getData().getTicket_info();

            CommonUtils.getCommonUtilsInstance().compareTwoDate(detailsModal.getData().getSellingStart(), detailsModal.getData().getSellingEnd());

            isExternalTicketStatus = detailsModal.getData().getExternalTicket();


            if (isTicketAvailable != null && isTicketAvailable || isExternalTicketStatus != null && isExternalTicketStatus) {
                detailsBinding.btnLogin.setBackground(getResources().getDrawable(R.drawable.custom_interested_btn));
                detailsBinding.btnLogin.setText(showPriceMinMax(ticket_info));
            } else {
                detailsBinding.btnLogin.setBackground(getResources().getDrawable(R.drawable.custom_disable_btn));
                detailsBinding.btnLogin.setText(showPriceMinMax(ticket_info));
                shouldButtonDisable = true;
            }

            String ticketInfoURL = detailsModal.getData().getTicketInfoURL();
            String eventHelpLine = detailsModal.getData().getEventHelpLine();

            if (ticketInfoURL != null) {
                stringBuffer.append(ticketInfoURL);
            }
            if (eventHelpLine != null) {
                stringBuffer.append("\n").append(eventHelpLine);
            }


            if (detailsModal.getData().getEventImages() != null && !detailsModal.getData().getEventImages().isEmpty()) {
                Glide.with(EventDetailsActivity.this).load(detailsModal.getData().getEventImages().get(0).getEventImage()).placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(detailsBinding.ivEventImg);
            } else {
                Glide.with(EventDetailsActivity.this).load("").placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(detailsBinding.ivEventImg);
            }


            hostId = detailsModal.getData().getHost().getId();
            hostName = detailsModal.getData().getHost().getName();
            ticketInfoUrl = detailsModal.getData().getTicketInfoURL();
            eventHelpLine = detailsModal.getData().getEventHelpLine();

            validateEventDetails();
            if (tagList.size() > 0)
                tagList.clear();
            if (allGalleryImgModalList.size() > 0)
                allGalleryImgModalList.clear();

            if (!CommonUtils.getCommonUtilsInstance().isUserLogin() || detailsModal.getData().getReviewed() != null && detailsModal.getData().getReviewed()) {
                detailsBinding.content.tvAddReview.setVisibility(View.GONE);
            } else {
                if (!detailsModal.getData().getReviewed())
                    detailsBinding.content.tvAddReview.setVisibility(View.VISIBLE);
                else {
                    detailsBinding.content.tvAddReview.setVisibility(View.GONE);
                }
            }
            if (!CommonUtils.getCommonUtilsInstance().isUserLogin()) {
                detailsBinding.content.ivLikeDislikeImg.setVisibility(View.GONE);
                detailsBinding.content.ivLikeDislikeImg.setVisibility(View.GONE);
            } else {
                detailsBinding.content.ivLikeDislikeImg.setVisibility(View.VISIBLE);
                detailsBinding.content.ivLikeDislikeImg.setVisibility(View.VISIBLE);
                if (detailsModal.getData().getFavorite()) {
                    Glide.with(EventDetailsActivity.this).load(R.drawable.heart).into(detailsBinding.content.ivLikeDislikeImg);
                } else {
                    Glide.with(EventDetailsActivity.this).load(R.drawable.unselect_heart_icon).into(detailsBinding.content.ivLikeDislikeImg);
                }
            }

            if (detailsModal.getData().getAddress() != null && detailsModal.getData().getAddress().get(0).getVenueAddress() != null) {
                address = detailsModal.getData().getAddress().get(0).getVenueAddress();
                detailsBinding.content.tvShowMapAdd.setText(address);

            } else {
                detailsBinding.content.tvShowMapAdd.setText(getString(R.string.na));
            }
            eventName = detailsModal.getData().getName();
            eventShortDes = detailsModal.getData().getDescription();
            eventImg = !detailsModal.getData().getEventImages().isEmpty() ? detailsModal.getData().getEventImages().get(0).getEventImage() : "";
            detailsBinding.content.ivEventTitle.setText(CommonUtils.getCommonUtilsInstance().makeFirstLatterCapital(eventName));

            String hostPic = detailsModal.getData().getHost().getProfilePic();
            String hostName = detailsModal.getData().getHost().getName();
            if (!TextUtils.isEmpty(hostPic)) {
                detailsBinding.content.hostUserImgShowName.setVisibility(View.GONE);
                detailsBinding.content.ivHostedUserImg.setVisibility(View.VISIBLE);
                Glide.with(EventDetailsActivity.this).load(hostPic).placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(detailsBinding.content.ivHostedUserImg);
            } else {
                detailsBinding.content.hostUserImgShowName.setVisibility(View.VISIBLE);
                detailsBinding.content.ivHostedUserImg.setVisibility(View.GONE);
                ((TextView) detailsBinding.content.hostUserImgShowName.findViewById(R.id.tvShowUserName)).setText(CommonUtils.getCommonUtilsInstance().getHostName(hostName));
            }

            detailsBinding.content.tvShowHostName.setText(hostName);
            detailsBinding.content.ratingBar.setRating(detailsModal.getData().getRating() != null ? detailsModal.getData().getRating() : 0);
            detailsBinding.content.tvShowRatingCount.setText(detailsModal.getData().getReviewCount() != null ? String.valueOf(detailsModal.getData().getReviewCount()) : "(0)");

            eventStartDateOrTime = detailsModal.getData().getStart();
            eventEndDateOrTime = detailsModal.getData().getEnd();

            eventStartDate = CommonUtils.getCommonUtilsInstance().getDateMonthName(eventStartDateOrTime, true);
            eventEndDate = CommonUtils.getCommonUtilsInstance().getDateMonthName(eventEndDateOrTime, true);


            eventStartTime = CommonUtils.getCommonUtilsInstance().getStartEndEventTime(eventStartDateOrTime);
            eventEndTime = CommonUtils.getCommonUtilsInstance().getStartEndEventTime(eventEndDateOrTime);

            detailsBinding.content.tvStartEventDate.setText(eventStartDate != null ? eventStartDate : getString(R.string.na));
            detailsBinding.content.tvEndEventDate.setText(detailsModal.getData().getEnd() != null ? eventEndDate : getString(R.string.na));

            if (eventStartTime != null && eventEndTime != null) {
                detailsBinding.content.tvStartEventTime.setText(eventStartTime + " - " + eventEndTime);
                detailsBinding.content.tvEndEventTime.setText(eventStartTime + " - " + eventEndTime);
            } else {
                detailsBinding.content.tvStartEventTime.setText(getString(R.string.na));
                detailsBinding.content.tvEndEventTime.setText(getString(R.string.na));
            }
            if (detailsModal.getData().getDescription() != null) {
                detailsBinding.content.descriptionContainer.setVisibility(View.VISIBLE);
                detailsBinding.content.tvShowDescription.setText(detailsModal.getData().getDescription());
            }
            if (detailsModal.getData().getAdditionalInfo() != null) {
                detailsBinding.content.additionalInfoContainer.setVisibility(View.VISIBLE);
                detailsBinding.content.tvShowAdditionalInfo.setText(detailsModal.getData().getAdditionalInfo());
            }

            if (detailsModal.getData().getCategoryName() != null) {

                tagList.add(detailsModal.getData().getCategoryName());
                detailsBinding.content.tagContainer.setVisibility(View.VISIBLE);
            } else {
                detailsBinding.content.tagContainer.setVisibility(View.GONE);
            }
            if (detailsModal.getData().getSubCategories() != null) {
                detailsBinding.content.tagContainer.setVisibility(View.VISIBLE);
                for (int i = 0; i < detailsModal.getData().getSubCategories().size(); i++) {
                    tagList.add(detailsModal.getData().getSubCategories().get(i).getSubCategoryName());
                }
            } else {
                detailsBinding.content.tagContainer.setVisibility(View.GONE);
            }
            showEventDetailsTag();
            getCurrentLocation(Double.parseDouble(detailsModal.getData().getAddress().get(0).getLatitude()), Double.parseDouble(detailsModal.getData().getAddress().get(0).getLongitude()));

            if (detailsModal.getData().getReviews() != null && detailsModal.getData().getReviews().size() > 0) {
                detailsBinding.content.tvShowReviewTitle.setVisibility(View.VISIBLE);
                detailsBinding.content.reviewContainer.setVisibility(View.VISIBLE);
                detailsBinding.content.recyclerReviews.setVisibility(View.VISIBLE);
                setupUserReview(detailsModal.getData().getReviews());
            } else {
                detailsBinding.content.tvShowReviewTitle.setVisibility(View.VISIBLE);
                detailsBinding.content.tvShowNoReviews.setVisibility(View.VISIBLE);
                detailsBinding.content.recyclerReviews.setVisibility(View.GONE);
            }
            if (detailsModal.getData().getEventImages() != null || detailsModal.getData().getVenueVenuImages() != null) {
                if (detailsModal.getData().getEventImages().size() > 0) {
                    for (int i = 0; i < detailsModal.getData().getEventImages().size(); i++) {
                        allGalleryImgModalList.add(new GetAllGalleryImgModal(detailsModal.getData().getEventImages().get(i).getEventImage()));
                    }
                }
                if (detailsModal.getData().getVenueVenuImages().size() > 0) {
                    for (int i = 0; i < detailsModal.getData().getVenueVenuImages().size(); i++) {
                        allGalleryImgModalList.add(new GetAllGalleryImgModal(detailsModal.getData().getVenueVenuImages().get(i).getVenueImage()));
                    }
                }
            }
            if (allGalleryImgModalList.size() > 0) {
                detailsBinding.content.galleryContainer.setVisibility(View.VISIBLE);
                //galleryAdapter.notifyDataSetChanged();
            } else {
                detailsBinding.content.galleryContainer.setVisibility(View.GONE);
            }
            if (detailsModal.getData().getRelatedEvents() != null && detailsModal.getData().getRelatedEvents().size() != 0) {
                detailsBinding.content.relatedEventContainer.setVisibility(View.VISIBLE);
                setupShowEventRelatedList(detailsModal.getData().getRelatedEvents());
            }
            isUserGaveReview = false;
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

    private void setupUserReview(List<Review> seeMoreDataList) {

        if (CommonUtils.getCommonUtilsInstance().isUserLogin())
            detailsBinding.content.tvSeeMore.setVisibility(View.VISIBLE);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        detailsBinding.content.recyclerReviews.setLayoutManager(manager);
        reviewsAdapter = new ReviewsAdapter(EventDetailsActivity.this, seeMoreDataList, null, false);
        detailsBinding.content.recyclerReviews.setAdapter(reviewsAdapter);
    }

    public void addReviewDialogLaunch() {
        if (ratingDialogFragment == null) {
            ratingDialogFragment = new RatingDialogFragment();
        }
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.ApiKeyName.eventId, getEventId);
        ratingDialogFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        ratingDialogFragment.show(fragmentTransaction, "RatingDialogFragment");
        ratingDialogFragment.getUserReviewListener(isReviewedSuccess -> {
            isUserGaveReview = true;
            getDynamicLinks();
            detailsBinding.content.tvShowNoReviews.setVisibility(View.GONE);
            detailsBinding.content.nested.scrollTo(0, (int) detailsBinding.content.reviewContainer.getY());

        });

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
        if (!CommonUtils.getCommonUtilsInstance().isUserLogin()) {
            CommonUtils.getCommonUtilsInstance().loginAlert(EventDetailsActivity.this, false, "");
            return;
        }
        Intent hostProfileIntent = new Intent(this, HostProfileActivity.class);
        hostProfileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        hostProfileIntent.putExtra(Constants.hostId, detailsModal.getData().getHost().getId());
        startActivity(hostProfileIntent);
    }

    private void validateEventDetails() {
        if (!CommonUtils.getCommonUtilsInstance().isUserLogin()) {
            detailsBinding.content.tvAddReview.setVisibility(View.GONE);
        }
    }

    private void eventDetailsAuthRequest(int getEventId) {
        myLoader.show(" ");
        Call<JsonElement> userEventDetailsObj = APICall.getApiInterface().getEventDetailsAuth(CommonUtils.getCommonUtilsInstance().getDeviceAuth(), getEventId);
        new APICall(EventDetailsActivity.this).apiCalling(userEventDetailsObj, this, APIs.USER_EVENT_DETAILS_NO_AUTH);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (SessionValidation.getPrefsHelper().getPref("userlogin") != null) {
            SessionValidation.getPrefsHelper().delete("userlogin");
            Toast.makeText(this, "deleted", Toast.LENGTH_SHORT).show();
        }
    }

    private void likeEventRequest() {
        myLoader.show("");
        JsonObject obj = new JsonObject();
        obj.addProperty(Constants.ApiKeyName.eventId, getEventId);
        obj.addProperty(Constants.ApiKeyName.isFavorite, !detailsModal.getData().getFavorite() ? true : false);
        Call<JsonElement> likeEventCall = APICall.getApiInterface().likeEvent(CommonUtils.getCommonUtilsInstance().getDeviceAuth(), obj);
        new APICall(EventDetailsActivity.this).apiCalling(likeEventCall, this, APIs.MARK_FAVORITES_EVENT);
    }


    public void markFavoriteEventOnClick(View view) {
        likeEventRequest();
    }

    private void showEventDetailsTag() {
        Log.d("fmnafnkla", "showEventDetailsTag: " + tagList);
        EventDetailsTagAdapter eventDetailsTagAdapter = new EventDetailsTagAdapter(tagList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        detailsBinding.content.showTagRecycler.setLayoutManager(gridLayoutManager);
        detailsBinding.content.showTagRecycler.setAdapter(eventDetailsTagAdapter);
    }

    public void shareOnClick(View view) {
        createDynamicLinks(eventName, eventShortDes, eventImg, getEventId);
    }

    public void addEventToCalenderOnClick(View view) {
        addEventDateToGoogleCalender(eventName, address, eventShortDes);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        if (mOfferDetail.getLongitude() != null && mOfferDetail.getAddress() != null){
//            LatLng latLng = new LatLng(Double.parseDouble(mOfferDetail.getAddress()), Double.parseDouble(mOfferDetail.getLongitude()));
//            googleMap.addMarker(new MarkerOptions().position(latLng));
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivBackBtn)
            finish();
    }

    public void onBackClick(View view) {
        finish();
    }

    private void createDynamicLinks(String eventTitle, String eventDes, String eventImg, int eventId) {

        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://365live.com/user/event/" + eventId))
                .setDomainUriPrefix("https://365live.page.link")

                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder("com.ebabu.event365live")
                        .setMinimumVersion(1)
                        .build()
                )
                .setIosParameters(new DynamicLink.IosParameters.Builder("com.eventuser.app")
                        .setAppStoreId("1492460553")
                        .setMinimumVersion("1.5")
                        .build()
                )
                .setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder()
                        .setTitle(eventTitle)
                        .setDescription(eventDes)
                        .setImageUrl(Uri.parse(eventImg))
                        .build()
                )
                .buildShortDynamicLink()
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {
                        Uri shortLink = task.getResult().getShortLink();
                        Uri flowchartLink = task.getResult().getPreviewLink();
                        CommonUtils.getCommonUtilsInstance().shareIntent(EventDetailsActivity.this, flowchartLink.toString());
                    }
                });

    }

    private void getDynamicLinks() {
        myLoader = new MyLoader(this);
        tagList = new ArrayList<>();
        allGalleryImgModalList = new ArrayList<>();

        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    Uri deepLink = null;
                    if (pendingDynamicLinkData != null) {
                        if (pendingDynamicLinkData.getLink() != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            Log.d("fnalksnfa", "getDynamicLinks: " + deepLink.toString());
                            Log.d("fnalksnfa", "getDynamicLinks: " + deepLink.getLastPathSegment());
                            if (deepLink.getLastPathSegment() != null) {
                                getEventId = Integer.parseInt(deepLink.getLastPathSegment());
                                setBundleData(getEventId);
                                return;
                            }
                            ShowToast.errorToast(EventDetailsActivity.this, getString(R.string.something_wrong));
                            finish();
                            return;
                        }
                        finish();
                    } else {
                        getEventId = getIntent().getExtras().getInt(Constants.ApiKeyName.eventId);
                        setBundleData(getEventId);
                    }
                });
    }

    private void addEventDateToGoogleCalender(String eventTitle, String eventLocation, String eventDes) {
        String[] startSetDate = CommonUtils.getCommonUtilsInstance().addToGCalenderDateTime(detailsModal.getData().getStart()).split("-");
        String[] endSetDate = CommonUtils.getCommonUtilsInstance().addToGCalenderDateTime(detailsModal.getData().getEnd()).split("-");

        Intent calIntent = new Intent(Intent.ACTION_INSERT);
        calIntent.setData(CalendarContract.Events.CONTENT_URI);
        calIntent.putExtra(CalendarContract.Events.TITLE, eventTitle);
        calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, eventLocation);
        calIntent.putExtra(CalendarContract.Events.DESCRIPTION, eventDes);
        Calendar startTime = Calendar.getInstance();
        startTime.set(Integer.parseInt(startSetDate[0]), Integer.parseInt(startSetDate[1]), Integer.parseInt(startSetDate[2]), Integer.parseInt(startSetDate[3]), Integer.parseInt(startSetDate[4]));
        Calendar endTime = Calendar.getInstance();
        endTime.set(Integer.parseInt(endSetDate[0]), Integer.parseInt(endSetDate[1]), Integer.parseInt(endSetDate[2]), Integer.parseInt(endSetDate[3]), Integer.parseInt(endSetDate[4]));
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                startTime.getTimeInMillis());
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                endTime.getTimeInMillis());
        startActivity(calIntent);
    }

    private StringBuilder showPriceMinMax(UserData.TicketInfo ticketInfo) {
        StringBuilder showPrice = new StringBuilder();
        String letsDoIt = " Interested? Let's do it";

        if (ticketInfo != null) {
            String min = String.valueOf(ticketInfo.getMinValue() != null ? ticketInfo.getMinValue() : "0");
            String max = String.valueOf(ticketInfo.getMaxValue() != null ? ticketInfo.getMaxValue() : "0");
            String type = ticketInfo.getType() != null ? ticketInfo.getType() : "";

            if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase("free")) {
                if (max.equalsIgnoreCase("0"))
                    showPrice.append("Free Event").append(" ").append(letsDoIt);
                else
                    showPrice.append("Free").append(" - $").append(NumberFormat.getNumberInstance(Locale.US).format(Double.parseDouble(max))).append(letsDoIt);
            } else {
                if (!TextUtils.isEmpty(min)) {
                    showPrice.append("$").append(NumberFormat.getNumberInstance(Locale.US).format(Double.parseDouble(min))).append(" - ").append(letsDoIt);
                } else if (!TextUtils.isEmpty(max)) {
                    showPrice.append("$").append(Double.parseDouble(max)).append(" - ").append(letsDoIt);
                } else if (!TextUtils.isEmpty(min) && !TextUtils.isEmpty(max)) {
                    showPrice.append("$").append(NumberFormat.getNumberInstance(Locale.US).format(Double.parseDouble(min))).append(" - $ ").append(NumberFormat.getNumberInstance(Locale.US).format(Double.parseDouble(max))).append(letsDoIt);
                }
            }
        } else
            showPrice.append(letsDoIt);
        return showPrice;
    }

}