package com.ebabu.event365live.homedrawer.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ebabu.event365live.BaseActivity;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityBookedEventsBinding;
import com.ebabu.event365live.homedrawer.adapter.MyWishListAdapter;
import com.ebabu.event365live.homedrawer.modal.upcoming.UpcomingAttendModal;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import retrofit2.Call;

public class BookedEventsActivity extends BaseActivity implements GetResponseData {
    private ActivityBookedEventsBinding bookedEventsBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookedEventsBinding = DataBindingUtil.setContentView(this, R.layout.activity_booked_events);
        requestingEventListOfUpcomingAttend();
        //setupWishList();
    }

    public void backBtnOnClick(View view) {
        finish();
    }
    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        if(responseObj != null){

            UpcomingAttendModal upcomingAttendModal = new Gson().fromJson(responseObj.toString(), UpcomingAttendModal.class);

            bookedEventsBinding.rsvpViewpager.setAdapter(new MyWishListAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,upcomingAttendModal));
            bookedEventsBinding.tlWishList.setupWithViewPager(bookedEventsBinding.rsvpViewpager);
        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        ShowToast.errorToast(BookedEventsActivity.this,message);
        if(errorBody != null){

        }
    }

    private void requestingEventListOfUpcomingAttend(){
        myLoader.show("");
        Call<JsonElement> eventCallBack = APICall.getApiInterface().getEventListOfUpcomingAttend(CommonUtils.getCommonUtilsInstance().getDeviceAuth());
        new APICall(BookedEventsActivity.this).apiCalling(eventCallBack,this, APIs.GET_EVENT_LIST_OF_UPCOMING_ATTEND);
    }
}
