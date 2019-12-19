package com.ebabu.event365live.home.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.FragmentRecommendedBinding;
import com.ebabu.event365live.home.adapter.RecommendedEventListAdapter;
import com.ebabu.event365live.home.modal.GetAllCategoryModal;
import com.ebabu.event365live.home.modal.SubCategoryModal;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.oncelaunch.activity.EventListActivity;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.json.JSONObject;
import java.util.List;
import retrofit2.Call;
/**
 * A simple {@link Fragment} subclass.
 */
public class RecommendedFragment extends Fragment implements GetResponseData, SwipeRefreshLayout.OnRefreshListener {
    private FragmentRecommendedBinding recommendedBinding;
    private RecommendedEventListAdapter recommendedEventListAdapter;
    private int resId = R.anim.layout_animation_fall_down;
    private Context context;
    private MyLoader myLoader;
    private ChipGroup chipGroup;
    private List<GetAllCategoryModal.GetAllCategoryData> allCategoryModalData;
    private boolean isRecommendedListShowing;
    private int categoryId;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        myLoader = new MyLoader(context);
    }

    public RecommendedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        recommendedBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_recommended,container,false);
        recommendedBinding.swipeLayout.setOnRefreshListener(this);

        if(!CommonUtils.getCommonUtilsInstance().isUserLogin()){
            recommendedBinding.recommendedRecycler.setVisibility(View.GONE);
            recommendedBinding.recommendedCardView.setVisibility(View.VISIBLE);
            categoryRecommendedRequest();
        }else {
           // showRecommendedListRequest();
//            recommendedBinding.recommendedRecycler.setVisibility(View.VISIBLE);
//            recommendedBinding.recommendedCardView.setVisibility(View.GONE);H
            recommendedBinding.recommendedCardView.setVisibility(View.GONE);
            recommendedBinding.noDataFoundContainer.setVisibility(View.VISIBLE);
            recommendedBinding.recommendedRecycler.setVisibility(View.GONE);
        }




        return recommendedBinding.getRoot();
    }
    private void setupRecommendedEventList(){
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recommendedEventListAdapter = new RecommendedEventListAdapter(false,null);
        recommendedBinding.recommendedRecycler.setLayoutManager(linearLayoutManager);
        recommendedBinding.recommendedRecycler.setLayoutAnimation(animation);
        recommendedBinding.recommendedRecycler.setAdapter(recommendedEventListAdapter);
        recommendedEventListAdapter.notifyDataSetChanged();

    }
    private void showOnWithoutLogin(){
        chipGroup = recommendedBinding.chipGroup;
        chipGroup.setSingleSelection(true);
        for(GetAllCategoryModal.GetAllCategoryData getCatData : allCategoryModalData){
            final Chip chip = new Chip(context);
            chip.setCheckable(true);
            chip.setCheckedIconVisible(true);
            chip.setClickable(true);
            chip.setGravity(Gravity.HORIZONTAL_GRAVITY_MASK);
            chip.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimaryDark)));
            chip.setChipStrokeWidth(2);
            chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorWhite)));
            chip.setTag(getCatData.getId());
            chip.setText(getCatData.getCategoryName());
            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isRecommendedListShowing = true;
                            if(isChecked){
                                categoryId = (int) buttonView.getTag();
                                Intent eventListIntent = new Intent(context,EventListActivity.class);
                                eventListIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                eventListIntent.putExtra(Constants.ApiKeyName.categoryId,categoryId);
                                context.startActivity(eventListIntent);
                            }
                        }
                    },300);
                }
            });
            chipGroup.addView(chip);
        }
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        if(responseObj != null){
            if(typeAPI.equalsIgnoreCase(APIs.GET_CATEGORY)){
                GetAllCategoryModal getAllCategoryModal = new Gson().fromJson(responseObj.toString(),GetAllCategoryModal.class);
                allCategoryModalData = getAllCategoryModal.getData();
                if(allCategoryModalData != null && allCategoryModalData.size()>0) {
                    showOnWithoutLogin();
                }
            }
            else if(typeAPI.equalsIgnoreCase(APIs.SUB_CATEGORY_BY_CAT_ID)){
                    SubCategoryModal categoryModal = new Gson().fromJson(responseObj.toString(),SubCategoryModal.class);
                    if(categoryModal.getSubCategoryData().getEvents().size() > 0){
                        recommendedBinding.recommendedRecycler.setVisibility(View.VISIBLE);
                        recommendedBinding.recommendedCardView.setVisibility(View.GONE);
//                        setupRecommendedEventList();
                        return;
                    }
                    recommendedBinding.recommendedRecycler.setVisibility(View.GONE);
                    recommendedBinding.recommendedCardView.setVisibility(View.VISIBLE);
                    ShowToast.errorToast(getActivity(),getString(R.string.no_data_found));
            }else if(typeAPI.equalsIgnoreCase(APIs.GET_RECOMMENDED__AUTH)){

                recommendedBinding.noDataFoundContainer.setVisibility(View.VISIBLE);
                recommendedBinding.recommendedRecycler.setVisibility(View.GONE);

                //setupRecommendedEventList();
            }

        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        ShowToast.errorToast(context,message);
        if(errorBody != null){

        }
    }

    private void categoryRecommendedRequest(){
        myLoader.show("");
        Call<JsonElement> catRecommendedCallBack = APICall.getApiInterface().getCategory();
        new APICall(context).apiCalling(catRecommendedCallBack,this, APIs.GET_CATEGORY);
    }

    @Override
    public void onRefresh() {
        if(allCategoryModalData.size() >0 && chipGroup != null && isRecommendedListShowing){
            allCategoryModalData.clear();
            chipGroup.removeAllViews();
            categoryRecommendedRequest();
        }
        recommendedBinding.swipeLayout.setRefreshing(false);
    }
    private void showRecommendedEventListAsPerSelectTag(int categoryId){
        myLoader.show("");
        recommendedBinding.swipeLayout.setEnabled(false);
        JsonObject subObj = new JsonObject();
        subObj.addProperty(Constants.ApiKeyName.categoryId,categoryId);
        Call<JsonElement> subCallBack = APICall.getApiInterface().getSubCategoryByCatId(100,0,subObj);
        new APICall(context).apiCalling(subCallBack,this,APIs.SUB_CATEGORY_BY_CAT_ID);
    }


    private void showRecommendedListRequest(){
        myLoader.show("");
        Call<JsonElement> recommnededCall = APICall.getApiInterface().getRecommendedAuth(CommonUtils.getCommonUtilsInstance().getDeviceAuth());
        new APICall(context).apiCalling(recommnededCall,this,APIs.GET_RECOMMENDED__AUTH);
    }



}