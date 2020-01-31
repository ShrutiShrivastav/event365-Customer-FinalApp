package com.ebabu.event365live.auth.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivitySmsVerificationBinding;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.userinfo.fragment.UpdateInfoFragmentDialog;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hbb20.CountryCodePicker;

import org.json.JSONObject;

import retrofit2.Call;

public class SmsVerificationActivity extends AppCompatActivity implements GetResponseData {
    private ActivitySmsVerificationBinding smsVerificationBinding;
    private boolean isEnteredNoValid;
    private UpdateInfoFragmentDialog infoFragmentDialog;
    private MyLoader myLoader;
    private String enteredValidMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        smsVerificationBinding = DataBindingUtil.setContentView(this,R.layout.activity_sms_verification);
//        smsVerificationBinding.countryCodePicker.registerCarrierNumberEditText(smsVerificationBinding.etEnterMobile);
//        myLoader = new MyLoader(this);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String getPhoneNo = bundle.getString(Constants.ApiKeyName.phoneNo);
//            smsVerificationBinding.etEnterMobile.setText(getPhoneNo);
//            smsVerificationBinding.etEnterMobile.setSelection(getPhoneNo.length());

        }

//        smsVerificationBinding.countryCodePicker.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
//            @Override
//            public void onValidityChanged(boolean isValidNumber) {
//                if(isValidNumber)
//                {
//                    smsVerificationBinding.ivShowNameTick.setVisibility(View.VISIBLE);
//                    isEnteredNoValid = isValidNumber; }
//                else
//                {
//                    smsVerificationBinding.ivShowNameTick.setVisibility(View.INVISIBLE);
//                    isEnteredNoValid = isValidNumber; }
//            }
//        });
    }

    public void backBtnOnClick(View view) {
        finish();
    }

    public void skipBtnOnClick(View view) {

        if (infoFragmentDialog == null) {
            infoFragmentDialog = new UpdateInfoFragmentDialog();
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        infoFragmentDialog.show(fragmentTransaction, UpdateInfoFragmentDialog.TAG);
    }

    public void continueBtnOnClick(View view) {
//        enteredValidMobile = smsVerificationBinding.countryCodePicker.getFullNumberWithPlus();
//        if(isEnteredNoValid){
//            generateOtpRequest();
//            return;
//        }
        ShowToast.errorToast(this,getString(R.string.error_please_enter_valid_no));
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        ShowToast.successToast(SmsVerificationActivity.this,message);
        if(responseObj != null){
            Intent intent = new Intent(SmsVerificationActivity.this, OtpVerificationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
            intent.putExtra("activityName",getString(R.string.is_from_sms_verification_activity));
            intent.putExtra("mobile",enteredValidMobile);
            startActivity(intent);
        }
    }
    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        if(errorBody != null)
        {
            myLoader.dismiss();
            ShowToast.errorToast(SmsVerificationActivity.this,message);
        }
    }

    private void generateOtpRequest(){
        myLoader.show(getString(R.string.please_wait));
        JsonObject getMobNoObj = new JsonObject();
        getMobNoObj.addProperty(Constants.ApiKeyName.userId,Constants.userId);
        getMobNoObj.addProperty(Constants.ApiKeyName.phoneNo,enteredValidMobile);

        Log.d("fnalsknflas", "generateOtpRequest: "+Constants.userId);
        Call<JsonElement> generateCallObj = APICall.getApiInterface().phoneSendOtp(getMobNoObj);
        new APICall(SmsVerificationActivity.this).apiCalling(generateCallObj,this, APIs.PHONE_SEND_OTP);
    }
}
