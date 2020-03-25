package com.ebabu.event365live.homedrawer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityNotificationBinding;
import com.ebabu.event365live.homedrawer.adapter.NotificationListAdapter;
import com.ebabu.event365live.homedrawer.modal.NotificationListModal;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.EndlessRecyclerViewScrollListener;
import com.ebabu.event365live.utils.MyLoader;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class NotificationActivity extends AppCompatActivity implements GetResponseData {
    MyLoader myLoader;
    private ActivityNotificationBinding notificationBinding;
    private NotificationListAdapter notificationListAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    private List<NotificationListModal.NotificationList> notificationLists;
    private List<NotificationListModal.NotificationList> getNotificationLists;
    private LinearLayoutManager manager;
    private int currentPage = 1;
    public static boolean isNotificationActivityLaunched =  true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationBinding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
        myLoader = new MyLoader(this);
        notificationLists = new ArrayList<>();
        manager = new LinearLayoutManager(this);
        notificationBinding.recyclerNotificationList.setLayoutManager(manager);
        notificationListAdapter = new NotificationListAdapter(notificationLists);
        notificationBinding.recyclerNotificationList.setAdapter(notificationListAdapter);

        showNotificationListRequest(currentPage);
    }

    private void setupNotificationList(List<NotificationListModal.NotificationList> lists) {

        notificationListAdapter.notifyDataSetChanged();
        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(manager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (lists.size() != 0) {
                    ++currentPage;
                    showNotificationListRequest(currentPage);
                }
            }
        };
        notificationBinding.recyclerNotificationList.addOnScrollListener(endlessRecyclerViewScrollListener);
    }

    public void closeOnClick(View view) {
        finish();
    }

    private void showNotificationListRequest(int currentPage) {
        myLoader.show("");
        Call<JsonElement> notificationListCall = APICall.getApiInterface().getNotificationList(CommonUtils.getCommonUtilsInstance().getDeviceAuth(), 25, currentPage);
        new APICall(this).apiCalling(notificationListCall, this, APIs.GET_ALL_NOTIFICATION_LIST);
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        NotificationListModal notificationListModal = new Gson().fromJson(responseObj.toString(), NotificationListModal.class);
        if (notificationListModal.getData().getNotificationList().size() == 0) {
            if (currentPage > 1) {
                setupNotificationList(notificationListModal.getData().getNotificationList());
                return;
            }
            notificationBinding.noNotificationCard.setVisibility(View.VISIBLE);
            notificationBinding.recyclerNotificationList.setVisibility(View.GONE);
        } else {
            getNotificationLists = prepareList(notificationListModal.getData().getNotificationList());
            notificationLists.addAll(getNotificationLists);
            setupNotificationList(notificationLists);
        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
    }

    private List<NotificationListModal.NotificationList> prepareList(List<NotificationListModal.NotificationList> notificationLists) {

        List<String> uniqueList = new ArrayList<>();

        for (int i = 0; i < notificationLists.size(); i++) {
            NotificationListModal.NotificationList list = notificationLists.get(i);
            if (!uniqueList.contains(list.getDateString())) {
                uniqueList.add(list.getDateString());
            }
        }
        List<NotificationListModal.NotificationList> expectedList = new ArrayList<>();

        for (String getDateOnly : uniqueList) {

            NotificationListModal.NotificationList mItemHead = new NotificationListModal.NotificationList();
            mItemHead.setHead(true);
            mItemHead.setDateString(getDateOnly);
            expectedList.add(mItemHead);

            for (int i = 0; i < notificationLists.size(); i++) {
                NotificationListModal.NotificationList mItem = notificationLists.get(i);
                if (getDateOnly.equals(mItem.getDateString())) {
                    expectedList.add(mItem);
                }
            }

        }

//        HashMap<String, NotificationListModal.NotificationList> dates = new HashMap<>();
//
//        for (NotificationListModal.NotificationList item : notificationLists) {
//            dates.put(item.getDateTime().split("T")[0],item);
//        }
//
//
//        Log.d("nflasknla", notificationLists.size()+" prepareList: "+dates.size());
//
//        List<NotificationListModal.NotificationList> expectedList = new ArrayList<>();
//
//
//        for (Map.Entry<String,NotificationListModal.NotificationList> item: dates.entrySet()) {
//
//            for (int i = 0; i < notificationLists.size(); i++){
//                NotificationListModal.NotificationList mItem = notificationLists.get(i);
//                if (item.getValue().getDateString().equals(mItem.getDateString())) {
//                    expectedList.add(mItem);
//                }
//            }
//
//            NotificationListModal.NotificationList mItemHead = new NotificationListModal.NotificationList();
//            mItemHead.setHead(true);
//            mItemHead.setDateString(item.getValue().getDateString());
//            mItemHead.setGetEventDate(item.getValue().getGetEventDate());
//            expectedList.add(mItemHead);
//        }
//
//        //Collections.sort(expectedList,new NotificationListModal.NotificationList());
//
//        for(NotificationListModal.NotificationList date: expectedList){
//            Log.d("fbnalksnbfklsa", "onSuccess: "+date.getDateString());
//        }
//
        //    Collections.reverse(expectedList);

        return expectedList;
    }
}
