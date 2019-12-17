package com.ebabu.event365live.auth.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.ebabu.event365live.R;
import com.ebabu.event365live.home.activity.HomeActivity;
import com.ebabu.event365live.utils.CommonUtils;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        initView();
    }
    private void initView()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateToHome();
            }
        },3000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CommonUtils.getCommonUtilsInstance().transparentStatusAndNavigation(this);
    }

    private void navigateToHome()
    {
        startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));
    }
}
