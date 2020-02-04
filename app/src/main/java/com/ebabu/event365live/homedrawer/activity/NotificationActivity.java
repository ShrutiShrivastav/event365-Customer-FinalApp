package com.ebabu.event365live.homedrawer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.ebabu.event365live.utils.MyLoader;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;

public class NotificationActivity extends AppCompatActivity implements GetResponseData {
    MyLoader myLoader;
    private ActivityNotificationBinding notificationBinding;
    private NotificationListAdapter notificationListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationBinding = DataBindingUtil.setContentView(this,R.layout.activity_notification);
        myLoader = new MyLoader(this);
        showNotificationListRequest();

    }
    private void setupNotificationList(List<NotificationListModal.NotificationList> lists){
        notificationListAdapter = new NotificationListAdapter(lists);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        notificationBinding.recyclerNotificationList.setLayoutManager(manager);
        notificationBinding.recyclerNotificationList.setAdapter(notificationListAdapter);
    }

    public void closeOnClick(View view) {
        finish();
    }

    private void showNotificationListRequest(){
        myLoader.show("");
        Call<JsonElement> notificationListCall = APICall.getApiInterface().getNotificationList(CommonUtils.getCommonUtilsInstance().getDeviceAuth(),10,1);
        new APICall(this).apiCalling(notificationListCall,this, APIs.GET_ALL_NOTIFICATION_LIST);
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        NotificationListModal notificationListModal = new Gson().fromJson(responseObj.toString(), NotificationListModal.class);
        setupNotificationList(notificationListModal.getData().getNotificationList());
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
    }

    private List<GetRsvpUserModal.RSPVList> prepareList(List<GetRsvpUserModal.RSPVList> rsvpHeaderModals){
        HashSet<UniqueDateModal> dates = new HashSet<>();
        for (GetRsvpUserModal.RSPVList item : rsvpHeaderModals) {
            UniqueDateModal uniqueDateModal = new UniqueDateModal();
            uniqueDateModal.setCompareDate(item.getDateTime().split("T")[0]);
            uniqueDateModal.setShowDate(item.getDateTime());
            dates.add(uniqueDateModal);
        }
        List<GetRsvpUserModal.RSPVList> expectedList = new ArrayList<>();

        for (UniqueDateModal date: dates){
            GetRsvpUserModal.RSPVList mItemHead = new GetRsvpUserModal.RSPVList();
            mItemHead.setHead(true);
            mItemHead.setDateString(date.getCompareDate());
            mItemHead.setGetEventDate(date.getShowDate());
            expectedList.add(mItemHead);
            Log.d("fnaklsnflsa", "prepareList: "+mItemHead.isHead());
            for (int i = 0; i < rsvpHeaderModals.size(); i++){
                GetRsvpUserModal.RSPVList mItem = rsvpHeaderModals.get(i);
                if (date.getCompareDate().equalsIgnoreCase(mItem.getDateString())) {
                    Log.d("fnaklsnfsssslsa", "prepareList: "+i);
                    expectedList.add(mItem);
                }
            }
        }

        Log.d("fnaklsnfsssslsssssa", "prepareList: "+expectedList.size());
        return expectedList;
    }
}
