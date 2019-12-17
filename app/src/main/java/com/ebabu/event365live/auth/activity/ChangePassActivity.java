package com.ebabu.event365live.auth.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ChangePasswordLayoutBinding;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.ebabu.event365live.utils.ValidationUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;

public class ChangePassActivity extends AppCompatActivity implements GetResponseData {

    private ChangePasswordLayoutBinding changePassBinding;
    private MyLoader myLoader;
    String oldPass = "", newPass = "", confirmPass = "";
    private boolean isFirstTimeOldPassClick,isFirstTimeNewPassClick,isFirstTimeConfirmPassClick;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changePassBinding = DataBindingUtil.setContentView(this, R.layout.change_password_layout);
        myLoader = new MyLoader(this);
        changePassBinding.etOldPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                oldPass = changePassBinding.etOldPass.getText().toString();
                if (charSequence.length() > 0) {
                    if (ValidationUtil.passwordValidatorWithoutToast(ChangePassActivity.this, charSequence.toString())) {
                        changePassBinding.ivShowOldPassTick.setVisibility(View.VISIBLE);
                    } else {
                        changePassBinding.ivShowOldPassTick.setVisibility(View.INVISIBLE);
                        changePassBinding.ivShowOldPass.setVisibility(View.VISIBLE);
                    }
                } else
                    changePassBinding.ivShowOldPass.setVisibility(View.INVISIBLE);
            }


            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        changePassBinding.etNewPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                newPass = changePassBinding.etNewPass.getText().toString();
                if (charSequence.length() > 0) {
                    if (ValidationUtil.passwordValidatorWithoutToast(ChangePassActivity.this, charSequence.toString())) {
                        changePassBinding.ivShowNewPassTick.setVisibility(View.VISIBLE);
                    } else {
                        changePassBinding.ivShowNewPassTick.setVisibility(View.INVISIBLE);
                        changePassBinding.ivShowNewPassIcon.setVisibility(View.VISIBLE);
                    }
                } else
                    changePassBinding.ivShowNewPassIcon.setVisibility(View.INVISIBLE);
            }


            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        changePassBinding.etConfirmPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                confirmPass = changePassBinding.etConfirmPass.getText().toString();
                if (charSequence.length() > 0) {
                    if (ValidationUtil.passwordValidatorWithoutToast(ChangePassActivity.this, charSequence.toString())) {
                        changePassBinding.ivShowConfirmPassTick.setVisibility(View.VISIBLE);
                    } else {
                        changePassBinding.ivShowConfirmPassTick.setVisibility(View.INVISIBLE);
                        changePassBinding.ivShowConfirmPass.setVisibility(View.VISIBLE);
                    }
                } else
                    changePassBinding.ivShowConfirmPass.setVisibility(View.INVISIBLE);
            }


            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    public void changePassOnClick(View view) {
        if (!ValidationUtil.passwordValidator(ChangePassActivity.this, oldPass))
            return;
        else if (!ValidationUtil.passwordValidator(ChangePassActivity.this, newPass))
            return;
        else if (!ValidationUtil.passwordValidator(ChangePassActivity.this, confirmPass))
            return;

        changePasswordRequest(oldPass, newPass, confirmPass);

    }

    private void changePasswordRequest(String getOldPass, String getNewPass, String getConfirmPass) {

        if (!getNewPass.equalsIgnoreCase(getConfirmPass)) {
            ShowToast.errorToast(ChangePassActivity.this, getString(R.string.password_does_not_match));
            return;
        }
        myLoader.show("Please Wait...");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Constants.ApiKeyName.newPassword, getConfirmPass);
        Call<JsonElement> changePasObj = APICall.getApiInterface().changePassword("", jsonObject);
        new APICall(ChangePassActivity.this).apiCalling(changePasObj, this, APIs.CHANGE_PASSWORD);

    }

    public void backBtnOnClick(View view) {
        finish();
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        if (responseObj != null) {

        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        ShowToast.errorToast(ChangePassActivity.this, message);
    }


    public void showOldPassOnClick(View view) {
        if(changePassBinding.ivShowOldPass.isShown() && !isFirstTimeOldPassClick)
        {
            changePassBinding.ivShowOldPass.setImageDrawable(getResources().getDrawable(R.drawable.hide_pass_icon));
            changePassBinding.etOldPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            changePassBinding.etOldPass.setSelection(changePassBinding.etOldPass.length());
            isFirstTimeOldPassClick = true;
        }
        else if(changePassBinding.ivShowOldPass.isShown() && isFirstTimeOldPassClick){
            changePassBinding.ivShowOldPass.setImageDrawable(getResources().getDrawable(R.drawable.unselect_pass_icon));
            changePassBinding.etOldPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            changePassBinding.etOldPass.setSelection(changePassBinding.etOldPass.length());
            isFirstTimeOldPassClick = false;
        }
    }

    public void showNewPassOnClick(View view) {
        if(changePassBinding.ivShowNewPassIcon.isShown() && !isFirstTimeNewPassClick)
        {
            changePassBinding.ivShowNewPassIcon.setImageDrawable(getResources().getDrawable(R.drawable.hide_pass_icon));
            changePassBinding.etNewPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            changePassBinding.etNewPass.setSelection(changePassBinding.etNewPass.length());
            isFirstTimeNewPassClick = true;
        }
        else if(changePassBinding.ivShowNewPassIcon.isShown() && isFirstTimeNewPassClick){
            changePassBinding.ivShowNewPassIcon.setImageDrawable(getResources().getDrawable(R.drawable.unselect_pass_icon));
            changePassBinding.etNewPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            changePassBinding.etNewPass.setSelection(changePassBinding.etNewPass.length());
            isFirstTimeNewPassClick = false;
        }
    }
    public void showConfirmPassOnClick(View view) {
        if(changePassBinding.ivShowNewPassIcon.isShown() && !isFirstTimeConfirmPassClick)
        {
            changePassBinding.ivShowConfirmPass.setImageDrawable(getResources().getDrawable(R.drawable.hide_pass_icon));
            changePassBinding.etConfirmPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            changePassBinding.etConfirmPass.setSelection(changePassBinding.etConfirmPass.length());
            isFirstTimeConfirmPassClick = true;
        }
        else if(changePassBinding.ivShowConfirmPass.isShown() && isFirstTimeConfirmPassClick){
            changePassBinding.ivShowConfirmPass.setImageDrawable(getResources().getDrawable(R.drawable.unselect_pass_icon));
            changePassBinding.etConfirmPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            changePassBinding.etConfirmPass.setSelection(changePassBinding.etConfirmPass.length());
            isFirstTimeConfirmPassClick = false;
        }
    }
}
