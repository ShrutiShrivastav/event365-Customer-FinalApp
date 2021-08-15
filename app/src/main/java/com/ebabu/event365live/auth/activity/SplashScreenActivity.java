package com.ebabu.event365live.auth.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ebabu.event365live.R;
import com.ebabu.event365live.home.activity.HomeActivity;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.oncelaunch.LandingActivity;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.SessionValidation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

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

       /* ((Button)findViewById(R.id.Click)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                move();
            }
        });*/

        move();

    }

    private void move() {
        new Handler().postDelayed(() -> {
            if(CommonUtils.getCommonUtilsInstance().isUserLogin() && SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.isHomeSwipeView) == null){
                navigateToLandingScreen();
                return;
            }else if(CommonUtils.getCommonUtilsInstance().isUserLogin() && SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.isHomeSwipeView) != null) {
                navigateToLandingScreen();
                return;
            }
            startActivity(new Intent(SplashScreenActivity.this, LandingActivity.class));
            finish();
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

}
