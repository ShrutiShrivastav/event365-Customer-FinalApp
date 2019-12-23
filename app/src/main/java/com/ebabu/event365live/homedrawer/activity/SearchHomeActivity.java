package com.ebabu.event365live.homedrawer.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.ebabu.event365live.oncelaunch.adapter.EventLandingCatAdapter;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.ebabu.event365live.utils.Utility;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class SearchHomeActivity extends AppCompatActivity implements GetResponseData {
    private ActivitySearchHomeBinding searchHomeBinding;
    private ExploreEventAdapter exploreEventAdapter;
    private GridItemDecorationManager gridItemDecorationManager;
    private MyLoader myLoader;
    private Handler handler;
    private Runnable updateRunnable;
    private String getSearchKeyword = "";
    private boolean isEventSearchFromKey = false;
    private SearchEventModal searchEventModal;
    private List<SearchEventModal.TopEvent> topEventList;
    private List<SearchEventModal.SearchDatum> searchDataList;
    private List<SearchEventModal.RecentSearch> recentSearchList;
    private boolean isSearchedEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_home);
        myLoader = new MyLoader(this);
        new Handler().postDelayed(() -> {
            handleSearchEventRequest();

            if (!CommonUtils.getCommonUtilsInstance().isUserLogin()) {
                searchNoEventRequest("");
                searchHomeBinding.recentSearchContainer.setVisibility(View.GONE);
            } else {
                searchAuthRequest("");
                searchHomeBinding.recentSearchContainer.setVisibility(View.VISIBLE);
            }
            topFiveEventRequest();
            setupExploreEvent();
        }, 400);
    }

    private void setupExploreEvent() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        int gridItemMargin = getResources().getDimensionPixelOffset(R.dimen._14sdp);
        gridItemDecorationManager = new GridItemDecorationManager(2, gridItemMargin, true);
        searchHomeBinding.recyclerExploreEvent.addItemDecoration(gridItemDecorationManager);
        searchHomeBinding.recyclerExploreEvent.setLayoutManager(gridLayoutManager);
    }

    public void backBtnOnClick(View view) {
        finish();
        onBackPressed();
    }

    private void searchNoEventRequest(String searchedKeyword) {
        myLoader.show("");
        JsonObject searchKeywordObj = new JsonObject();
        searchKeywordObj.addProperty(Constants.ApiKeyName.keyword, searchedKeyword);
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
            searchDataList = searchEventModal.getData().getSearchData();
            recentSearchList = searchEventModal.getData().getRecentSearch();


            if (CommonUtils.getCommonUtilsInstance().isUserLogin() && recentSearchList.size() > 0) {
                setupRecentSearchList();
            }


            if(searchDataList.size() >0){
                isEventSearchFromKey = true;
                CommonUtils.getCommonUtilsInstance().showSnackBar(SearchHomeActivity.this,searchHomeBinding.searchRootContainer,searchDataList.size()+" Events Found");
            }
            else {
                isEventSearchFromKey = false;
            }


            if (topEventList.size() > 0 || searchDataList.size() > 0) {
                setupSearchItem();
                return;
            }

            showNoDataFoundView(message);
        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        Log.d("fmnlaknfnkasfa", "onFailed: " + errorBody);
        myLoader.dismiss();
        if (errorBody != null) {
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
                if (charSequence.length() == 0 && isEventSearchFromKey) {
                    if (!CommonUtils.getCommonUtilsInstance().isUserLogin()) {
                        searchNoEventRequest("");
                    } else {
                        searchAuthRequest("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                handler.removeCallbacks(updateRunnable);
                if (editable.toString().length() != 0) {
                    getSearchKeyword = editable.toString();
                    handler.postDelayed(updateRunnable, 600);
                }
            }
        });

        handler = new Handler();
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                searchNoEventRequest(getSearchKeyword);
            }
        };

    }

    private void topFiveEventRequest() {
        myLoader.show("");
        isEventSearchFromKey = false;
    }

    public void anyWhereOnClick(View view) {
        myLoader.show(getString(R.string.please_wait));
        Call<JsonElement> eventCall = APICall.getApiInterface().getAllEvent(CommonUtils.getCommonUtilsInstance().getDeviceAuth());
        new APICall(SearchHomeActivity.this).apiCalling(eventCall, this, APIs.GET_ALL_EVENT);
    }

    private void searchAuthRequest(String searchedKeyword) {
        JsonObject searchKeywordObj = new JsonObject();
        searchKeywordObj.addProperty(Constants.ApiKeyName.keyword, searchedKeyword);
        Call<JsonElement> searchCallBack = APICall.getApiInterface().searchAuth(CommonUtils.getCommonUtilsInstance().getDeviceAuth(),10, 1, searchKeywordObj);
        new APICall(SearchHomeActivity.this).apiCalling(searchCallBack, this, APIs.SEARCH_AUTH_API);
    }

    private void setupSearchItem() {
        searchHomeBinding.noDataFoundContainer.setVisibility(View.GONE);
        searchHomeBinding.recyclerContainer.setVisibility(View.VISIBLE);
        exploreEventAdapter = new ExploreEventAdapter(topEventList, searchDataList, isSearchedEvent);
        searchHomeBinding.recyclerExploreEvent.setAdapter(exploreEventAdapter);
        exploreEventAdapter.notifyDataSetChanged();
    }

    private void showNoDataFoundView(String message) {
        searchHomeBinding.noDataFoundContainer.setVisibility(View.VISIBLE);
        ((TextView) searchHomeBinding.noDataFoundContainer.findViewById(R.id.tvShowNoDataFound)).setText(message);
        ((TextView) searchHomeBinding.noDataFoundContainer.findViewById(R.id.tvShowNoDataFound)).setTextColor(Color.WHITE);
        searchHomeBinding.recyclerContainer.setVisibility(View.GONE);
        Utility.hideKeyboardFrom(SearchHomeActivity.this, searchHomeBinding.noDataFoundContainer);
        isEventSearchFromKey = true;
    }

    private void setupRecentSearchList() {
        EventLandingCatAdapter landingAdapter = new EventLandingCatAdapter(null, recentSearchList, true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        searchHomeBinding.recyclerRecentEvent.setLayoutManager(linearLayoutManager);
        searchHomeBinding.recyclerRecentEvent.setAdapter(landingAdapter);

    }


}
