package com.ebabu.event365live.homedrawer.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityWishListBinding;
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

public class WishListActivity extends AppCompatActivity implements GetResponseData {
    private ActivityWishListBinding wishListBinding;
    private MyLoader myLoader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wishListBinding = DataBindingUtil.setContentView(this, R.layout.activity_wish_list);
        myLoader = new MyLoader(this);
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

//            Log.d("nbfbasjfbjsafjask", upcomingAttendModal.getUpcomingAttendData().getAttendentEvent().size()+" onSuccess: "+upcomingAttendModal.getUpcomingAttendData().getUpcomingEvent().size());
            wishListBinding.rsvpViewpager.setAdapter(new MyWishListAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,upcomingAttendModal));
            wishListBinding.tlWishList.setupWithViewPager(wishListBinding.rsvpViewpager);
        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        ShowToast.errorToast(WishListActivity.this,message);
        if(errorBody != null){

        }
    }

    private void requestingEventListOfUpcomingAttend(){
        myLoader.show("");
        Call<JsonElement> eventCallBack = APICall.getApiInterface().getEventListOfUpcomingAttend(CommonUtils.getCommonUtilsInstance().getDeviceAuth());
        new APICall(WishListActivity.this).apiCalling(eventCallBack,this, APIs.GET_EVENT_LIST_OF_UPCOMING_ATTEND);
    }
}
