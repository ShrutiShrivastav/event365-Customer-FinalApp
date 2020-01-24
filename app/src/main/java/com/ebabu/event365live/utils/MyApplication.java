package com.ebabu.event365live.utils;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.ebabu.event365live.R;
import com.ebabu.event365live.auth.modal.LoginModal;
import com.ebabu.event365live.home.activity.HomeActivity;
import com.ebabu.event365live.home.modal.LoginViewModal;
import com.ebabu.event365live.homedrawer.activity.ChooseRecommendedCatActivity;
import com.ebabu.event365live.httprequest.Constants;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.view.PaymentMethodsActivity;

import java.io.IOException;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

public class MyApplication extends Application {

    private static MyApplication mMyApplicationInstance;
    private LoginViewModal loginViewModal;
    private String apiKey = "AIzaSyDnzMr8HJEL5gCdH8UnIEC0JrSugVsGysQ";

    @Override
    public void onCreate() {
        super.onCreate();
        SessionValidation.init(MyApplication.this);
        FirebaseApp.initializeApp(this);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        if(SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.deviceType) == null)
            SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.deviceType,"android");

        PaymentConfiguration.init(
                getApplicationContext(),
                getString(R.string.stripe_published_key)
        );

        ;


//        LoginViewModal mainViewModel = ViewModelProviders.of(this).get(LoginViewModal.class);
//
//
//
//        LiveData<LoginModal> loginViewModalMutableLiveData = new MutableLiveData<>();
//        LoginModal loginViewModal = new LoginModal();
//        loginViewModal.setEventListSwipe(true);
//        loginViewModal.setUserLogin(false);
//         loginViewModalMutableLiveData.observe((LifecycleOwner) this,loginViewModal);


    }

    public LoginViewModal getLoginViewModal()
    {
        return loginViewModal;
    }

    public static MyApplication getMyAppInstance()
    {
        if(mMyApplicationInstance == null)
        {
            mMyApplicationInstance = new MyApplication();
        }
        return mMyApplicationInstance;
    }

//    private void getFirebaseToken(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//
//
//
//            }
//        }).start();
//    }


}
