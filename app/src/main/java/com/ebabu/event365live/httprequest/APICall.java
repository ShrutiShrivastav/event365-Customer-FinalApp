package com.ebabu.event365live.httprequest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ebabu.event365live.R;
import com.ebabu.event365live.auth.activity.LoginActivity;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.SessionValidation;
import com.ebabu.event365live.utils.ShowToast;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class APICall {
    private Context mContext;
    private JSONObject errorBodyObj;
    private Integer errorCode = 0;
    private String errorMsg;

    // Class constructor
    public APICall(Context ctx) {
        this.mContext = ctx;
    }

    // Call API
    public void apiCalling(Call<JsonElement> call, final GetResponseData returnData, final String typeAPI) {

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
                            nullCase(false, returnData, typeAPI);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        nullCase(false, returnData, typeAPI);
                        Log.d("fanslfbasjkf", "Exception e: "+e.getMessage());
                    }
                }
                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    nullCase(true, returnData, typeAPI);
                    Log.d("fanslfbasjkf", "onFailure: ");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            nullCase(false, returnData, typeAPI);
            Log.d("fanslfbasjkf", "Exception last: ");
        }
    }
    private void nullCase(boolean isNetwork, GetResponseData returnData, String typeAPI) {
        if (isNetwork)
            returnData.onFailed(errorBodyObj,mContext.getString(R.string.check_network),errorCode, typeAPI);
        else {
            Log.d("fnlaknfklasnlfnas", "nullCase: ");
            returnData.onFailed(errorBodyObj,mContext.getString(R.string.something_wrong),errorCode, typeAPI);
        }
    }

    public static ApiInterface getApiInterface() {
        return ApiClient.getClient().create(ApiInterface.class);
    }
    private void navigateToLogin(){
        CommonUtils.getCommonUtilsInstance().deleteUser(mContext);
        Intent loginIntent = new Intent(mContext,LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(loginIntent);
        ShowToast.infoToast(mContext,mContext.getString(R.string.session_expired));
        ((Activity)mContext).finish();
    }
}