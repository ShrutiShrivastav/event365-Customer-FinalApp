package com.ebabu.event365live.auth.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityResetPassBinding;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.ebabu.event365live.utils.ValidationUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;

public class ResetPassActivity extends AppCompatActivity implements GetResponseData {

    private ActivityResetPassBinding resetPassBinding;
    private String newPassEntered = "", confirmPassEntered = "";
    private MyLoader myLoader;
    private String matchedPass;
    private boolean isNewPassFirstTimeShow, isConfirmPassFirstTimeShow;
    private String getEmail;
    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resetPassBinding = DataBindingUtil.setContentView(this, R.layout.activity_reset_pass);
        myLoader = new MyLoader(this);

        bundle = getIntent().getExtras();

        if(bundle != null){
            getEmail = bundle.getString(Constants.SharedKeyName.userEmail);
        }


        resetPassBinding.etEnterNewPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                newPassEntered = resetPassBinding.etEnterNewPass.getText().toString();
                if (charSequence.length() > 0) {
                    if (ValidationUtil.passwordValidatorWithoutToast(ResetPassActivity.this, charSequence.toString()))
                        resetPassBinding.ivShowTickOne.setVisibility(View.VISIBLE);
                    else
                        resetPassBinding.ivShowTickOne.setVisibility(View.INVISIBLE);
                    resetPassBinding.ivShowNewPassIcon.setVisibility(View.VISIBLE);

                } else {
                    resetPassBinding.ivShowTickOne.setVisibility(View.INVISIBLE);
                    resetPassBinding.ivShowNewPassIcon.setVisibility(View.INVISIBLE);
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        resetPassBinding.etEnterConfirmPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                confirmPassEntered = resetPassBinding.etEnterConfirmPass.getText().toString();
                if (charSequence.length() > 0) {
                    if (ValidationUtil.passwordValidatorWithoutToast(ResetPassActivity.this, charSequence.toString())
                            && newPassEntered.equalsIgnoreCase(confirmPassEntered))
                        resetPassBinding.ivShowTickTwo.setVisibility(View.VISIBLE);
                    else
                        resetPassBinding.ivShowTickTwo.setVisibility(View.INVISIBLE);
                    resetPassBinding.ivShowConfirmPass.setVisibility(View.VISIBLE);
                } else {

                    resetPassBinding.ivShowTickTwo.setVisibility(View.INVISIBLE);
                    resetPassBinding.ivShowConfirmPass.setVisibility(View.INVISIBLE);
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void resetPassOnClick(View view) {

        if (newPassEntered.isEmpty() || confirmPassEntered.isEmpty()) {
            ShowToast.errorToast(ResetPassActivity.this, getString(R.string.filed_can_not_empty));
            return;
        } else if (!newPassEntered.equalsIgnoreCase(confirmPassEntered)) {
            ShowToast.errorToast(ResetPassActivity.this, getString(R.string.password_does_not_match));
            return;
        }

        passwordResetRequest();
    }

    private void passwordResetRequest() {
        myLoader.show("Loading...");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Constants.ApiKeyName.email, getEmail);
        jsonObject.addProperty(Constants.ApiKeyName.newPassword, newPassEntered);

        Call<JsonElement> resetPassObj = APICall.getApiInterface().resetPassword(jsonObject);
        new APICall(ResetPassActivity.this).apiCalling(resetPassObj, this, APIs.RESET_PASSWORD);

    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        if (responseObj != null) {
            ShowToast.successToast(ResetPassActivity.this,message);
            navigateToLogin();
        }

    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        ShowToast.errorToast(ResetPassActivity.this, message);
    }

    public void backBtnOnClick() {
          finish();
    }

    public void newPassShowOnClick(View view) {
        if (resetPassBinding.ivShowNewPassIcon.isShown() && !isNewPassFirstTimeShow) {
            resetPassBinding.ivShowNewPassIcon.setImageDrawable(getResources().getDrawable(R.drawable.hide_pass_icon));
            resetPassBinding.etEnterNewPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            resetPassBinding.etEnterNewPass.setSelection(resetPassBinding.etEnterNewPass.length());
            isNewPassFirstTimeShow = true;
        } else if (resetPassBinding.ivShowNewPassIcon.isShown() && isNewPassFirstTimeShow) {
            resetPassBinding.ivShowNewPassIcon.setImageDrawable(getResources().getDrawable(R.drawable.unselect_pass_icon));
            resetPassBinding.etEnterNewPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            resetPassBinding.etEnterNewPass.setSelection(resetPassBinding.etEnterNewPass.length());
            isNewPassFirstTimeShow = false;
        }
    }

    public void confirmPassShowOnClick(View view) {
        if (resetPassBinding.ivShowConfirmPass.isShown() && !isConfirmPassFirstTimeShow) {
            resetPassBinding.ivShowConfirmPass.setImageDrawable(getResources().getDrawable(R.drawable.hide_pass_icon));
            resetPassBinding.etEnterConfirmPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            resetPassBinding.etEnterConfirmPass.setSelection(resetPassBinding.etEnterConfirmPass.length());
            isConfirmPassFirstTimeShow = true;
        } else if (resetPassBinding.ivShowConfirmPass.isShown() && isConfirmPassFirstTimeShow) {
            resetPassBinding.ivShowConfirmPass.setImageDrawable(getResources().getDrawable(R.drawable.unselect_pass_icon));
            resetPassBinding.etEnterConfirmPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            resetPassBinding.etEnterConfirmPass.setSelection(resetPassBinding.etEnterConfirmPass.length());
            isConfirmPassFirstTimeShow = false;
        }
    }

    public void backBtnOnClick(View view) {
        finish();
    }

    private void navigateToLogin(){
        Intent loginIntent = new Intent(ResetPassActivity.this,LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);
        finish();
    }
}
