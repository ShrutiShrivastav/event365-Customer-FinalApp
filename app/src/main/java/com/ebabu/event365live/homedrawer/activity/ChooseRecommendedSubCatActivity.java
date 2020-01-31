package com.ebabu.event365live.homedrawer.activity;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.ebabu.event365live.R;
import com.ebabu.event365live.auth.activity.LoginActivity;
import com.ebabu.event365live.auth.activity.OtpVerificationActivity;
import com.ebabu.event365live.databinding.ActivityChooseRecommendedSubCatBinding;
import com.ebabu.event365live.home.activity.HomeActivity;
import com.ebabu.event365live.homedrawer.modal.SelectedEventCategoryModal;
import com.ebabu.event365live.homedrawer.modal.SelectedEventRecommendedModal;
import com.ebabu.event365live.homedrawer.modal.bubblecategory.EventSubCategoryData;
import com.ebabu.event365live.homedrawer.modal.bubblecategory.EventSubCategoryModal;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.userinfo.adapter.RecommendedCatAdapter;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kienht.bubblepicker.BubblePickerListener;
import com.kienht.bubblepicker.adapter.BubblePickerAdapter;
import com.kienht.bubblepicker.model.BubbleGradient;
import com.kienht.bubblepicker.model.PickerItem;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class ChooseRecommendedSubCatActivity extends AppCompatActivity implements GetResponseData {
    private List<SelectedEventCategoryModal> selectedSubCatEvent;
    private List<SelectedEventRecommendedModal> selectedEventRecommendedModalList;
    private ActivityChooseRecommendedSubCatBinding subCatBinding;
    private MyLoader myLoader;
    private RecommendedCatAdapter eventChooseAdapter;
    private JsonObject categorySelectedObj;
    TypedArray colors;
    private EventSubCategoryModal eventSubCategoryModal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subCatBinding = DataBindingUtil.setContentView(this,R.layout.activity_choose_recommended_sub_cat);
        selectedSubCatEvent = new ArrayList<>();
        colors = getResources().obtainTypedArray(R.array.colors);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            myLoader = new MyLoader(ChooseRecommendedSubCatActivity.this);
            selectedEventRecommendedModalList = new ArrayList<>();
            String getSelectedCatId = bundle.getString(Constants.catData);
            if(getSelectedCatId != null && !TextUtils.isEmpty(getSelectedCatId)){
                JsonParser jsonParser = new JsonParser();
                categorySelectedObj = (JsonObject) jsonParser.parse(getSelectedCatId);
                showEventSubCategoryListRequest(categorySelectedObj);
                Log.d("nflknaklfnlasnlfa", "onCreate: "+ categorySelectedObj.toString());
            }
        }
        //intView();
    }

    private void intView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        subCatBinding.recyclerSubCatShowEvent.setLayoutManager(linearLayoutManager);
        eventChooseAdapter = new RecommendedCatAdapter(ChooseRecommendedSubCatActivity.this,selectedSubCatEvent);
        subCatBinding.recyclerSubCatShowEvent.setAdapter(eventChooseAdapter);
        setupBubblePicker(eventSubCategoryModal.getEventSubCatData());
    }

    private void showEventSubCategoryListRequest(JsonObject object) {
        myLoader.show("Please Wait...");
        Call<JsonElement> catObj = APICall.getApiInterface().getEventSubCategory(object);
        new APICall(ChooseRecommendedSubCatActivity.this).apiCalling(catObj, this, APIs.GET_SUB_CATEGORY_NO_AUTH);
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        if (typeAPI.equalsIgnoreCase(APIs.CHOOSE_EVENT_CATEGORY)) {

            /* Registering AppLogiz*/
            try {
                String userId = responseObj.getJSONObject("data").getString("id");
                String name = responseObj.getJSONObject("data").getString("name");
                boolean isRemind = responseObj.getJSONObject("data").getBoolean("isRemind");
                boolean isNotify = responseObj.getJSONObject("data").getBoolean("isNotify");
                String customerId = responseObj.getJSONObject("data").getString("customerId");

                if(!CommonUtils.getCommonUtilsInstance().isUserLogin()){
                    CommonUtils.getCommonUtilsInstance().validateUser(userId,name,isRemind,isNotify,customerId);
                    CommonUtils.getCommonUtilsInstance().navigateTo(ChooseRecommendedSubCatActivity.this,HomeActivity.class, true);
                    HomeActivity.isComeFromPreferencesScreen = false;
                    return;
                }
                CommonUtils.getCommonUtilsInstance().navigateTo(ChooseRecommendedSubCatActivity.this,HomeActivity.class, false);
                HomeActivity.isComeFromPreferencesScreen = true;


               // CommonUtils.getCommonUtilsInstance().appLozicRegister(this,userId,name,profilePic,isRemind,isNotify, false, myLoader);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return;
        }
        eventSubCategoryModal = new Gson().fromJson(responseObj.toString(), EventSubCategoryModal.class);
        intView();

       // new MyThread().start();
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        ShowToast.errorToast(ChooseRecommendedSubCatActivity.this,message);
    }

    private void setupBubblePicker(final List<EventSubCategoryData> eventSubCategoryData){
        subCatBinding.bubbleSubPicker.setAdapter(new BubblePickerAdapter() {
            @Override
            public int getTotalCount() {

                return eventSubCategoryData.size() >= 10 ? 10 : eventSubCategoryData.size();
            }
            @NotNull
            @Override
            public PickerItem getItem(int i) {
                PickerItem pickerItem = new PickerItem();
                SelectedEventRecommendedModal eventRecommendedModal = new SelectedEventRecommendedModal(eventSubCategoryData.get(i).getCategoryId(),eventSubCategoryData.get(i).getId());
                pickerItem.setTitle(eventSubCategoryData.get(i).getSubCategoryName());
                pickerItem.setCustomData(eventRecommendedModal);
                pickerItem.setGradient(new BubbleGradient(colors.getColor((i * 2) % 8, 0),
                        colors.getColor((i * 2) % 8 + 1, 0), BubbleGradient.VERTICAL));
                pickerItem.setTextColor(ContextCompat.getColor(ChooseRecommendedSubCatActivity.this, R.color.colorWhite));

                return pickerItem;

            }
        });


        subCatBinding.bubbleSubPicker.onPause();
        subCatBinding.bubbleSubPicker.onResume();
        subCatBinding.bubbleSubPicker.setListener(new BubblePickerListener() {
            @Override
            public void onBubbleSelected(@NotNull PickerItem pickerItem) {
                if(!isItemFound(String.valueOf(pickerItem.getCustomData()))) {
                    selectedSubCatEvent.add(new SelectedEventCategoryModal(String.valueOf(((SelectedEventRecommendedModal)pickerItem.getCustomData()).getSubCategoryId()), pickerItem.getTitle()));

                    SelectedEventRecommendedModal selectedEventRecommendedModal = (SelectedEventRecommendedModal) pickerItem.getCustomData();
                    selectedEventRecommendedModalList.add(selectedEventRecommendedModal);
                    Log.d("fnlanlfknalknfkla", selectedEventRecommendedModal.getCategoryId()+" onBubbleSelected: "+selectedEventRecommendedModal.getSubCategoryId()+" == "+pickerItem.getTitle());
                    eventChooseAdapter.notifyDataSetChanged();

                }
            }
            @Override
            public void onBubbleDeselected(@NotNull PickerItem pickerItem) {
                eventChooseAdapter.removeCatItem(String.valueOf(((SelectedEventRecommendedModal)pickerItem.getCustomData()).getSubCategoryId()));
                selectedEventRecommendedModalList.remove((SelectedEventRecommendedModal) pickerItem.getCustomData());
            }
        });

        subCatBinding.bubbleSubPicker.setAlwaysSelected(false);
        subCatBinding.bubbleSubPicker.setCenterImmediately(true);
        subCatBinding.bubbleSubPicker.setBubbleSize(80);
        subCatBinding.bubbleSubPicker.setSwipeMoveSpeed(.8f);


    }

    private boolean isItemFound (String itemId){
        if (selectedSubCatEvent.size() > 0) {
            for (SelectedEventCategoryModal categoryModal : selectedSubCatEvent) {
                if (categoryModal.getId().equalsIgnoreCase(itemId))
                    return true;
            }
        }
        return false;
    }

    private void submitCatSubCatEventRequest(JsonArray jsonArray) {
        myLoader.show("Please Wait...");
        Call<JsonElement> catObj = APICall.getApiInterface().chooseEventCategory(CommonUtils.getCommonUtilsInstance().getDeviceAuth(),jsonArray);
        new APICall(ChooseRecommendedSubCatActivity.this).apiCalling(catObj, this, APIs.CHOOSE_EVENT_CATEGORY);
    }
    public void submitRecommendedOnClick(View view) {
        JSONArray catIdArray = new JSONArray();
        JSONObject catIdJsonObj = null;

        if(selectedEventRecommendedModalList.size() >0){
            for(int i=0;i<selectedEventRecommendedModalList.size();i++){

                catIdJsonObj = new JSONObject();

                try {
                    catIdJsonObj.put(Constants.categoryId,selectedEventRecommendedModalList.get(i).getCategoryId());
                    catIdJsonObj.put(Constants.subCategoryId,selectedEventRecommendedModalList.get(i).getSubCategoryId());

                    catIdArray.put(catIdJsonObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.d("fnklanfklanls", "submitRecommendedOnClick: "+catIdArray.toString());
            JsonParser jsonParser = new JsonParser();
            JsonArray jsonArray = (JsonArray) jsonParser.parse(catIdArray.toString());
            submitCatSubCatEventRequest(jsonArray);
            return;
        }
        ShowToast.errorToast(ChooseRecommendedSubCatActivity.this, getString(R.string.please_select_recommended_event));

    }



    public void backBtnOnClick(View view) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(selectedSubCatEvent.size()>0) {
            selectedSubCatEvent.clear();
            eventChooseAdapter.notifyDataSetChanged();
            ShowToast.infoToast(ChooseRecommendedSubCatActivity.this,getString(R.string.please_choose_bubble));
            //eventChooserBinding.bubblePicker.setMaxSelectedCount(5);
        }
    }
    class MyThread extends Thread{
        @Override
        public void run() {
            super.run();
            Handler handler = new Handler(getMainLooper());
            Runnable runnable = () -> {

            };

            handler.post(runnable);


        }
    }
}
