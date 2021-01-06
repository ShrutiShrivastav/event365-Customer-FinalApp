package com.ebabu.event365live.homedrawer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.ebabu.event365live.BaseActivity;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityContactUsBinding;
import com.ebabu.event365live.homedrawer.adapter.ContactUsAdapter;
import com.ebabu.event365live.homedrawer.modal.GetIssueModal;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.userinfo.activity.EventDetailsActivity;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;

public class ContactUsActivity extends BaseActivity implements GetResponseData {
    private ActivityContactUsBinding contactUsBinding;
    private Integer getIssueId;
    private  GetIssueModal getIssueModal;
    private ContactUsAdapter categoryListAdapter;
    private BottomSheetBehavior contactUsBottomSheet;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contactUsBinding = DataBindingUtil.setContentView(this,R.layout.activity_contact_us);
        contactUsBottomSheet = BottomSheetBehavior.from(contactUsBinding.contactUs.contactBottomSheet);
        setContactUsQueryRequest();


//        contactUsBinding.etEnterUserIssue.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                contactUsBinding.nestedView.requestDisallowInterceptTouchEvent(true);
//                return false;
//            }
//        });


        contactUsBinding.spinnerSelectIssue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getIssueId = getIssueModal.getIssueData().get(i).getId();
                categoryListAdapter.setSelection(adapterView.getSelectedItemPosition());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        contactUsBinding.contactUs.ivCancelCall.setOnClickListener(v-> {
            if(contactUsBottomSheet.getState() == BottomSheetBehavior.STATE_EXPANDED){
                contactUsBinding.shadow.setVisibility(View.GONE);
                contactUsBottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            else if(contactUsBottomSheet.getState() == BottomSheetBehavior.STATE_COLLAPSED){
                contactUsBinding.shadow.setVisibility(View.VISIBLE);
                contactUsBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        contactUsBinding.contactUs.ivShowCall.setOnClickListener(v->{
//            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "8666913303"));
//            startActivity(intent);
        });

    }

    public void backBtnOnClick(View view) {
        finish();
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        if(responseObj != null){
            if(typeAPI.equalsIgnoreCase(APIs.GET_CONTACT_US_ISSUE)){
                getIssueModal = new Gson().fromJson(responseObj.toString(), GetIssueModal.class);
                setupContactList(getIssueModal.getIssueData());

            }else if(typeAPI.equalsIgnoreCase(APIs.POST_CONTACT_US_ISSUE)){
                ShowToast.successToast(ContactUsActivity.this,message);
                finish();
            }
        }
    }
    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        ShowToast.errorToast(ContactUsActivity.this,message);
        finish();
    }

    private void setContactUsQueryRequest(){
        myLoader.show("");
        Call<JsonElement> contactUsCallBack = APICall.getApiInterface().getIssue();
        new APICall(ContactUsActivity.this).apiCalling(contactUsCallBack,this, APIs.GET_CONTACT_US_ISSUE);
    }
    private void postContactUsQueryRequest(Integer issueId,String userQueryMsg){
        myLoader.show("");
        JsonObject userObj = new JsonObject();
        userObj.addProperty(Constants.ApiKeyName.issueId,issueId);
        userObj.addProperty(Constants.ApiKeyName.message,userQueryMsg);

        Call<JsonElement> contactUsCallBack = APICall.getApiInterface().postIssue(CommonUtils.getCommonUtilsInstance().getDeviceAuth(),userObj);
        new APICall(ContactUsActivity.this).apiCalling(contactUsCallBack,this, APIs.POST_CONTACT_US_ISSUE);
    }
    private void setupContactList(List<GetIssueModal.GetIssueData> getIssueDataList){
        if(getIssueDataList.size()> 0) {
            categoryListAdapter = new ContactUsAdapter(ContactUsActivity.this,getIssueDataList);
            contactUsBinding.spinnerSelectIssue.setAdapter(categoryListAdapter);
            return;
        }
        ShowToast.infoToast(ContactUsActivity.this,getString(R.string.no_cate_data_found));
    }

    public void sendUserIssueOnClick(View view) {

        if(!CommonUtils.getCommonUtilsInstance().isUserLogin()){
            CommonUtils.getCommonUtilsInstance().loginAlert(ContactUsActivity.this,false,"");
        }else {
            if(getIssueId == null){
                ShowToast.infoToast(ContactUsActivity.this,getString(R.string.select_issue_first));
                return;
            }
            else if(TextUtils.isEmpty(contactUsBinding.etEnterUserIssue.getText().toString())){
                ShowToast.infoToast(ContactUsActivity.this,getString(R.string.enter_your_issue));
                return;
            }
            postContactUsQueryRequest(getIssueId,contactUsBinding.etEnterUserIssue.getText().toString());
        }


    }

    public void callUsOnClick(View view) {
        if(contactUsBottomSheet.getState() == BottomSheetBehavior.STATE_EXPANDED){
            contactUsBinding.shadow.setVisibility(View.GONE);
            contactUsBottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        else if(contactUsBottomSheet.getState() == BottomSheetBehavior.STATE_COLLAPSED){
            contactUsBinding.shadow.setVisibility(View.VISIBLE);
            contactUsBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }
}
