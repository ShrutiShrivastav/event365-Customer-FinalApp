package com.ebabu.event365live.userinfo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityHostProfileBinding;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.userinfo.modal.hostmodal.HostProfileModal;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import retrofit2.Call;

public class HostProfileActivity extends AppCompatActivity implements GetResponseData {
    private MyLoader myLoader;
    private ActivityHostProfileBinding hostProfileActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hostProfileActivity = DataBindingUtil.setContentView(this,R.layout.activity_host_profile);
        myLoader = new MyLoader(this);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            int getHostId = bundle.getInt(Constants.hostId);
            Log.d("nflkankfnals", "onCreate: "+getHostId);
            hostProfileInfoRequest(getHostId);

        }
    }
    public void ChatOnClick(View view) {
        ShowToast.infoToast(HostProfileActivity.this,getString(R.string.on_progress));
    }

    public void backBtnOnClick(View view) {
        finish();
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        hostProfileActivity.hostRootContainer.setVisibility(View.VISIBLE);
        if(responseObj != null){
            HostProfileModal hostProfileModal = new Gson().fromJson(responseObj.toString(), HostProfileModal.class);
            setHostUserProfileData(hostProfileModal.getHostProfileData());
        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        ShowToast.errorToast(HostProfileActivity.this,message);
        hostProfileActivity.hostRootContainer.setVisibility(View.GONE);
        finish();
    }

    private void setHostUserProfileData(HostProfileModal.HostProfileData hostProfileData){
        hostProfileActivity.tvShowHostName.setText(hostProfileData.getName());
        hostProfileActivity.ivShowHostAdd.setText(hostProfileData.getAddress());
        hostProfileActivity.tvShowHostAbout.setText(hostProfileData.getShortInfo());
        hostProfileActivity.tvShowHostPhone.setText(hostProfileData.getPhoneNo());
        hostProfileActivity.tvShowHostCity.setText(hostProfileData.getCity());
        hostProfileActivity.tvShowHostState.setText(hostProfileData.getState());
        hostProfileActivity.tvShowHostZip.setText(hostProfileData.getZip());
        hostProfileActivity.tvShowHostEmailId.setText(hostProfileData.getEmail());
        hostProfileActivity.tvShowHostUrl.setText(hostProfileData.getURL());
        Glide.with(HostProfileActivity.this).load(hostProfileData.getProfilePic()).into(hostProfileActivity.ivShowHostUserImg);

    }

    private void hostProfileInfoRequest(int hostId){
        myLoader.show("Please Wait...");
       Call<JsonElement> hostCallBack = APICall.getApiInterface().getHostProfileInfo(hostId);
       new APICall(HostProfileActivity.this).apiCalling(hostCallBack,this, APIs.GET_HOST_PROFILE_INFO);
    }
}
