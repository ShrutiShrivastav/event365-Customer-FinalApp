package com.ebabu.event365live.homedrawer.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

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

public class SearchHomeActivity extends AppCompatActivity implements GetResponseData{
    private ActivitySearchHomeBinding searchHomeBinding;
    private ExploreEventAdapter exploreEventAdapter;
    private GridItemDecorationManager gridItemDecorationManager;
    private MyLoader myLoader;
    private Handler handler;
    private Runnable updateRunnable;
    private String getSearchKeyword = "";
    private List<SearchModal> searchModalList;
    private boolean isEventSearchFromKey = false;
    private SearchEventModal searchEventModal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_home);
        myLoader = new MyLoader(this);
        searchModalList = new ArrayList<>();
        new Handler().postDelayed(()->{
            handleSearchEventRequest();

            if (!CommonUtils.getCommonUtilsInstance().isUserLogin()) {
                searchNoEventRequest("");
            } else {
                searchAuthRequest("");
            }


            topFiveEventRequest();
            setupExploreEvent();
        },400);
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
        onBackPressed(); }

    private void searchNoEventRequest(String searchedKeyword) {
        JsonObject searchKeywordObj = new JsonObject();
        searchKeywordObj.addProperty(Constants.ApiKeyName.keyword, searchedKeyword);
        Call<JsonElement> searchCallBack = APICall.getApiInterface().searchNoAuth(10,1,searchKeywordObj);
        new APICall(SearchHomeActivity.this).apiCalling(searchCallBack, this, APIs.SEARCH_NO_AUTH_API);
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        Log.d("fmnlaknfnkasfa", "onSuccess: "+responseObj);
        if (responseObj != null) {
            if(typeAPI.equalsIgnoreCase(APIs.GET_ALL_EVENT)){
                return;
            }
            searchEventModal = new Gson().fromJson(responseObj.toString(),SearchEventModal.class);

            if(searchEventModal.getData().getTopEvents() != null){


            }

            searchHomeBinding.noDataFoundContainer.setVisibility(View.VISIBLE);
            searchHomeBinding.recyclerContainer.setVisibility(View.GONE);
            Utility.hideKeyboardFrom(SearchHomeActivity.this,searchHomeBinding.noDataFoundContainer);
            isEventSearchFromKey = true;
        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        Log.d("fmnlaknfnkasfa", "onFailed: "+errorBody);
        myLoader.dismiss();
        if (errorBody != null) {
            ShowToast.errorToast(SearchHomeActivity.this, message);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    private void handleSearchEventRequest()
    {
        searchHomeBinding.etSearchEvent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 0 && isEventSearchFromKey)
                    topFiveEventRequest();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                handler.removeCallbacks(updateRunnable);
                if(editable.toString().length() != 0)
                {
                    getSearchKeyword  = editable.toString();
                    handler.postDelayed(updateRunnable,800);
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

    private void topFiveEventRequest()
    {   myLoader.show("");
        isEventSearchFromKey = false;






    }

    public void anyWhereOnClick(View view) {
        myLoader.show(getString(R.string.please_wait));
        Call<JsonElement> eventCall = APICall.getApiInterface().getAllEvent(CommonUtils.getCommonUtilsInstance().getDeviceAuth());
        new APICall(SearchHomeActivity.this).apiCalling(eventCall,this,APIs.GET_ALL_EVENT);
    }

    private void searchAuthRequest(String searchedKeyword) {
        JsonObject searchKeywordObj = new JsonObject();
        searchKeywordObj.addProperty(Constants.ApiKeyName.keyword, searchedKeyword);
        Call<JsonElement> searchCallBack = APICall.getApiInterface().searchAuth(10,1,searchKeywordObj);
        new APICall(SearchHomeActivity.this).apiCalling(searchCallBack, this, APIs.SEARCH_AUTH_API);
    }

//    private void setupSearchItem(){
//        exploreEventAdapter = new ExploreEventAdapter(this,searchEventModal.getData());
//        searchHomeBinding.recyclerContainer.setVisibility(View.VISIBLE);
//        searchHomeBinding.recyclerExploreEvent.setAdapter(exploreEventAdapter);
//        exploreEventAdapter.notifyDataSetChanged();
//    }


}
