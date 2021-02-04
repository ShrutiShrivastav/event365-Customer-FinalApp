
package com.ebabu.event365live.home.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.ebabu.event365live.BaseActivity;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityHomeFilterBinding;
import com.ebabu.event365live.home.adapter.CategoryListAdapter;
import com.ebabu.event365live.home.modal.AllSubCategoryModal;
import com.ebabu.event365live.home.modal.GetCategoryModal;
import com.ebabu.event365live.home.modal.nearbymodal.EventList;
import com.ebabu.event365live.home.modal.nearbymodal.NearByEventModal;
import com.ebabu.event365live.homedrawer.modal.bubblecategory.EventSubCategoryData;
import com.ebabu.event365live.homedrawer.modal.bubblecategory.EventSubCategoryModal;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.SessionValidation;
import com.ebabu.event365live.utils.ShowToast;
import com.ebabu.event365live.utils.Utility;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

public class HomeFilterActivity extends BaseActivity implements TabLayout.BaseOnTabSelectedListener, GetResponseData {

    public static LatLng currentLatLng;
    public static Place place;
    private static List<EventSubCategoryData> getSubCatList = new ArrayList<>();
    private static List<AllSubCategoryModal.AllSubCategoryModalData> allSubCategoryModals = new ArrayList<>();
    private static GetCategoryModal getCategoryModal;
    private static int persistSelectedCatPosition = 0;
    private static int persistSelectedCategoryId = -1;
    private static List<Integer> persistChipIdsList;
    private static boolean flagForShowAllEvent;
    private ActivityHomeFilterBinding filterBinding;
    private ChipGroup chipGroup;
    private CategoryListAdapter categoryListAdapter;
    private PlacesClient placesClient;
    private String whichDate = "thisWeek";
    private Integer getCategoryId = -1;
    private boolean isSubCatSelected;
    private JsonArray subCatIdArray;
    private boolean isSwipeMode;
    private boolean firstTimeOpenScreen;
    private int getCategorySelectedPos = -1;
    private int currentCategoryIdSelected;
    private int maxPrice = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filterBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_filter);
        filterBinding.viewTabLayout.addOnTabSelectedListener(this);
        chipGroup = filterBinding.chipGroupShowEvent;
        persistChipIdsList = new ArrayList<>();

        if (SessionValidation.getPrefsHelper().getPref(Constants.distance) != null) {
            filterBinding.tvShowDistance.setText(SessionValidation.getPrefsHelper().getPref(Constants.distance) + " Miles");
            filterBinding.seekBarDistance.setProgress(SessionValidation.getPrefsHelper().getPref(Constants.distance));
        }

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
                if (progress != 0){
                    filterBinding.tvShowRupee.setText("$" + progress);
                    CommonUtils.getCommonUtilsInstance().saveFilterAdmissionCost(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
                if (firstTimeOpenScreen) {
                    launchOnTabClick(tab.getPosition());
                }
                firstTimeOpenScreen = true;
            }
        });


        init();
        categoryRequest();

        filterBinding.spinnerShowCatRecommended.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.d("fnalksfla", flagForShowAllEvent + " onItemSelected: " + persistSelectedCatPosition);
                if (flagForShowAllEvent || persistSelectedCatPosition != -1) {
                    if (persistSelectedCatPosition != -1) {
                        getCategorySelectedPos = persistSelectedCatPosition;
                        getCategoryId = persistSelectedCategoryId;
                        persistSelectedCatPosition = -1;
                        persistSelectedCategoryId = -1;
                    } else {
                        getCategorySelectedPos = parent.getSelectedItemPosition();
                        getCategoryId = getCategoryModal.getData().getCategory().get(getCategorySelectedPos).getId();
                    }
                    getCategoryId = getCategoryModal.getData().getCategory().get(getCategorySelectedPos).getId();
                    filterBinding.tvShowSpinnerItem.setText(getCategoryModal.getData().getCategory().get(getCategorySelectedPos).getCategoryName());
                    subCategoryRequest(getCategoryId);
                    categoryListAdapter.setSelection(getCategorySelectedPos);
                }
                flagForShowAllEvent = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("fnklasnkfla", "onNothingSelected: ");
            }
        });

    }

    private void showRecommendedCategory() {
        if (allSubCategoryModals != null && allSubCategoryModals.size() > 0) {
            allSubCategoryModals.clear();
            chipGroup.removeAllViews();
        }

        int firstId = getSubCatList.get(0).getId();

        for (EventSubCategoryData getCatData : getSubCatList) {

            boolean isChipCheck = false;
            if (getCatData.getId() == firstId) {
                getSelectedSubCatId(getCatData.getId(), false);
                isChipCheck = true;
            }

            Chip chip = new Chip(HomeFilterActivity.this);
            chip.setCheckable(true);
            chip.setCheckedIconVisible(true);
            chip.setClickable(true);
            chip.setGravity(Gravity.HORIZONTAL_GRAVITY_MASK);
            chip.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(HomeFilterActivity.this, R.color.blueColor)));
            chip.setChipStrokeWidth(2);
            chip.setCheckedIcon(getResources().getDrawable(R.drawable.tick));
            chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(HomeFilterActivity.this, R.color.colorWhite)));
            chip.setTag(getCatData.getId());
            chip.setText(getCatData.getSubCategoryName());

            chip.setChecked(isChipCheck);

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                isSubCatSelected = isChecked;

                // Change by Lokesh Panachal on 25-01-2021

                if (isChecked && getCategoryId == getCatData.getCategoryId()) {

                    getSelectedSubCatId((int) buttonView.getTag(), false);
//                    persistChipIdsList.add(getCatData.getId());

                } else if (!isChecked && getCategoryId == getCatData.getCategoryId()) {
                    getSelectedSubCatId((int) buttonView.getTag(), true);
//                    persistChipIdsList.remove(getCatData.getId());
                }
            });
            chipGroup.addView(chip);
        }
    }

    private void showAllSubCategory() {
        if (getSubCatList != null && getSubCatList.size() > 0) {
            getSubCatList.clear();
            chipGroup.removeAllViews();
        }

        //chipGroup.removeAllViews();
        for (AllSubCategoryModal.AllSubCategoryModalData getSubCat : allSubCategoryModals) {
            Chip chip = new Chip(HomeFilterActivity.this);

            //chip.setCheckable(true);
            //   chip.setSelected(true);
            //chip.setSelectAllOnFocus(true);
            // chip.setCheckedIconVisible(true);
            //chip.setClickable(false);

            chip.setGravity(Gravity.HORIZONTAL_GRAVITY_MASK);
            chip.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(HomeFilterActivity.this, R.color.blueColor)));
            chip.setChipStrokeWidth(2);
            chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(HomeFilterActivity.this, R.color.colorWhite)));
            chip.setTag(getSubCat.getId());
            chip.setText(getSubCat.getSubCategoryName());
            chip.setChipIcon(getResources().getDrawable(R.drawable.tick));

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                isSubCatSelected = isChecked;
//                if (isChecked) {
//                    getSelectedSubCatId((int) buttonView.getTag(), false);
//                } else {
//                    getSelectedSubCatId((int) buttonView.getTag(), true);
//                }
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

        if (responseObj != null) {

            if (typeAPI.equalsIgnoreCase(APIs.GET_CATEGORY)) {
                myLoader.dismiss();
                getCategoryModal = new Gson().fromJson(responseObj.toString(), GetCategoryModal.class);
                try {
                    if (getCategoryModal.getData().getMaxPrice() != null) {
                        maxPrice = getCategoryModal.getData().getMaxPrice().getMax();
                        filterBinding.tvShowFinalFee.setText("$" + maxPrice);
                        filterBinding.seekBarAdmissionFee.setMax(maxPrice);
//                        filterBinding.seekBarAdmissionFee.setProgress(maxPrice);
                        if (CommonUtils.getCommonUtilsInstance().getFilterAdmissionCost() > 0) {
                            filterBinding.tvShowRupee.setText("$" + SessionValidation.getPrefsHelper().getPref(Constants.admission_cost));
                            filterBinding.seekBarAdmissionFee.setProgress(SessionValidation.getPrefsHelper().getPref(Constants.admission_cost));
                        } else {
                            filterBinding.tvShowRupee.setText(String.valueOf(maxPrice));
                            filterBinding.seekBarAdmissionFee.setProgress(maxPrice);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (getCategoryModal.getData().getCategory().size() > 1) {
                    categoryListAdapter = new CategoryListAdapter(HomeFilterActivity.this, getCategoryModal.getData().getCategory());
                    filterBinding.spinnerShowCatRecommended.setAdapter(categoryListAdapter);
                    categoryListAdapter.notifyDataSetChanged();
                    return;
                }


                ShowToast.errorToast(HomeFilterActivity.this, getString(R.string.no_cate_data_found));
            } else if (typeAPI.equalsIgnoreCase(APIs.GET_ALL_SUB_CATEGORY)) {
                myLoader.dismiss();
                AllSubCategoryModal allSubCategoryModal = new Gson().fromJson(responseObj.toString(), AllSubCategoryModal.class);
                if (allSubCategoryModal.getData().size() > 0) {
                    allSubCategoryModals.addAll(allSubCategoryModal.getData());
                    showAllSubCategory();
                }
            } else if (typeAPI.equalsIgnoreCase(APIs.GET_SUB_CATEGORY_NO_AUTH)) {
                myLoader.dismiss();
                EventSubCategoryModal eventSubCategoryModal = new Gson().fromJson(responseObj.toString(), EventSubCategoryModal.class);

                if (getSubCatList != null && getSubCatList.size() > 0) {
                    getSubCatList.clear();
                    chipGroup.removeAllViews();
                }

                Log.d(">>>>>>>", eventSubCategoryModal.getEventSubCatData().size() + " GET_SUB_CATEGORY_NO_AUTH: ");
                if (eventSubCategoryModal.getEventSubCatData().size() > 0) {
                    getSubCatList.addAll(eventSubCategoryModal.getEventSubCatData());

                    showRecommendedCategory();
                    return;
                }
                //  ShowToast.errorToast(HomeFilterActivity.this, getString(R.string.noCateFound));
                isSubCatSelected = false;
            } else if (typeAPI.equalsIgnoreCase(APIs.NEAR_BY_AUTH_EVENT) || typeAPI.equalsIgnoreCase(APIs.NO_AUTH_NEAR_BY_EVENT)) {
                myLoader.dismiss();
                Log.d("fnaslkfnklas", "HomeFilter: " + responseObj.toString());
                navigateToHomeScreen(responseObj, true);
            }
        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        if (errorCode == 406) {
            /* Need to show not data available at home page at 406 shows not event available at this location */
            navigateToHomeScreen(null, false);
        }
    }

    private void subCategoryRequest(Integer categoryId) {
        myLoader.show("");
        JsonArray jsonElements = new JsonArray();
        JsonObject subCatObj = new JsonObject();
        jsonElements.add(categoryId);
        subCatObj.add(Constants.ApiKeyName.categoryId, jsonElements);

        Call<JsonElement> subCatCallBack = APICall.getApiInterface().getSubCategoryNoAuth(subCatObj);
        new APICall(HomeFilterActivity.this).apiCalling(subCatCallBack, this, APIs.GET_SUB_CATEGORY_NO_AUTH);
    }

    private void categoryRequest() {

        myLoader.show("");
        Call<JsonElement> categoryCallBack = APICall.getApiInterface().getCategory();
        new APICall(HomeFilterActivity.this).apiCalling(categoryCallBack, this, APIs.GET_CATEGORY);
    }

    public void selectAddressOnClick(View view) {
        CommonUtils.getCommonUtilsInstance().launchSelectAddressFrag(HomeFilterActivity.this, null, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = Autocomplete.getPlaceFromIntent(data);
                currentLatLng = place.getLatLng();
                CommonUtils.getCommonUtilsInstance().saveCurrentLocation(currentLatLng.latitude, currentLatLng.longitude);
                setLocation(currentLatLng.latitude, currentLatLng.longitude);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == 9001) {
            if (resultCode == Activity.RESULT_OK) {
            }
        }
    }


    private void showSubCatEventRequest(int categoryId) {
        myLoader.show("");
        JsonObject subObj = new JsonObject();
        subObj.addProperty(Constants.ApiKeyName.categoryId, categoryId);
        Call<JsonElement> subCallBack = APICall.getApiInterface().getSubCategoryByCatId(100, 0, subObj);
        new APICall(HomeFilterActivity.this).apiCalling(subCallBack, this, APIs.SUB_CATEGORY_BY_CAT_ID);
    }

    private void launchOnTabClick(int position) {

        switch (position) {
            case 0:
                whichDate = "today";
                getDate(whichDate);
                firstTimeOpenScreen = true;
                filterBinding.llSelectedDate.setVisibility(View.GONE);
                break;
            case 1:
                whichDate = "tomorrow";
                getDate(whichDate);
                firstTimeOpenScreen = true;
                filterBinding.llSelectedDate.setVisibility(View.GONE);
                break;  
            case 2:
                whichDate = "thisWeek";
                getDate(whichDate);
                firstTimeOpenScreen = true;
                filterBinding.llSelectedDate.setVisibility(View.GONE);
                break;
            case 3:
                if (firstTimeOpenScreen) {
                    filterBinding.llSelectedDate.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(() -> {
                        Intent calenderIntent = new Intent(HomeFilterActivity.this, CalenderActivity.class);
                        calenderIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(calenderIntent, 9001);
                        firstTimeOpenScreen = false;
                    }, 400);
                }
                firstTimeOpenScreen = true;
                /*first time flag was needed because of auto call calender screen multiple time calling whenever this screen open first time open due to tab listener calls*/
                break;
        }
    }

    private void getDate(String whichDate) {
        String selectedStartDate = "", selectedEndDate = "";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        switch (whichDate) {
            case "today":
                Date today = calendar.getTime();
                selectedEndDate = "";
                Utility.startDate = Utility.localToUTC(today);
                Utility.endDate = selectedEndDate;
                Log.d("nlkfnaklnkfl", Utility.startDate + "today: " + Utility.endDate + " -- -" + today);

                break;

            case "tomorrow":
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                Date tomorrow = calendar.getTime();
                selectedEndDate = "";

                Utility.startDate = Utility.localToUTC(tomorrow);
                Utility.endDate = selectedEndDate;
                Log.d("nlkfnaklnkfl", tomorrow + " tomorrow: " + selectedEndDate);
                Log.d("nlkfnaklnkfl", Utility.startDate + "tomorrow: " + Utility.endDate);
                break;

            case "thisWeek":
                Utility.getThisWeekDate();
                break;
        }
    }

    private void filterEventWithAuthRequest() {
        if (!filterBinding.tvShowSpinnerItem.getText().toString().equals("Category")) {
            if (getCategoryId == null) {
                ShowToast.errorToast(HomeFilterActivity.this, getString(R.string.choose_category_first));
                return;
            }
//            else if (getSubCatList.size() == 0 && allSubCategoryModals.size() == 0) {
//                ShowToast.errorToast(HomeFilterActivity.this, getString(R.string.sorry_no_found_event_at_this_category));
//                return;
//            }
        } else flagForShowAllEvent = false;

        Log.d("fnsanfla", "filterEventWithAuthRequest: " + flagForShowAllEvent);

        /* here flagForShowAllEvent is true, should not give startDate,endDate,categoryId, for get all event at this location other wise give false*/
        myLoader.show("");
        JsonObject filterObj = new JsonObject();
        filterObj.addProperty(Constants.latitude, currentLatLng.latitude);
        filterObj.addProperty(Constants.longitude, currentLatLng.longitude);
        filterObj.addProperty(Constants.miles, String.valueOf(CommonUtils.getCommonUtilsInstance().getFilterDistance()));
        filterObj.addProperty(Constants.cost, String.valueOf(CommonUtils.getCommonUtilsInstance().getFilterAdmissionCost()));
        filterObj.addProperty(Constants.startDate, Utility.startDate);
        filterObj.addProperty(Constants.endDate, Utility.endDate);
        filterObj.addProperty(Constants.categoryId, flagForShowAllEvent ? String.valueOf(getCategoryId) : "");

//        filterObj.addProperty(Constants.startDate, flagForShowAllEvent ? Utility.startDate : "");
//        filterObj.addProperty(Constants.endDate, flagForShowAllEvent ? Utility.endDate : "");

//        filterObj.addProperty(Constants.startDate, flagForShowAllEvent ? CommonUtils.getCommonUtilsInstance().getStartDate() : "");
//        filterObj.addProperty(Constants.endDate, flagForShowAllEvent ? CommonUtils.getCommonUtilsInstance().getEndDate() : "");

        if (subCatIdArray.size() > 0)
            filterObj.add(Constants.subCategoryId, getSelectedChip());


        if (CommonUtils.getCommonUtilsInstance().isUserLogin()) {
            Call<JsonElement> homeFilterCallBack = APICall.getApiInterface().nearByWithAuthEvent(CommonUtils.getCommonUtilsInstance().getDeviceAuth(), filterObj);
            new APICall(HomeFilterActivity.this).apiCalling(homeFilterCallBack, this, APIs.NEAR_BY_AUTH_EVENT);
            return;
        }
        Call<JsonElement> homeFilterCallBack = APICall.getApiInterface().noAuthNearByEvent(filterObj);
        new APICall(HomeFilterActivity.this).apiCalling(homeFilterCallBack, this, APIs.NO_AUTH_NEAR_BY_EVENT);
    }

    private void navigateToHomeScreen(JSONObject responseObj, boolean isHttpRequestSuccess) {
        ArrayList<EventList> eventLists = new ArrayList<>();
        NearByEventModal nearByEventModal = null;

        Intent homeIntent = new Intent(HomeFilterActivity.this, HomeActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeIntent.putExtra(Constants.activityName, getString(R.string.home_filter_activity));

        if (isHttpRequestSuccess) {
            nearByEventModal = new Gson().fromJson(responseObj.toString(), NearByEventModal.class);
            homeIntent.putExtra(Constants.nearByData, nearByEventModal.getData().getEventList());

        } else {
            homeIntent.putExtra(Constants.nearByData, eventLists);
        }


        startActivity(homeIntent);
        finish();
    }

    private void getSelectedSubCatId(int id, boolean isRemove) {

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

    private void getLocation() {
        String[] currentLocation = CommonUtils.getCommonUtilsInstance().getCurrentLocation().split(" ");
        Log.d("nalksnflksa", "getLocation: " + currentLocation);
        currentLatLng = new LatLng(Double.parseDouble(currentLocation[0]), Double.parseDouble(currentLocation[1]));
        setLocation(currentLatLng.latitude, currentLatLng.longitude);
    }

    private void setLocation(double lat, double lng) {
        Log.d("fnaslfnklas", currentLatLng.latitude + " getLocation: " + currentLatLng.longitude);
        Geocoder geocoder = new Geocoder(HomeFilterActivity.this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            String fullAdd = addresses.get(0).getAddressLine(0);
            String stateName = addresses.get(0).getAdminArea();
            String city = addresses.get(0).getLocality();

            //filterBinding.tvSelectAdd.setText(city + " " + stateName);
            filterBinding.tvSelectAdd.setText(fullAdd);
            filterBinding.tvSelectAdd.setSelected(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetFilter() {
        /*here i saved as default value of distance, admission cost, event date index as well*/

        CommonUtils.getCommonUtilsInstance().saveEventDate(2);
        CommonUtils.getCommonUtilsInstance().saveFilterDistance(500);
        CommonUtils.getCommonUtilsInstance().saveFilterAdmissionCost(maxPrice);
        filterBinding.tabLayout.getTabAt(CommonUtils.getCommonUtilsInstance().getEventDate()).select();
        filterBinding.seekBarDistance.setProgress(CommonUtils.getCommonUtilsInstance().getFilterDistance());
        filterBinding.seekBarAdmissionFee.setProgress(CommonUtils.getCommonUtilsInstance().getFilterAdmissionCost());
        filterBinding.tvShowDistance.setText(filterBinding.seekBarDistance.getProgress() + " Miles");
        filterBinding.tvShowRupee.setText("$" + filterBinding.seekBarAdmissionFee.getProgress());
        filterBinding.tvShowSpinnerItem.setText("Category");
        persistSelectedCatPosition = -1;
        persistSelectedCategoryId = -1;
        getCategoryId = -1;
        getCategorySelectedPos = -1;

        flagForShowAllEvent = false;
        if (getSubCatList != null && getSubCatList.size() > 0) {
            getSubCatList.clear();
            chipGroup.removeAllViews();
        }

        // Change by Lokesh Panchal at 25-01-2021
        if (persistChipIdsList != null && persistChipIdsList.size() > 0) {
            getSubCatList.clear();
        }

        if (CommonUtils.getCommonUtilsInstance().isSwipeMode()) {
            filterBinding.viewTabLayout.getTabAt(0).select();
        } else {
            filterBinding.viewTabLayout.getTabAt(1).select();
        }
    }

    public void resetFilterSettingsOnClick(View view) {
        CommonUtils.getCommonUtilsInstance().validateSwipeMode(true);
        resetFilter();
    }

    private void init() {
        getDate(whichDate);
        subCatIdArray = new JsonArray();
        placesClient = Places.createClient(this);
        getLocation();
        filterBinding.tabLayout.getTabAt(CommonUtils.getCommonUtilsInstance().getEventDate()).select();

        if (CommonUtils.getCommonUtilsInstance().isSwipeMode()) {
            filterBinding.viewTabLayout.getTabAt(0).select();
        } else {
            filterBinding.viewTabLayout.getTabAt(1).select();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {

            String selectedCalenderDate = SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.showSelectedCurrentCalenderDate);
            String selectedDate = SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.startDate);
            String selectedEndDate = SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.endDate);

            if (CommonUtils.getCommonUtilsInstance().getEventDate() == 3) {
                filterBinding.llSelectedDate.setVisibility(View.VISIBLE);
                filterBinding.tvSelectedDate.setText(selectedCalenderDate);

                if (selectedDate != null && !selectedDate.equals("")) {
                    filterBinding.tvSelectedDate.setText(selectedDate + " - " + selectedEndDate);
                }
            } else {
                filterBinding.llSelectedDate.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        persistSelectedCatPosition = getCategorySelectedPos;
        persistSelectedCategoryId = getCategoryId;
        if (filterBinding.tvShowSpinnerItem.getText().toString().equals("Category"))
            flagForShowAllEvent = false;
        super.onDestroy();
    }

    private JsonArray getSelectedChip() {
        for (int j = 0; j < chipGroup.getChildCount(); j++) {
            Chip childAt = (Chip) chipGroup.getChildAt(j);
            if (childAt.isChecked()) {

                JsonParser jsonParser = new JsonParser();
                JsonElement jsonElement = jsonParser.parse(String.valueOf((int) childAt.getTag()));

                if (!subCatIdArray.contains(jsonElement)) {
                    subCatIdArray.add((int) childAt.getTag());
                }
                //persistChipIdsList.add((int) childAt.getTag());
            }
        }

        Log.v("Krishn ", " subCatIdArray " + subCatIdArray.toString());

        return subCatIdArray;
    }


    public void spinnerOpenOnClick(View view) {
        filterBinding.spinnerShowCatRecommended.performClick();
    }
}
