package com.ebabu.event365live.userinfo.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.bumptech.glide.Glide;
import com.ebabu.event365live.BaseActivity;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityHostContactDetailBinding;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.userinfo.modal.hostmodal.HostProfileModal;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.ShowToast;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import retrofit2.Call;

public class HostContactDetailActivity extends BaseActivity implements GetResponseData {

    private ActivityHostContactDetailBinding binding;
    private String hostName, hostMobile, hostAddress, hostUrl;
    private Integer hostUserId;
    private String number = "", email = "", urlH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_host_contact_detail);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int getHostId = bundle.getInt(Constants.hostId);
            hostMobile = bundle.getString(Constants.HOST_MOBILE);
            hostAddress = bundle.getString(Constants.HOST_ADDRESS);
            hostUrl = bundle.getString(Constants.HOST_WEBSITE_URL);
            Log.d("nflkankfnals", "onCreate: " + getHostId);
            hostProfileInfoRequest(getHostId);
        }
    }

    public void backBtnOnClick(View view) {
        finish();
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        binding.hostRootContainer.setVisibility(View.VISIBLE);


        if (responseObj != null) {
            HostProfileModal hostProfileModal = new Gson().fromJson(responseObj.toString(), HostProfileModal.class);
            HostProfileModal.HostProfileData hostData = hostProfileModal.getHostProfileData();
            String profilePic = hostData.getProfilePic();
            hostUserId = hostData.getId();
            hostName = hostData.getName();
            email = hostData.getEmail();
            String phone = hostData.getPhoneNo();
            String countryCode = hostData.getCountryCode();
            String url = hostData.getURL();
            String shortInfo = hostData.getShortInfo();
            String add = hostData.getAddress();
            String state = hostData.getState();
            String city = hostData.getCity();
            String zip = hostData.getZip();

            if (profilePic != null && !TextUtils.isEmpty(profilePic)) {
                binding.homeNameImgContainer.setVisibility(View.GONE);
                binding.ivShowUserImg.setVisibility(View.VISIBLE);
                Glide.with(HostContactDetailActivity.this).load(profilePic).placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(binding.ivShowUserImg);
            } else {
                binding.homeNameImgContainer.setVisibility(View.VISIBLE);
                binding.ivShowUserImg.setVisibility(View.GONE);
                binding.ivShowImgName.setText(CommonUtils.getCommonUtilsInstance().getHostName(hostName));
            }
            if (hostName != null && !TextUtils.isEmpty(hostName)) {
                binding.tvShowUserName.setText(hostName);
            }
            if (email != null && !TextUtils.isEmpty(email)) {
                binding.layoutMail.setVisibility(View.VISIBLE);
                binding.tvEmail.setText(email);
            }

            if (hostMobile != null && !TextUtils.isEmpty(hostMobile)) {
                binding.layoutPhone.setVisibility(View.VISIBLE);
                binding.tvPhone.setText(hostMobile);
                number = hostMobile;
            } else if (phone != null && !TextUtils.isEmpty(phone)) {
                binding.layoutPhone.setVisibility(View.VISIBLE);
                if (countryCode != null){
                    binding.tvPhone.setText(countryCode + " " + phone);
                    number = countryCode + " " + phone;
                }else {
                    binding.tvPhone.setText(phone);
                    number = phone;
                }

            }

            if (hostUrl != null && !TextUtils.isEmpty(hostUrl)) {
                binding.layoutUrl.setVisibility(View.VISIBLE);
                binding.tvUrl.setText(hostUrl);
                urlH = hostUrl;
            } else if (url != null && !TextUtils.isEmpty(url)) {
                binding.layoutUrl.setVisibility(View.VISIBLE);
                binding.tvUrl.setText(url);
                urlH = url;
            }

            if (add != null && !TextUtils.isEmpty(add)) {
                binding.tvShowUserAddress.setVisibility(View.VISIBLE);
                binding.tvShowUserAddress.setText(add);
            }

            if (hostAddress != null && !TextUtils.isEmpty(hostAddress)) {
                binding.tvShowUserAddress.setVisibility(View.VISIBLE);
                binding.tvShowUserAddress.setText(hostAddress);
            }

            binding.layoutMail.setOnClickListener(view -> {
                openMail(email);
            });

            binding.layoutPhone.setOnClickListener(view -> {
                callMethod(number);
            });

            binding.layoutUrl.setOnClickListener(view -> {
                openUrl(hostUrl);
            });
        }
    }

    private void callMethod(String number) {

        if (!isCallingPermissionGiven()) {
            ActivityCompat.requestPermissions(HostContactDetailActivity.this, new String[]{Manifest.permission.CALL_PHONE}, Constants.CURRENT_FUSED_LOCATION_REQUEST);
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(intent);
        }
    }

    private void openMail(String mailAddress) {
        Intent emailIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + mailAddress));
        startActivity(emailIntent);
    }

    private void openUrl(String url) {
        try {
            Uri uri = Uri.parse("http://" + url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Uri uri = Uri.parse("https://" + url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private boolean isCallingPermissionGiven() {
        if (ContextCompat.checkSelfPermission(HostContactDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
            return false;
        return true;
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        ShowToast.errorToast(HostContactDetailActivity.this, message);
        binding.hostRootContainer.setVisibility(View.GONE);
        finish();
    }

    private void hostProfileInfoRequest(int hostId) {
        myLoader.show("Please Wait...");
        Call<JsonElement> hostCallBack = APICall.getApiInterface().getHostProfileInfo(hostId);
        new APICall(HostContactDetailActivity.this).apiCalling(hostCallBack, this, APIs.GET_HOST_PROFILE_INFO);
    }

    public void onBackClick(View view) {
        finish();
    }

    public void chatOnClick(View view) {
        if (CommonUtils.getCommonUtilsInstance().isUserLogin()) {
            Intent intent = new Intent(this, ConversationActivity.class);
            intent.putExtra(ConversationUIService.USER_ID, String.valueOf(hostUserId));
            intent.putExtra(ConversationUIService.DISPLAY_NAME, hostName); //put it for displaying the title.
            intent.putExtra(ConversationUIService.TAKE_ORDER, true);
            startActivity(intent);
            return;
        }
        CommonUtils.getCommonUtilsInstance().loginAlert(HostContactDetailActivity.this, false, "");

    }

    public void codePickerOnCLick(View view) {
    }


}