package com.ebabu.event365live.auth.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;

import com.ebabu.event365live.BaseActivity;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityOtpVerificationBinding;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.userinfo.fragment.UpdateInfoFragmentDialog;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.SessionValidation;
import com.ebabu.event365live.utils.ShowToast;
import com.ebabu.event365live.utils.Utility;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import retrofit2.Call;

public class OtpVerificationActivity extends BaseActivity implements GetResponseData {

    private ActivityOtpVerificationBinding verificationBinding;
    private String activityName, mobileNo;
    private UpdateInfoFragmentDialog infoFragmentDialog;
    private boolean isFromLogin;
    private String getUserName, getUserEmail, countryCode;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verificationBinding = DataBindingUtil.setContentView(this, R.layout.activity_otp_verification);
        Bundle bundle = getIntent().getExtras();
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
                        activityName.equalsIgnoreCase(getString(R.string.isFromEventActivity)) ||
                        activityName.equalsIgnoreCase(getString(R.string.isFromSettingsActivity))) {
                    mobileNo = bundle.getString(Constants.ApiKeyName.phoneNo);
                    countryCode = bundle.getString(Constants.ApiKeyName.countryCode);

                    isFromLogin = true;
                    if (mobileNo != null) {
                        if (mobileNo.contains(" ")) {
                            mobileNo = mobileNo.replace(" ", "");
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
                if (s.length() == 4)
                    Utility.hideKeyboardFrom(OtpVerificationActivity.this);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getDeviceId();
        NetworkDetect();
        countDown();


    }

    public void backBtnOnClick(View view) {
        finish();
    }

    private void getDeviceId() {
        String device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("Android", "Android ID : " + device_id);
        SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.deviceId, device_id);
    }

    //Check the internet connection.
    private void NetworkDetect() {
        boolean WIFI = false;
        boolean MOBILE = false;
        ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = CM.getAllNetworkInfo();
        for (NetworkInfo netInfo : networkInfo) {
            if (netInfo.getTypeName().equalsIgnoreCase("WIFI"))
                if (netInfo.isConnected())
                    WIFI = true;
            if (netInfo.getTypeName().equalsIgnoreCase("MOBILE"))
                if (netInfo.isConnected())
                    MOBILE = true;
        }
        if (WIFI) {
            String IPaddress = GetDeviceipWiFiData();
            SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.sourceIp, IPaddress);
        }
        if (MOBILE) {
            String IPaddress = GetDeviceipMobileData();
            SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.sourceIp, IPaddress);
        }

    }

    public String GetDeviceipMobileData() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                NetworkInterface networkinterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = networkinterface.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("Current IP", ex.toString());
        }
        return null;
    }

    public String GetDeviceipWiFiData() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
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
        verifyOtp.addProperty(Constants.SharedKeyName.deviceToken, SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.deviceToken) == null ? FirebaseInstanceId.getInstance().getToken() : SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.deviceToken).toString());
        verifyOtp.addProperty(Constants.SharedKeyName.deviceType, SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.deviceType).toString());
        verifyOtp.addProperty("OS", "android");
        verifyOtp.addProperty("platform", "playstore");
        verifyOtp.addProperty("deviceId", SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.deviceId).toString());
        verifyOtp.addProperty("sourceIp", SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.sourceIp).toString());

        Call<JsonElement> emailVerifyObj = APICall.getApiInterface().emailOtpVerify(verifyOtp);
        new APICall(OtpVerificationActivity.this).apiCalling(emailVerifyObj, this, APIs.EMAIL_OTP_VERIFY);
    }

    private void verifyPhoneOtp() {
        myLoader.show("Verifying...");

        JsonObject verifyOtp = new JsonObject();
        verifyOtp.addProperty(Constants.ApiKeyName.userId, userId);
        verifyOtp.addProperty(Constants.ApiKeyName.countryCode, countryCode);
        verifyOtp.addProperty(Constants.ApiKeyName.phoneNo, mobileNo);
        verifyOtp.addProperty(Constants.ApiKeyName.otp, verificationBinding.otpView.getText().toString());
        verifyOtp.addProperty(Constants.SharedKeyName.deviceToken, SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.deviceToken) == null ? FirebaseInstanceId.getInstance().getToken() : SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.deviceToken).toString());
        verifyOtp.addProperty(Constants.SharedKeyName.deviceType, SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.deviceType).toString());
        verifyOtp.addProperty("OS", "android");
        verifyOtp.addProperty("platform", "playstore");
        verifyOtp.addProperty("deviceId", SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.deviceId).toString());
        verifyOtp.addProperty("sourceIp", SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.sourceIp).toString());

        Call<JsonElement> phoneCallObj = APICall.getApiInterface().phoneOtpVerify(verifyOtp);
        new APICall(OtpVerificationActivity.this).apiCalling(phoneCallObj, this, APIs.PHONE_OTP_VERIFY);
    }

    public void otpVerifyOnClick(View view) {
        if (verificationBinding.otpView.getText() != null && verificationBinding.otpView.getText().length() == 4) {
            if (!isFromLogin) {
                if (activityName.equalsIgnoreCase(getString(R.string.is_from_Forgot_pass_activity))) {
                    resetPassRequest();
                    return;
                }
                verifyEmailOtp();
            } else
                verifyPhoneOtp();
        } else
            ShowToast.infoToast(OtpVerificationActivity.this, "Please enter valid OTP");
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();

        countDown();
        if (responseObj != null) {
            if (typeAPI.equalsIgnoreCase(APIs.RESEND_OTP)) {
                ShowToast.infoToast(OtpVerificationActivity.this, message);
            } else if (typeAPI.equalsIgnoreCase(APIs.RESEND_EMAIL_CODE)) {
                ShowToast.infoToast(OtpVerificationActivity.this, message);
            } else if (typeAPI.equalsIgnoreCase(APIs.PHONE_OTP_VERIFY)) {
                if (activityName.equalsIgnoreCase(getString(R.string.isFromProfileActivity))) {
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    return;
                } else if (activityName.equalsIgnoreCase(getString(R.string.isFromEventActivity))) {
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    return;
                }
//                navigateToRecommendedCategorySelect();
            } else if (typeAPI.equalsIgnoreCase(APIs.RESET_PW)) {
                ShowToast.successToast(this, message);
                ShowToast.successToast(OtpVerificationActivity.this, getString(R.string.please_enter_new_pass));
                navigateToResetPassScreen();
            } else if (typeAPI.equalsIgnoreCase(APIs.EMAIL_OTP_VERIFY)) {
                ShowToast.successToast(this, message);
                launchUpdateProfileFragment();
            }
        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        Log.d("nfklasnlan", "onFailed: " + errorBody.toString());
        myLoader.dismiss();
        if (!TextUtils.isEmpty(verificationBinding.otpView.getText().toString())) {
            verificationBinding.otpView.getText().clear();
        }
        if (typeAPI.equalsIgnoreCase(APIs.RESET_PW) && activityName.equalsIgnoreCase(getString(R.string.is_from_Forgot_pass_activity))) {
            ShowToast.infoToast(this, message);

        } else if (activityName.equalsIgnoreCase(getString(R.string.isFromSettingsActivity))) {
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK);
            startActivity(intent);
            finish();
        } else if (errorCode == APIs.NEED_PROFILE_UPDATE) {
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
        } else if (errorCode == APIs.OTHER_FAILED) {
            ShowToast.infoToast(this, message);
        }
    }

    public void launchUpdateProfileFragment() {
        if (infoFragmentDialog == null) {
            infoFragmentDialog = new UpdateInfoFragmentDialog();
        }
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SharedKeyName.userName, getUserName);
        bundle.putString(Constants.SharedKeyName.userEmail, getUserEmail);
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

    private void navigateToResetPassScreen() {
        Intent resetIntent = new Intent(OtpVerificationActivity.this, ResetPassActivity.class);
        resetIntent.putExtra(Constants.SharedKeyName.userEmail, getUserEmail);
        startActivity(resetIntent);
        finish();
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
        if (!TextUtils.isEmpty(verificationBinding.otpView.getText().toString())) {
            verificationBinding.otpView.getText().clear();
        }
    }
}
