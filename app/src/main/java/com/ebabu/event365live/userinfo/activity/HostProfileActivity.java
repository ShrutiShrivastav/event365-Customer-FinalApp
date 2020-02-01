package com.ebabu.event365live.userinfo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;

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
        List<String> hostDetails = new ArrayList<>();
        hostDetails.add(hostProfileData.getName());
        hostDetails.add(hostProfileData.getAddress());
        hostDetails.add(hostProfileData.getShortInfo());
        hostDetails.add(hostProfileData.getPhoneNo());
        hostDetails.add(hostProfileData.getCity());
        hostDetails.add(hostProfileData.getState());
        hostDetails.add(hostProfileData.getZip());
        hostDetails.add(hostProfileData.getEmail());
        hostDetails.add(hostProfileData.getURL());

        for(String getHostDetails: hostDetails){
            if(getHostDetails != null){
                View view = LayoutInflater.from(HostProfileActivity.this).inflate(R.layout.host_profile_details_layout,null,false);
                TextView tvHost = view.findViewById(R.id.tvHost);
                tvHost.setText(getHostDetails);
                hostProfileActivity.addViewContainer.addView(view);
            }
        }
        if(hostDetails.get(0) != null){
            hostProfileActivity.tvShowHostName.setText(hostDetails.get(0));
        }
        if(hostDetails.get(1) != null){
            hostProfileActivity.ivShowHostAdd.setText(hostDetails.get(1));
        }

        if(hostProfileData.getProfilePic() != null && !TextUtils.isEmpty(hostProfileData.getProfilePic())){
            hostProfileActivity.homeNameImgContainer.setVisibility(View.GONE);
            hostProfileActivity.ivShowUserImg.setVisibility(View.VISIBLE);
            Glide.with(HostProfileActivity.this).load(hostProfileData.getProfilePic()).placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(hostProfileActivity.ivShowUserImg);

        }else {
            hostProfileActivity.homeNameImgContainer.setVisibility(View.VISIBLE);
            hostProfileActivity.ivShowUserImg.setVisibility(View.GONE);
            hostProfileActivity.ivShowImgName.setText(CommonUtils.getCommonUtilsInstance().getHostName(hostProfileData.getName()));

            }
        }

    private void hostProfileInfoRequest(int hostId){
        myLoader.show("Please Wait...");
       Call<JsonElement> hostCallBack = APICall.getApiInterface().getHostProfileInfo(hostId);
       new APICall(HostProfileActivity.this).apiCalling(hostCallBack,this, APIs.GET_HOST_PROFILE_INFO);
    }

    public void onBackClick(View view) {
        finish();
    }

}
