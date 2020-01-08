package com.ebabu.event365live.auth.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.ebabu.event365live.R;
import com.ebabu.event365live.home.activity.HomeActivity;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.oncelaunch.LandingActivity;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.SessionValidation;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        SessionValidation.getPrefsHelper().delete(Constants.currentLat);
        SessionValidation.getPrefsHelper().delete(Constants.currentLng);
        initView();
    }
    private void initView(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(CommonUtils.getCommonUtilsInstance().isUserLogin() && SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.isHomeSwipeView) == null){
                    navigateToLandingScreen();
                    return;
                }else if(CommonUtils.getCommonUtilsInstance().isUserLogin() && SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.isHomeSwipeView) != null) {
                    navigateToHomeScreen();
                    return;
                }
                startActivity(new Intent(SplashScreenActivity.this, LandingActivity.class));
                finish();
            }
        },3000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CommonUtils.getCommonUtilsInstance().transparentStatusAndNavigation(this);
    }

    private void navigateToLandingScreen(){
        Intent homeIntent = new Intent(SplashScreenActivity.this, LandingActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
        finish();
    }

    private void navigateToHomeScreen(){
        Intent homeIntent = new Intent(SplashScreenActivity.this, HomeActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
        finish();
    }
}
