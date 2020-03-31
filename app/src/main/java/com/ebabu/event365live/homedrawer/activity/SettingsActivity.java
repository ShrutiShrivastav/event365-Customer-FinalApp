package com.ebabu.event365live.homedrawer.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;

import com.ebabu.event365live.R;
import com.ebabu.event365live.auth.activity.ChangePassActivity;
import com.ebabu.event365live.auth.activity.LoginActivity;
import com.ebabu.event365live.auth.activity.OtpVerificationActivity;
import com.ebabu.event365live.databinding.ActivitySettingsBinding;
import com.ebabu.event365live.home.activity.HomeActivity;
import com.ebabu.event365live.homedrawer.fragment.WebViewDialogFragment;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.ticketbuy.SelectTicketActivity;
import com.ebabu.event365live.userinfo.activity.ProfileActivity;
import com.ebabu.event365live.userinfo.modal.userdetails.GetUserDetailsModal;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;

public class SettingsActivity extends AppCompatActivity implements GetResponseData {
    private ActivitySettingsBinding settingsBinding;
    MyLoader myLoader;
    private WebViewDialogFragment webViewDialogFragment;
    private int eventReminderOrNotificationType;
    private boolean eventReminderClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        myLoader = new MyLoader(this);
        getNotifyOrReminderStatusRequest();
        settingsBinding.switchNotificationReminder.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            /*type 2 shows for notification*/
            eventReminderOrNotificationType = 2;
            eventReminderClicked = isChecked;
            if (isChecked) {
                notificationReminderRequest(eventReminderOrNotificationType, isChecked);
                return;
            }
            notificationReminderRequest(eventReminderOrNotificationType, isChecked);
        });

        settingsBinding.switchEventReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                /*type 1 shows for event reminder*/
                eventReminderOrNotificationType = 1;
                eventReminderClicked = isChecked;
                if (isChecked) {
                    notificationReminderRequest(eventReminderOrNotificationType, isChecked);
                    return;
                }
                notificationReminderRequest(eventReminderOrNotificationType, isChecked);
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
        if (responseObj != null) {
            if (typeAPI.equalsIgnoreCase(APIs.USER_LOGOUT)) {

                if (CommonUtils.getCommonUtilsInstance().isUserLogin()) {
                    CommonUtils.getCommonUtilsInstance().logoutAppLozic(SettingsActivity.this,isLogoutSuccess -> {
                        if(isLogoutSuccess){
                            myLoader.dismiss();
//                            ShowToast.successToast(SettingsActivity.this, message);
//                            Intent intent = new Intent();
//                            setResult(Activity.RESULT_OK,intent);

                            Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                            finish();
                        }else {
                            myLoader.dismiss();
                            ShowToast.errorToast(SettingsActivity.this,getString(R.string.something_wrong));
                        }
                    });
                }
            } else if (typeAPI.equalsIgnoreCase(APIs.GET_USER_DETAILS)) {
                myLoader.dismiss();
                GetUserDetailsModal detailsModal = new Gson().fromJson(responseObj.toString(), GetUserDetailsModal.class);
                settingsBinding.switchEventReminder.setChecked(detailsModal.getData().getIsRemind());
                settingsBinding.switchNotificationReminder.setChecked(detailsModal.getData().getIsNotify());
            } else if (typeAPI.equalsIgnoreCase(APIs.NOTIFICATION_REMINDER)) {
                myLoader.dismiss();
                if (eventReminderOrNotificationType == 1) {
                    settingsBinding.switchEventReminder.setChecked(eventReminderClicked);
                } else if (eventReminderOrNotificationType == 2) {
                    settingsBinding.switchNotificationReminder.setChecked(eventReminderClicked);
                }
            }
        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        ShowToast.errorToast(SettingsActivity.this, message);


    }

    private void notificationReminderRequest(int type, boolean isChecked) {
        myLoader.show("");
        JsonObject reminderObj = new JsonObject();
        reminderObj.addProperty(APIs.TYPE, type);
        reminderObj.addProperty(APIs.VALUE, isChecked);
        Call<JsonElement> jsonElementCall = APICall.getApiInterface().notificationReminder(CommonUtils.getCommonUtilsInstance().getDeviceAuth(), reminderObj);
        new APICall(SettingsActivity.this).apiCalling(jsonElementCall, this, APIs.NOTIFICATION_REMINDER);
    }

    public void logoutOnClick(View view) {
        logoutDialog();
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
        CommonUtils.getCommonUtilsInstance().shareIntent(SettingsActivity.this, "");
    }

    private void getNotifyOrReminderStatusRequest() {
        myLoader.show("");
        Call<JsonElement> userLogout = APICall.getApiInterface().getUserDetailsInfo(CommonUtils.getCommonUtilsInstance().getDeviceAuth());
        new APICall(SettingsActivity.this).apiCalling(userLogout, this, APIs.GET_USER_DETAILS);
    }

    private void logoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        View view = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.logout_layout, null, false);
        builder.setView(view);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        }
        dialog.show();

        view.findViewById(R.id.btnNo).setOnClickListener(v -> {
            dialog.dismiss();
        });

        view.findViewById(R.id.btnYes).setOnClickListener(v -> {
            userLogoutRequest();
            dialog.dismiss();
        });
    }

}
