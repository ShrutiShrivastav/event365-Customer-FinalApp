package com.ebabu.event365live.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.ebabu.event365live.R;
import com.ebabu.event365live.httprequest.Constants;
import com.google.android.gms.dynamic.DeferredLifecycleHelper;
import com.google.android.gms.dynamic.LifecycleDelegate;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.FirebaseApp;
import com.stripe.android.PaymentConfiguration;

public class MyApplication extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("fnasklnla", "onCreate: ");
        context = this;
        SessionValidation.init(MyApplication.this);
        FirebaseApp.initializeApp(this);
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
        CommonUtils.getCommonUtilsInstance().saveFilterDistance(500);
        CommonUtils.getCommonUtilsInstance().saveFilterAdmissionCost(4000);
    }



}
