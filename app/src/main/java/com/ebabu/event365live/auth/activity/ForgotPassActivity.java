package com.ebabu.event365live.auth.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityForgotPassBinding;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.ebabu.event365live.utils.ValidationUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;

public class ForgotPassActivity extends AppCompatActivity implements GetResponseData {

    private ActivityForgotPassBinding forgotPassBinding;
    private MyLoader myLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        forgotPassBinding = DataBindingUtil.setContentView(this,R.layout.activity_forgot_pass);
        myLoader = new MyLoader(this);
        forgotPassBinding.etEnterEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (ValidationUtil.emailValidatorWithoutToast(ForgotPassActivity.this,charSequence.toString()))
                    forgotPassBinding.ivShowEmailTick.setVisibility(View.VISIBLE);
                else
                    forgotPassBinding.ivShowEmailTick.setVisibility(View.INVISIBLE);
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void resetOnClick(View view) {
        if(!ValidationUtil.emailValidator(ForgotPassActivity.this,forgotPassBinding.etEnterEmail.getText().toString()))
            return;
        emailVerifyRequest();
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
            if(responseObj != null){
                    CommonUtils.getCommonUtilsInstance().validateUserIdFromErrorResponse(responseObj);
                    Intent intent = new Intent(this, OtpVerificationActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("activityName",getString(R.string.is_from_Forgot_pass_activity));
                    intent.putExtra(Constants.SharedKeyName.userEmail,forgotPassBinding.etEnterEmail.getText().toString());
                    startActivity(intent);
                    ShowToast.successToast(ForgotPassActivity.this,message);
            }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        CommonUtils.getCommonUtilsInstance().validateUserIdFromErrorResponse(errorBody);
        ShowToast.infoToast(this,message);
    }

    private void emailVerifyRequest(){
        myLoader.show("Verifying...");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Constants.ApiKeyName.email,forgotPassBinding.etEnterEmail.getText().toString());
        Call<JsonElement> forgotPassObj  = APICall.getApiInterface().forgetPassword(jsonObject);
        new APICall(ForgotPassActivity.this).apiCalling(forgotPassObj,this, APIs.FORGET_PASSWORD);
    }

    public void backBtnOnClick(View view) {
        finish();
    }
}
