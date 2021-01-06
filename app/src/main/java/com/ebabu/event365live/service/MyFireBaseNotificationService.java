package com.ebabu.event365live.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.ebabu.event365live.R;
import com.ebabu.event365live.homedrawer.activity.RSVPTicketActivity;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.userinfo.activity.EventDetailsActivity;
import com.ebabu.event365live.utils.SessionValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class MyFireBaseNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("fnbklasnfkla", "onMessageReceived: "+remoteMessage.getData().toString());


        Map<String,String> data = remoteMessage.getData();
        String userId = data.get("userId");
        String eventId = data.get("eventId");
        String eventType = data.get("type");
        String message = data.get("message");
        String dateTime = data.get("DateTime");

        Log.d("fnklasnfklsa", eventType+"onMessageReceived: "+message);
        if(eventType != null){
            if(eventType.equalsIgnoreCase("Invited") || eventType.equalsIgnoreCase("eventOfInterest") || eventType.equalsIgnoreCase("eventRminder") || eventType.equalsIgnoreCase("eventFav") || eventType.equalsIgnoreCase("eventReview")){
                setNotification(message, EventDetailsActivity.class,Integer.parseInt(eventId));
            }else if(eventType.equalsIgnoreCase("ticketBooked") || eventType.equalsIgnoreCase("hostTicketBooked") ){
                setNotification(message, RSVPTicketActivity.class,Integer.parseInt(eventId));
            }
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>(){
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(task.isSuccessful()){
                    Log.d("nfklanfklnaslnfkla", "runsss: "+ task.getResult().getToken());
                    if(task.getResult() != null){
                        SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.deviceToken,task.getResult().getToken());
                    }
                }

            }
        });
    }
    private void setNotification(String msgBody, Class<?> getClassName,  int eventId){
        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(msgBody)
                        .setAutoCancel(true)
                        .setContentIntent(startPendingIntent(getClassName,eventId))
                        .setSound(defaultSoundUri);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.app_icon);
        } else {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        int when = (int) System.currentTimeMillis();
        notificationManager.notify(when, notificationBuilder.build());

    }

    private PendingIntent startPendingIntent(Class<?> className, int getEventId){
        Intent intent = new Intent(this, className);
        intent.putExtra(Constants.ApiKeyName.eventId,getEventId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
    }

}
