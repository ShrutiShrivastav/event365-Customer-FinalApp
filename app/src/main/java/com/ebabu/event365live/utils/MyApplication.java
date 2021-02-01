package com.ebabu.event365live.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.ebabu.event365live.R;
import com.ebabu.event365live.httprequest.Constants;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.FirebaseApp;
import com.splunk.mint.Mint;
import com.stripe.android.PaymentConfiguration;

public class MyApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("fnasklnla", "onCreate: ");
        context = this;
        SessionValidation.init(MyApplication.this);
        FirebaseApp.initializeApp(this);

        try {
            Mint.setApplicationEnvironment(Mint.appEnvironmentRelease);
            Mint.initAndStartSession(this, "ecfd2ff1");
        } catch (Exception e) {
            e.printStackTrace();
        }

        init();
        if (!Places.isInitialized()) {
            String apiKey = "AIzaSyDnzMr8HJEL5gCdH8UnIEC0JrSugVsGysQ";
            Places.initialize(getApplicationContext(), apiKey);
        }
        if (SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.deviceType) == null)
            SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.deviceType, "android");

        PaymentConfiguration.init(
                getApplicationContext(),
                getString(R.string.stripe_published_key));

    }

    private void init(){
        Utility.getThisWeekDate();
        CommonUtils.getCommonUtilsInstance().validateSwipeMode(true);
        CommonUtils.getCommonUtilsInstance().saveEventDate(2);
        CommonUtils.getCommonUtilsInstance().saveFilterDistance(1000);
//        CommonUtils.getCommonUtilsInstance().saveFilterAdmissionCost(0);
    }

}
