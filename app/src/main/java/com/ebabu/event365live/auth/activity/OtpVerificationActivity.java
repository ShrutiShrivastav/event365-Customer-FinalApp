package com.ebabu.event365live.auth.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
    private String getUserName, getUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verificationBinding = DataBindingUtil.setContentView(this, R.layout.activity_otp_verification);
        Bundle bundle = getIntent().getExtras();
        myLoader = new MyLoader(this);

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
        verifyOtp.addProperty(Constants.ApiKeyName.userId, CommonUtils.getCommonUtilsInstance().getUserId());
        verifyOtp.addProperty(Constants.ApiKeyName.otp, verificationBinding.otpView.getText().toString());

        Call<JsonElement> emailVerifyObj = APICall.getApiInterface().emailOtpVerify(verifyOtp);
        new APICall(OtpVerificationActivity.this).apiCalling(emailVerifyObj, this, APIs.EMAIL_OTP_VERIFY);
    }

    private void verifyPhoneOtp() {
        myLoader.show("Verifying...");
        JsonObject verifyOtp = new JsonObject();
        verifyOtp.addProperty(Constants.ApiKeyName.userId,CommonUtils.getCommonUtilsInstance().getUserId());
        verifyOtp.addProperty(Constants.ApiKeyName.phoneNo, mobileNo);
        verifyOtp.addProperty(Constants.ApiKeyName.otp, verificationBinding.otpView.getText().toString());

        Call<JsonElement> phoneCallObj = APICall.getApiInterface().phoneOtpVerify(verifyOtp);
        new APICall(OtpVerificationActivity.this).apiCalling(phoneCallObj, this, APIs.PHONE_OTP_VERIFY);
    }

    public void otpVerifyOnClick(View view) {

        Log.d("fnlaknfa", "otpVerifyOnClick: "+CommonUtils.getCommonUtilsInstance().getUserId());
            if (verificationBinding.otpView.getText() != null && verificationBinding.otpView.getText().length() == 4) {
            if (!isFromLogin) {
                verifyEmailOtp();
            } else {
                verifyPhoneOtp();
            }
        } else
            ShowToast.errorToast(OtpVerificationActivity.this, "Please enter valid OTP");
    }

    private void navigateToRecommendedCategorySelect() {
        Intent catIntent = new Intent(OtpVerificationActivity.this,ChooseRecommendedCatActivity.class);
        catIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
                navigateToRecommendedCategorySelect();
            }
        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        if(activityName.equalsIgnoreCase(getString(R.string.is_from_Forgot_pass_activity))){
            ShowToast.infoToast(this, getString(R.string.please_reset_pass));
            navigateToResetPassScreen();
        }else if(activityName.equalsIgnoreCase(getString(R.string.isFromSettingsActivity))){
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK);
            startActivity(intent);
            finish();
        } else if(errorCode == APIs.NEED_PROFILE_UPDATE){
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
        }else if(errorCode == APIs.OTHER_FAILED){
            ShowToast.infoToast(this, message);
        }
    }

    private void navigateToLogin() {
        Intent loginIntent = new Intent(OtpVerificationActivity.this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
        finish();
    }

    private void navigateToPhoneVerify() {
        Intent phoneIntent = new Intent(OtpVerificationActivity.this, SmsVerificationActivity.class);
        phoneIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(phoneIntent);
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
        fragmentTransaction.addToBackStack(null);
        infoFragmentDialog.show(fragmentTransaction, UpdateInfoFragmentDialog.TAG);
    }

    private void regenerateOTP() {
        verificationBinding.otpView.setText("");
        myLoader.show(getString(R.string.please_wait));
        JsonObject getMobNoObj = new JsonObject();
        getMobNoObj.addProperty(Constants.ApiKeyName.userId, CommonUtils.getCommonUtilsInstance().getUserId());
        getMobNoObj.addProperty(Constants.ApiKeyName.phoneNo, mobileNo);

        Call<JsonElement> generateCallObj = APICall.getApiInterface().resendOTP(getMobNoObj);
        new APICall(OtpVerificationActivity.this).apiCalling(generateCallObj, this, APIs.RESEND_OTP);
    }

    private void resendEmailOtpRequest() {

        verificationBinding.otpView.setText("");
        if (verificationBinding.otpView.getText() != null && verificationBinding.otpView.getText().length() == 4) {

            myLoader.show("Sending...");
            JsonObject emailObj = new JsonObject();
            emailObj.addProperty(Constants.ApiKeyName.email, getUserEmail);

            Call<JsonElement> getEmailObj = APICall.getApiInterface().resendEmailCode(emailObj);
            new APICall(OtpVerificationActivity.this).apiCalling(getEmailObj, this, APIs.RESEND_EMAIL_CODE);
        }
//    private void navigateToRecommendedCategorySelect() {
//        Intent recommendedIntent = new Intent(OtpVerificationActivity.this, ChooseRecommendedCatActivity.class);
//        recommendedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(recommendedIntent);
//    }


    }

    private void navigateToResetPassScreen(){
        Intent resetIntent = new Intent(OtpVerificationActivity.this, ResetPassActivity.class);
        resetIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        resetIntent.putExtra(Constants.SharedKeyName.userEmail,getUserEmail);
        startActivity(resetIntent);
        finish();
    }
}
