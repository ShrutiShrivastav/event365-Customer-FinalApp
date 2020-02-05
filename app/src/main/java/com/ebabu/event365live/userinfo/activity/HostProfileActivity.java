package com.ebabu.event365live.userinfo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityHostProfileBinding;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.userinfo.modal.hostmodal.HostProfileModal;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import retrofit2.Call;

public class HostProfileActivity extends AppCompatActivity implements GetResponseData {
    private MyLoader myLoader;
    private ActivityHostProfileBinding hostProfileActivity;
    private String hostName;
    private Integer hostUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hostProfileActivity = DataBindingUtil.setContentView(this, R.layout.activity_host_profile);
        myLoader = new MyLoader(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int getHostId = bundle.getInt(Constants.hostId);
            Log.d("nflkankfnals", "onCreate: " + getHostId);
            hostProfileInfoRequest(getHostId);
        }
    }

    public void ChatOnClick(View view) {
        ShowToast.infoToast(HostProfileActivity.this, getString(R.string.on_progress));
    }

    public void backBtnOnClick(View view) {
        finish();
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        hostProfileActivity.hostRootContainer.setVisibility(View.VISIBLE);
        if (responseObj != null) {
            HostProfileModal hostProfileModal = new Gson().fromJson(responseObj.toString(), HostProfileModal.class);
            HostProfileModal.HostProfileData hostData = hostProfileModal.getHostProfileData();
            String profilePic = hostData.getProfilePic();
            hostUserId = hostData.getId();
            hostName = hostData.getName();
            String email = hostData.getEmail();
            String phone = hostData.getPhoneNo();
            String countryCode = hostData.getCountryCode();
            String url = hostData.getURL();
            String shortInfo = hostData.getShortInfo();
            String add = hostData.getAddress();
            String state = hostData.getState();
            String city = hostData.getCity();
            String zip = hostData.getZip();

            if (profilePic != null && !TextUtils.isEmpty(profilePic)) {
                hostProfileActivity.homeNameImgContainer.setVisibility(View.GONE);
                hostProfileActivity.ivShowUserImg.setVisibility(View.VISIBLE);
                Glide.with(HostProfileActivity.this).load(profilePic).placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(hostProfileActivity.ivShowUserImg);
            } else {
                hostProfileActivity.homeNameImgContainer.setVisibility(View.VISIBLE);
                hostProfileActivity.ivShowUserImg.setVisibility(View.GONE);
                hostProfileActivity.ivShowImgName.setText(CommonUtils.getCommonUtilsInstance().getHostName(hostName));
            }
            if (hostName != null && !TextUtils.isEmpty(hostName)) {
                hostProfileActivity.nameLayout.setVisibility(View.VISIBLE);
                hostProfileActivity.etEnterName.setText(hostName);
                hostProfileActivity.tvShowUserName.setText(hostName);
            }
            if (email != null && !TextUtils.isEmpty(email)) {
                hostProfileActivity.emailLayout.setVisibility(View.VISIBLE);
                hostProfileActivity.etEnterEmail.setText(email);
            }
            if (phone != null && !TextUtils.isEmpty(phone)) {
                hostProfileActivity.etEnterMobile.setVisibility(View.VISIBLE);
                hostProfileActivity.etEnterMobile.setText(phone);
                if (countryCode != null && !TextUtils.isEmpty(countryCode)) {
                    hostProfileActivity.countryCodePicker.setVisibility(View.VISIBLE);
                    hostProfileActivity.countryCodePicker.setCountryForPhoneCode(Integer.parseInt(countryCode));
                }
            }
            if (url != null && !TextUtils.isEmpty(url)) {
                hostProfileActivity.urlLayout.setVisibility(View.VISIBLE);
                hostProfileActivity.etEnterUrl.setText(url);
            }
            if (shortInfo != null && !TextUtils.isEmpty(shortInfo)) {
                hostProfileActivity.shorInfoLayout.setVisibility(View.VISIBLE);
                hostProfileActivity.etEnterInfo.setText(shortInfo);
            }
            if (add != null && !TextUtils.isEmpty(add)) {
                hostProfileActivity.addressLayout.setVisibility(View.VISIBLE);
                hostProfileActivity.etEnterAdd.setText(add);
            }
            if (state != null && !TextUtils.isEmpty(state)) {
                hostProfileActivity.stateLayout.setVisibility(View.VISIBLE);
                hostProfileActivity.etEnterState.setText(state);
            }
            if (city != null && !TextUtils.isEmpty(city)) {
                hostProfileActivity.cityLayout.setVisibility(View.VISIBLE);
                hostProfileActivity.etEnterCity.setText(city);
            }
            if (zip != null && !TextUtils.isEmpty(zip)) {
                hostProfileActivity.zipLayout.setVisibility(View.VISIBLE);
                hostProfileActivity.etEnterZip.setText(zip);
            }
        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        ShowToast.errorToast(HostProfileActivity.this, message);
        hostProfileActivity.hostRootContainer.setVisibility(View.GONE);
        finish();
    }

    private void hostProfileInfoRequest(int hostId) {
        myLoader.show("Please Wait...");
        Call<JsonElement> hostCallBack = APICall.getApiInterface().getHostProfileInfo(hostId);
        new APICall(HostProfileActivity.this).apiCalling(hostCallBack, this, APIs.GET_HOST_PROFILE_INFO);
    }

    public void onBackClick(View view) {
        finish();
    }

    public void chatOnClick(View view) {
        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtra(ConversationUIService.USER_ID, hostUserId);
        intent.putExtra(ConversationUIService.DISPLAY_NAME, hostName); //put it for displaying the title.
        
        startActivity(intent);
    }
}
