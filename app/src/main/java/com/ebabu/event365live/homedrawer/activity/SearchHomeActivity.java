package com.ebabu.event365live.homedrawer.activity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivitySearchHomeBinding;
import com.ebabu.event365live.homedrawer.adapter.ExploreEventAdapter;
import com.ebabu.event365live.homedrawer.modal.SearchModal;
import com.ebabu.event365live.homedrawer.modal.searchevent.SearchEventModal;
import com.ebabu.event365live.homedrawer.utils.GridItemDecorationManager;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.oncelaunch.LandingActivity;
import com.ebabu.event365live.oncelaunch.adapter.EventLandingCatAdapter;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.ebabu.event365live.utils.Utility;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.stripe.android.view.PaymentMethodsActivity;
import com.stripe.android.view.PaymentMethodsActivityStarter;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

public class SearchHomeActivity extends AppCompatActivity implements GetResponseData {
    private ActivitySearchHomeBinding searchHomeBinding;
    private ExploreEventAdapter exploreEventAdapter;
    private GridItemDecorationManager gridItemDecorationManager;
    private MyLoader myLoader;
    private Handler handler;
    private Runnable updateRunnable;
    private String getSearchKeyword = "";
    private SearchEventModal searchEventModal;
    private List<SearchEventModal.TopEvent> topEventList;
    private List<SearchEventModal.SearchData> searchDataList;
    private List<SearchEventModal.RecentSearch> recentSearchList;
    private boolean isSearchedEvent;
    private LatLng currentLatLng;
    private String selectedCityName = "";
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    List<String> recentAllList;
    private ArrayAdapter<String> recentArrayAdapter;
    private boolean showOneTimeGridView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_home);
        myLoader = new MyLoader(this);
        recentAllList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(this);

        new Handler().postDelayed(() -> {
            handleSearchEventRequest();

            if (!CommonUtils.getCommonUtilsInstance().isUserLogin()) {
                searchNoEventRequest("","");
                searchHomeBinding.recentSearchContainer.setVisibility(View.GONE);
            } else {
                searchAuthRequest("","");
                searchHomeBinding.recentSearchContainer.setVisibility(View.VISIBLE);
            }
            topFiveEventRequest();
            setupExploreEvent();
        }, 400);
    }

    private void setupExploreEvent() {
        gridLayoutManager = new GridLayoutManager(this, 2);
        int gridItemMargin = getResources().getDimensionPixelOffset(R.dimen._14sdp);
        gridItemDecorationManager = new GridItemDecorationManager(2, gridItemMargin, true);
        showOneTimeGridView = true;
    }

    public void backBtnOnClick(View view) {
        finish();
        onBackPressed();
    }

    private void searchNoEventRequest(String searchedKeyword,String city) {
        myLoader.show("");
        JsonObject searchKeywordObj = new JsonObject();
        searchKeywordObj.addProperty(Constants.ApiKeyName.keyword, searchedKeyword);
        searchKeywordObj.addProperty(Constants.ApiKeyName.city, city);
        Call<JsonElement> searchCallBack = APICall.getApiInterface().searchNoAuth(10, 1, searchKeywordObj);
        new APICall(SearchHomeActivity.this).apiCalling(searchCallBack, this, APIs.SEARCH_NO_AUTH_API);
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        Log.d("fmnlaknfnkasfa", "onSuccess: " + responseObj);
        if (responseObj != null) {

            if (typeAPI.equalsIgnoreCase(APIs.GET_ALL_EVENT)) {
                return;
            }
            searchEventModal = new Gson().fromJson(responseObj.toString(), SearchEventModal.class);
            topEventList = searchEventModal.getData().getTopEvents();
            searchDataList = searchEventModal.getData().getData();
            recentSearchList = searchEventModal.getData().getRecentSearch();

            if (CommonUtils.getCommonUtilsInstance().isUserLogin() && recentSearchList.size() > 0) {
                if(recentAllList.size()>0)
                    recentAllList.clear();
                for(SearchEventModal.RecentSearch recentSearch: recentSearchList){
                    if(recentSearch.getText() != null)
                        recentAllList.add(recentSearch.getText());
                }
                setupRecentSearchList();
            }

            if(isSearchedEvent){
                if(searchDataList.size() >0){
                    setupSearchItem();
                    CommonUtils.getCommonUtilsInstance().showSnackBar(SearchHomeActivity.this,searchHomeBinding.searchRootContainer,searchDataList.size()+" Events Found");
                    CommonUtils.hideKeyboard(SearchHomeActivity.this,searchHomeBinding.etSearchEvent);
                }
                else {
                    searchHomeBinding.recyclerExploreEvent.removeItemDecoration(gridItemDecorationManager);
                    showOneTimeGridView = true;
                    isSearchedEvent = false;
                    showNoDataFoundView(message);
                    CommonUtils.hideKeyboard(SearchHomeActivity.this,searchHomeBinding.etSearchEvent);
                }
            }else{
                if(showOneTimeGridView)
                    searchHomeBinding.recyclerExploreEvent.addItemDecoration(gridItemDecorationManager);
                if(topEventList.size()>0){
                    setupSearchItem();
                }else {
                    showNoDataFoundView(message);
                }
            }
        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        Log.d("fmnlaknfnkasfa", "onFailed: " + errorBody);
        myLoader.dismiss();
        if (errorBody != null && errorCode == APIs.OTHER_FAILED) {
            //ShowToast.infoToast(SearchHomeActivity.this, message);
            showNoDataFoundView(message);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    private void handleSearchEventRequest() {
        searchHomeBinding.etSearchEvent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() !=0) {
                    searchHomeBinding.crossContainer.setVisibility(View.VISIBLE);
                }else {
                    searchHomeBinding.crossContainer.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() != 0) {
                    isSearchedEvent = true;
                    handler.removeCallbacks(updateRunnable);
                    getSearchKeyword = editable.toString();
                    handler.postDelayed(updateRunnable, 600);
                }else {
                    //showOneTimeGridView = false;
                    isSearchedEvent = false;
                    if (!CommonUtils.getCommonUtilsInstance().isUserLogin()) {
                        searchNoEventRequest(getSearchKeyword,selectedCityName);
                    } else {
                        searchAuthRequest(getSearchKeyword,selectedCityName);
                    }
                }
            }
        });

        handler = new Handler();
        updateRunnable =  () ->{
            if (!CommonUtils.getCommonUtilsInstance().isUserLogin()) {
                searchNoEventRequest(getSearchKeyword,selectedCityName);
            } else {
                searchAuthRequest(getSearchKeyword,selectedCityName);
            }
        };

    }

    private void topFiveEventRequest() {
        myLoader.show("");
        isSearchedEvent = false;
    }

    public void anyWhereOnClick(View view) {
        CommonUtils.getCommonUtilsInstance().launchSelectAddressFrag(SearchHomeActivity.this,null,false);
        // new PaymentMethodsActivityStarter(this).startForResult();
    }

    private void searchAuthRequest(String searchedKeyword, String city) {
        JsonObject searchKeywordObj = new JsonObject();
        searchKeywordObj.addProperty(Constants.ApiKeyName.keyword, searchedKeyword);
        searchKeywordObj.addProperty(Constants.ApiKeyName.city, city);
        Call<JsonElement> searchCallBack = APICall.getApiInterface().searchAuth(CommonUtils.getCommonUtilsInstance().getDeviceAuth(),10, 1, searchKeywordObj);
        new APICall(SearchHomeActivity.this).apiCalling(searchCallBack, this, APIs.SEARCH_AUTH_API);
    }

    private void setupSearchItem() {
        if(isSearchedEvent){
            searchHomeBinding.recyclerExploreEvent.removeItemDecoration(gridItemDecorationManager);
        }else {
            //searchHomeBinding.recyclerExploreEvent.addItemDecoration(gridItemDecorationManager);
        }
        searchHomeBinding.recyclerExploreEvent.setLayoutManager(isSearchedEvent ? linearLayoutManager : gridLayoutManager);
        searchHomeBinding.noDataFoundContainer.setVisibility(View.GONE);
        searchHomeBinding.recyclerContainer.setVisibility(View.VISIBLE);
        exploreEventAdapter = new ExploreEventAdapter(topEventList, searchDataList, isSearchedEvent);
        Log.d("fnalfnklas", "setupSearchItem: "+topEventList.size());
        searchHomeBinding.recyclerExploreEvent.setAdapter(exploreEventAdapter);
        exploreEventAdapter.notifyDataSetChanged();
    }

    private void showNoDataFoundView(String message) {
        searchHomeBinding.noDataFoundContainer.setVisibility(View.VISIBLE);
        ((TextView) searchHomeBinding.noDataFoundContainer.findViewById(R.id.tvShowNoDataFound)).setText(message);
        ((TextView) searchHomeBinding.noDataFoundContainer.findViewById(R.id.tvShowNoDataFound)).setTextColor(Color.WHITE);
        searchHomeBinding.recyclerContainer.setVisibility(View.GONE);
        Utility.hideKeyboardFrom(SearchHomeActivity.this, searchHomeBinding.noDataFoundContainer);
    }

    private void setupRecentSearchList() {
        Log.d("anflknsal", "setupRecentSearchList: "+recentAllList.size());
        recentArrayAdapter = new ArrayAdapter<>(SearchHomeActivity.this,android.R.layout.simple_list_item_1,recentAllList);

        searchHomeBinding.recentShowList.setAdapter(recentArrayAdapter);

        searchHomeBinding.recentShowList.setOnItemClickListener((parent, view, position, id) -> {

            searchHomeBinding.etSearchEvent.setText((String)parent.getItemAtPosition(position));
            searchHomeBinding.etSearchEvent.setSelection(((String)parent.getItemAtPosition(position)).length());
        });




       /* EventLandingCatAdapter landingAdapter = new EventLandingCatAdapter(null, recentSearchList, true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        searchHomeBinding.recyclerRecentEvent.setLayoutManager(linearLayoutManager);
        searchHomeBinding.recyclerRecentEvent.setAdapter(landingAdapter);

        landingAdapter.getSearchKeywordListener(new EventLandingCatAdapter.SearchKeyWorkListener() {
            @Override
            public void searchKeyword(String keyword) {
                searchHomeBinding.etSearchEvent.setText(keyword);
                searchHomeBinding.etSearchEvent.setSelection(keyword.length());
            }
        });*/

    }

    public void clearSearchKeywordOnClick(View view) {
        searchHomeBinding.etSearchEvent.setText("");
        searchHomeBinding.crossContainer.setVisibility(View.INVISIBLE);


        //else
        //searchHomeBinding.recyclerExploreEvent.removeItemDecoration(gridItemDecorationManager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                currentLatLng = place.getLatLng();
                Geocoder geocoder = new Geocoder(SearchHomeActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(currentLatLng.latitude, currentLatLng.longitude, 1);
                    String cityName = addresses.get(0).getLocality();

                    if (!CommonUtils.getCommonUtilsInstance().isUserLogin()) {
                        searchNoEventRequest("",cityName);
                    } else {
                        searchAuthRequest("",cityName);

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
