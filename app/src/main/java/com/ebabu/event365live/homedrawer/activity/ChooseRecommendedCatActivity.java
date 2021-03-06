package com.ebabu.event365live.homedrawer.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ebabu.event365live.BaseActivity;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityRecommendedChooserBinding;
import com.ebabu.event365live.home.activity.HomeActivity;
import com.ebabu.event365live.homedrawer.modal.SelectedEventCategoryModal;
import com.ebabu.event365live.homedrawer.modal.SelectedEventRecommendedModal;
import com.ebabu.event365live.homedrawer.modal.bubblecategory.EventCategoryModal;
import com.ebabu.event365live.homedrawer.modal.bubblecategory.EventSubCategoryModal;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.userinfo.adapter.RecommendedCatAdapter;
import com.ebabu.event365live.utils.CommonUtils;
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

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import retrofit2.Call;

public class ChooseRecommendedCatActivity extends BaseActivity implements GetResponseData {

    private ActivityRecommendedChooserBinding eventChooserBinding;
    private RecommendedCatAdapter eventChooseAdapter;
    public static boolean isRecommendedSelected = false;

    private List<SelectedEventCategoryModal> selectedSubCatEvent;
    private List<SelectedEventCategoryModal> selectedEvent;
    private List<SelectedEventRecommendedModal> subCategoryBubbleItem;
    private boolean isItMainCategory = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventChooserBinding = DataBindingUtil.setContentView(this, R.layout.activity_recommended_chooser);
        eventChooserBinding.ivBackBtn.setOnClickListener(v -> finish());
        intView();
        showEventCategoryListRequest();
    }

    private void intView() {
        selectedSubCatEvent = new ArrayList<>();
        selectedEvent = new ArrayList<>();
        subCategoryBubbleItem = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        eventChooserBinding.recyclerCatShowEvent.setLayoutManager(linearLayoutManager);
        eventChooseAdapter = new RecommendedCatAdapter(ChooseRecommendedCatActivity.this, selectedEvent);
        eventChooserBinding.recyclerCatShowEvent.setAdapter(eventChooseAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isItMainCategory)
            eventChooserBinding.bubblePicker.onResume();
        else
            eventChooserBinding.bubblePickerSubCat.onResume();
        if (selectedEvent.size() > 0) {
            selectedEvent.clear();
            eventChooseAdapter.notifyDataSetChanged();
            ShowToast.infoToast(ChooseRecommendedCatActivity.this, getString(R.string.please_choose_bubble));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if(isItMainCategory)
                eventChooserBinding.bubblePicker.onPause();
            else
                eventChooserBinding.bubblePickerSubCat.onResume();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupBubblePicker(BubblePickerAdapter bubblePickerAdapter) {
        eventChooserBinding.bubblePicker.setAdapter(bubblePickerAdapter);
        eventChooserBinding.bubblePicker.onPause();
        eventChooserBinding.bubblePicker.onResume();
        eventChooserBinding.bubblePicker.setMaxSelectedCount(5);
        eventChooserBinding.bubblePicker.setAlwaysSelected(false);
        eventChooserBinding.bubblePicker.setCenterImmediately(true);
        eventChooserBinding.bubblePicker.setBubbleSize(80);
        eventChooserBinding.bubblePicker.setSwipeMoveSpeed(.4f);

        eventChooserBinding.bubblePicker.setListener(new BubblePickerListener() {
            @Override
            public void onBubbleSelected(@NotNull PickerItem pickerItem) {

                if (!isItemFound(String.valueOf(pickerItem.getCustomData()))) {
                    selectedEvent.add(new SelectedEventCategoryModal(String.valueOf(pickerItem.getCustomData()), pickerItem.getTitle()));
                    eventChooseAdapter.notifyDataSetChanged();
                }

                if (eventChooserBinding.bubblePicker.getSelectedItems().size() == 6) {
                    eventChooserBinding.bubblePicker.setSelected(false);
                }

            }

            @Override
            public void onBubbleDeselected(@NotNull PickerItem pickerItem) {
                eventChooseAdapter.removeCatItem(String.valueOf(pickerItem.getCustomData()));
                eventChooseAdapter.notifyDataSetChanged();
            }
        });

    }

    private void setupSubCatBubblePicker(BubblePickerAdapter bubblePickerAdapter) {
        eventChooseAdapter.setSabCategoryItem(selectedEvent);
        eventChooserBinding.btnNext.setText(getString(R.string.submit));
        eventChooserBinding.bubblePicker.setVisibility(View.GONE);
        eventChooserBinding.bubblePickerSubCat.setVisibility(View.VISIBLE);

        eventChooserBinding.bubblePickerSubCat.setAdapter(bubblePickerAdapter);
        eventChooserBinding.bubblePickerSubCat.onPause();
        eventChooserBinding.bubblePickerSubCat.onResume();
        eventChooserBinding.bubblePickerSubCat.setMaxSelectedCount(10);
        eventChooserBinding.bubblePickerSubCat.setAlwaysSelected(false);
        eventChooserBinding.bubblePickerSubCat.setCenterImmediately(true);
        eventChooserBinding.bubblePickerSubCat.setBubbleSize(80);
        eventChooserBinding.bubblePickerSubCat.setSwipeMoveSpeed(1.5f);

        eventChooserBinding.bubblePickerSubCat.setListener(new BubblePickerListener() {
            @Override
            public void onBubbleSelected(@NotNull PickerItem pickerItem) {

                if (!isItemFound(String.valueOf(pickerItem.getCustomData()))) {
                    selectedEvent.add(new SelectedEventCategoryModal(String.valueOf(pickerItem.getCustomData()), pickerItem.getTitle()));
                    selectedSubCatEvent.add(new SelectedEventCategoryModal(String.valueOf(((SelectedEventRecommendedModal) pickerItem.getCustomData()).getSubCategoryId()), pickerItem.getTitle()));
                    SelectedEventRecommendedModal selectedEventRecommendedModal = (SelectedEventRecommendedModal) pickerItem.getCustomData();
                    subCategoryBubbleItem.add(selectedEventRecommendedModal);
                    eventChooseAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onBubbleDeselected(@NotNull PickerItem pickerItem) {
                eventChooseAdapter.removeCatItem(String.valueOf(pickerItem.getCustomData()));
                eventChooseAdapter.notifyDataSetChanged();

            }
        });

    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        if (responseObj != null) {
            if (typeAPI.equalsIgnoreCase(APIs.GET_EVENT_CATEGORY)) {
                myLoader.dismiss();
                HomeActivity.isComeFromPreferencesScreen = false;
                EventCategoryModal eventCategoryModal = new Gson().fromJson(responseObj.toString(), EventCategoryModal.class);

                BubblePickerAdapter categoryBubbleAdapter = new BubblePickerAdapter() {
                    @Override
                    public int getTotalCount() {
                        return eventCategoryModal.getData().getCategory().size();
                    }

                    @NotNull
                    @Override
                    public PickerItem getItem(int i) {
                        PickerItem pickerItem = new PickerItem();
                        pickerItem.setTitle(eventCategoryModal.getData().getCategory().get(i).getCategoryName());
                        pickerItem.setColor(ContextCompat.getColor(ChooseRecommendedCatActivity.this, R.color.bubbleColor));
                        pickerItem.setCustomData(eventCategoryModal.getData().getCategory().get(i).getId());
                        pickerItem.setTextColor(ContextCompat.getColor(ChooseRecommendedCatActivity.this, R.color.bubbleTextColor));
                        Typeface regular = ResourcesCompat.getFont(ChooseRecommendedCatActivity.this, R.font.caros_medium);
                        pickerItem.setTypeface(regular);
                        return pickerItem;

                    }
                };

                setupBubblePicker(categoryBubbleAdapter);
            } else if (typeAPI.equalsIgnoreCase(APIs.GET_SUB_CATEGORY_NO_AUTH)) {
                myLoader.dismiss();
                EventSubCategoryModal eventSubCategoryModal = new Gson().fromJson(responseObj.toString(), EventSubCategoryModal.class);
                BubblePickerAdapter subCategoryBubbleAdapter = new BubblePickerAdapter() {
                    @Override
                    public int getTotalCount() {
                        return eventSubCategoryModal.getEventSubCatData().size();
                    }

                    @NotNull
                    @Override
                    public PickerItem getItem(int i) {
                        PickerItem pickerItem = new PickerItem();
                        SelectedEventRecommendedModal eventRecommendedModal = new SelectedEventRecommendedModal(eventSubCategoryModal.getEventSubCatData().get(i).getCategoryId(), eventSubCategoryModal.getEventSubCatData().get(i).getId());
                        pickerItem.setTitle(eventSubCategoryModal.getEventSubCatData().get(i).getSubCategoryName());
                        pickerItem.setCustomData(eventRecommendedModal);
                        pickerItem.setColor(ContextCompat.getColor(ChooseRecommendedCatActivity.this, R.color.bubbleColor));
                        pickerItem.setTextColor(ContextCompat.getColor(ChooseRecommendedCatActivity.this, R.color.bubbleTextColor));
                        Typeface regular = ResourcesCompat.getFont(ChooseRecommendedCatActivity.this, R.font.caros_medium);
                        pickerItem.setTypeface(regular);
                        return pickerItem;
                    }
                };
                isItMainCategory = false;
                setupSubCatBubblePicker(subCategoryBubbleAdapter);

            } else if (typeAPI.equalsIgnoreCase(APIs.CHOOSE_EVENT_CATEGORY)) {

                try {
                    String userId = responseObj.getJSONObject("data").getString("id");
                    String name = responseObj.getJSONObject("data").getString("name");
                    boolean isRemind = responseObj.getJSONObject("data").getBoolean("isRemind");
                    boolean isNotify = responseObj.getJSONObject("data").getBoolean("isNotify");
                    String customerId = responseObj.getJSONObject("data").getString("customerId");

                    if (!CommonUtils.getCommonUtilsInstance().isUserLogin()) {
                        CommonUtils.getCommonUtilsInstance().appLozicRegister(this, userId, name);
                        CommonUtils.getCommonUtilsInstance().getAppLozicListener(new CommonUtils.AppLozicListener() {
                            @Override
                            public void appLozicOnSuccess() {
                                myLoader.dismiss();
                                CommonUtils.getCommonUtilsInstance().validateUser(userId, name, isRemind, isNotify, customerId);
                                navigateToHomePage();
                            }

                            @Override
                            public void appLozicOnFailure() {
                                myLoader.dismiss();
                            }
                        });

                        return;
                    }
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);
                    isRecommendedSelected = true;
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        ShowToast.errorToast(ChooseRecommendedCatActivity.this, message);
    }

    private void showEventCategoryListRequest() {
        myLoader.show("");
        Call<JsonElement> catObj = APICall.getApiInterface().getEventCategory(CommonUtils.getCommonUtilsInstance().getDeviceAuth());
        new APICall(ChooseRecommendedCatActivity.this).apiCalling(catObj, this, APIs.GET_EVENT_CATEGORY);
    }

    private boolean isItemFound(String itemId) {
        if (selectedEvent.size() > 0) {
            for (SelectedEventCategoryModal categoryModal : selectedEvent) {
                if (categoryModal.getId().equalsIgnoreCase(itemId))
                    return true;
            }
        }
        return false;
    }

    public void selectEventOnClick(View view) {
        if (eventChooserBinding.btnNext.getText().toString().equalsIgnoreCase(getString(R.string.submit))) {
            JSONArray catIdArray = new JSONArray();
            JSONObject catIdJsonObj = null;

            if (subCategoryBubbleItem.size() > 0) {
                for (int i = 0; i < subCategoryBubbleItem.size(); i++) {

                    catIdJsonObj = new JSONObject();

                    try {
                        catIdJsonObj.put(Constants.categoryId, subCategoryBubbleItem.get(i).getCategoryId());
                        catIdJsonObj.put(Constants.subCategoryId, subCategoryBubbleItem.get(i).getSubCategoryId());

                        catIdArray.put(catIdJsonObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("fnklanfklanls", "submitRecommendedOnClick: " + catIdArray.toString());
                JsonParser jsonParser = new JsonParser();
                JsonArray jsonArray = (JsonArray) jsonParser.parse(catIdArray.toString());

                Log.d("fanfklnaslf", "selectEventOnClick: " + jsonArray.toString());

                submitCatSubCatEventRequest(jsonArray);
                return;
            }
            ShowToast.infoToast(ChooseRecommendedCatActivity.this, getString(R.string.please_choose_subcategory));
            return;
        }
        if (selectedEvent.size() != 0) {
            getSelectedCatIdArray();
            return;
        }
        ShowToast.infoToast(ChooseRecommendedCatActivity.this, getString(R.string.please_select_recommended_event));
    }

    private void getSelectedCatIdArray() {
        JSONArray catIdArray = new JSONArray();
        JSONObject catIdJsonObj = new JSONObject();
        for (int i = 0; i < eventChooseAdapter.getCurrentSelectedItem().size(); i++) {
            int id = Integer.parseInt(eventChooseAdapter.getCurrentSelectedItem().get(i).getId());
            catIdArray.put(id);
        }

        try {
            catIdJsonObj.put("categoryId", catIdArray);
            Log.d("nfnlanflknalnfklan", "getSelectedCatIdArray: " + catIdJsonObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonParser jsonParser = new JsonParser();
        showEventSubCategoryListRequest((JsonObject) jsonParser.parse(catIdJsonObj.toString()));
    }

    private void showEventSubCategoryListRequest(JsonObject object) {
        myLoader.show("Please Wait...");
        Call<JsonElement> catObj = APICall.getApiInterface().getEventSubCategory(object);
        new APICall(ChooseRecommendedCatActivity.this).apiCalling(catObj, this, APIs.GET_SUB_CATEGORY_NO_AUTH);
    }

    private void submitCatSubCatEventRequest(JsonArray jsonArray) {
        myLoader.show("Please Wait...");
        Call<JsonElement> catObj = APICall.getApiInterface().chooseEventCategory(CommonUtils.getCommonUtilsInstance().getDeviceAuth(), jsonArray);
        new APICall(ChooseRecommendedCatActivity.this).apiCalling(catObj, this, APIs.CHOOSE_EVENT_CATEGORY);
    }

    private void navigateToHomePage() {
        Intent homeIntent = new Intent(ChooseRecommendedCatActivity.this, HomeActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
        isRecommendedSelected = true;
        finish();
    }

}

