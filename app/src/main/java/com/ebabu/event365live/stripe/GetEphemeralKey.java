package com.ebabu.event365live.stripe;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.ApiClient;
import com.ebabu.event365live.utils.CommonUtils;
import com.google.gson.JsonObject;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.stripe.android.EphemeralKeyProvider;
import com.stripe.android.EphemeralKeyUpdateListener;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class GetEphemeralKey implements EphemeralKeyProvider {

    BackendApi backendApi = ApiClient.getClient().create(BackendApi.class);
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void createEphemeralKey(
            @NonNull @Size(min = 4)  String apiVersion,
            @NonNull EphemeralKeyUpdateListener keyUpdateListener) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("api_version",apiVersion);
        jsonObject.addProperty("customer",CommonUtils.getCommonUtilsInstance().getStripeCustomerId());


        compositeDisposable.add(backendApi.createEphemeralKey(CommonUtils.getCommonUtilsInstance().getDeviceAuth(),jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            try {
                                final String rawKey = response.string();
                                JSONObject stripesRawJSON = new JSONObject(rawKey);
                                if(stripesRawJSON.has("data")){
                                    JSONObject getRawObj = stripesRawJSON.getJSONObject("data");
                                    keyUpdateListener.onKeyUpdate(getRawObj.toString());

                                    Log.d("fnalknfklsa", "createEphemeralKey: "+getRawObj.toString());
                                }

                            } catch (IOException ignored) {
                                Log.d("fnalknfklsa", "IOException: "+ignored.getMessage());
                            }
                        }));
    }
}