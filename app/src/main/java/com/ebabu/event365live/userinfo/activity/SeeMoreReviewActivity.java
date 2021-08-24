package com.ebabu.event365live.userinfo.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import com.ebabu.event365live.BaseActivity;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivitySeeMoreReviewBinding;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.userinfo.adapter.ReviewsAdapter;
import com.ebabu.event365live.userinfo.modal.seemore.SeeMoreData;
import com.ebabu.event365live.userinfo.modal.seemore.SeeMoreReviewModal;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.ShowToast;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;

public class SeeMoreReviewActivity extends BaseActivity implements GetResponseData {

    private ActivitySeeMoreReviewBinding seeMoreReviewBinding;
    private ReviewsAdapter reviewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        seeMoreReviewBinding = DataBindingUtil.setContentView(this,R.layout.activity_see_more_review);
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
        seeMoreReviewBinding.recyclerSeeMoreEvent.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = CommonUtils.getCommonUtilsInstance().dpToPixel(SeeMoreReviewActivity.this,10);
            }
        });
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
