package com.ebabu.event365live.httprequest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AlertDialog;

import com.ebabu.event365live.R;
import com.ebabu.event365live.auth.activity.LoginActivity;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.SessionValidation;
import com.ebabu.event365live.utils.ShowToast;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class APICall {
    private Context mContext;
    private JSONObject errorBodyObj;
    private Integer errorCode = 0;
    private String errorMsg;
    private Call<JsonElement> call;
    private GetResponseData returnData;
    String typeAPI;
    private boolean isNoInternetDialogShown;

    // Class constructor
    public APICall(Context ctx) {
        this.mContext = ctx;
    }

    // Call API
    public void apiCalling(Call<JsonElement> call, final GetResponseData returnData, final String typeAPI) {
        this.call = call;
        this.returnData = returnData;
        this.typeAPI = typeAPI;

        try {

            call.enqueue(new Callback<JsonElement>() {

                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                    try {
                        if (response.headers().get(APIs.AUTHORIZATION) != null) {
                                CommonUtils.getCommonUtilsInstance().validateDeviceAuth("Bearer "+response.headers().get(APIs.AUTHORIZATION));
                            }

                        Log.d("APICalling", "onResponse: " + response.message());
                        Log.d("APICalling", "onResponse: " + response.body());
                        Log.d("APICalling", "onResponse: " + response.code());

                        if (response.isSuccessful()) {
                            JSONObject responseObj = new JSONObject(response.body().toString());
                            returnData.onSuccess(responseObj, responseObj.has(APIs.MESSAGE) ? responseObj.getString(APIs.MESSAGE).trim() : "", typeAPI);

                        } else if (response.code() == APIs.SESSION_EXPIRE) {
                            navigateToLogin();
                        } else {

                            errorBodyObj = new JSONObject(response.errorBody().string());
                            errorCode = errorBodyObj.getInt("code");
                            errorMsg = errorBodyObj.getString("message");

                            Log.d(" APICalling", errorMsg+" onResponse: error "+errorCode);

                            if (response.code() == APIs.BLOCK_ADMIN) {
                                returnData.onFailed(errorBodyObj, errorMsg, errorCode, typeAPI);
                                return;
                            } else if (response.code() == APIs.BAD_GATEWAY) {
                                returnData.onFailed(errorBodyObj, errorMsg, errorCode, typeAPI);
                                return;
                            } else if (errorCode == APIs.EMAIL_NOT_VERIFIED || errorCode == APIs.NEED_PROFILE_UPDATE || errorCode == APIs.CHOOSE_RECOMMENDED_CATEGORY) {
                                returnData.onFailed(errorBodyObj, errorMsg, errorCode, typeAPI);
                                return;
                            } else if (response.code() == APIs.OTHER_FAILED || response.code() == APIs.PRECONDITION_FAILED) {
                                if (errorMsg.length() > 0) {
                                    returnData.onFailed(errorBodyObj, errorMsg, errorCode, typeAPI);
                                    return;
                                } else {
                                    returnData.onFailed(errorBodyObj, errorMsg, errorCode, typeAPI);
                                    return;
                                }
                            }
                            nullCase(returnData, typeAPI);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        nullCase(returnData, typeAPI);
                        Log.d("fanslfbasjkf", "Exception e: "+e.getMessage());
                    }
                }
                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    if(t instanceof SocketTimeoutException){
                        ShowToast.errorToast(mContext,mContext.getString(R.string.time_out));
                        ((Activity)mContext).finish();
                        return;
                    }
                    nullCase(returnData, typeAPI);
                    Log.d("fanslfbasjkf", "onFailure: "+t.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            nullCase( returnData, typeAPI);
            Log.d("fanslfbasjkf", "Exception last: "+e.getMessage());
        }
    }
    private void nullCase(GetResponseData returnData, String typeAPI) {
        if (!CommonUtils.getCommonUtilsInstance().isNetworkAvailable(mContext)){
            gpsAlertDialog();
        }
        else {
            returnData.onFailed(errorBodyObj,mContext.getString(R.string.something_wrong),errorCode, typeAPI);
        }
    }

    public static ApiInterface getApiInterface() {
        return ApiClient.getClient().create(ApiInterface.class);
    }
    private void navigateToLogin(){
        CommonUtils.getCommonUtilsInstance().deleteUser();
        Intent loginIntent = new Intent(mContext,LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(loginIntent);
        ShowToast.infoToast(mContext,mContext.getString(R.string.session_expired));
        ((Activity)mContext).finish();
    }

    private void gpsAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        View view = LayoutInflater.from(mContext).inflate(R.layout.no_internet_layout, null, false);
        builder.setView(view);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.show();
        view.findViewById(R.id.btnRetry).setOnClickListener(v -> {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    apiCalling(call.clone(),returnData,typeAPI);
                }
            },500);
            dialog.dismiss();
        });
    }
}