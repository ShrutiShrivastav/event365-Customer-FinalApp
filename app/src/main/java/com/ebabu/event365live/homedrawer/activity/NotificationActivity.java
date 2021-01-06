package com.ebabu.event365live.homedrawer.activity;

import android.os.Bundle;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.ebabu.event365live.BaseActivity;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityNotificationBinding;
import com.ebabu.event365live.home.adapter.NotificationAdapter;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayoutMediator;

public class NotificationActivity extends BaseActivity {

    public static boolean isNotificationActivityLaunched = true;
    private ActivityNotificationBinding notificationBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationBinding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
        notificationBinding.ivBackBtn.setOnClickListener(view -> finish());
        setUpViewPager();
    }

    private void setUpViewPager() {
        NotificationAdapter notificationAdapter = new NotificationAdapter(this);
        notificationBinding.viewPager.setAdapter(notificationAdapter);

        new TabLayoutMediator(notificationBinding.tabLayout, notificationBinding.viewPager,
                (tab, position) -> {
                    tab.setText(getResources().getStringArray(R.array.notification_tab)[position]);
                    BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                    badgeDrawable.setBackgroundColor(ContextCompat.getColor(this, R.color.redColor));
                    badgeDrawable.setVisible(true);
                    badgeDrawable.setNumber(2);
                }
        ).attach();
    }
}
