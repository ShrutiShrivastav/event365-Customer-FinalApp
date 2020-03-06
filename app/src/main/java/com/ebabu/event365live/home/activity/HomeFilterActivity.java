
package com.ebabu.event365live.home.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.ebabu.event365live.MainActivity;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityHomeFilterBinding;
import com.ebabu.event365live.event.LoginEvent;
import com.ebabu.event365live.home.adapter.CategoryListAdapter;
import com.ebabu.event365live.home.modal.SubCategoryModal;
import com.ebabu.event365live.home.modal.GetCategoryModal;
import com.ebabu.event365live.home.modal.LoginViewModal;
import com.ebabu.event365live.home.modal.nearbymodal.EventList;
import com.ebabu.event365live.home.modal.nearbymodal.NearByEventModal;
import com.ebabu.event365live.homedrawer.modal.bubblecategory.EventSubCategoryData;
import com.ebabu.event365live.homedrawer.modal.bubblecategory.EventSubCategoryModal;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.SessionValidation;
import com.ebabu.event365live.utils.ShowToast;
import com.ebabu.event365live.utils.ValidationUtil;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

public class HomeFilterActivity extends AppCompatActivity implements TabLayout.BaseOnTabSelectedListener, GetResponseData {

    private ActivityHomeFilterBinding filterBinding;
    LoginEvent loginEvent;
    private MyLoader myLoader;
    private List<EventSubCategoryData> getSubCatList = new ArrayList<>();
    private ChipGroup chipGroup;
    private CategoryListAdapter categoryListAdapter;
    private PlacesClient placesClient;
    public static LatLng currentLatLng;
    public static Place place;
    private GetCategoryModal getCategoryModal;

    private String whichDate = "thisWeek";
    private Integer getCategoryId;
    private boolean isSubCatSelected;
    private JsonArray subCatIdArray;
    private boolean isSwipeMode;
    boolean firstTimeOpenScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filterBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_filter);
        filterBinding.viewTabLayout.addOnTabSelectedListener(this);
        myLoader = new MyLoader(this);
        getDate(whichDate);

        subCatIdArray = new JsonArray();
        placesClient = Places.createClient(this);
        getLocation();
        if (CommonUtils.getCommonUtilsInstance().isSwipeMode()) {
           // filterBinding.viewTabLayout.getTabAt(0).select();
            filterBinding.viewTabLayout.getTabAt(0).select();
        } else {
            filterBinding.viewTabLayout.getTabAt(1).select();
        }

//        if (!CommonUtils.getCommonUtilsInstance().isSwipeMode()) {
//            filterBinding.viewTabLayout.getTabAt(1).select();
//        }


        filterBinding.seekBarDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                filterBinding.tvShowDistance.setText(progress + " Miles");
                CommonUtils.getCommonUtilsInstance().saveFilterDistance(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        filterBinding.seekBarAdmissionFee.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                filterBinding.tvShowRupee.setText(String.valueOf("$"+progress));
                CommonUtils.getCommonUtilsInstance().saveFilterAdmissionCost(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        filterBinding.spinnerShowCatRecommended.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getCategoryId = getCategoryModal.getData().get(adapterView.getSelectedItemPosition()).getId();
                filterBinding.tvShowSpinnerItem.setText(getCategoryModal.getData().get(adapterView.getSelectedItemPosition()).getCategoryName());
                subCategoryRequest(getCategoryId);
                categoryListAdapter.setSelection(adapterView.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        filterBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                CommonUtils.getCommonUtilsInstance().saveEventDate(tab.getPosition());
                launchOnTabClick(tab.getPosition());


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if(firstTimeOpenScreen){
                    launchOnTabClick(tab.getPosition());
                }
                firstTimeOpenScreen = true;


            }
        });

        loginEvent = new LoginEvent();
        categoryRequest();
    }
    private void showRecommendedCategory() {
        chipGroup = filterBinding.chipGroupShowEvent;
        for (EventSubCategoryData getCatData : getSubCatList) {
            Chip chip = new Chip(HomeFilterActivity.this);
            chip.setCheckable(true);
            chip.setCheckedIconVisible(true);
            chip.setClickable(true);
            chip.setGravity(Gravity.HORIZONTAL_GRAVITY_MASK);
            chip.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(HomeFilterActivity.this, R.color.blueColor)));
            chip.setChipStrokeWidth(2);
            chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(HomeFilterActivity.this, R.color.colorWhite)));
            chip.setTag(getCatData.getId());
            chip.setText(getCatData.getSubCategoryName());
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                isSubCatSelected = isChecked;
               if(isChecked){
                   getSelectedSubCatId((int) buttonView.getTag(),false);
               }else{
                   getSelectedSubCatId((int) buttonView.getTag(),true);
               }
            });
            chipGroup.addView(chip);
        }
    }

    public void backBtnOnClick(View view) {
        finish();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        if (tab.getPosition() == 0) {
            isSwipeMode = true;
            CommonUtils.getCommonUtilsInstance().validateSwipeMode(true);

        } else if (tab.getPosition() == 1) {
            isSwipeMode = false;
            CommonUtils.getCommonUtilsInstance().validateSwipeMode(false);

        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    public void doItOnClick(View view) {
            filterEventWithAuthRequest();
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        if (responseObj != null) {

            if(typeAPI.equalsIgnoreCase(APIs.GET_CATEGORY)){
                getCategoryModal = new Gson().fromJson(responseObj.toString(), GetCategoryModal.class);
                if(getCategoryModal.getData().size()> 0) {
                    categoryListAdapter = new CategoryListAdapter(HomeFilterActivity.this, getCategoryModal.getData());
                    filterBinding.spinnerShowCatRecommended.setAdapter(categoryListAdapter);
                    return;
                }
                ShowToast.errorToast(HomeFilterActivity.this,getString(R.string.no_cate_data_found));
            }
            else if(typeAPI.equalsIgnoreCase(APIs.GET_SUB_CATEGORY_NO_AUTH)){

                EventSubCategoryModal eventSubCategoryModal = new Gson().fromJson(responseObj.toString(), EventSubCategoryModal.class);
                if (getSubCatList != null && getSubCatList.size() > 0) {
                    getSubCatList.clear();
                    chipGroup.removeAllViews();
                }

                if(eventSubCategoryModal.getEventSubCatData().size()> 0){
                    getSubCatList.addAll(eventSubCategoryModal.getEventSubCatData());
                    showRecommendedCategory();
                    return;
                }
                ShowToast.errorToast(HomeFilterActivity.this,getString(R.string.noCateFound));
                isSubCatSelected = false;
            }else if(typeAPI.equalsIgnoreCase(APIs.NEAR_BY_AUTH_EVENT) || typeAPI.equalsIgnoreCase(APIs.NO_AUTH_NEAR_BY_EVENT) ){
                Log.d("fnaslkfnklas", "HomeFilter: "+responseObj.toString());
                navigateToHomeScreen(responseObj,true);
            }
        }
    }
    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        if(errorCode == 406){
            /* Need to show not data available at home page at 406 shows not event available at this location */
            navigateToHomeScreen(null,false);
        }
    }

    private void subCategoryRequest(Integer categoryId) {
        myLoader.show("");
        JsonArray jsonElements = new JsonArray();
        JsonObject subCatObj = new JsonObject();
        jsonElements.add(categoryId);
        subCatObj.add(Constants.ApiKeyName.categoryId,jsonElements);
        Log.d("fnalsnflka", "subCategoryRequest: "+subCatObj);

        Call<JsonElement> subCatCallBack = APICall.getApiInterface().getSubCategoryNoAuth(subCatObj);
        new APICall(HomeFilterActivity.this).apiCalling(subCatCallBack, this, APIs.GET_SUB_CATEGORY_NO_AUTH);
    }


    private void categoryRequest(){
        myLoader.show("");
        Call<JsonElement> categoryCallBack = APICall.getApiInterface().getCategory();
        new APICall(HomeFilterActivity.this).apiCalling(categoryCallBack, this, APIs.GET_CATEGORY);
    }

    public void selectAddressOnClick(View view) {
        CommonUtils.getCommonUtilsInstance().launchSelectAddressFrag(HomeFilterActivity.this,null,false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = Autocomplete.getPlaceFromIntent(data);
                currentLatLng = place.getLatLng();
                CommonUtils.getCommonUtilsInstance().saveCurrentLocation(currentLatLng.latitude,currentLatLng.longitude);
                setLocation(currentLatLng.latitude,currentLatLng.longitude);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        else if(requestCode == 9001){
            if(resultCode == Activity.RESULT_OK){
                if(data != null && data.getExtras() != null){
                    whichDate = "calender";
                    SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.startDate,data.getExtras().getString(Constants.startDate));
                    SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.endDate,data.getExtras().getString(Constants.endDate));

                }
            }
        }
    }


    private void showSubCatEventRequest(int categoryId){
        myLoader.show("");
        JsonObject subObj = new JsonObject();
        subObj.addProperty(Constants.ApiKeyName.categoryId,categoryId);
        Call<JsonElement> subCallBack = APICall.getApiInterface().getSubCategoryByCatId(100,0,subObj);
        new APICall(HomeFilterActivity.this).apiCalling(subCallBack,this, APIs.SUB_CATEGORY_BY_CAT_ID);
    }

    private void launchOnTabClick(int position){

        switch (position){
            case 0:
                whichDate = "today";
                getDate(whichDate);
                firstTimeOpenScreen = true;
                break;
            case 1:
                whichDate = "tomorrow";
                getDate(whichDate);
                firstTimeOpenScreen = true;
                break;
            case 2:
                whichDate = "thisWeek";
                getDate(whichDate);
                firstTimeOpenScreen = true;
                break;
            case 3:
                if(firstTimeOpenScreen){
                    new Handler().postDelayed(()-> {
                        Intent calenderIntent = new Intent(HomeFilterActivity.this,CalenderActivity.class);
                        calenderIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(calenderIntent,9001);
                        firstTimeOpenScreen = false;
                    },400);
                }
                firstTimeOpenScreen = true;
                /*first time flag was needed because of auto call calender screen multiple time calling whenever this screen open first time open due to tab listener calls*/
                break;
        }
    }

    private void getDate(String whichDate){
        String selectedStartDate = "", selectedEndDate = "";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);

        switch (whichDate){
            case "today":
                Date today = calendar.getTime();
                Log.d("nlkfnaklnkfl", "getDate: "+formatter.format(today));
                selectedStartDate = formatter.format(today);
                selectedEndDate = "";
                SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.startDate,selectedStartDate);
                SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.endDate,selectedEndDate);
                break;

            case "tomorrow":
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                Date tomorrow = calendar.getTime();
                Log.d("nlkfnaklnkfl", "towww: "+formatter.format(tomorrow));
                selectedStartDate = formatter.format(tomorrow);
                selectedEndDate = "";
                SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.startDate,selectedStartDate);
                SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.endDate,selectedEndDate);
                break;

            case "thisWeek":
                Date  todayThisWeek= calendar.getTime();
                selectedStartDate = formatter.format(todayThisWeek);
                int getDayOfTheWeek = formatter.getCalendar().get(Calendar.DAY_OF_WEEK);
                calendar.add(Calendar.DAY_OF_WEEK,7);
                Date data1 = calendar.getTime();
                selectedEndDate = formatter.format(data1);
                SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.startDate,selectedStartDate);
                SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.endDate,selectedEndDate);
                break;
        }
    }
    private void filterEventWithAuthRequest(){
        if(getCategoryId == null){
            ShowToast.errorToast(HomeFilterActivity.this,getString(R.string.choose_category_first));
            return;
        }else if(getSubCatList.size() == 0){
            ShowToast.errorToast(HomeFilterActivity.this,getString(R.string.sorry_no_found_event_at_this_category));
            return;
        }

        //TODO managed with no_auth and auth
        myLoader.show("");
        JsonObject filterObj = new JsonObject();
        filterObj.addProperty(Constants.latitude,currentLatLng.latitude);
        filterObj.addProperty(Constants.longitude,currentLatLng.longitude);
        filterObj.addProperty(Constants.miles,String.valueOf(CommonUtils.getCommonUtilsInstance().getFilterDistance()));
        filterObj.addProperty(Constants.cost,String.valueOf(CommonUtils.getCommonUtilsInstance().getFilterAdmissionCost()));
        filterObj.addProperty(Constants.startDate,CommonUtils.getCommonUtilsInstance().getStartDate());
        filterObj.addProperty(Constants.endDate,CommonUtils.getCommonUtilsInstance().getEndDate());
        filterObj.addProperty(Constants.categoryId,String.valueOf(getCategoryId));
        if(subCatIdArray.size()>0)
        filterObj.add(Constants.subCategoryId,subCatIdArray);

        if (CommonUtils.getCommonUtilsInstance().isUserLogin()) {
            Call<JsonElement> homeFilterCallBack = APICall.getApiInterface().nearByWithAuthEvent(CommonUtils.getCommonUtilsInstance().getDeviceAuth(), filterObj);
            new APICall(HomeFilterActivity.this).apiCalling(homeFilterCallBack, this, APIs.NEAR_BY_AUTH_EVENT);
            return;
        }
        Call<JsonElement> homeFilterCallBack = APICall.getApiInterface().noAuthNearByEvent(filterObj);
        new APICall(HomeFilterActivity.this).apiCalling(homeFilterCallBack, this, APIs.NO_AUTH_NEAR_BY_EVENT);
    }

    private void navigateToHomeScreen(JSONObject responseObj,boolean isHttpRequestSuccess){
        ArrayList<EventList> eventLists = new ArrayList<>();
        NearByEventModal nearByEventModal = null;

        Intent homeIntent = new Intent(HomeFilterActivity.this,HomeActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeIntent.putExtra(Constants.activityName,getString(R.string.home_filter_activity));

        if(isHttpRequestSuccess){
            nearByEventModal = new Gson().fromJson(responseObj.toString(), NearByEventModal.class);
            homeIntent.putExtra(Constants.nearByData,nearByEventModal.getData().getEventList());

        }else {
            homeIntent.putExtra(Constants.nearByData,eventLists);
        }
        startActivity(homeIntent);
        finish();
    }
    private void getSelectedSubCatId(int id, boolean isRemove){
        for (int i = 0; i < getSubCatList.size(); i++) {
            if (getSubCatList.get(i).getId() == id && !isRemove) {
                subCatIdArray.add(id);
                return;
            } else if (subCatIdArray.size() > 0 && isRemove) {
                JsonParser jsonParser = new JsonParser();
                JsonElement jsonElement = jsonParser.parse(String.valueOf(id));
                subCatIdArray.remove(jsonElement);
            }
        }
    }

    private void getLocation(){
        String[] currentLocation = CommonUtils.getCommonUtilsInstance().getCurrentLocation().split(" ");
        currentLatLng = new LatLng(Double.parseDouble(currentLocation[0]),Double.parseDouble(currentLocation[1]));
        setLocation(currentLatLng.latitude,currentLatLng.longitude);
    }

    private void setLocation(double lat, double lng) {
        Log.d("fnaslfnklas", currentLatLng.latitude+" getLocation: "+currentLatLng.longitude);
        Geocoder geocoder = new Geocoder(HomeFilterActivity.this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(lat, lng, 1);
                String stateName = addresses.get(0).getAdminArea();
                String city = addresses.get(0).getLocality();

                filterBinding.tvSelectAdd.setText(city + " " + stateName);
                filterBinding.tvSelectAdd.setSelected(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void resetFilter(boolean isDefaultFilterSettings){
            /*here i saved as default value of distance, admission cost, event date index as well*/
            if(!isDefaultFilterSettings){
                CommonUtils.getCommonUtilsInstance().saveEventDate(2);
                CommonUtils.getCommonUtilsInstance().saveFilterDistance(500);
                CommonUtils.getCommonUtilsInstance().saveFilterAdmissionCost(4000);
            }
            filterBinding.tabLayout.getTabAt(CommonUtils.getCommonUtilsInstance().getEventDate()).select();
            filterBinding.seekBarDistance.setProgress(CommonUtils.getCommonUtilsInstance().getFilterDistance());
            filterBinding.seekBarAdmissionFee.setProgress(CommonUtils.getCommonUtilsInstance().getFilterAdmissionCost());
            filterBinding.tvShowDistance.setText(filterBinding.seekBarDistance.getProgress() + " Miles");
            filterBinding.tvShowRupee.setText("$"+filterBinding.seekBarAdmissionFee.getProgress());
            CommonUtils.getCommonUtilsInstance().validateSwipeMode(true);
            filterBinding.viewTabLayout.getTabAt(0).select();
        }

    @Override
    protected void onStart() {
        super.onStart();
        resetFilter(true);
    }

    public void resetFilterSettingsOnClick(View view) {
        resetFilter(false);
    }
}
