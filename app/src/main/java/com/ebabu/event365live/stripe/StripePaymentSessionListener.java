package com.ebabu.event365live.stripe;

import android.util.Log;

import androidx.annotation.NonNull;

import com.stripe.android.PaymentSession;
import com.stripe.android.PaymentSessionData;
import com.stripe.android.model.PaymentMethod;

public class StripePaymentSessionListener implements PaymentSession.PaymentSessionListener {

    @Override
    public void onCommunicatingStateChanged(boolean isCommunicating) {
        if(isCommunicating){
            Log.d("fnaklsnflas", "onCommunicatingStateChanged: ");
        }else {
            Log.d("fnaklsnflas", "else: ");
        }

    }

    @Override
    public void onError(int errorCode, @NonNull String errorMessage) {
        Log.d("fnaklsnflas", "error: "+errorMessage);
    }

    @Override
    public void onPaymentSessionDataChanged(@NonNull PaymentSessionData data) {
        PaymentMethod paymentMethod = data.getPaymentMethod();
        if(paymentMethod != null){
            Log.d("fnaklsnflas", "paymentMethod: ");


        }

        if(data.isPaymentReadyToCharge()){
            Log.d("fnaklsnflas", "isPaymentReadyToCharge: ");
        }

    }
}
