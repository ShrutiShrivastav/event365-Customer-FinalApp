package com.ebabu.event365live.checkout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityCheckoutBinding;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.stripe.GetEphemeralKey;
import com.ebabu.event365live.stripe.StripeConnect;
import com.ebabu.event365live.stripe.StripePaymentSessionListener;
import com.ebabu.event365live.ticketbuy.SelectTicketActivity;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.SessionValidation;
import com.ebabu.event365live.utils.ShowToast;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.stripe.android.CustomerSession;
import com.stripe.android.PaymentSession;
import com.stripe.android.PaymentSessionConfig;
import com.stripe.android.Stripe;
import com.stripe.android.StripeError;
import com.stripe.android.model.Customer;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.view.BillingAddressFields;
import com.stripe.android.view.PaymentMethodsActivityStarter;
import com.stripe.android.view.ShippingInfoWidget;

import org.jetbrains.annotations.NotNull;

import okhttp3.OkHttpClient;

public class CheckoutActivity extends AppCompatActivity {

    private ActivityCheckoutBinding checkoutBinding;
    private Context context;
    private OkHttpClient httpClient = new OkHttpClient();
    private String paymentIntentClientSecret;
    private PaymentSession paymentSession;
    private Stripe mStripe;
    private Customer customer;
    private String getPrice;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //   paymentSession.savePaymentSessionInstanceState(outState);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_checkout);
        context = this;

        getPrice = getIntent().getStringExtra("price");




    }






}



