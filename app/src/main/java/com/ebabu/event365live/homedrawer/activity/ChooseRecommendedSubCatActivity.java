package com.ebabu.event365live.homedrawer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.ebabu.event365live.R;
import com.ebabu.event365live.auth.activity.LoginActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subCatBinding = DataBindingUtil.setContentView(this,R.layout.activity_choose_recommended_sub_cat);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Bundle bundle = getIntent().getExtras();
                if(bundle != null){
                    myLoader = new MyLoader(ChooseRecommendedSubCatActivity.this);
                    selectedSubCatEvent = new ArrayList<>();
                    selectedEventRecommendedModalList = new ArrayList<>();
                    String getSelectedCatId = bundle.getString(Constants.catData);
                    if(getSelectedCatId != null && !TextUtils.isEmpty(getSelectedCatId)){
                        JsonParser jsonParser = new JsonParser();
                        categorySelectedObj = (JsonObject) jsonParser.parse(getSelectedCatId);
                        showEventSubCategoryListRequest(categorySelectedObj);
                        Log.d("nflknaklfnlasnlfa", "onCreate: "+ categorySelectedObj.toString());
                    }
                }
            }
        },500);


        //intView();

    }

    private void intView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        subCatBinding.recyclerSubCatShowEvent.setLayoutManager(linearLayoutManager);
        eventChooseAdapter = new RecommendedCatAdapter(ChooseRecommendedSubCatActivity.this,selectedSubCatEvent);
        subCatBinding.recyclerSubCatShowEvent.setAdapter(eventChooseAdapter);
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
            CommonUtils.getCommonUtilsInstance().validateUser(responseObj);
            navigateToHomePage();
            return;
        }
        EventSubCategoryModal eventSubCategoryModal = new Gson().fromJson(responseObj.toString(), EventSubCategoryModal.class);
        intView();
        setupBubblePicker(eventSubCategoryModal.getEventSubCatData());
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
                return eventSubCategoryData.size();
            }
            @NotNull
            @Override
            public PickerItem getItem(int i) {
                PickerItem pickerItem = new PickerItem();
                SelectedEventRecommendedModal eventRecommendedModal = new SelectedEventRecommendedModal(eventSubCategoryData.get(i).getCategoryId(),eventSubCategoryData.get(i).getId());
                pickerItem.setTitle(eventSubCategoryData.get(i).getSubCategoryName());

                pickerItem.setCustomData(eventRecommendedModal);
                //pickerItem.setCustomData(eventSubCategoryData.get(i).getCategoryId());

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
        //eventChooserBinding.bubblePicker.setBackground(getResources().getDrawable(R.drawable.color_bg));

        subCatBinding.bubbleSubPicker.setAlwaysSelected(false);
        subCatBinding.bubbleSubPicker.setCenterImmediately(true);
        subCatBinding.bubbleSubPicker.setBubbleSize(80);
        subCatBinding.bubbleSubPicker.setSwipeMoveSpeed(.4f);

        //eventChooserBinding.bubblePicker.setClipBounds();
        //eventChooserBinding.bubblePicker.setCenterImmediately(true);
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

    private void navigateToHomePage(){
        Intent homeIntent = new Intent(ChooseRecommendedSubCatActivity.this, HomeActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
        finish();
    }
}
