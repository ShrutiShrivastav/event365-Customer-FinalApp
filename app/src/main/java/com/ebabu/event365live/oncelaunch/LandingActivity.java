package com.ebabu.event365live.oncelaunch;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ebabu.event365live.R;
import com.ebabu.event365live.auth.activity.LoginActivity;
import com.ebabu.event365live.databinding.LandingBeforeLoginBinding;
import com.ebabu.event365live.home.activity.HomeActivity;
import com.ebabu.event365live.home.activity.HomeFilterActivity;
import com.ebabu.event365live.home.adapter.EventListAdapter;
import com.ebabu.event365live.homedrawer.activity.ChooseRecommendedCatActivity;
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
import com.ebabu.event365live.utils.EnableGps;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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


public class LandingActivity extends AppCompatActivity implements View.OnClickListener, GetResponseData,CommonUtils.FusedCurrentLocationListener {

    private MyLoader myLoader;
    private LandingBeforeLoginBinding beforeLoginBinding;
    private EventLandingCatAdapter landingAdapter;
    private EventListAdapter eventListAdapter;
    private UpdateInfoFragmentDialog infoFragmentDialog;
    private List<Address> addresses;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    private List<NearByNoAuthModal.Category> categoryList;
    private LatLng currentLatLng;

    @Override
    protected void onStart() {
        super.onStart();
        if(CommonUtils.getCommonUtilsInstance().isUserLogin()){
            navigateToHomeScreen();
            return;
        }
        CommonUtils.getCommonUtilsInstance().getCurrentLocation(LandingActivity.this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeLoginBinding = DataBindingUtil.setContentView(this,R.layout.landing_before_login);
        beforeLoginBinding.searchContainer.setOnClickListener(this);
        myLoader = new MyLoader(this);
        //getCurrentLocation();

     //   new EnableGps(LandingActivity.this).enablegps();




    }

    private void setupLandingEvent(){
        landingAdapter = new EventLandingCatAdapter(this,categoryList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        beforeLoginBinding.recyclerEvent.setLayoutManager(linearLayoutManager);
        beforeLoginBinding.recyclerEvent.setAdapter(landingAdapter);
    }

    private void setupFeaturedEvent(List<NearByNoAuthModal.EventList> eventList){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d("fnlanflanfkla", totalItemsCount+" onLoadMore: "+page);
            }
        } ;
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
        startActivity(loginIntent);
    }

    public void filterOnClick(View view) {
        Intent homeFilterIntent = new Intent(LandingActivity.this, HomeFilterActivity.class);
        homeFilterIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeFilterIntent);
        //skipBtnOnClick();
//        dialog();
    }


    public void skipBtnOnClick() {
        if (infoFragmentDialog == null) {
            infoFragmentDialog = new UpdateInfoFragmentDialog();
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        infoFragmentDialog.show(fragmentTransaction, UpdateInfoFragmentDialog.TAG);

    }

    public void dialog() {
        if (infoFragmentDialog == null) {
            infoFragmentDialog = new UpdateInfoFragmentDialog();
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        infoFragmentDialog.show(fragmentTransaction, UpdateInfoFragmentDialog.TAG);

    }

    public void currentLocation(View view) {
        //dialog();
        CommonUtils.getCommonUtilsInstance().launchSelectAddressFrag(LandingActivity.this,null,false);
        // navigateToRecommendedCategorySelect();
        //startActivity(new Intent(LandingActivity.this, ContactUsActivity.class));
        //startActivity(new Intent(LandingActivity.this, EventDetailsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(Constants.ApiKeyName.eventId,"275"));
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

    private void getCurrentLocation() {
        final FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1001);
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null)
                {
                    currentLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                    try {
                        Geocoder geocoder = new Geocoder(LandingActivity.this, Locale.getDefault());
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if(addresses != null) {
                            String city = addresses.get(0).getLocality();
                            String country = addresses.get(0).getCountryName();
                            beforeLoginBinding.tvShowCurrentLocation.setText(city + " "+country);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == Constants.CURRENT_FUSED_LOCATION_REQUEST){
            if(grantResults.length >0 && permissions[0].equals(Manifest.permission.ACCESS_COARSE_LOCATION) &&
                    permissions[1].equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                //getCurrentLocation();
                CommonUtils.getCommonUtilsInstance().getCurrentLocation(LandingActivity.this);
            }
            else {
                //getCurrentLocation();
                CommonUtils.getCommonUtilsInstance().getCurrentLocation(LandingActivity.this);
            }
        }
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
                beforeLoginBinding.tvRegularTitle.setVisibility(View.VISIBLE);
                beforeLoginBinding.tvRegularTitle.setVisibility(View.GONE);
                landingAdapter.notifyDataSetChanged();
            }
            if(nearByNoAuthModal.getData().getEventList()!= null){
                setupFeaturedEvent(nearByNoAuthModal.getData().getEventList());
                beforeLoginBinding.recyclerEventFeature.setVisibility(View.VISIBLE);
                beforeLoginBinding.noDataFoundContainer.setVisibility(View.GONE);
                beforeLoginBinding.tvRegularTitle.setVisibility(View.VISIBLE);
                beforeLoginBinding.tvRegularTitle.setVisibility(View.GONE);
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

    private void nearByEventRequest(){
        myLoader.show("");

        JsonObject filterObj = new JsonObject();
        filterObj.addProperty(Constants.latitude,currentLatLng.latitude);
        filterObj.addProperty(Constants.longitude,currentLatLng.longitude);

        Log.d("fasnflnasklfnla", currentLatLng.latitude+" nearByEventRequest: "+currentLatLng.longitude);

        filterObj.addProperty(Constants.miles,"10000");
        filterObj.addProperty(Constants.cost,"4000");

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
                nearByEventRequest();
                Geocoder geocoder = new Geocoder(LandingActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(currentLatLng.latitude, currentLatLng.longitude, 1);
                    String stateName = addresses.get(0).getAdminArea();
                    String cityName = addresses.get(0).getLocality();
                    beforeLoginBinding.tvShowCurrentLocation.setText(place.getName() + ", " + cityName + ", " + stateName);
                    beforeLoginBinding.tvShowCurrentLocation.setSelected(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }if(requestCode == Constants.REQUEST_CHECK_SETTINGS){
            CommonUtils.getCommonUtilsInstance().getCurrentLocation(LandingActivity.this);
        }
    }
    private void navigateToRecommendedCategorySelect() {
        Intent recommendedIntent = new Intent(LandingActivity.this, ChooseRecommendedCatActivity.class);
        recommendedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(recommendedIntent);
    }

    @Override
    public void getFusedCurrentSuccess(Location location) {
        if(location != null) {
            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            nearByEventRequest();
            try {
                Geocoder geocoder = new Geocoder(LandingActivity.this, Locale.getDefault());
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null) {
                    String city = addresses.get(0).getLocality();
                    String country = addresses.get(0).getCountryName();
                    beforeLoginBinding.tvShowCurrentLocation.setText(city + " " + country);
                }
            } catch (IOException e) {
                e.printStackTrace();
                ShowToast.errorToast(LandingActivity.this, getString(R.string.something_wrong_to_get_location));
            }
        }
        Log.d("nflkanfklan", currentLatLng.latitude+" HOmfilter: "+currentLatLng.longitude);
    }
    @Override
    public void getFusedCurrentFailed(Exception e) {
        ShowToast.errorToast(LandingActivity.this,getString(R.string.something_wrong_to_get_location));
    }

    private void navigateToHomeScreen(){
        Intent homeIntent = new Intent(LandingActivity.this, HomeActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
        finish();
    }


}
