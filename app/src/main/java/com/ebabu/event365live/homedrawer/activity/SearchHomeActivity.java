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
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ebabu.event365live.BaseActivity;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivitySearchHomeBinding;
import com.ebabu.event365live.homedrawer.adapter.SearchEventAdapter;
import com.ebabu.event365live.homedrawer.adapter.TopFiveEventsAdapter;
import com.ebabu.event365live.homedrawer.modal.searchevent.SearchEventModal;
import com.ebabu.event365live.homedrawer.utils.GridItemDecorationManager;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.RecyclerPagination;
import com.ebabu.event365live.utils.Utility;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

public class SearchHomeActivity extends BaseActivity implements GetResponseData {
    private ActivitySearchHomeBinding searchHomeBinding;
    private SearchEventAdapter searchEventAdapter;
    private GridItemDecorationManager gridItemDecorationManager;
    private Handler handler;
    private Runnable updateRunnable;
    private String getSearchKeyword = "";
    private SearchEventModal searchEventModal;
    private List<SearchEventModal.TopEvent> topEventList = new ArrayList<>();
    private List<SearchEventModal.SearchData> searchDataList = new ArrayList<>();
    private List<SearchEventModal.RecentSearch> recentSearchList = new ArrayList<>();
    private boolean isSearchedEvent;
    private LatLng currentLatLng;
    private String selectedCityName = "";
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    List<String> recentAllList;
    private ArrayAdapter<String> recentArrayAdapter;
    private boolean showOneTimeGridView = false;
    private int currentPage = 1;
    private TopFiveEventsAdapter topFiveEventsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_home);
        recentAllList = new ArrayList<>();

        new Handler().postDelayed(() -> {
            handleSearchEventRequest();

            if (!CommonUtils.getCommonUtilsInstance().isUserLogin()) {
                searchNoEventRequest(getSearchKeyword, selectedCityName, currentPage);
                searchHomeBinding.recentSearchContainer.setVisibility(View.GONE);
            } else {
                searchAuthRequest(getSearchKeyword, selectedCityName, currentPage);
                searchHomeBinding.recentSearchContainer.setVisibility(View.VISIBLE);
            }
            showTopFiveEvents();
        }, 400);
    }

    private void showTopFiveEvents() {
        gridLayoutManager = new GridLayoutManager(this, 2);
        int gridItemMargin = getResources().getDimensionPixelOffset(R.dimen._14sdp);
        gridItemDecorationManager = new GridItemDecorationManager(2, gridItemMargin, true);
        linearLayoutManager = new LinearLayoutManager(this);

    }

    public void backBtnOnClick(View view) {
        finish();
        onBackPressed();
    }

    private void searchNoEventRequest(String searchedKeyword, String city, int currentPage) {
        myLoader.show("");
        JsonObject searchKeywordObj = new JsonObject();
        searchKeywordObj.addProperty(Constants.ApiKeyName.keyword, searchedKeyword);
        searchKeywordObj.addProperty(Constants.ApiKeyName.city, city);
        Call<JsonElement> searchCallBack = APICall.getApiInterface().searchNoAuth(50, currentPage, searchKeywordObj);
        new APICall(SearchHomeActivity.this).apiCalling(searchCallBack, this, APIs.SEARCH_NO_AUTH_API);
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        Log.d("fmnlaknfnkasfa", "onSuccess: " + responseObj);
        if (responseObj != null) {

            if (typeAPI.equalsIgnoreCase(APIs.GET_USER_TICKET_BOOKED)) {
                return;
            }
            searchEventModal = new Gson().fromJson(responseObj.toString(), SearchEventModal.class);
            topEventList = searchEventModal.getData().getTopEvents();
            searchDataList = searchEventModal.getData().getData();
            if(recentAllList.isEmpty())
            recentSearchList = searchEventModal.getData().getRecentSearch();


            if (isSearchedEvent && searchDataList.size() > 0) {
                setupSearchItem();
                CommonUtils.getCommonUtilsInstance().showSnackBar(SearchHomeActivity.this, searchHomeBinding.searchRootContainer, searchDataList.size() + " Events Found");
                CommonUtils.hideKeyboard(SearchHomeActivity.this, searchHomeBinding.etSearchEvent);
                isSearchedEvent = false;
            } else {
                if (isSearchedEvent) {
                    showNoDataFoundView("Events not found");
                } else if (topEventList.size() > 0) {
                    setupTopFiveEventsItem();
                }
            }

        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        Log.d("fmnlaknfnkasfa", "onFailed: " + errorBody);
        myLoader.dismiss();
        if (errorBody != null && errorCode == APIs.OTHER_FAILED) {
            showNoDataFoundView("No Events Found.");

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

                if (charSequence.length() != 0) {
                    searchHomeBinding.crossContainer.setVisibility(View.VISIBLE);
                } else {
                    handler.removeCallbacks(updateRunnable);
                    searchHomeBinding.crossContainer.setVisibility(View.INVISIBLE);
                    isSearchedEvent = false;
                    CommonUtils.hideSoftKeyboard(SearchHomeActivity.this);
                    if (topEventList.size() > 0) {
                        setupTopFiveEventsItem();
                    } else {
                        showNoDataFoundView("Events not found.");
                    }
                }

                if (charSequence.length() == 0)

                    Log.d("fnlasknf", "onTextChanged: " + charSequence.length());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() != 0) {
                    handler.removeCallbacks(updateRunnable);
                    getSearchKeyword = editable.toString();
                    handler.postDelayed(updateRunnable, 600);
                }
                Log.d("fnlasknf", "afterTextChanged: " + editable.length());
            }
        });

        handler = new Handler();
        updateRunnable = () -> {
            isSearchedEvent = true;
            if (!CommonUtils.getCommonUtilsInstance().isUserLogin()) {
                searchNoEventRequest(getSearchKeyword, selectedCityName, currentPage);
            } else {
                searchAuthRequest(getSearchKeyword, selectedCityName, currentPage);
            }
        };

    }


    public void anyWhereOnClick(View view) {
        CommonUtils.getCommonUtilsInstance().launchSelectAddressFrag(SearchHomeActivity.this, null, false);
        // new PaymentMethodsActivityStarter(this).startForResult();
    }

    private void searchAuthRequest(String searchedKeyword, String city, int currentPage) {
        myLoader.show("");
        JsonObject searchKeywordObj = new JsonObject();
        searchKeywordObj.addProperty(Constants.ApiKeyName.keyword, searchedKeyword);
        searchKeywordObj.addProperty(Constants.ApiKeyName.city, city);
        Call<JsonElement> searchCallBack = APICall.getApiInterface().searchAuth(CommonUtils.getCommonUtilsInstance().getDeviceAuth(), 50, currentPage, searchKeywordObj);
        new APICall(SearchHomeActivity.this).apiCalling(searchCallBack, this, APIs.SEARCH_AUTH_API);
    }

    private void setupSearchItem() {
        searchHomeBinding.recentSearchContainer.setVisibility(View.GONE);
        searchHomeBinding.ivRecentTitle.setVisibility(View.GONE);
        searchHomeBinding.ivShowExploreEvents.setVisibility(View.GONE);
        searchHomeBinding.recyclerExploreEvent.setVisibility(View.VISIBLE);
        searchHomeBinding.recyclerExploreEvent.setLayoutManager(linearLayoutManager);
        searchEventAdapter = new SearchEventAdapter(searchDataList);
        searchHomeBinding.recyclerExploreEvent.setAdapter(searchEventAdapter);
        searchHomeBinding.noDataFoundContainer.setVisibility(View.GONE);
        searchHomeBinding.recyclerContainer.setVisibility(View.VISIBLE);
        searchEventAdapter.notifyDataSetChanged();


        searchHomeBinding.recyclerExploreEvent.addOnScrollListener(new RecyclerPagination(linearLayoutManager) {
            @Override
            protected void loadMore() {
//                if(exploreEventAdapter !=null){
//                    exploreEventAdapter.isLoading(true);
//                    currentPage ++;
//
//                    if (!CommonUtils.getCommonUtilsInstance().isUserLogin()) {
//                        searchNoEventRequest(getSearchKeyword,selectedCityName,currentPage);
//                        searchHomeBinding.recentSearchContainer.setVisibility(View.GONE);
//                    } else {
//                        searchAuthRequest(getSearchKeyword,selectedCityName,currentPage);
//                        searchHomeBinding.recentSearchContainer.setVisibility(View.VISIBLE);
//                    }
//                }
            }
        });
    }

    private void setupTopFiveEventsItem() {

        if (CommonUtils.getCommonUtilsInstance().isUserLogin() && recentSearchList.size() > 0) {
            if (recentAllList.size() == 0){
                for (SearchEventModal.RecentSearch recentSearch : recentSearchList) {
                    if (recentSearch.getText() != null)
                        recentAllList.add(recentSearch.getText());
                }
            }
            setupRecentSearchList();
        }
        searchHomeBinding.ivShowExploreEvents.setVisibility(View.VISIBLE);
        searchHomeBinding.recyclerExploreEvent.setLayoutManager(gridLayoutManager);
        topFiveEventsAdapter = new TopFiveEventsAdapter(topEventList);
        searchHomeBinding.recyclerExploreEvent.setAdapter(topFiveEventsAdapter);
        searchHomeBinding.noDataFoundContainer.setVisibility(View.GONE);
        searchHomeBinding.recyclerContainer.setVisibility(View.VISIBLE);
        topFiveEventsAdapter.notifyDataSetChanged();
    }


    private void showNoDataFoundView(String message) {
        searchHomeBinding.noDataFoundContainer.setVisibility(View.VISIBLE);
        ((TextView) searchHomeBinding.noDataFoundContainer.findViewById(R.id.tvShowNoDataFound)).setText(message);
        ((TextView) searchHomeBinding.noDataFoundContainer.findViewById(R.id.tvShowNoDataFound)).setTextColor(Color.WHITE);
        searchHomeBinding.recyclerContainer.setVisibility(View.GONE);
        Utility.hideKeyboardFrom(SearchHomeActivity.this);
    }

    private void setupRecentSearchList() {
        searchHomeBinding.recentSearchContainer.setVisibility(View.VISIBLE);
        searchHomeBinding.ivRecentTitle.setVisibility(View.VISIBLE);
        recentArrayAdapter = new ArrayAdapter<>(SearchHomeActivity.this, R.layout.recent_layout, recentAllList);
        searchHomeBinding.recentShowList.setAdapter(recentArrayAdapter);
        searchHomeBinding.recentShowList.setOnItemClickListener((parent, view, position, id) -> {

            searchHomeBinding.etSearchEvent.setText((String) parent.getItemAtPosition(position));
            searchHomeBinding.etSearchEvent.setSelection(((String) parent.getItemAtPosition(position)).length());
        });

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
                    selectedCityName = addresses.get(0).getLocality();
                    searchHomeBinding.tvAnyWhere.setText(selectedCityName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                CommonUtils.hideSoftKeyboard(SearchHomeActivity.this);
            }
        }
    }

    private void notifyAdapter(boolean isLoading) {
        if (searchEventAdapter != null)
            searchEventAdapter.isLoading(isLoading);
    }

    private void removeAllViewFromRecycler() {
        searchHomeBinding.recyclerExploreEvent.removeAllViewsInLayout();
    }


}
