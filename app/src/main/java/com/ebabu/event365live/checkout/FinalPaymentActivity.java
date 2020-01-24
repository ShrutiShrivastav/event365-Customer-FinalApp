package com.ebabu.event365live.checkout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ebabu.event365live.R;
import com.stripe.android.PaymentSession;
import com.stripe.android.PaymentSessionConfig;
import com.stripe.android.PaymentSessionData;
import com.stripe.android.model.Address;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.ShippingInformation;
import com.stripe.android.view.PaymentMethodsActivity;

public class FinalPaymentActivity extends AppCompatActivity {

    private PaymentSession paymentSession;
    private String name,phone;
    private Address address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_payment);

    }
    private void setupPaymentSession() {
    }
}
