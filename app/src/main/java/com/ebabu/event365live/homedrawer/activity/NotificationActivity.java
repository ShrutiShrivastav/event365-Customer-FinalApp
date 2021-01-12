package com.ebabu.event365live.homedrawer.activity;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.widget.ViewPager2;

import com.ebabu.event365live.BaseActivity;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityNotificationBinding;
import com.ebabu.event365live.homedrawer.adapter.NotificationAdapter;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.utils.CommonUtils;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;

public class NotificationActivity extends BaseActivity {

    public static boolean isNotificationActivityLaunched = true;
    private ActivityNotificationBinding notificationBinding;
    private String eventCount, rsvpCount, transactionCount, organizationCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationBinding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
        notificationBinding.ivBackBtn.setOnClickListener(view -> finish());
        notificationBinding.ivEventImg.setOnClickListener(view -> finish());
        getNotificationCountRequest();
    }

    private void getNotificationCountRequest() {
        Call<JsonElement> elementCall = APICall.getApiInterface().notificationCount(CommonUtils.getCommonUtilsInstance().getDeviceAuth());
        new APICall(NotificationActivity.this).apiCalling(elementCall, new GetResponseData() {
            @Override
            public void onSuccess(JSONObject jsonObject1, String message, String typeAPI) {
                try {
                    eventCount = jsonObject1.getJSONObject("data").getJSONObject("eventCount").getString("count");
                    rsvpCount = jsonObject1.getJSONObject("data").getJSONObject("rsvpCount").getString("count");
                    transactionCount = jsonObject1.getJSONObject("data").getJSONObject("transactionCount").getString("count");
                    organizationCount = jsonObject1.getJSONObject("data").getJSONObject("organizationCount").getString("count");
                    setUpViewPager();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {

            }
        }, APIs.NOTIFICATION_COUNT);
    }

    private void setUpViewPager() {
        NotificationAdapter notificationAdapter = new NotificationAdapter(this);
        notificationBinding.viewPager.setAdapter(notificationAdapter);
        notificationBinding.viewPager.setOffscreenPageLimit(ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT);

        new TabLayoutMediator(notificationBinding.tabLayout, notificationBinding.viewPager,
                (tab, position) -> {
                    tab.setText(getResources().getStringArray(R.array.notification_tab)[position]);
                    switch (position) {
                        case 0:
                            if (Integer.parseInt(eventCount) > 0) {
                                BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                                badgeDrawable.setBackgroundColor(ContextCompat.getColor(this, R.color.redColor));
                                badgeDrawable.setVisible(true);
                                badgeDrawable.setNumber(Integer.parseInt(eventCount));
                            }
                            break;
                        case 1:
                            if (Integer.parseInt(rsvpCount) > 0) {
                                BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                                badgeDrawable.setBackgroundColor(ContextCompat.getColor(this, R.color.redColor));
                                badgeDrawable.setVisible(true);
                                badgeDrawable.setNumber(Integer.parseInt(rsvpCount));
                            }
                            break;
                        case 2:
                            if (Integer.parseInt(transactionCount) > 0) {
                                BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                                badgeDrawable.setBackgroundColor(ContextCompat.getColor(this, R.color.redColor));
                                badgeDrawable.setVisible(true);
                                badgeDrawable.setNumber(Integer.parseInt(transactionCount));
                            }
                            break;
                        case 3:
                            if (Integer.parseInt(organizationCount) > 0) {
                                BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                                badgeDrawable.setBackgroundColor(ContextCompat.getColor(this, R.color.redColor));
                                badgeDrawable.setVisible(true);
                                badgeDrawable.setNumber(Integer.parseInt(organizationCount));
                            }
                            break;
                    }
                }
        ).attach();

        notificationBinding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                BadgeDrawable badgeDrawable = notificationBinding.tabLayout.getTabAt(position).getOrCreateBadge();
                badgeDrawable.setVisible(false);
            }
        });
    }
}
