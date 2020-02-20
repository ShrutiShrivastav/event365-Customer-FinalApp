package com.ebabu.event365live.checkout;

import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.ebabu.event365live.R;
import com.ebabu.event365live.httprequest.APIs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Dem {

    private void startCheckout() {
        // Create a PaymentIntent by calling the sample server's /create-payment-intent endpoint.
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        String json = "{"
                + "\"currency\":\"usd\","
                + "\"items\":["
                + "{\"id\":\"photo_subscription\"}"
                + "]"
                + "}";
        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(APIs.BASE_URL + "create-payment-intent")
                .post(body)
                .build();

//        httpClient.newCall(request)
//                .enqueue(new PayCallback(this));

        // Hook up the pay button to the card widget and stripe instance

//        checkoutBinding.btnPayNow.setOnClickListener((View view) -> {
//            CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
//            PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
//            if (params != null) {
//                ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
//                        .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
//                stripe.confirmPayment(this, confirmParams);
//            }
//        });
    }



    private static final class PayCallback implements Callback {
        @NonNull
        private final WeakReference<CheckoutActivity> activityRef;
        PayCallback(@NonNull CheckoutActivity activity) {
            activityRef = new WeakReference<>(activity);
        }
        @Override
        public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
            final CheckoutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(() ->
                    Toast.makeText(
                            activity, "Error: " + e.toString(), Toast.LENGTH_LONG
                    ).show()
            );
        }

        @Override
        public void onResponse(@NotNull okhttp3.Call call, @NotNull Response response) throws IOException {
            final CheckoutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            if (!response.isSuccessful()) {
                activity.runOnUiThread(() ->
                        Toast.makeText(
                                activity, "Error: " + response.toString(), Toast.LENGTH_LONG
                        ).show()
                );
            } else {
               // activity.onPaymentSuccess(response);
            }
        }
    }




    private void onPaymentSuccess(@NonNull final Response response) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(),
                type
        );

        // The response from the server includes the Stripe publishable key and
        // PaymentIntent details.
        // For added security, our sample app gets the publishable key from the server
        String stripePublishableKey = responseMap.get("publishableKey");
       // paymentIntentClientSecret = responseMap.get("clientSecret");

        // Configure the SDK with your Stripe publishable key so that it can make requests to the Stripe API
//        stripe = new Stripe(
//                getApplicationContext(),
//                Objects.requireNonNull(stripePublishableKey)
//        );
    }

    private static final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        @NonNull private final WeakReference<CheckoutActivity> activityRef;

        PaymentResultCallback(@NonNull CheckoutActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final CheckoutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
//            if (status == PaymentIntent.Status.Succeeded) {
//                // Payment completed successfully
//                Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                activity.displayAlert(
//                        "Payment completed",
//                        gson.toJson(paymentIntent),
//                        true
//                );
//            }
//
//            else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
//                // Payment failed – allow retrying using a different payment method
//                activity.displayAlert(
//                        "Payment failed",
//                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage(),
//                        false
//                );
//            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final CheckoutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            // Payment request failed – allow retrying using the same payment method
           // activity.displayAlert("Error", e.toString(), false);
        }
    }

    private void displayAlert(@NonNull String title,
                              @Nullable String message,
                              boolean restartDemo) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this)
//                .setTitle(title)
//                .setMessage(message);
//        if (restartDemo) {
//            builder.setPositiveButton("Restart demo",
//                    (DialogInterface dialog, int index) -> {
//                        CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
//                        cardInputWidget.clear();
//                        startCheckout();
//                    });
//        } else {
//            builder.setPositiveButton("Ok", null);
//        }
//        builder.create().show();
    }


}
