package com.ebabu.event365live.homedrawer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityRecommendedChooserBinding;
import com.ebabu.event365live.homedrawer.listener.EventBubbleSelectListener;
import com.ebabu.event365live.homedrawer.modal.SelectedEventCategoryModal;
import com.ebabu.event365live.homedrawer.modal.bubblecategory.EventCategoryData;
import com.ebabu.event365live.homedrawer.modal.bubblecategory.EventCategoryModal;
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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kienht.bubblepicker.BubblePickerListener;
import com.kienht.bubblepicker.adapter.BubblePickerAdapter;
import com.kienht.bubblepicker.model.Color;
import com.kienht.bubblepicker.model.PickerItem;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;


public class ChooseRecommendedCatActivity extends AppCompatActivity implements GetResponseData, EventBubbleSelectListener {

    private ActivityRecommendedChooserBinding eventChooserBinding;
    private List<SelectedEventCategoryModal> selectedEvent;
    private ArrayList<EventSubCategoryData> eventSubCategoryList;
    private ArrayList<EventCategoryData> eventCategoryList;
    private RecommendedCatAdapter eventChooseAdapter;
    private MyLoader myLoader;
    private ArrayList<Integer> selectedEventCatId;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventChooserBinding = DataBindingUtil.setContentView(this,R.layout.activity_recommended_chooser);
        intView();
        showEventCategoryListRequest();
    }

    private void intView() {
        myLoader = new MyLoader(this);
        selectedEvent = new ArrayList<>();
        selectedEventCatId = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        eventChooserBinding.recyclerCatShowEvent.setLayoutManager(linearLayoutManager);
        eventChooseAdapter = new RecommendedCatAdapter(ChooseRecommendedCatActivity.this,selectedEvent);
        eventChooserBinding.recyclerCatShowEvent.setAdapter(eventChooseAdapter);

    }

    private void setupBubblePicker(){
        eventChooserBinding.bubblePicker.setAdapter(new BubblePickerAdapter() {
            @Override
            public int getTotalCount() {
                return eventCategoryList.size();
            }

            @NotNull
            @Override
            public PickerItem getItem(int i) {
                PickerItem pickerItem = new PickerItem();
                    pickerItem.setTitle(eventCategoryList.get(i).getCategoryName());
                    //pickerItem.setCustomData(loadEventCat.get(i).getId());
                    pickerItem.setCustomData(eventCategoryList.get(i).getId());
                    // pickerItem.setSelected(true);
                //pickerItem.setTextColor(ContextCompat.getColor(EventChooserActivity.this, R.color.colorPrimary));

                return pickerItem;

            }
        });

        eventChooserBinding.bubblePicker.onPause();
        eventChooserBinding.bubblePicker.onResume();

        eventChooserBinding.bubblePicker.setListener(new BubblePickerListener() {
            @Override
            public void onBubbleSelected(@NotNull PickerItem pickerItem) {
                if(!isItemFound(String.valueOf(pickerItem.getCustomData()))) {
                    selectedEvent.add(new SelectedEventCategoryModal(String.valueOf(pickerItem.getCustomData()), pickerItem.getTitle()));
                    eventChooseAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onBubbleDeselected(@NotNull PickerItem pickerItem) {
                 eventChooseAdapter.removeCatItem(String.valueOf(pickerItem.getCustomData()));

            }
        });
        //eventChooserBinding.bubblePicker.setBackground(getResources().getDrawable(R.drawable.color_bg));

        eventChooserBinding.bubblePicker.setAlwaysSelected(false);
        eventChooserBinding.bubblePicker.setCenterImmediately(true);
        eventChooserBinding.bubblePicker.setBubbleSize(80);
        eventChooserBinding.bubblePicker.setSwipeMoveSpeed(1f);
        //eventChooserBinding.bubblePicker.setBackgroundResource(R.color.colorPrimary);

        //eventChooserBinding.bubblePicker.setClipBounds();
        //eventChooserBinding.bubblePicker.setCenterImmediately(true);
    }


    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        if (responseObj != null) {

            if (typeAPI.equalsIgnoreCase(APIs.GET_EVENT_CATEGORY)) {
                 EventCategoryModal eventCategoryModal = new Gson().fromJson(responseObj.toString(), EventCategoryModal.class);
                 eventCategoryList = eventCategoryModal.getData();
                 setupBubblePicker();
                 return;
            }
        }

    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        ShowToast.errorToast(ChooseRecommendedCatActivity.this, message);
        Log.d("nflakfkanfa", "onFailed: " + message);
    }

    private void showEventCategoryListRequest() {
        myLoader.show("");

        Call<JsonElement> catObj = APICall.getApiInterface().getEventCategory(CommonUtils.getCommonUtilsInstance().getDeviceAuth());
        new APICall(ChooseRecommendedCatActivity.this).apiCalling(catObj, this, APIs.GET_EVENT_CATEGORY);
    }

    private void showEventSubCategoryListRequest() {
        myLoader.show("");
        if (selectedEvent.size() > 0) {
            for (SelectedEventCategoryModal item : selectedEvent) {
                selectedEventCatId.add(Integer.valueOf(item.getId()));
            }
            JSONArray catIdArray = new JSONArray();
            JSONObject catIdJsonObj = new JSONObject();
            for (int i = 0; i < eventChooseAdapter.getCurrentSelectedItem().size(); i++) {
                int id = Integer.parseInt(eventChooseAdapter.getCurrentSelectedItem().get(i).getId());
                catIdArray.put(id);
            }
            try {
                catIdJsonObj.put("categoryId", catIdArray);
                Log.d("fnlakflknasfa", "showEventSubCategoryListRequest: "+catIdJsonObj);
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = (JsonObject)jsonParser.parse(catIdJsonObj.toString());
                Call<JsonElement> catObj = APICall.getApiInterface().getEventSubCategory(jsonObject);
                new APICall(ChooseRecommendedCatActivity.this).apiCalling(catObj, this, APIs.GET_SUB_CATEGORY_NO_AUTH);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


        private boolean isItemFound (String itemId){
            if (selectedEvent.size() > 0) {
                for (SelectedEventCategoryModal categoryModal : selectedEvent) {
                    if (categoryModal.getId().equalsIgnoreCase(itemId))
                        return true;
                }
            }
            return false;
        }

    @Override
    public void selectBubble(PickerItem pickerItem) {
        if (!isItemFound(String.valueOf(pickerItem.getCustomData()))) {
            selectedEvent.add(new SelectedEventCategoryModal(String.valueOf(pickerItem.getCustomData()), pickerItem.getTitle()));
            eventChooseAdapter.notifyDataSetChanged();
        }
    }

    @Override
        public void unSelectBubble (PickerItem pickerItem){
        eventChooseAdapter.removeCatItem(String.valueOf(pickerItem.getCustomData()));
        }

        public void backBtnOnClick (View view){
            finish();
        }

        private void submitCategorySubCategoryListRequest () {
            myLoader.show("");

//            JsonArray jsonArray = new JsonArray();
//            JsonObject jsonObject = new JsonObject();
//            for (int i = 0; i < subCategoryDataList.size(); i++) {
//                EventSubCategoryModal.EventSubCategoryData categoryData = subCategoryDataList.get(i);
//                jsonObject.addProperty("categoryId", categoryData.getCategoryId());
//                jsonObject.addProperty("subCategoryId", categoryData.getId());
//            }
//
//            jsonArray.add(jsonObject);
//            Call<JsonElement> catObj = APICall.getApiInterface().chooseEventCategory(CommonUtils.getCommonUtilsInstance().getDeviceAuth(), jsonArray);
//            new APICall(ChooseRecommendedCatActivity.this).apiCalling(catObj, this, APIs.CHOOSE_EVENT_CATEGORY);
        }
        public void selectEventOnClick (View view){
            if (selectedEvent.size() != 0) {
//
                getSelectedCatIdArray();
                return;
            }
            ShowToast.errorToast(ChooseRecommendedCatActivity.this, getString(R.string.please_select_recommended_event));
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
            Log.d("nfnlanflknalnfklan", "getSelectedCatIdArray: "+catIdJsonObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        navigateToChooseSabCatRecommended(catIdJsonObj.toString());


    }
    private void navigateToChooseSabCatRecommended(String selectedCatId) {
        Log.d("nflkanklfnas", "onResume: "+eventChooserBinding.bubblePicker.getSelectedItems().size());
        Intent subCatIntent = new Intent(ChooseRecommendedCatActivity.this, ChooseRecommendedSubCatActivity.class);
        subCatIntent.putExtra(Constants.catData, selectedCatId);
        startActivity(subCatIntent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("nfnlanflknalnfklan", "onResume: ");
        if(selectedEvent.size()>0){
            eventChooserBinding.bubblePicker.setSelected(true);
            Log.d("nflkanklfnas", "onResume: "+eventChooserBinding.bubblePicker.getSelectedItems().size());
            eventChooserBinding.bubblePicker.setMaxSelectedCount(5);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventChooserBinding.bubblePicker.onPause();
    }
}

