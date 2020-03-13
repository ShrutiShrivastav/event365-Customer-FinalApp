package com.ebabu.event365live.auth.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityOtpVerificationBinding;
import com.ebabu.event365live.homedrawer.activity.ChooseRecommendedCatActivity;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.userinfo.fragment.UpdateInfoFragmentDialog;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.ebabu.event365live.utils.Utility;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;

public class OtpVerificationActivity extends AppCompatActivity implements GetResponseData {
    private ActivityOtpVerificationBinding verificationBinding;
    private String activityName, mobileNo;
    private MyLoader myLoader;
    private UpdateInfoFragmentDialog infoFragmentDialog;
    private boolean isFromLogin;
    private String getUserName, getUserEmail,countryCode;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verificationBinding = DataBindingUtil.setContentView(this, R.layout.activity_otp_verification);
        Bundle bundle = getIntent().getExtras();
        myLoader = new MyLoader(this);
        userId = Integer.parseInt(CommonUtils.getCommonUtilsInstance().getUserId());
        if (bundle != null) {
            activityName = bundle.getString("activityName");
            if (activityName != null) {
                if (activityName.equalsIgnoreCase(getString(R.string.is_from_register_activity)) ||
                        activityName.equalsIgnoreCase(getString(R.string.is_from_Forgot_pass_activity)) ||
                        activityName.equalsIgnoreCase(getString(R.string.is_from_login_activity))) {
                    verificationBinding.ivShowVerificationTitle.setText(getString(R.string.verify_your_email));
                    verificationBinding.ivShowOtpInstruction.setText(getString(R.string.enter_4_digit_code));

                    getUserName = bundle.getString(Constants.SharedKeyName.userName);
                    getUserEmail = bundle.getString(Constants.SharedKeyName.userEmail);
                    isFromLogin = false;
                } else if (activityName.equalsIgnoreCase(getString(R.string.is_from_update_dialog_fragment)) ||
                        activityName.equalsIgnoreCase(getString(R.string.isFromProfileActivity)) ||
                        activityName.equalsIgnoreCase(getString(R.string.isFromSettingsActivity))) {
                    mobileNo = bundle.getString(Constants.ApiKeyName.phoneNo);
                    countryCode = bundle.getString(Constants.ApiKeyName.countryCode);

                    isFromLogin = true;
                    if(mobileNo != null){
                        if(mobileNo.contains(" ")){
                            mobileNo = mobileNo.replace(" ","");
                        }
                    }
                    verificationBinding.ivShowVerificationTitle.setText(getString(R.string.verify_code));
                    verificationBinding.ivShowOtpInstruction.setText(getString(R.string.please_enter_otp));
                }
            }
        }

        verificationBinding.otpView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 4)
                    Utility.hideKeyboardFrom(OtpVerificationActivity.this);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        countDown();


    }
    public void backBtnOnClick(View view) {
        finish();
    }

    private void countDown() {
        int DELAY_TIME = 30000;
        int DELAY_PERIOD = 1000;

        new CountDownTimer(DELAY_TIME, DELAY_PERIOD) {
            @Override
            public void onTick(long l) {
                verificationBinding.ivShowTimeCount.setText(String.valueOf("Resend Code in : 0" + l / 1000));
            }

            @Override
            public void onFinish() {
                verificationBinding.ivShowTimeCount.setText(String.valueOf("Resend Code"));

            }
        }.start();
    }

    public void resendOtpOnClick(View view) {

        if (isFromLogin) {
            if (verificationBinding.ivShowTimeCount.getText().toString().equalsIgnoreCase("Resend Code")) {
                regenerateOTP();
            }
            return;
        }
        if (verificationBinding.ivShowTimeCount.getText().toString().equalsIgnoreCase("Resend Code")) {
            resendEmailOtpRequest();
        }
    }

    private void verifyEmailOtp() {
        myLoader.show("Verifying...");
        JsonObject verifyOtp = new JsonObject();
        verifyOtp.addProperty(Constants.ApiKeyName.userId, userId);
        verifyOtp.addProperty(Constants.ApiKeyName.otp, verificationBinding.otpView.getText().toString());

        Call<JsonElement> emailVerifyObj = APICall.getApiInterface().emailOtpVerify(verifyOtp);
        new APICall(OtpVerificationActivity.this).apiCalling(emailVerifyObj, this, APIs.EMAIL_OTP_VERIFY);
    }

    private void verifyPhoneOtp() {
        myLoader.show("Verifying...");

        JsonObject verifyOtp = new JsonObject();
        verifyOtp.addProperty(Constants.ApiKeyName.userId,userId);
        verifyOtp.addProperty(Constants.ApiKeyName.countryCode,countryCode);
        verifyOtp.addProperty(Constants.ApiKeyName.phoneNo,mobileNo);
        verifyOtp.addProperty(Constants.ApiKeyName.otp, verificationBinding.otpView.getText().toString());

        Call<JsonElement> phoneCallObj = APICall.getApiInterface().phoneOtpVerify(verifyOtp);
        new APICall(OtpVerificationActivity.this).apiCalling(phoneCallObj, this, APIs.PHONE_OTP_VERIFY);
    }

    public void otpVerifyOnClick(View view){
            if (verificationBinding.otpView.getText() != null && verificationBinding.otpView.getText().length() == 4){
            if (!isFromLogin) {
                if(activityName.equalsIgnoreCase(getString(R.string.is_from_Forgot_pass_activity))){
                    resetPassRequest();
                    return;
                }
                verifyEmailOtp();
            } else
                verifyPhoneOtp();
        } else
            ShowToast.infoToast(OtpVerificationActivity.this, "Please enter valid OTP");
    }

    private void navigateToRecommendedCategorySelect() {
        Intent catIntent = new Intent(OtpVerificationActivity.this,ChooseRecommendedCatActivity.class);
        catIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(catIntent);
        finish();
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        ShowToast.successToast(this, message);
        countDown();
        if (responseObj != null) {
            if (typeAPI.equalsIgnoreCase(APIs.RESEND_OTP)) {
                ShowToast.infoToast(OtpVerificationActivity.this, message);
            } else if (typeAPI.equalsIgnoreCase(APIs.RESEND_EMAIL_CODE)) {
                ShowToast.infoToast(OtpVerificationActivity.this, message);
            } else if (typeAPI.equalsIgnoreCase(APIs.PHONE_OTP_VERIFY)) {
                if(activityName.equalsIgnoreCase(getString(R.string.isFromProfileActivity))){
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK,intent);
                    finish();
                    return;
                }
                navigateToRecommendedCategorySelect();

            }else if(typeAPI.equalsIgnoreCase(APIs.RESET_PW)){
                ShowToast.successToast(OtpVerificationActivity.this,getString(R.string.please_enter_new_pass));
                navigateToResetPassScreen();
            }else if(typeAPI.equalsIgnoreCase(APIs.EMAIL_OTP_VERIFY)){
                launchUpdateProfileFragment();
            }
        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        Log.d("nfklasnlan", "onFailed: "+errorBody.toString());
        myLoader.dismiss();
        if(!TextUtils.isEmpty(verificationBinding.otpView.getText().toString())) {
            verificationBinding.otpView.getText().clear();
        }
       if(typeAPI.equalsIgnoreCase(APIs.RESET_PW) && activityName.equalsIgnoreCase(getString(R.string.is_from_Forgot_pass_activity))){
            ShowToast.infoToast(this, message);

        }else if(activityName.equalsIgnoreCase(getString(R.string.isFromSettingsActivity))){
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK);
            startActivity(intent);
            finish();
        }else if(errorCode == APIs.NEED_PROFILE_UPDATE){
            try {
                JSONObject object = errorBody.getJSONObject("data");
                String userName = object.getString("name");
                String userEmail = object.getString("email");
                getUserName = userName;
                getUserEmail = userEmail;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ShowToast.infoToast(this, message);
            launchUpdateProfileFragment();
        }
       else if(errorCode == APIs.OTHER_FAILED){
           ShowToast.infoToast(this, message);

       }
    }


    public void launchUpdateProfileFragment() {
        if (infoFragmentDialog == null) {
            infoFragmentDialog = new UpdateInfoFragmentDialog();
        }
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SharedKeyName.userName,getUserName);
        bundle.putString(Constants.SharedKeyName.userEmail,getUserEmail);
        infoFragmentDialog.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        infoFragmentDialog.show(fragmentTransaction, UpdateInfoFragmentDialog.TAG);
    }

    private void regenerateOTP() {
        verificationBinding.otpView.setText("");
        myLoader.show(getString(R.string.please_wait));
        JsonObject getMobNoObj = new JsonObject();
        getMobNoObj.addProperty(Constants.ApiKeyName.userId, userId);
        getMobNoObj.addProperty(Constants.ApiKeyName.countryCode, countryCode);
        getMobNoObj.addProperty(Constants.ApiKeyName.phoneNo, mobileNo);

        Call<JsonElement> generateCallObj = APICall.getApiInterface().resendPhoneOtp(getMobNoObj);
        new APICall(OtpVerificationActivity.this).apiCalling(generateCallObj, this, APIs.RESEND_OTP);

    }

    private void resendEmailOtpRequest() {
        verificationBinding.otpView.setText("");
        myLoader.show("Sending...");
        JsonObject emailObj = new JsonObject();
        emailObj.addProperty(Constants.ApiKeyName.email, getUserEmail);

        Call<JsonElement> getEmailObj = APICall.getApiInterface().resendEmailCode(emailObj);
        new APICall(OtpVerificationActivity.this).apiCalling(getEmailObj, this, APIs.RESEND_EMAIL_CODE);
    }

    private void navigateToResetPassScreen(){
        Intent resetIntent = new Intent(OtpVerificationActivity.this, ResetPassActivity.class);
        resetIntent.putExtra(Constants.SharedKeyName.userEmail,getUserEmail);
        startActivity(resetIntent);
    }


    private void resetPassRequest() {
        myLoader.show("Verifying...");
        JsonObject verifyOtp = new JsonObject();
        verifyOtp.addProperty(Constants.ApiKeyName.userId, CommonUtils.getCommonUtilsInstance().getUserId());
        verifyOtp.addProperty(Constants.ApiKeyName.otp, verificationBinding.otpView.getText().toString());
        verifyOtp.addProperty(Constants.ApiKeyName.email, getUserEmail);

        Call<JsonElement> emailVerifyObj = APICall.getApiInterface().resendOTP(verifyOtp);
        new APICall(OtpVerificationActivity.this).apiCalling(emailVerifyObj, this, APIs.RESET_PW);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!TextUtils.isEmpty(verificationBinding.otpView.getText().toString())) {
            verificationBinding.otpView.getText().clear();
        }
    }
}
