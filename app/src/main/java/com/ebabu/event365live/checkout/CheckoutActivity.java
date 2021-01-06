package com.ebabu.event365live.checkout;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityCheckoutBinding;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.stripe.GetEphemeralKey;
import com.ebabu.event365live.stripe.PaymentMethodAdapter;
import com.ebabu.event365live.stripe.StripeConnect;
import com.ebabu.event365live.utils.SessionValidation;
import com.ebabu.event365live.utils.ShowToast;
import com.google.gson.Gson;
import com.stripe.android.CustomerSession;
import com.stripe.android.PaymentSession;
import com.stripe.android.Stripe;
import com.stripe.android.StripeError;
import com.stripe.android.model.Customer;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardMultilineWidget;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;

public class CheckoutActivity extends AppCompatActivity {

    private CompositeDisposable compositeDisposable;
    private PaymentMethodAdapter paymentMethodAdapter;
    private ActivityCheckoutBinding checkoutBinding;
    private Context context;
    private OkHttpClient httpClient = new OkHttpClient();
    private String paymentIntentClientSecret;
    private PaymentSession paymentSession;
    private Stripe mStripe;
    private Customer customer;
    private String getPrice;
    private CardMultilineWidget cardInputWidget;
    private PaymentMethodCreateParams paymentMethodCreateParams;
    List<PaymentMethod> paymentMethods = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_checkout);
//        cardInputWidget = checkoutBinding.cardInputWidget;
//        compositeDisposable = new CompositeDisposable();
//        context = this;
//        getPrice = getIntent().getStringExtra("price");
//        mStripe = StripeConnect.paymentAuth(this);

        createStripeSession();
    }

//    private void setupPaymentMethod(List<PaymentMethod> paymentMethodList){
//            checkoutBinding.tvPaymentMethodInfo.setVisibility(View.GONE);
//            checkoutBinding.showPaymentMethodRecycler.setVisibility(View.VISIBLE);
//            paymentMethodAdapter = new PaymentMethodAdapter(paymentMethodList);
//            checkoutBinding.showPaymentMethodRecycler.setAdapter(paymentMethodAdapter);
//
//
////        checkoutBinding.tvPaymentMethodInfo.setVisibility(View.VISIBLE);
////        checkoutBinding.showPaymentMethodRecycler.setVisibility(View.GONE);
//    }

    private void createStripeSession(){
        CustomerSession.initCustomerSession(this,new GetEphemeralKey(CheckoutActivity.this));
        CustomerSession.getInstance().getPaymentMethods(PaymentMethod.Type.Card,
                new CustomerSession.PaymentMethodsRetrievalListener() {
                    @Override
                    public void onError(int i, @NotNull String s, @org.jetbrains.annotations.Nullable StripeError stripeError) {
                        Log.d("fblablfa", "createStripeSession: "+s);
                    }

                    @Override
                    public void onPaymentMethodsRetrieved(@NotNull List<PaymentMethod> list) {
                        Log.d("fblablfa", "onPaymentMethodsRetrieved: "+list.size());
                     //   setupPaymentMethod(list);
                    }

                });



        CustomerSession.getInstance().retrieveCurrentCustomer(new CustomerSession.CustomerRetrievalListener() {
            @Override
            public void onError(int i, @NotNull String s, @org.jetbrains.annotations.Nullable StripeError stripeError) {
                ShowToast.errorToast(CheckoutActivity.this,s);
                Log.d("fblablfa", "onError: "+s);
            }

            @Override
            public void onCustomerRetrieved(@NotNull Customer customer) {
                try {
                    Log.d("fblablfa", "onCustomerRetrieved: ");

                    SessionValidation.getPrefsHelper().savePref(Constants.customer,new Gson().toJson(customer));

                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Log.d("fblablfa", "NullPointerException: "+e.getMessage());
                }
            }

        });

    }

    public void addPaymentMethodBtn(View view) {

        paymentMethodCreateParams = cardInputWidget.getPaymentMethodCreateParams();
        compositeDisposable.add(getPaymentMethodData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> {
                    Log.d("fnalsfnskla", "addPaymentMethodBtn: "+throwable.getMessage());
                })
                .subscribe( paymentMethod->    paymentMethodAdapter.updatePaymentList(paymentMethod)





                ));






    }

    private Observable<PaymentMethod> getPaymentMethodData(){
        Callable<PaymentMethod> callable = () -> mStripe.createPaymentMethodSynchronous(paymentMethodCreateParams);

        return Observable.fromCallable(callable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(compositeDisposable != null){
            compositeDisposable.dispose();
        }
    }
}



