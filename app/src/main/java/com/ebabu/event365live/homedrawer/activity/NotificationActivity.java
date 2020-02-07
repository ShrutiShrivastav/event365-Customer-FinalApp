package com.ebabu.event365live.homedrawer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityNotificationBinding;
import com.ebabu.event365live.home.modal.rsvp.GetRsvpUserModal;
import com.ebabu.event365live.homedrawer.adapter.NotificationListAdapter;
import com.ebabu.event365live.homedrawer.modal.NotificationListModal;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.userinfo.modal.UniqueDateModal;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.EndlessRecyclerViewScrollListener;
import com.ebabu.event365live.utils.MyLoader;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class NotificationActivity extends AppCompatActivity implements GetResponseData {
    MyLoader myLoader;
    private ActivityNotificationBinding notificationBinding;
    private NotificationListAdapter notificationListAdapter;
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationBinding = DataBindingUtil.setContentView(this,R.layout.activity_notification);
        myLoader = new MyLoader(this);
        showNotificationListRequest(currentPage);

    }
    private void setupNotificationList(List<NotificationListModal.NotificationList> lists){
        notificationListAdapter = new NotificationListAdapter(lists);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        notificationBinding.recyclerNotificationList.setLayoutManager(manager);
        notificationBinding.recyclerNotificationList.setAdapter(notificationListAdapter);

        EndlessRecyclerViewScrollListener viewScrollListener = new EndlessRecyclerViewScrollListener(manager){
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                Log.d("nkasfa", totalItemsCount+" onLoadMore: "+page);
//                if(page>currentPage){
//                    currentPage++;
//                    showNotificationListRequest(currentPage);
//                }
            }


        };
        notificationBinding.recyclerNotificationList.addOnScrollListener(viewScrollListener);
    }

    public void closeOnClick(View view) {
        finish();
    }

    private void showNotificationListRequest(int currentPage){
        myLoader.show("");
        Call<JsonElement> notificationListCall = APICall.getApiInterface().getNotificationList(CommonUtils.getCommonUtilsInstance().getDeviceAuth(),10,currentPage);
        new APICall(this).apiCalling(notificationListCall,this, APIs.GET_ALL_NOTIFICATION_LIST);
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        NotificationListModal notificationListModal = new Gson().fromJson(responseObj.toString(), NotificationListModal.class);

        setupNotificationList(prepareList(notificationListModal.getData().getNotificationList()));
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
    }

    private List<NotificationListModal.NotificationList> prepareList(List<NotificationListModal.NotificationList> notificationLists){
        HashMap<String, NotificationListModal.NotificationList> dates = new HashMap<>();
        for (NotificationListModal.NotificationList item : notificationLists) {
            dates.put(item.getDateTime().split("T")[0],item);
        }

        List<NotificationListModal.NotificationList> expectedList = new ArrayList<>();
        for (Map.Entry<String,NotificationListModal.NotificationList> item: dates.entrySet()) {
            NotificationListModal.NotificationList mItemHead = new NotificationListModal.NotificationList();
            mItemHead.setHead(true);
            mItemHead.setDateString(item.getValue().getDateString());
            mItemHead.setGetEventDate(item.getValue().getGetEventDate());
            expectedList.add(mItemHead);

            for (int i = 0; i < notificationLists.size(); i++){
                NotificationListModal.NotificationList mItem = notificationLists.get(i);
                if (item.getValue().getDateString().equals(mItem.getDateString())) {
                    expectedList.add(mItem);
                }
            }

        }

        return expectedList;
    }



}
