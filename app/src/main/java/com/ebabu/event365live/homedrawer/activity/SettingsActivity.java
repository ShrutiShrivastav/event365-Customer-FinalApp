package com.ebabu.event365live.homedrawer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.ebabu.event365live.R;
import com.ebabu.event365live.auth.activity.ChangePassActivity;
import com.ebabu.event365live.auth.activity.LoginActivity;
import com.ebabu.event365live.databinding.ActivitySettingsBinding;
import com.ebabu.event365live.homedrawer.fragment.WebViewDialogFragment;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.ApiClient;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.userinfo.activity.ProfileActivity;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.SessionValidation;
import com.ebabu.event365live.utils.ShowToast;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;

public class SettingsActivity extends AppCompatActivity implements GetResponseData {
    private ActivitySettingsBinding settingsBinding;
    MyLoader myLoader;
    private WebViewDialogFragment webViewDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        myLoader = new MyLoader(this);
        validateSettings();
        settingsBinding.switchNotificationReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int type = 1;
                if (b) {
                    type = 1;
                    notificationReminderRequest(type, b);
                    return;
                }
                notificationReminderRequest(type, b);
            }
        });
    }

    public void changePassOnClick(View view) {
        Intent launchProfile = new Intent(SettingsActivity.this, ChangePassActivity.class);
        launchProfile.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(launchProfile);
    }

    public void editProfileOnClick(View view) {
        Intent launchProfile = new Intent(SettingsActivity.this, ProfileActivity.class);
        launchProfile.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(launchProfile);
    }

    public void backBtnOnClick(View view) {
        finish();
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        Log.d("nflanflnakfnkas", "onSuccess: " + responseObj);
        myLoader.dismiss();
        if (responseObj != null) {
            if (typeAPI.equalsIgnoreCase(APIs.USER_LOGOUT)) {

                if (CommonUtils.getCommonUtilsInstance().isUserLogin()) {
                    CommonUtils.getCommonUtilsInstance().deleteUser(SettingsActivity.this);
                    ShowToast.successToast(SettingsActivity.this, message);
                    Intent logoutIntent = new Intent(SettingsActivity.this, LoginActivity.class);
                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(logoutIntent);
                    finish();
                }
            }
        }
    }
    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        ShowToast.errorToast(SettingsActivity.this, message);

    }

    private void notificationReminderRequest(int type, boolean value) {
        myLoader.show("");
        JsonObject reminderObj = new JsonObject();
        reminderObj.addProperty(APIs.TYPE, type);
        reminderObj.addProperty(APIs.VALUE, value);
        String token = SessionValidation.getPrefsHelper().getPref(APIs.AUTHORIZATION);
        Call<JsonElement> jsonElementCall = APICall.getApiInterface().notificationReminder("Bearer " + token, reminderObj);
        new APICall(SettingsActivity.this).apiCalling(jsonElementCall, this, APIs.NOTIFICATION_REMINDER);
    }

    public void logoutOnClick(View view) {
        userLogoutRequest();
    }

    public void policyOnClick(View view) {
        launchWebViewDialog(1);
    }

    public void termsConditionOnClick(View view) {
        launchWebViewDialog(0);
    }


    public void launchWebViewDialog(int flag) {
        if (webViewDialogFragment == null) {
            webViewDialogFragment = new WebViewDialogFragment();
        }
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.flag, flag);
        webViewDialogFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        webViewDialogFragment.show(fragmentTransaction, WebViewDialogFragment.TAG);

    }

    private void userLogoutRequest() {
        myLoader.show("");
        Call<JsonElement> userLogout = APICall.getApiInterface().userLogout(CommonUtils.getCommonUtilsInstance().getDeviceAuth());
        new APICall(SettingsActivity.this).apiCalling(userLogout, this, APIs.USER_LOGOUT);
    }

    public void shareOnClick(View view) {
        CommonUtils.getCommonUtilsInstance().shareIntent(SettingsActivity.this);
    }

    private void validateSettings() {
        boolean isRemind = SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.isRemind);
        boolean isNotify = SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.isNotify);
        if (isRemind) {
            settingsBinding.switchEventReminder.setChecked(true);
        }
        if (isNotify) {
            settingsBinding.switchNotificationReminder.setChecked(true);
        }
    }
}
