package com.ebabu.event365live.homedrawer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityNotificationBinding;
import com.ebabu.event365live.homedrawer.adapter.NotificationListAdapter;

public class NotificationActivity extends AppCompatActivity {

    private ActivityNotificationBinding notificationBinding;
    private NotificationListAdapter notificationListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationBinding = DataBindingUtil.setContentView(this,R.layout.activity_notification);
        setupNotificationList();
    }
    private void setupNotificationList(){
        notificationListAdapter = new NotificationListAdapter(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        notificationBinding.recyclerNotificationList.setLayoutManager(manager);
        notificationBinding.recyclerNotificationList.setAdapter(notificationListAdapter);
    }

    public void closeOnClick(View view) {
        finish();
    }
}
