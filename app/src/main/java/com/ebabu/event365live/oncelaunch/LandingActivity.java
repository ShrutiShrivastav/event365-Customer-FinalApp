package com.ebabu.event365live.oncelaunch;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ebabu.event365live.MainActivity;
import com.ebabu.event365live.R;
import com.ebabu.event365live.auth.activity.LoginActivity;
import com.ebabu.event365live.databinding.LandingBeforeLoginBinding;
import com.ebabu.event365live.home.activity.HomeFilterActivity;
import com.ebabu.event365live.home.adapter.EventListAdapter;
import com.ebabu.event365live.homedrawer.activity.SearchHomeActivity;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.oncelaunch.adapter.EventLandingCatAdapter;
import com.ebabu.event365live.oncelaunch.modal.nearbynoauth.NearByNoAuthModal;
import com.ebabu.event365live.oncelaunch.utils.EndlessRecyclerViewScrollListener;
import com.ebabu.event365live.userinfo.fragment.UpdateInfoFragmentDialog;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.SessionValidation;
import com.ebabu.event365live.utils.ShowToast;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;


public class LandingActivity extends MainActivity implements View.OnClickListener, GetResponseData {


    private MyLoader myLoader;
    private LandingBeforeLoginBinding beforeLoginBinding;
    private EventLandingCatAdapter landingAdapter;
    private EventListAdapter eventListAdapter;
    private UpdateInfoFragmentDialog infoFragmentDialog;
    private List<Address> addresses;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    private List<NearByNoAuthModal.Category> categoryList;
    private LatLng currentLatLng;
    private boolean isLoading;
    private int currentPage=1, totalItem = 5;
    private boolean isLastPage = false;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeLoginBinding = DataBindingUtil.setContentView(this,R.layout.landing_before_login);
        beforeLoginBinding.searchContainer.setOnClickListener(this);
        myLoader = new MyLoader(this);
        myLoader.show("");

        if(CommonUtils.getCommonUtilsInstance().isUserLogin())
            beforeLoginBinding.tvLoginBtn.setVisibility(View.INVISIBLE);

        getCurrentLocationInstance(new GetCurrentLocation() {
            @Override
            public void getCurrentLocationListener(LatLng latLng) {
                if (latLng != null) {
                    myLoader.dismiss();
                    String[] currentLocation = CommonUtils.getCommonUtilsInstance().getCurrentLocation().split(" ");
                    currentLatLng = new LatLng(Double.parseDouble(currentLocation[0]), Double.parseDouble(currentLocation[1]));
                    setEvent(currentLatLng.latitude,currentLatLng.longitude);
                    Log.d("nfklanfklsa", currentLatLng.latitude+" getCurrentLocationListener: "+currentLatLng.longitude);
                }

            }
        });
    }

    private void setupLandingEvent(){
        landingAdapter = new EventLandingCatAdapter(categoryList,null,false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        beforeLoginBinding.recyclerEvent.setLayoutManager(linearLayoutManager);
        beforeLoginBinding.recyclerEvent.setAdapter(landingAdapter);
    }

    private void setupFeaturedEvent(List<NearByNoAuthModal.EventList> eventList){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        eventListAdapter = new EventListAdapter(this,true,eventList);
        beforeLoginBinding.recyclerEventFeature.setLayoutManager(linearLayoutManager);
        beforeLoginBinding.recyclerEventFeature.setAdapter(eventListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CommonUtils.getCommonUtilsInstance().transparentStatusBar(this);
    }

    public void loginOnClickBtn(View view) {
        Intent loginIntent = new Intent(LandingActivity.this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(loginIntent,4001);

    }

    public void filterOnClick(View view) {
        Intent homeFilterIntent = new Intent(LandingActivity.this, HomeFilterActivity.class);
        homeFilterIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeFilterIntent);
    }



    public void currentLocation(View view) {
        CommonUtils.getCommonUtilsInstance().launchSelectAddressFrag(LandingActivity.this,null,false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.searchContainer:
                Intent intent = new Intent(LandingActivity.this, SearchHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(this, beforeLoginBinding.ivSearchIcon, getString(R.string.search_event_transition));
                startActivity(intent, options.toBundle());
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        if(typeAPI.equalsIgnoreCase(APIs.NO_AUTH_NEAR_BY_EVENT)){
            NearByNoAuthModal nearByNoAuthModal = new Gson().fromJson(responseObj.toString(), NearByNoAuthModal.class);
            categoryList = nearByNoAuthModal.getData().getCategory();
            if(categoryList.size() != 0){
                setupLandingEvent();
                beforeLoginBinding.recyclerEvent.setVisibility(View.VISIBLE);
                beforeLoginBinding.noDataFoundContainer.setVisibility(View.GONE);
                landingAdapter.notifyDataSetChanged();
            }
            if(nearByNoAuthModal.getData().getEventList()!= null){
                setupFeaturedEvent(nearByNoAuthModal.getData().getEventList());
                beforeLoginBinding.recyclerEventFeature.setVisibility(View.VISIBLE);
                beforeLoginBinding.noDataFoundContainer.setVisibility(View.GONE);
                beforeLoginBinding.tvRegularTitle.setVisibility(View.VISIBLE);
                return;
            }
            beforeLoginBinding.noDataFoundContainer.setVisibility(View.VISIBLE);
            beforeLoginBinding.tvRegularTitle.setVisibility(View.GONE);
            ((TextView)beforeLoginBinding.noDataFoundContainer.findViewById(R.id.tvShowNoDataFound)).setText(getString(R.string.no_data_found));
            ((TextView)beforeLoginBinding.noDataFoundContainer.findViewById(R.id.tvShowNoDataFound)).setTextColor(Color.WHITE);

        }
    }
    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        if(errorCode == 406){
            beforeLoginBinding.noDataFoundContainer.setVisibility(View.VISIBLE);
            beforeLoginBinding.tvRegularTitle.setVisibility(View.GONE);
            beforeLoginBinding.recyclerEvent.setVisibility(View.GONE);
            beforeLoginBinding.recyclerEventFeature.setVisibility(View.GONE);
            ((TextView)beforeLoginBinding.noDataFoundContainer.findViewById(R.id.tvShowNoDataFound)).setText(getString(R.string.event_not_available));
            ((TextView)beforeLoginBinding.noDataFoundContainer.findViewById(R.id.tvShowNoDataFound)).setTextColor(Color.WHITE);
        }
    }

    private void nearByEventRequest(double getLat, double getLng){
        myLoader.show("");
        JsonObject filterObj = new JsonObject();
        filterObj.addProperty(Constants.latitude,getLat);
        filterObj.addProperty(Constants.longitude,getLng);
        filterObj.addProperty(Constants.miles,"10000");
        filterObj.addProperty(Constants.cost,"4000");
        Log.d("afnlksanflas", getLat+" nearByEventRequest: "+getLng);
        Call<JsonElement> landingCall = APICall.getApiInterface().noAuthNearByEvent(filterObj);
        new APICall(LandingActivity.this).apiCalling(landingCall,this, APIs.NO_AUTH_NEAR_BY_EVENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                currentLatLng = place.getLatLng();
                if(currentLatLng != null){
                    CommonUtils.getCommonUtilsInstance().saveCurrentLocation(currentLatLng.latitude,currentLatLng.longitude);
                    setEvent(currentLatLng.latitude, currentLatLng.longitude);
                }

                Log.d("nfklanfklsa", currentLatLng.latitude+" onActivityResult: "+currentLatLng.longitude);
            }
        }
        if(resultCode == Activity.RESULT_OK && requestCode == 4001){
            if(CommonUtils.getCommonUtilsInstance().isUserLogin())
                beforeLoginBinding.tvLoginBtn.setVisibility(View.INVISIBLE);
        }
    }
    private void setEvent(double lat, double lng){
        Log.d("fnaslfnklas", lat+" getLocation: "+lng);
        nearByEventRequest(lat, lng);
        try {
            Geocoder geocoder = new Geocoder(LandingActivity.this, Locale.getDefault());
            addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null) {
                String fullAddress = addresses.get(0).getAddressLine(0);
                String stateName = addresses.get(0).getAdminArea();
                String city = addresses.get(0).getLocality();
                String country = addresses.get(0).getCountryName();
                beforeLoginBinding.tvShowCurrentLocation.setText(city + " " + stateName+ " "+ country);
                beforeLoginBinding.tvShowCurrentLocation.setSelected(true);


            }
        } catch (IOException e) {
            e.printStackTrace();
            ShowToast.errorToast(LandingActivity.this, getString(R.string.something_wrong_to_get_location));
        }

    }
}
