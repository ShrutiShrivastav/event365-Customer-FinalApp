package com.ebabu.event365live.service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.utils.SessionValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFireBaseNotificationService extends FirebaseMessagingService {
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
}
