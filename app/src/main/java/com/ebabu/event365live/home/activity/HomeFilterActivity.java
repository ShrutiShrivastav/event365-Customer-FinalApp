
package com.ebabu.event365live.home.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityHomeFilterBinding;
import com.ebabu.event365live.event.LoginEvent;
import com.ebabu.event365live.home.adapter.CategoryListAdapter;
import com.ebabu.event365live.home.modal.SubCategoryModal;
import com.ebabu.event365live.home.modal.GetCategoryModal;
import com.ebabu.event365live.home.modal.LoginViewModal;
import com.ebabu.event365live.home.modal.nearbymodal.EventList;
import com.ebabu.event365live.home.modal.nearbymodal.NearByEventModal;
import com.ebabu.event365live.homedrawer.modal.pastmodal.Data;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.oncelaunch.LandingActivity;
import com.ebabu.event365live.oncelaunch.modal.nearbynoauth.NearByNoAuthModal;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.SessionValidation;
import com.ebabu.event365live.utils.ShowToast;
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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.prolificinteractive.materialcalendarview.format.DayFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

public class HomeFilterActivity extends AppCompatActivity implements TabLayout.BaseOnTabSelectedListener, GetResponseData,CommonUtils.FusedCurrentLocationListener {

    private ActivityHomeFilterBinding filterBinding;
    private LoginViewModal loginViewModal;
    LoginEvent loginEvent;
    private boolean getIsUserLogin, getIsHomeSwipeView;
    private MyLoader myLoader;
    private List<SubCategoryModal.Event> getSubCatList = new ArrayList<>();
    private ChipGroup chipGroup;
    private CategoryListAdapter categoryListAdapter;
  //  ArrayAdapter<SubCategoryModal.CatRecommendedData> recommendedData;
    private PlacesClient placesClient;
    public static LatLng currentLatLng;
    public static Place place;
    private double currentLat, currentLng;
    private GetCategoryModal getCategoryModal;
    private int selectDistanceInMile = 10000; //this is default value of distance in miles
    private int selectedAmount = 4000; // this is default value of amount
    private String selectedStartDate = "",selectedEndDate="";
    private String whichDate = "today";
    private Integer getCategoryId;
    private boolean isSubCatSelected;
    private JSONArray subCatIdArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filterBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_filter);
        filterBinding.viewTabLayout.addOnTabSelectedListener(this);
        myLoader = new MyLoader(this);
        CommonUtils.getCommonUtilsInstance().getCurrentLocation(HomeFilterActivity.this);
        getDate(whichDate);

        placesClient = Places.createClient(this);

        Log.d("fnklanfkla", "inCreate: "+CommonUtils.getCommonUtilsInstance().isSwipeMode());

        if (CommonUtils.getCommonUtilsInstance().isSwipeMode()) {
            filterBinding.viewTabLayout.getTabAt(0).select();
          //  filterBinding.viewTabLayout.getTabAt(0).select();
        } else {
            filterBinding.viewTabLayout.getTabAt(1).select();
        }

//        if (!CommonUtils.getCommonUtilsInstance().isSwipeMode()) {
//            filterBinding.viewTabLayout.getTabAt(1).select();
//        }

        filterBinding.seekBarDistance.setMax(500);
        filterBinding.seekBarDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                selectDistanceInMile = progress;
                filterBinding.tvShowDistance.setText(String.valueOf(progress+" Miles"));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        filterBinding.seekBarAdmissionFee.setMax(1000);

        filterBinding.seekBarAdmissionFee.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                selectedAmount = progress;
                filterBinding.tvShowRupee.setText(String.valueOf("$"+progress));
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
                showSubCatEventRequest(getCategoryId);
                categoryListAdapter.setSelection(adapterView.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        filterBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                launchOnTabClick(tab.getPosition());
                Log.d("bjbjbjjb", "onTabSelected: "+tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                launchOnTabClick(tab.getPosition());

            }
        });

        loginEvent = new LoginEvent();
        categoryRequest();
    }
    private void showRecommendedCategory() {
        chipGroup = filterBinding.chipGroupShowEvent;
        for (SubCategoryModal.Event getCatData : getSubCatList) {
            Chip chip = new Chip(HomeFilterActivity.this);
            chip.setCheckable(true);
            chip.setCheckedIconVisible(true);
            chip.setClickable(true);
            chip.setGravity(Gravity.HORIZONTAL_GRAVITY_MASK);
            chip.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(HomeFilterActivity.this, R.color.blueColor)));
            chip.setChipStrokeWidth(2);
            chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(HomeFilterActivity.this, R.color.colorWhite)));
            chip.setTag(getCatData.getId());
            chip.setText(getCatData.getName());
            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    isSubCatSelected = isChecked;
                   if(isChecked){
                       v1((int) buttonView.getTag());

                   }

//                    Log.d("fnskflanflna", "onCheckedChanged: "+(int) buttonView.getTag()) ;

                }
            });
            chipGroup.addView(chip);
        }
    }

    public void backBtnOnClick(View view) {
        finish();
//        Intent homeIntent = new Intent(HomeFilterActivity.this, HomeActivity.class);
//        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(homeIntent);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        if (tab.getPosition() == 0) {
            CommonUtils.getCommonUtilsInstance().validateSwipeMode(true);
            Log.d("fnklanfkla", "onTabSelected: "+CommonUtils.getCommonUtilsInstance().isSwipeMode());
        } else if (tab.getPosition() == 1) {
            CommonUtils.getCommonUtilsInstance().validateSwipeMode(false);
            Log.d("fnklanfkla", "List: "+CommonUtils.getCommonUtilsInstance().isSwipeMode());
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            else if(typeAPI.equalsIgnoreCase(APIs.SUB_CATEGORY_BY_CAT_ID)){
                SubCategoryModal categoryModal = new Gson().fromJson(responseObj.toString(),SubCategoryModal.class);
                if (getSubCatList != null && getSubCatList.size() > 0) {
                    getSubCatList.clear();
                    chipGroup.removeAllViews();
                }

                if(categoryModal.getSubCategoryData().getEvents().size()> 0){
                    getSubCatList.addAll(categoryModal.getSubCategoryData().getEvents());
                    showRecommendedCategory();
                    return;
                }
                ShowToast.errorToast(HomeFilterActivity.this,getString(R.string.noCateFound));
                isSubCatSelected = false;
            }else if(typeAPI.equalsIgnoreCase(APIs.NEAR_BY_AUTH_EVENT) || typeAPI.equalsIgnoreCase(APIs.NO_AUTH_NEAR_BY_EVENT) ){
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
        JsonObject subCatObj = new JsonObject();
        subCatObj.addProperty(Constants.ApiKeyName.categoryId,categoryId);

        Call<JsonElement> subCatCallBack = APICall.getApiInterface().getSubCategoryNoAuth(subCatObj);
        new APICall(HomeFilterActivity.this).apiCalling(subCatCallBack, this, APIs.GET_SUB_CATEGORY_NO_AUTH);
    }
    public void showCatOnClick(View view) {
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
                Geocoder geocoder = new Geocoder(HomeFilterActivity.this, Locale.getDefault());
               // int getDistanceInMile = CalculationByDistance(new LatLng(startLat,startLng), new LatLng(currentLat,currentLng));
                try {
                    List<Address> addresses = geocoder.getFromLocation(currentLatLng.latitude, currentLatLng.longitude, 1);
                    String stateName = addresses.get(0).getAdminArea();
                    String cityName = addresses.get(0).getLocality();
                    filterBinding.tvSelectAdd.setText(place.getName() + ", " +cityName +", "+ stateName);
                    filterBinding.tvSelectAdd.setSelected(true);

                }catch (IOException e) {
                    e.printStackTrace();
                }

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
                    selectedStartDate = data.getExtras().getString(Constants.startDate);
                    selectedEndDate = data.getExtras().getString(Constants.endDate);
                    Log.d("lfnlasnflkasnfl", selectedStartDate+" calendar tomorrow: "+selectedEndDate);
                }
            }
        }
    }

    public int CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("#.#");
        float kmInDec = Float.valueOf(newFormat.format(km));
//        double meter = valueResult % 1000;
//        int meterInDec = Integer.valueOf(newFormat.format(meter));

//        DecimalFormat decimalFormat = new DecimalFormat("#.#");
//        String g= decimalFormat.format(convertKmsToMiles(kmInDec));
//
//
//        int d =  Math.round(Integer.parseInt(g));


        Log.i("Radius_Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   "+ " == "+kmInDec);

        //return Radius * c;
        return Math.round(kmInDec);
    }

    public double convertKmsToMiles(float kms){
        return 0.621371 * kms;
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
                break;
            case 1:
                whichDate = "tomorrow";
                getDate(whichDate);
                break;
            case 2:
                whichDate = "thisWeek";
                getDate(whichDate);
                break;
            case 3:
                Intent calenderIntent = new Intent(HomeFilterActivity.this,CalenderActivity.class);
                calenderIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(calenderIntent,9001);
                break;
        }
    }

    private void getDate(String whichDate){

        Calendar calendar = Calendar.getInstance();
       // SimpleDateFormat df = new SimpleDateFormat("yyyy/mm/dd",Locale.ENGLISH);

       // String date = df.format(calendar.getTime());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);


        Date date1 = new Date();


        switch (whichDate){
            case "today":
                Date today = calendar.getTime();
                Log.d("nlkfnaklnkfl", "getDate: "+formatter.format(today));

                break;

            case "tomorrow":
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                Date tomorrow = calendar.getTime();
                Log.d("nlkfnaklnkfl", "towww: "+formatter.format(tomorrow));
                break;

            case "thisWeek":
                int getStartWeekDay = calendar.get(Calendar.DAY_OF_WEEK);
                int weekDayInNo = 7;
                int actualRemainingWeekDay = weekDayInNo - getStartWeekDay;;

                calendar.add(Calendar.DAY_OF_WEEK,actualRemainingWeekDay == 0 ? 1 : actualRemainingWeekDay);
                Date  date2= calendar.getTime();


                calendar.add(Calendar.DAY_OF_WEEK,6);
                Date data1 = calendar.getTime();

                Log.d("lfnlasnflkasnfl", formatter.format(date2)+" end week day: "+ formatter.format(data1));
               // selectedStartDate = df.format(calendar);
                break;
        }
    }
    private void filterEventWithAuthRequest(){
        if(currentLatLng == null){
            ShowToast.errorToast(HomeFilterActivity.this,getString(R.string.failed_to_get_location));
            return;
        }else if(getCategoryId == null){
            ShowToast.errorToast(HomeFilterActivity.this,getString(R.string.choose_category_first));
            return;
        }else if(!isSubCatSelected){
            ShowToast.errorToast(HomeFilterActivity.this,getString(R.string.please_choose_any_looking_into));
            return;
        }
        //TODO managed with no_auth and auth
        myLoader.show("");
        JsonObject filterObj = new JsonObject();
        filterObj.addProperty(Constants.latitude,currentLatLng.latitude);
        filterObj.addProperty(Constants.longitude,currentLatLng.longitude);
        filterObj.addProperty(Constants.miles,String.valueOf(selectDistanceInMile));
        filterObj.addProperty(Constants.cost,String.valueOf(selectedAmount));
        filterObj.addProperty(Constants.startDate,String.valueOf(selectedStartDate));
        filterObj.addProperty(Constants.endDate,String.valueOf(selectedEndDate));
        filterObj.addProperty(Constants.categoryId,String.valueOf(getCategoryId));

        Log.d("nflkanfklan", currentLatLng.latitude+" filterEventWithAuthRequest: "+currentLatLng.longitude);

        if(CommonUtils.getCommonUtilsInstance().isUserLogin()){
            Call<JsonElement> homeFilterCallBack = APICall.getApiInterface().nearByWithAuthEvent(CommonUtils.getCommonUtilsInstance().getDeviceAuth(),filterObj);
            new APICall(HomeFilterActivity.this).apiCalling(homeFilterCallBack,this,APIs.NEAR_BY_AUTH_EVENT);
            return;
        }
        Call<JsonElement> homeFilterCallBack = APICall.getApiInterface().noAuthNearByEvent(filterObj);
        new APICall(HomeFilterActivity.this).apiCalling(homeFilterCallBack,this,APIs.NO_AUTH_NEAR_BY_EVENT);
    }

    @Override
    public void getFusedCurrentSuccess(Location location) {
        if(location != null){
            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            try {
                Geocoder geocoder = new Geocoder(HomeFilterActivity.this, Locale.getDefault());
               List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null) {
                    String stateName = addresses.get(0).getAdminArea();
                    String cityName = addresses.get(0).getLocality();
                    filterBinding.tvSelectAdd.setText(cityName +", "+ stateName);
                    filterBinding.tvSelectAdd.setSelected(true);


                }
            } catch (IOException e) {
                e.printStackTrace();
                ShowToast.errorToast(HomeFilterActivity.this, getString(R.string.something_wrong_to_get_location));
            }
        }
    }
    @Override
    public void getFusedCurrentFailed(Exception e) {
        ShowToast.errorToast(HomeFilterActivity.this,getString(R.string.something_wrong_to_get_location));
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
            Log.d("fnalksnfskla", "isHttpRequestSuccess: "+nearByEventModal.getData().getEventList().size());

        }else {
            homeIntent.putExtra(Constants.nearByData,eventLists);
        }
        startActivity(homeIntent);
        finish();

    }
    private void v1(int id){
        subCatIdArray = new JSONArray();
        JSONObject subSatIdJsonObj = new JSONObject();
        for (int i = 0; i < getSubCatList.size(); i++) {
            if(getSubCatList.get(i).getId() == id){
                 subCatIdArray.put(id);
            }
           // int id = Integer.parseInt(eventChooseAdapter.getCurrentSelectedItem().get(i).getId());

        }

        try {
            subSatIdJsonObj.put("subCategoryId", subCatIdArray);
            Log.d("nfnlanflknalnfklan", "getSelectedCatIdArray: "+subSatIdJsonObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
