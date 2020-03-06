package com.ebabu.event365live.oncelaunch.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityEventListBinding;
import com.ebabu.event365live.home.adapter.EventListAdapter;
import com.ebabu.event365live.home.modal.SubCategoryModal;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.oncelaunch.adapter.SubCategoryAdapter;
import com.ebabu.event365live.userinfo.activity.EventDetailsActivity;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.ebabu.event365live.utils.VerticalItemDecoration;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;

public class EventListActivity extends AppCompatActivity implements GetResponseData {

    private ActivityEventListBinding eventListBinding;
    private SubCategoryAdapter subCategoryAdapter;
    private MyLoader myLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventListBinding = DataBindingUtil.setContentView(this,R.layout.activity_event_list);
        myLoader = new MyLoader(this);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            int getCatId = bundle.getInt(Constants.ApiKeyName.categoryId);
            showSubCatEventRequest(getCatId);
        }
    }
    private void setupShowEventList(List<SubCategoryModal.Event> eventList){
        LinearLayoutManager manager = new LinearLayoutManager(this);
        subCategoryAdapter = new SubCategoryAdapter(this, eventList);
        eventListBinding.recyclerShowEventList.setLayoutManager(manager);
        eventListBinding.recyclerShowEventList.setAdapter(subCategoryAdapter);
        eventListBinding.recyclerShowEventList.addItemDecoration(new VerticalItemDecoration(EventListActivity.this,false));
    }
    public void backBtnOnClick(View view) {
        finish();
    }

    private void showSubCatEventRequest(int categoryId){
        myLoader.show("");
        JsonObject subObj = new JsonObject();
        subObj.addProperty(Constants.ApiKeyName.categoryId,categoryId);
        Call<JsonElement> subCallBack = APICall.getApiInterface().getSubCategoryByCatId(100,0,subObj);
        new APICall(EventListActivity.this).apiCalling(subCallBack,this, APIs.SUB_CATEGORY_BY_CAT_ID);
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        if(responseObj != null){
            SubCategoryModal categoryModal = new Gson().fromJson(responseObj.toString(),SubCategoryModal.class);
            if(categoryModal.getSubCategoryData().getEvents().size() > 0){
                eventListBinding.noDataFoundContainer.setVisibility(View.GONE);
                setupShowEventList(categoryModal.getSubCategoryData().getEvents());
                return;
            }
            eventListBinding.noDataFoundContainer.setVisibility(View.VISIBLE);
            eventListBinding.recyclerShowEventList.setVisibility(View.GONE);
            ((TextView)eventListBinding.noDataFoundContainer.findViewById(R.id.tvShowNoDataFound)).setText("Sorry!\nNo events found for this category");
        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        ShowToast.errorToast(EventListActivity.this,message);
        finish();
    }
}
