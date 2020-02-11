package com.ebabu.event365live.userinfo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.ebabu.event365live.R;

import com.ebabu.event365live.databinding.ActivitySeeMoreReviewBinding;
import com.ebabu.event365live.home.adapter.EventListAdapter;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.userinfo.adapter.ReviewsAdapter;
import com.ebabu.event365live.userinfo.modal.eventdetailsmodal.Review;
import com.ebabu.event365live.userinfo.modal.seemore.SeeMoreData;
import com.ebabu.event365live.userinfo.modal.seemore.SeeMoreReviewModal;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;

public class SeeMoreReviewActivity extends AppCompatActivity implements GetResponseData {

    MyLoader myLoader;

    private ActivitySeeMoreReviewBinding seeMoreReviewBinding;
    private EventListAdapter eventListAdapter;
    private ReviewsAdapter reviewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        seeMoreReviewBinding = DataBindingUtil.setContentView(this,R.layout.activity_see_more_review);
        myLoader = new MyLoader(this);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            int getCatId = bundle.getInt(Constants.ApiKeyName.eventId);
            seeMoreReviewRequest(getCatId);
        }
    }
    private void setupUserReview(List<SeeMoreData> seeMoreDataList){
        LinearLayoutManager manager = new LinearLayoutManager(this);
        seeMoreReviewBinding.recyclerSeeMoreEvent.setLayoutManager(manager);
        reviewsAdapter = new ReviewsAdapter(SeeMoreReviewActivity.this,null,seeMoreDataList,true);
        seeMoreReviewBinding.recyclerSeeMoreEvent.setAdapter(reviewsAdapter);
    }
    public void backBtnOnClick(View view) {
        finish();
    }

    private void seeMoreReviewRequest(int getCatId){
        myLoader.show("");
        Call<JsonElement> seeMoreCallBack = APICall.getApiInterface().getSeeMoreReviewByCatId(CommonUtils.getCommonUtilsInstance().getDeviceAuth(),getCatId);
        new APICall(SeeMoreReviewActivity.this).apiCalling(seeMoreCallBack,this,APIs.GET_SEE_MORE_REVIEW_BY_CAT_ID);
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        if(responseObj != null){
            SeeMoreReviewModal moreReviewModal = new Gson().fromJson(responseObj.toString(), SeeMoreReviewModal.class);
            if(moreReviewModal.getSeeMoreData().size() >0){
                setupUserReview(moreReviewModal.getSeeMoreData());
                seeMoreReviewBinding.recyclerSeeMoreEvent.setVisibility(View.VISIBLE);
                seeMoreReviewBinding.noDataFoundContainer.setVisibility(View.GONE);
                return;
            }
            seeMoreReviewBinding.recyclerSeeMoreEvent.setVisibility(View.GONE);
            seeMoreReviewBinding.noDataFoundContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        ShowToast.errorToast(SeeMoreReviewActivity.this,message);
    }
}
